package core.ledger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.locks.ReentrantLock;

import core.ledger.exception.InsufficientFundsException;
import core.ledger.exception.LockTimeoutException;
import core.ledger.interfaces.AccountLedger;

public class PessimisticAccountLedger implements AccountLedger
{
	// Lock físico separado e independiente por cada cuenta diferente
	private final Map<String, ReentrantLock> accountLocks = new ConcurrentHashMap<>();
	private final Map<String, Long> balances = new ConcurrentHashMap<>();
	private final long lockTimeoutMs;
	
	public PessimisticAccountLedger(long lockTimeoutMs)
	{
		this.lockTimeoutMs = lockTimeoutMs;
	}
	
	@Override
	public long debit(String accountId, long amount)
	{
		ReentrantLock lock = accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
		try
		{
			// Intentar adueñarse de la cuenta (fila), si está ocupada esperamos lockTimeoutMs
			if( !lock.tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS) )
			{
				throw new LockTimeoutException("Timeout de " + lockTimeoutMs + "ms. La DB está trancada procesando otra trx.");
			}
			try
			{
				// SECCIÓN CRÍTICA EXCLUSIVA
				long current = balances.getOrDefault(accountId, 0L);
				if( current < amount )
				{
					throw new InsufficientFundsException("Fondos insuficientes en " + accountId);
				}
				long next = current - amount;
				balances.put(accountId, next);
				return next;
			}
			finally
			{
				// MUY IMPORTANTE: Suelta el lock para el siguiente obrero pase lo que pase
				lock.unlock();
				cleanupLock(accountId, lock); // Evitamos Mem Leak
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw new RuntimeException("Hilo interrumpido violentamente esperando Lock DB.");
		}
	}
	
	@Override
	public long credit(String accountId, long amount)
	{
		ReentrantLock lock = accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
		try
		{
			if( !lock.tryLock(lockTimeoutMs, TimeUnit.MILLISECONDS) )
			{
				throw new LockTimeoutException("Timeout al acreditar fondos.");
			}
			try
			{
				long current = balances.getOrDefault(accountId, 0L);
				long next = current + amount;
				balances.put(accountId, next);
				return next;
			}
			finally
			{
				lock.unlock();
				cleanupLock(accountId, lock);
			}
		}
		catch (InterruptedException e)
		{
			Thread.currentThread().interrupt();
			throw new RuntimeException("Interrupción");
		}
	}
	
	@Override
	public long getBalance(String accountId)
	{
		// En pesimista estricto las puras lecturas también bloquean para impedir "Lecturas Sucias" (Dirty Reads)
		ReentrantLock lock = accountLocks.computeIfAbsent(accountId, k -> new ReentrantLock());
		lock.lock();
		try
		{
			return balances.getOrDefault(accountId, 0L);
		}
		finally
		{
			lock.unlock();
			cleanupLock(accountId, lock);
		}
	}
	
	// Remueve el objeto lock del Mapa si soy el ultimo y devolví la llave
	private void cleanupLock(String accountId, ReentrantLock lock)
	{
		if( !lock.isLocked() && !lock.hasQueuedThreads() )
		{
			accountLocks.remove(accountId, lock);
		}
	}
}
