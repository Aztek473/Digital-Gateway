package main.java.iso8583.enums;

public enum EIsoVersion
{
	V_1987('0'),
	V_1993('1'),
	V_2003('2'),
	;
	
	private final char digit;
	
	private EIsoVersion(char digit)
	{
		this.digit = digit;
	}

	public char getDigit()
	{
		return digit;
	}
}
