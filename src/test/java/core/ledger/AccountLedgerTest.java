package core.ledger;
import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.stream.Stream;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;

import core.ledger.interfaces.AccountLedger;

public class AccountLedgerTest
{
	@Test
	void shouldDemonstrateRaceConditionWithoutSync() throws InterruptedException
	{
		AccountLedger ledger = new UnsafeAccountLedger();
		ledger.credit("CTA-001", 100_000L); // saldo inicial: S/ 1,000.00
		int threads = 20;
		long debitAmount = 10_000L; // S/ 100 por thread = S/ 2000 total
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		CountDownLatch latch = new CountDownLatch(threads);
		List<Future<Long>> results = new ArrayList<>();
		for( int i = 0; i < threads; i++ )
		{
			results.add(pool.submit(() -> {
				latch.countDown();
				latch.await(); // Barrera para que exploten todos contra la RAM al mismo tiempo
				return ledger.debit("CTA-001", debitAmount);
			}));
		}
		pool.shutdown();
		pool.awaitTermination(5, TimeUnit.SECONDS);
		System.out.println("====== RACE CONDITION UN-SYNC ======");
		System.out.println("Saldo Teoricamente debió ser S/ 800.00 (80000) o que lance excepcion");
		System.out.println("Saldo Final Real (unsafe): " + ledger.getBalance("CTA-001"));
		System.out.println("====================================");
	}
	
	@ParameterizedTest
	@MethodSource("provideImplementations")
	void shouldHandleConcurrentDebitsCorrectly(AccountLedger ledger, String implName) throws InterruptedException
	{
		ledger.credit("CTA-TEST", 1_000_000L); // S/ 10,000.00 iniciales
		int threads = 50;
		long debitAmount = 20_000L; // S/ 200 por thread x 50 = S/ 10,000 robados. El saldo bebe quedar 0 exacto.
		ExecutorService pool = Executors.newFixedThreadPool(threads);
		CountDownLatch latch = new CountDownLatch(threads);
		List<Future<Long>> results = new ArrayList<>();
		for( int i = 0; i < threads; i++ )
		{
			results.add(pool.submit(() -> {
				latch.countDown();
				latch.await();
				return ledger.debit("CTA-TEST", debitAmount);
			}));
		}
		pool.shutdown();
		pool.awaitTermination(30, TimeUnit.SECONDS);
		// ¡Validación Crítica! Exigimos el resultado de los 50 hilos.
		// Si uno tiró ConcurrentUpdate o LockTimeout escondido, el Future.get() lo va a escupir y hará fallar al Test!
		for( Future<Long> future : results )
		{
			try
			{
				future.get();
			}
			catch (ExecutionException e)
			{
				throw new AssertionError("🚨 Un hilo fracasó intentando debitar: " + e.getCause().getMessage(), e);
			}
		}
		assertEquals(0L, ledger.getBalance("CTA-TEST"), "⚠️ " + implName + " falló en proteger el saldo, resultando en: " + ledger.getBalance("CTA-TEST"));
		System.out.println("El algoritmo de bloqueo [" + implName + "] protegió EXITOSAMENTE la cuenta con saldo : 0");
	}
	
	static Stream<Arguments> provideImplementations()
	{
		return Stream.of(
				// Optmistic con 100 retries (porque 50 hilos pegan mucho) y 10ms de backoff
				Arguments.of(new OptimisticAccountLedger(100, 10), "Optimistic (CAS)"),
				// Pessimistic perdonando hasta 5000ms antes de tirar LockTimeoutException
				Arguments.of(new PessimisticAccountLedger(5000), "Pessimistic (Locks)"));
	}
}
