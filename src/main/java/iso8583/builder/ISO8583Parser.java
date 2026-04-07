package iso8583.builder;
import iso8583.enums.EIsoMessageClass;
import iso8583.enums.EIsoMessageFunction;
import iso8583.enums.EIsoMessageOrigin;
import iso8583.enums.EIsoVersion;

public class ISO8583Parser
{
	private EIsoVersion version;
	private EIsoMessageClass messageClass;
	private EIsoMessageFunction function;
	private EIsoMessageOrigin origin;
	
	public ISO8583Parser version(EIsoVersion version) throws Exception
	{
		if( version != null )
		{
			this.version = version;
			return this;
		}
		else
		{
			throw new Exception("ISO Version is null");
		}
	}
	
	public ISO8583Parser messageClass(EIsoMessageClass messageClass) throws Exception
	{
		if( messageClass != null )
		{
			this.messageClass = messageClass;
			return this;
		}
		else
		{
			throw new Exception("ISO MessageClass is null");
		}
	}
	
	public ISO8583Parser function(EIsoMessageFunction function) throws Exception
	{
		if( function != null )
		{
			this.function = function;
			return this;
		}
		else
		{
			throw new Exception("ISO Function is null");
		}
	}
	
	public ISO8583Parser origin(EIsoMessageOrigin origin) throws Exception
	{
		if( origin != null )
		{
			this.origin = origin;
			return this;
		}
		else
		{
			throw new Exception("ISO Origin is null");
		}
	}
	
	public String build() throws Exception
	{
		java.util.List<String> missing = new java.util.ArrayList<>();
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
