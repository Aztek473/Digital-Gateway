package model;

import java.util.Map;

import iso8583.enums.EIsoMessageClass;
import iso8583.enums.EIsoMessageFunction;
import iso8583.enums.EIsoMessageOrigin;
import iso8583.enums.EIsoVersion;

public class IsoMessage
{
	private final String mti;
	
	private EIsoVersion version;
	private EIsoMessageClass messageClass;
	private EIsoMessageFunction function;
	private EIsoMessageOrigin origin;
	
	private final Map<Integer, String> parsedFields;
	
	public IsoMessage(String mti, Map<Integer, String> parsedFields)
	{
		this.mti = mti;
		this.parsedFields = parsedFields;
		
		processMTI();
	}
	
	private void processMTI() throws IllegalArgumentException
	{
		char[] mti = this.mti.toCharArray();
		
		version = EIsoVersion.valueOfDigit(mti[0]);
		messageClass = EIsoMessageClass.valueOfDigit(mti[1]);
		function = EIsoMessageFunction.valueOfDigit(mti[2]);
		origin = EIsoMessageOrigin.valueOfDigit(mti[3]);
	}

	@Override
	public String toString()
	{
		return "ParsedIsoMessage{" + "MTI='" + mti + '\'' + ", Fields=" + parsedFields + '}';
	}
	
	public String getMti()
	{
		return mti;
	}
	
	public Map<Integer, String> getFields()
	{
		return parsedFields;
	}

	public EIsoVersion getVersion()
	{
		return version;
	}

	public EIsoMessageClass getMessageClass()
	{
		return messageClass;
	}

	public EIsoMessageFunction getFunction()
	{
		return function;
	}

	public EIsoMessageOrigin getOrigin()
	{
		return origin;
	}
	
	
}
