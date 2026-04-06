package main.java.iso8583.enums;
public enum EIsoField
{
	/** Bit Map - b 16 */
	DE_1(1, 16, EIsoLengthType.FIXED, EIsoFieldType.BINARY, "Bit Map"),
	/** Primary Account Number (PAN) - an ..19 */
	DE_2(2, 19, EIsoLengthType.LLVAR, EIsoFieldType.ALPHANUMERIC, "Primary Account Number (PAN)"), // n
	/** Processing Code - n 6 */
	DE_3(3, 6, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Processing Code"),
	/** Amount, Transaction - n 12 */
	DE_4(4, 12, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Amount, Transaction"),
	/** Amount, Settlement - n 12 */
	DE_5(5, 12, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Amount, Settlement"),
	/** Amount, Cardholder Billing - n 12 */
	DE_6(6, 12, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Amount, Cardholder Billing"),
	/** Transmission Date and Time - n 10 */
	DE_7(7, 10, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Transmission Date and Time"),
	/** Amount, Cardholder Billing Fee - n 8 */
	DE_8(8, 8, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Amount, Cardholder Billing Fee"),
	/** Conversion Rate, Settlement - n 8 */
	DE_9(9, 8, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Conversion Rate, Settlement"),
	/** Conversion Rate, Cardholder Billing - n 8 */
	DE_10(10, 8, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Conversion Rate, Cardholder Billing"),
	/** System Trace Audit Number - n 6 */
	DE_11(11, 6, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "System Trace Audit Number"),
	/** Time, Local Transaction - n 6 */
	DE_12(12, 6, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Time, Local Transaction"),
	/** Date, Local Transaction - n 4 */
	DE_13(13, 4, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Date, Local Transaction"),
	/** Date, Expiration - n 4 */
	DE_14(14, 4, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Date, Expiration"),
	/** Date, Settlement - n 4 */
	DE_15(15, 4, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Date, Settlement"),
	/** Date, Conversion - n 4 */
	DE_16(16, 4, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Date, Conversion"),
	/** Date, Capture - n 4 */
	DE_17(17, 4, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Date, Capture"),
	/** Merchant Type - n 4 */
	DE_18(18, 4, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Merchant Type"),
	/** Acquiring Institution Country Code - n 3 */
	DE_19(19, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Acquiring Institution Country Code"),
	/** PAN Extended, Country Code - n 3 */
	DE_20(20, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "PAN Extended, Country Code"),
	/** Forwarding Institution. Country Code - n 3 */
	DE_21(21, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Forwarding Institution. Country Code"),
	/** POS Entry Mode - n 3 */
	DE_22(22, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "POS Entry Mode"),
	/** Card Sequence Number - n 3 */
	DE_23(23, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Card Sequence Number"),
	/** Network International Identifier (NII) - n 3 */
	DE_24(24, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Network International Identifier (NII)"),
	/** POS Condition Code - n 2 */
	DE_25(25, 2, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "POS Condition Code"),
	/** POS Capture Code - n 2 */
	DE_26(26, 2, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "POS Capture Code"),
	/** Authorizing Identification Response Length - n 1 */
	DE_27(27, 1, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Authorizing Identification Response Length"),
	/** Amount, Transaction Fee - an 8 */
	DE_28(28, 8, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC, "Amount, Transaction Fee"),
	/** Amount, Settlement Fee - an 8 */
	DE_29(29, 8, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC, "Amount, Settlement Fee"),
	/** Amount, Transaction Processing Fee - an 8 */
	DE_30(30, 8, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC, "Amount, Transaction Processing Fee"),
	/** Amount, Settlement Processing Fee - an 8 */
	DE_31(31, 8, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC, "Amount, Settlement Processing Fee"),
	/** Acquiring Institution Identification Code - n ..11 */
	DE_32(32, 11, EIsoLengthType.LLVAR, EIsoFieldType.NUMERIC, "Acquiring Institution Identification Code"),
	/** Forwarding Institution Identification Code - n ..11 */
	DE_33(33, 11, EIsoLengthType.LLVAR, EIsoFieldType.NUMERIC, "Forwarding Institution Identification Code"),
	/** Primary Account Number, Extended - ns ..28 */
	DE_34(34, 28, EIsoLengthType.LLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Primary Account Number, Extended"),
	/** Track 2 Data - z ..37 */
	DE_35(35, 37, EIsoLengthType.LLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Track 2 Data"),
	/** Track 3 Data - z ...104 */
	DE_36(36, 104, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Track 3 Data"),
	/** Retrieval Reference Number - ans 12 */
	DE_37(37, 12, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Retrieval Reference Number"), // an
	/** Authorization Identification Response - an 6 */
	DE_38(38, 6, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC, "Authorization Identification Response"), // an
	/** Response Code - an 2 */
	DE_39(39, 2, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC, "Response Code"), // an
	/** Service Restriction Code - an 3 */
	DE_40(40, 3, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC, "Service Restriction Code"),
	/** Card Acceptor Terminal Identification - ans 8 */
	DE_41(41, 8, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Card Acceptor Terminal Identification"),
	/** Card Acceptor Identification Code - ans 15 */
	DE_42(42, 15, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Card Acceptor Identification Code"),
	/** Card Acceptor Name/Location - ans 40 */
	DE_43(43, 40, EIsoLengthType.FIXED, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Card Acceptor Name/Location"),
	/** Additional Response Data - an ..25 */
	DE_44(44, 25, EIsoLengthType.LLVAR, EIsoFieldType.ALPHANUMERIC, "Additional Response Data"),
	/** Track 1 Data - an ..76 */
	DE_45(45, 76, EIsoLengthType.LLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Track 1 Data"),
	/** Additional Data - ISO - an ...999 */
	DE_46(46, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC, "Additional Data - ISO"),
	/** Additional Data - National - an ...999 */
	DE_47(47, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC, "Additional Data - National"),
	/** Additional Data - Private - ans ...999 */
	DE_48(48, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Additional Data - Private"),
	/** Transaction Currency Code - n 3 */
	DE_49(49, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Transaction Currency Code"),
	/** Settlement Currency Code - n 3 */
	DE_50(50, 3, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Settlement Currency Code"),
	/** Cardholder Billing Currency Code - a 3 */
	DE_51(51, 3, EIsoLengthType.FIXED, EIsoFieldType.ALPHA, "Cardholder Billing Currency Code"),
	/** Personal Identification Number Data - b 16 */
	DE_52(52, 16, EIsoLengthType.FIXED, EIsoFieldType.BINARY, "Personal Identification Number Data"),
	/** Security Related Control Information - n 18 */
	DE_53(53, 18, EIsoLengthType.FIXED, EIsoFieldType.NUMERIC, "Security Related Control Information"),
	/** Additional Amounts - ans ...120 */
	DE_54(54, 120, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Additional Amounts"),
	/** Reserved ISO - ans ...999 */
	DE_55(55, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved ISO"),
	/** Reserved ISO - ans ...999 */
	DE_56(56, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved ISO"),
	/** Reserved National - ans ...999 */
	DE_57(57, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved National"),
	/** Reserved National - ans ...999 */
	DE_58(58, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved National"),
	/** Reserved National - ans ...999 */
	DE_59(59, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved National"),
	/** Reserved National - ans ...999 */
	DE_60(60, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved National"),
	/** Reserved Private - ans ...999 */
	DE_61(61, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved Private"),
	/** Reserved Private - ans ...999 */
	DE_62(62, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved Private"),
	/** Reserved Private Use - ans ...999 */
	DE_63(63, 999, EIsoLengthType.LLLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Reserved Private Use"),
	/** Message Authentication Code (MAC) - b 16 */
	DE_64(64, 16, EIsoLengthType.FIXED, EIsoFieldType.BINARY, "Message Authentication Code (MAC)"),
	/** Account Identification 1 - ans ..28 */
	DE_102(102, 28, EIsoLengthType.LLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Account Identification 1"),
	/** Account Identification 2 - ans ..28 */
	DE_103(103, 28, EIsoLengthType.LLVAR, EIsoFieldType.ALPHANUMERIC_SPECIAL, "Account Identification 2");
	
	private final int field;
	private final int length;
	private final EIsoLengthType lengthType;
	private final EIsoFieldType fieldType;
	private final String description;
	
	private EIsoField(int field, int length, EIsoLengthType lengthType, EIsoFieldType fieldType, String description)
	{
		this.field = field;
		this.length = length;
		this.lengthType = lengthType;
		this.fieldType = fieldType;
		this.description = description;
	}
	
	public int getField()
	{
		return field;
	}
	
	public int getLength()
	{
		return length;
	}
	
	public EIsoLengthType getLengthType()
	{
		return lengthType;
	}
	
	public EIsoFieldType getFieldType()
	{
		return fieldType;
	}
	
	public String getDescription()
	{
		return description;
	}
	
	public static EIsoField fromFieldNumber(int field)
	{
		for( EIsoField e : values() )
		{
			if( e.field == field )
			{
				return e;
			}
		}
		return null;
	}
}
