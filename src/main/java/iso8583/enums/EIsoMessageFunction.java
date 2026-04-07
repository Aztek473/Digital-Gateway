package iso8583.enums;
public enum EIsoMessageFunction
{
	/** xx0x - Request */
	REQUEST(						'0', "Request"),
	/** xx1x - Response */
	REQUEST_RESPONSE(				'1', "Response"),
	/** xx2x - Advice */
	ADVICE(							'2', "Advice"),
	/** xx3x - Advice Response */
	ADVICE_RESPONSE(				'3', "Advice Response"), 
	/** xx4x - Notification */
	NOTIFICATION(					'4', "Notification"),
	/** xx5x - Notification acknowledgement */
	NOTIFICATION_ACKNOWLEDGEMENT(	'4', "Notification acknowledgement"),
	/** xx6x - Instruction */
	INSTRUCTION(					'4', "Instruction"),
	/** xx7x - Instruction acknowledgement */
	INSTRUCTION_ACKNOWLEDGEMENT(	'4', "Instruction acknowledgement"),
	/** xx8x - Reserved for ISO 8 */
	RESERVED_ISO_8(					'8', "Reserved for ISO 8"),
	/** xx9x - Reserved for ISO 9 */
	RESERVED_ISO_9(					'9', "Reserved for ISO 9"),
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
	
	public static EIsoMessageFunction valueOfDigit(char digit)
	{
		for( EIsoMessageFunction e : values() )
		{
			if( e.getDigit() == digit )
			{
				return e;
			}
		}
		throw new IllegalArgumentException("No existe una versión ISO definida para el dígito: " + digit);
	}
}
