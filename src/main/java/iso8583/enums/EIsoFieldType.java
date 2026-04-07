package iso8583.enums;
public enum EIsoFieldType
{
	NUMERIC(),				// n
	ALPHA(),				// a
	ALPHANUMERIC(),			// an
	ALPHANUMERIC_SPECIAL(),	// ans
	BINARY(),				// b
	;
	
	private EIsoFieldType()
	{
	}
	
}
