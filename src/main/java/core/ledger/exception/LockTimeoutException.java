package core.ledger.exception;
public class LockTimeoutException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public LockTimeoutException(String message)
	{
		super(message);
	}
}
