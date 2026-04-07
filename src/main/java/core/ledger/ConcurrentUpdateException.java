package core.ledger;
public class ConcurrentUpdateException extends RuntimeException
{
	public ConcurrentUpdateException(String message)
	{
		super(message);
	}
}
