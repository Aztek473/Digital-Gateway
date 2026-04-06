package iso8583.enums;
public enum EIsoMessageClass
{
	/** x1xx - Autorización */
	AUTHORIZATION(		'1',	"Autorización"),
	/** x2xx - Financiero */
	FINANCIAL(			'2',	"Financiero"),
	/** x3xx - Manejo de Archivos */
	FILE_ACTIONS(		'3',	"Manejo de Archivos"),
	/** x4xx - Reverso */
	REVERSAL(			'4',	"Reverso"),
	/** x5xx - Reconciliación */
	RECONCILIATION(		'5',	"Reconciliación"),
	/** x6xx - Administrativo */
	ADMINISTRATIVE(		'6',	"Administrativo"),
	/** x7xx - Cobro de tarifas */
	FEE_COLLECTION(		'7',	"Cobro de tarifas"),
	/** x8xx - Gestión de red */
	NETWORK_MANAGEMENT(	'8',	"Gestión de red"),
	/** x9xx - Reservado por la ISO */
	RESERVED(			'9',	"Reservado por la ISO");
	
	private final char digit;
	private final String description;
	
	private EIsoMessageClass(char digit, String description)
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
