package core.ledger;
public interface AccountLedger
{
	// Debita amount de la cuenta accountId.
	// Lanza InsufficientFundsException si saldo < amount.
	// Lanza ConcurrentUpdateException si detecta conflicto de versión.
	long debit(String accountId, long amount) throws InsufficientFundsException;
	
	// Acredita amount a la cuenta accountId.
	long credit(String accountId, long amount);
	
	long getBalance(String accountId);
}
