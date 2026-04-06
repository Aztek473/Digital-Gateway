package main.java.iso8583.builder;

import main.java.iso8583.enums.EIsoMessageClass;
import main.java.iso8583.enums.EIsoMessageFunction;
import main.java.iso8583.enums.EIsoMessageOrigin;
import main.java.iso8583.enums.EIsoVersion;

public class MTIBuilder
{
	private String mti;
	
	public MTIBuilder version(EIsoVersion version) throws Exception
	{
		if ( version != null )
		{
			this.mti.concat(String.valueOf(version.getDigit()));
			return this;
		}
		else
		{
			throw new Exception("ISO Version is null");
		}
	}
	
	public MTIBuilder messageClass(EIsoMessageClass messageClass) throws Exception
	{
		if ( messageClass != null )
		{
			this.mti.concat(String.valueOf(messageClass.getDigit()));
			return this;
		}
		else
		{
			throw new Exception("ISO MessageClass is null");
		}
	}
	
	public MTIBuilder function(EIsoMessageFunction function) throws Exception
	{
		if ( function != null )
		{
			this.mti.concat(String.valueOf(function.getDigit()));
			return this;
		}
		else
		{
			throw new Exception("ISO Function is null");
		}
	}
	
	public MTIBuilder origin(EIsoMessageOrigin origin) throws Exception
	{
		if ( origin != null )
		{
			this.mti.concat(String.valueOf(origin.getDigit()));
			return this;
		}
		else
		{
			throw new Exception("ISO Origin is null");
		}
	}
	
	public String build()
	{
		return mti;
	}

	
}
