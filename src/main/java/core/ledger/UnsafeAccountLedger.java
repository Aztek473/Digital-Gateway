package core.ledger;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import core.ledger.exception.InsufficientFundsException;
import core.ledger.interfaces.AccountLedger;

public class UnsafeAccountLedger implements AccountLedger
{
	private final Map<String, Long> balances = new ConcurrentHashMap<>();
	
	@Override
	public long debit(String accountId, long amount)
	{
		long current = balances.getOrDefault(accountId, 0L);
		if( current < amount )
		{
			throw new InsufficientFundsException("Fondos insuficientes");
		}
		// Race Condition Clásico: thread preempted aquí causa sobreescritura sucia
		long newBalance = current - amount;
		balances.put(accountId, newBalance);
		return newBalance;
	}
	
	@Override
	public long credit(String accountId, long amount)
	{
		long current = balances.getOrDefault(accountId, 0L);
		long newBalance = current + amount;
		balances.put(accountId, newBalance);
		return newBalance;
	}
	
	@Override
	public long getBalance(String accountId)
	{
		return balances.getOrDefault(accountId, 0L);
	}
}
