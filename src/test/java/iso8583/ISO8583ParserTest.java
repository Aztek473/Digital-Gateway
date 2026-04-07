package iso8583;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import iso8583.enums.EIsoField;
import iso8583.parser.ISO8583Parser;
import iso8583.parser.ParsedIsoMessage;

class ISO8583ParserTest
{
	@Test
	@DisplayName("1.1 Parser de mensaje ISO 8583 entrante")
	void testParseMessage_01() throws Exception
	{
		try
		{
			String rawContiguousMessage = "0200" + // MTI
					"F23AC48128E08000" + // BIT MAP PRIMARIO
					"0000000000000000" + // BIT SECUNDARIO PRIMARIO
					"0210" + // ??? INCORRECTO DEBERIA SER LONGITUD DE DE_2
					"4111111111111111" + // DE_2
					"000000050000" + // 
					"240405" + //
					"000001" + //
					"120000" + //
					"0405" + //
					"0405" + //
					"6011" + //
					"101" + //
					"BANCOMDIGITAL     " + //
					"BANCOM001         " + //
					"000000000050000" + //
					"604"; //
			ISO8583Parser parser = new ISO8583Parser();
			Exception e = assertThrows(Exception.class, () -> parser.parseMessage(rawContiguousMessage));
			assertTrue(e.getMessage().contains("La trama es invalida al ISO 8583."));
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("1.1 Parser de mensaje ISO 8583 entrante (Corregido)")
	void testParseMessage_02() throws Exception
	{
		System.out.println("1.1 Parser de mensaje ISO 8583 entrante (Corregido)");
		try
		{
			String rawContiguousMessage = "0200" // MTI
					+ "723804800EC08000" // BITMAP PRIMARIO
					+ "16" // DE_2 Longitud
					+ "4111111111111111" // DE_2
					+ "010000" // DE_3
					+ "000000050000" // DE_4
					+ "0405120000" // DE_7
					+ "000001" // DE_11
					+ "120000" // DE_12
					+ "0405" // DE_13
					+ "051" // DE_22
					+ "00" // DE_25
					+ "000000000001" // DE_37
					+ "123456" // DE_38
					+ "00" // DE_39
					+ "BANCOM01" // DE_41
					+ "BANCOMDIGITAL01" // DE_42
					+ "604" // DE_49
			;
			ISO8583Parser parser = new ISO8583Parser();
			ParsedIsoMessage result = parser.parseMessage(rawContiguousMessage);
			assertNotNull(result);
			assertEquals("0200", result.getMti(), "El MTI extraído debe ser 0200");
			
			System.out.println("-----------------------------------------------");
			System.out.println("MTI          : " + result.getMti());
			
			assertNotNull(result.getVersion());
			System.out.println("VERSION      : " + result.getVersion().getDescription());
			
			assertNotNull(result.getMessageClass());
			System.out.println("MessageClass : " + result.getMessageClass().getDescription());
			
			assertNotNull(result.getFunction());
			System.out.println("Function     : " + result.getFunction().getDescription());
			
			assertNotNull(result.getOrigin());
			System.out.println("Origin       : " + result.getOrigin().getDescription());
			System.out.println("-----------------------------------------------");
			
			result.getFields().entrySet().forEach(x -> {
				assertNotNull(x.getValue());
				System.out.println(EIsoField.fromFieldNumber(x.getKey()).getDescription() + " : " + x.getValue());
			});
		}
		catch (Exception e)
		{
			e.printStackTrace();
			fail(e.getMessage());
		}
	}
	
}
