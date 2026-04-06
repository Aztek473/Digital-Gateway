package iso8583.enums;
public enum EIsoMessageFunction
{
	/** xx0x - Request */
	REQUEST(			'0', "Request"),
	/** xx1x - Response */
	REQUEST_RESPONSE(	'1', "Response"),
	/** xx2x - Advice */
	ADVICE(				'2', "Advice"),
	/** xx3x - Advice Response */
	ADVICE_RESPONSE(	'3', "Advice Response"), 
	/** xx4x - Notificación */
	NOTIFICATION(		'4', "Notificación"),
	;
	
	private final char digit;
	private final String description;
	
	private EIsoMessageFunction(char digit, String description)
	{
		this.digit = digit;
		this.description = description;
	}
	
	public char getDigit()
	{
		return digit;
	}
	
	public String getDescription()
	{
		return description;
	}
}
