package core.ledger;
public class LockTimeoutException extends RuntimeException
{
	public LockTimeoutException(String message)
	{
		super(message);
	}
}
