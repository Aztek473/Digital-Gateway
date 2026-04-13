package core.ledger.exception;
public class ConcurrentUpdateException extends RuntimeException
{
	private static final long serialVersionUID = 1L;

	public ConcurrentUpdateException(String message)
	{
		super(message);
	}
}
