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
	/** xxx5 - Other Repeat */
	OTHER_REPEAT(		'5',	"Other Repeat");
	
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
}
