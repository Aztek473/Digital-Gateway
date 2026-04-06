package main.java.iso8583.enums;
public enum EIsoLengthType
{
	FIXED(0),	// longitud exacta
	LLVAR(2),	// 2 dígitos al principio para longitud.
	LLLVAR(3);	// 3 dígitos al principio para longitud.
	
	private final int length;
	
	private EIsoLengthType(int length)
	{
		this.length = length;
	}

	public int getLength()
	{
		return length;
	}
}
