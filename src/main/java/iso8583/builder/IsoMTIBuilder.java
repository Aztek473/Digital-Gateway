package iso8583.builder;
import java.util.ArrayList;
import java.util.List;

import iso8583.enums.EIsoMessageClass;
import iso8583.enums.EIsoMessageFunction;
import iso8583.enums.EIsoMessageOrigin;
import iso8583.enums.EIsoVersion;

public class IsoMTIBuilder
{
	private EIsoVersion version;
	private EIsoMessageClass messageClass;
	private EIsoMessageFunction function;
	private EIsoMessageOrigin origin;
	
	public IsoMTIBuilder version(EIsoVersion version) throws Exception
	{
		if( version != null )
		{
			this.version = version;
			return this;
		}
		else
		{
			throw new Exception("Version is null");
		}
	}
	
	public IsoMTIBuilder messageClass(EIsoMessageClass messageClass) throws Exception
	{
		if( messageClass != null )
		{
			this.messageClass = messageClass;
			return this;
		}
		else
		{
			throw new Exception("MessageClass is null");
		}
	}
	
	public IsoMTIBuilder function(EIsoMessageFunction function) throws Exception
	{
		if( function != null )
		{
			this.function = function;
			return this;
		}
		else
		{
			throw new Exception("Function is null");
		}
	}
	
	public IsoMTIBuilder origin(EIsoMessageOrigin origin) throws Exception
	{
		if( origin != null )
		{
			this.origin = origin;
			return this;
		}
		else
		{
			throw new Exception("Origin is null");
		}
	}
	
	public String build() throws Exception
	{
		List<String> missing = new ArrayList<>();
		if( version == null )
		{
			missing.add("Version");
		}
		if( messageClass == null )
		{
			missing.add("MessageClass");
		}
		if( function == null )
		{
			missing.add("Function");
		}
		if( origin == null )
		{
			missing.add("Origin");
		}
		if( !missing.isEmpty() )
		{
			throw new Exception("Faltan inicializar las siguientes propiedades del MTI: " + String.join(", ", missing));
		}
		return String.valueOf(version.getDigit()) + String.valueOf(messageClass.getDigit()) + String.valueOf(function.getDigit()) + String.valueOf(origin.getDigit());
	}
}
