package iso8583.parser;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import iso8583.enums.EIsoField;
import iso8583.enums.EIsoLengthType;
import model.IsoMessage;

public class ISO8583Parser
{
	public IsoMessage parseMessage(String rawMessage) throws Exception
	{
		if( rawMessage == null || rawMessage.length() < 20 )
		{
			throw new IllegalArgumentException("La trama ISO de entrada está vacía o es demasiado corta.");
		}
		int cursor = 0;
		
		// 1. Extraer el mti 4
		String mti = rawMessage.substring(cursor, cursor + 4);
		cursor += 4;
		
		// 2. Extraer el Bitmap Primario 16
		String primaryBitmapHex = rawMessage.substring(cursor, cursor + 16);
		cursor += 16;
		String totalBitmapHex = primaryBitmapHex;
		
		// 3. Validar Bitmap secundario
		int firstHexValue = Integer.parseInt(primaryBitmapHex.substring(0, 1), 16);
		boolean hasSecondaryBitmap = (firstHexValue & 8) == 8;
		if( hasSecondaryBitmap )
		{
			if( cursor + 16 > rawMessage.length() )
			{
				throw new Exception("La trama se cortó antes de poder leer el Bitmap Secundario entero.");
			}
			// Bitmap Secundario 16
			String secondaryBitmapHex = rawMessage.substring(cursor, cursor + 16);
			totalBitmapHex += secondaryBitmapHex;
			cursor += 16;
		}
		
		// 4. list de filds
		List<Integer> activeFields = getActiveFieldsFromHexBitmap(totalBitmapHex);
		
		// 5. Armado de filds
		LinkedHashMap<Integer, String> parsedFields = new LinkedHashMap<>();
		try
		{
			for( Integer fieldNumber : activeFields )
			{
				if( fieldNumber == 1 )
					continue; // bitmap Secundario
				EIsoField isoField = EIsoField.fromFieldNumber(fieldNumber);
				if( isoField == null )
				{
					throw new Exception("El campo " + fieldNumber + " se marcó activo pero no existe en EIsoField.");
				}
				String fieldValue;
				if( isoField.getLengthType() == EIsoLengthType.FIXED )
				{
					// FIXED - Longitud exacta
					int len = isoField.getLength();
					if( cursor + len > rawMessage.length() )
					{
						throw new Exception("Trama incompleta en el Campo " + fieldNumber + ". Se esperaban " + len + " caracteres fijos.");
					}
					fieldValue = rawMessage.substring(cursor, cursor + len);
					cursor += len;
				}
				else
				{
					// Variable : LLVAR / LLLVAR - LongitudDinamica xx/xxx
					int prefixQty = isoField.getLengthType().getLength();
					
					// Validar longitud del prefijo
					if( cursor + prefixQty > rawMessage.length() )
					{
						throw new Exception("Trama incompleta leyendo la longitud variable del Campo " + fieldNumber);
					}
					
					// Longitud
					String prefixStr = rawMessage.substring(cursor, cursor + prefixQty);
					int dynamicLen = Integer.parseInt(prefixStr);
					cursor += prefixQty;
					
					// Validar que realmente vengan los caracteres dinámicos prometidos
					if( cursor + dynamicLen > rawMessage.length() )
					{
						throw new Exception("Trama incompleta. El prefijo del Campo " + fieldNumber + " exigía " + dynamicLen + " caracteres pero el mensaje terminó abruptamente.");
					}
					fieldValue = rawMessage.substring(cursor, cursor + dynamicLen);
					cursor += dynamicLen;
				}
				parsedFields.put(fieldNumber, fieldValue);
			}
		}
		catch (Exception e)
		{
			throw new Exception("La trama es invalida al ISO 8583.");
		}
		
		return new IsoMessage(mti, parsedFields);
	}
	
	/**
	 * Devuelve lista fields 2, 3, 4, 11...
	 */
	private List<Integer> getActiveFieldsFromHexBitmap(String hexBitmap)
	{
		List<Integer> activeFields = new ArrayList<>();
		for( int i = 0; i < hexBitmap.length(); i++ )
		{
			int dec = Character.digit(hexBitmap.charAt(i), 16);
			if( (dec & 8) != 0 )
				activeFields.add((i * 4) + 1);
			if( (dec & 4) != 0 )
				activeFields.add((i * 4) + 2);
			if( (dec & 2) != 0 )
				activeFields.add((i * 4) + 3);
			if( (dec & 1) != 0 )
				activeFields.add((i * 4) + 4);
		}
		return activeFields;
	}
}
