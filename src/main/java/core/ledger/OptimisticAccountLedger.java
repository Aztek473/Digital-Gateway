package core.ledger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicReference;

import core.ledger.exception.ConcurrentUpdateException;
import core.ledger.exception.InsufficientFundsException;
import core.ledger.interfaces.AccountLedger;

public class OptimisticAccountLedger implements AccountLedger
{
	record AccountEntry(long balance, long version)
	{
	}
	
	private final Map<String, AtomicReference<AccountEntry>> storage = new ConcurrentHashMap<>();
	private final int maxRetries;
	private final long backoffBaseMs;
	
	public OptimisticAccountLedger(int maxRetries, long backoffBaseMs)
	{
		this.maxRetries = maxRetries;
		this.backoffBaseMs = backoffBaseMs;
	}
	
	@Override
	public long debit(String accountId, long amount)
	{
		AtomicReference<AccountEntry> ref = storage.computeIfAbsent(accountId, k -> new AtomicReference<>(new AccountEntry(0L, 0L)));
		int attempt = 0;
		while (attempt <= maxRetries)
		{
			// 1. Lectura del estado actual (Snapshot)
			AccountEntry current = ref.get();
			// 2. Validación Comercial
			if( current.balance() < amount )
			{
				throw new InsufficientFundsException("Fondos insuficientes en " + accountId);
			}
			// 3. Ensamblaje tentativo del nuevo estado sumándole una versión
			AccountEntry next = new AccountEntry(current.balance() - amount, current.version() + 1);
			// 4. Compare y Cambie a Nivel de Hardware
			if( ref.compareAndSet(current, next) )
			{
				return next.balance(); // Éxito Atómico!
			}
			// El CAS rebotó, alguien nos ganó de mano mientras validabamos. Toca backoff y reintento.
			attempt++;
			System.out.println("[Optimistic Lock] Colisión detectada en " + accountId + ". Intento: " + attempt);
			if( attempt <= maxRetries )
			{
				try
				{
					Thread.sleep(backoffBaseMs * attempt); // Exponential backoff sencillo
				}
				catch (InterruptedException e)
				{
					Thread.currentThread().interrupt();
					throw new ConcurrentUpdateException("Interrumpido durante espera de CAS");
				}
			}
		}
		throw new ConcurrentUpdateException("Se superó el máximo de reintentos (" + maxRetries + ") peleando por la concurrencia.");
	}
	
	@Override
	public long credit(String accountId, long amount)
	{
		AtomicReference<AccountEntry> ref = storage.computeIfAbsent(accountId, k -> new AtomicReference<>(new AccountEntry(0L, 0L)));
		while (true)
		{
			AccountEntry current = ref.get();
			AccountEntry next = new AccountEntry(current.balance() + amount, current.version() + 1);
			if( ref.compareAndSet(current, next) )
			{
				return next.balance();
			}
			// En Acreditaciones reintentamos en bucle infinito inmediatamente (spinlock) sin penalidad grave a usuario.
		}
	}
	
	@Override
	public long getBalance(String accountId)
	{
		AtomicReference<AccountEntry> ref = storage.get(accountId);
		return ref == null ? 0L : ref.get().balance();
	}
}
