package iso8583.enums;
public enum EIsoMessageOrigin
{
	/** xxx0 - Acquirer */
	ACQUIRER(			'0',	"Acquirer"),
	/** xxx1 - Acquirer Repeat */
	ACQUIRER_REPEAT(	'1',	"Acquirer Repeat"),
	/** xxx2 - Issuer */
	ISSUER(				'2',	"Issuer"),
	/** xxx3 - Issuer Repeat */
	ISSUER_REPEAT(		'3',	"Issuer Repeat"),
	/** xxx4 - Other */
	OTHER(				'4',	"Other"),
	/** xxx5 - Reserved by ISO 5 */
	OTHER_5(			'5',	"Reserved by ISO 5"),
	/** xxx6 - Reserved by ISO 6 */
	OTHER_6(			'6',	"Reserved by ISO 6"),
	/** xxx7 - Reserved by ISO 7 */
	OTHER_7(			'7',	"Reserved by ISO 7"),
	/** xxx8 - Reserved by ISO 8 */
	OTHER_8(			'8',	"Reserved by ISO 8"),
	/** xxx9 - Reserved by ISO 9 */
	OTHER_9(			'9',	"Reserved by ISO 9"),
	;
	
	private final char digit;
	private final String description;
	
	private EIsoMessageOrigin(char digit, String description)
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
	
	public static EIsoMessageOrigin valueOfDigit(char digit)
	{
		for( EIsoMessageOrigin e : values() )
		{
			if( e.getDigit() == digit )
			{
				return e;
			}
		}
		throw new IllegalArgumentException("No existe una versión ISO definida para el dígito: " + digit);
	}
}
