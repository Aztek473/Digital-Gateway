package iso8583.builder;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.TreeMap;

import iso8583.enums.EIsoField;
import iso8583.enums.EIsoFieldType;
import iso8583.enums.EIsoLengthType;

public class ISO8583Builder
{
	private String mti;
	
	public ISO8583Builder(String mti) throws Exception
	{
		this.mti = mti;
	}
	
	public ISO8583Builder(ISO8583Parser mtiBuilder) throws Exception
	{
		this.mti = mtiBuilder.build();
	}
	
	public String buildMessage(Map<Integer, String> dataFields)
	{
		Map<Integer, String> sortedFields = new TreeMap<>(dataFields);
		String bitmapHex = buildContinuousBitmap(sortedFields);
		StringBuilder messagePayload = new StringBuilder();
		messagePayload.append(mti);
		messagePayload.append(bitmapHex);
		for( Map.Entry<Integer, String> entry : sortedFields.entrySet() )
		{
			int fieldNumber = entry.getKey();
			String rawValue = entry.getValue();
			if( rawValue == null )
			{
				throw new IllegalArgumentException("El campo " + fieldNumber + " no puede ser nulo.");
			}
			EIsoField definition = EIsoField.fromFieldNumber(fieldNumber);
			if( definition == null )
			{
				throw new IllegalArgumentException("El campo " + fieldNumber + " no está definido en el Enum.");
			}
			String formattedValue = formatField(definition, rawValue);
			messagePayload.append(formattedValue);
		}
		return messagePayload.toString();
	}
	
	public byte[] buildMessageBytes(Map<Integer, String> dataFields)
	{
		String payload = buildMessage(dataFields);
		return payload.getBytes(StandardCharsets.US_ASCII);
	}
	
	private String buildContinuousBitmap(Map<Integer, String> dataFields)
	{
		boolean hasSecondary = dataFields.keySet().stream().anyMatch(key -> key > 64);
		int totalBits = hasSecondary ? 128 : 64;
		char[] bitArray = new char[totalBits];
		for( int i = 0; i < totalBits; i++ )
		{
			bitArray[i] = '0';
		}
		if( hasSecondary )
		{
			bitArray[0] = '1';
		}
		for( Integer fieldNumber : dataFields.keySet() )
		{
			int index = fieldNumber - 1;
			bitArray[index] = '1';
		}
		return binaryToHex(new String(bitArray));
	}
	
	/**
	 * Aplica las reglas del estándar para devolver el campo rellenado o con su prefijo LL/LLL.
	 */
	private String formatField(EIsoField isoField, String rawValue)
	{
		if( rawValue.length() > isoField.getLength() )
		{
			throw new IllegalArgumentException(String.format("El campo %d (%s) excede la longitud máxima de %d.", isoField.getField(), rawValue, isoField.getLength()));
		}
		if( isoField.getLengthType() == EIsoLengthType.FIXED )
		{
			return padValue(rawValue, isoField.getLength(), isoField.getFieldType());
		}
		else
		{
			int currentLength = rawValue.length();
			String formatStr = "%0" + isoField.getLengthType().getLength() + "d";
			String lengthPrefix = String.format(formatStr, currentLength);
			return lengthPrefix + rawValue;
		}
	}
	
	/**
	 * Rellena un valor de longitud fija con ceros o espacios según su tipo.
	 */
	private String padValue(String value, int targetLength, EIsoFieldType isoFielType)
	{
		StringBuilder sb = new StringBuilder();
		int charsToAdd = targetLength - value.length();
		if( isoFielType == EIsoFieldType.NUMERIC )
		{
			for( int i = 0; i < charsToAdd; i++ )
			{
				sb.append('0');
			}
			sb.append(value);
		}
		else
		{
			sb.append(value);
			for( int i = 0; i < charsToAdd; i++ )
			{
				sb.append(' ');
			}
		}
		return sb.toString();
	}
	
	/**
	 * Utilidad para convertir string binario a string hexadecimal.
	 */
	private String binaryToHex(String binaryString)
	{
		StringBuilder hexString = new StringBuilder();
		for( int i = 0; i < binaryString.length(); i += 4 )
		{
			String chunk = binaryString.substring(i, i + 4);
			int decimal = Integer.parseInt(chunk, 2);
			hexString.append(Integer.toHexString(decimal).toUpperCase());
		}
		return hexString.toString();
	}
}
