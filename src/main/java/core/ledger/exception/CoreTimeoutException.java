package core.ledger.exception;
public class CoreTimeoutException extends RuntimeException
{
	private static final long serialVersionUID = 1L;
	
	public CoreTimeoutException(String message)
	{
		super(message);
	}
}
