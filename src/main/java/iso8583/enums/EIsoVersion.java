package iso8583.enums;
public enum EIsoVersion
{
	V_1987('0', "1987"), //
	V_1993('1', "1993"), //
	V_2003('2', "2003"), //
	;
	
	private final char digit;
	private final String description;
	
	private EIsoVersion(char digit, String description)
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
	
	public static EIsoVersion valueOfDigit(char digit)
	{
		for( EIsoVersion e : values() )
		{
			if( e.getDigit() == digit )
			{
				return e;
			}
		}
		throw new IllegalArgumentException("No existe una versión ISO definida para el dígito: " + digit);
	}
}
