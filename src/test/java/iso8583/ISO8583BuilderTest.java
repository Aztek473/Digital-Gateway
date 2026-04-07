package iso8583;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import iso8583.builder.ISO8583Builder;
import iso8583.builder.IsoMtiBuilder;
import iso8583.enums.EIsoField;
import iso8583.enums.EIsoMessageClass;
import iso8583.enums.EIsoMessageFunction;
import iso8583.enums.EIsoMessageOrigin;
import iso8583.enums.EIsoVersion;

class ISO8583BuilderTest
{
	private String mtiBase;
	
	@BeforeEach
	void setUp() throws Exception
	{
		IsoMtiBuilder mtiBuilder = new IsoMtiBuilder().version(EIsoVersion.V_1987) //
				.messageClass(EIsoMessageClass.FINANCIAL) //
				.function(EIsoMessageFunction.REQUEST_RESPONSE) //
				.origin(EIsoMessageOrigin.ACQUIRER);
		mtiBase = mtiBuilder.build(); // Debería ser "0210"
	}
	
	@Test
	void testBuildMessage_Success_WithPrimaryBitmapAndNumericPadding()
	{
		try
		{
			ISO8583Builder builder = new ISO8583Builder(mtiBase);
			Map<Integer, String> fields = new HashMap<>();
			// Activamos los campos 3 y 4
			// DE_3: n 6 (Fijo Numérico) -> max 6
			fields.put(EIsoField.DE_3.getField(), "0100"); // Debería padearse a "000100" (agrega dos 0 a la izq)
			// DE_4: n 12 (Fijo Numérico) -> max 12
			fields.put(EIsoField.DE_4.getField(), "5000"); // Debería padearse a "000000005000" (agrega ocho 0 a la izq)
			String result = builder.buildMessage(fields);
			// MTI: 0210
			// Bitmap Primario (Campos 3 y 4 activados):
			// 1 2 3 4 5 6 7 8 -> 0 0 1 1 0 0 0 0
			// Hex equivalente de 0011 0000 -> 30
			// Hexadecimal completo: 3000000000000000
			assertTrue(result.startsWith("02103000000000000000"), "Debe generar MTI + Bitmap correctamente");
			assertTrue(result.contains("000100"), "Debe padear con ceros a la izquierda el campo 3");
			assertTrue(result.endsWith("000000005000"), "Debe padear con ceros a la izquierda el campo 4");
			System.out.println("Result : " + result);
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	void testBuildMessage_Success_WithVariableLengthFieldLLVAR()
	{
		try
		{
			ISO8583Builder builder = new ISO8583Builder(mtiBase);
			Map<Integer, String> fields = new HashMap<>();
			// DE_2: LLVAR an ..19
			fields.put(2, "4111111111");
			String result = builder.buildMessage(fields);
			// Bitmap para el campo 2:
			// 1 2 3 4 -> 0 1 0 0 -> hex: 4
			// Hex completo: 4000000000000000
			// Longitud del campo 2 ingresado: 10 caracteres. Por lo que su LL debe ser "10"
			assertTrue(result.contains("4000000000000000"), "El bitmap en base Hex debe tener la posicion 2 activa (4)");
			assertTrue(result.endsWith("104111111111"), "Debe concatenar la longitud '10' por ser LLVAR");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	void testBuildMessage_Success_SecondaryBitmapGeneratedWhenFieldOver64()
	{
		try
		{
			ISO8583Builder builder = new ISO8583Builder(mtiBase);
			Map<Integer, String> fields = new HashMap<>();
			// Activamos Campo 3 y el Campo 102
			fields.put(3, "010000");
			fields.put(102, "CUENTA123");
			String result = builder.buildMessage(fields);
			// Como usamos el 102 (mayor a 64), el sistema automáticamente DEBE encender DE_1
			// Eso significa que el primer bit será '1' indicando que hay bitmap secundario.
			// Bits del byte 1: [Bit 1=1 (secundario), Bit 2=0, Bit 3=1 (codigo proceso), Bit 4=0] -> 1010 -> Hex A
			assertTrue(result.startsWith("0210A"), "El bitmap debe iniciar en A indicando secundario habilitado y campo 3");
			assertTrue(result.contains("09CUENTA123"), "El campo 102 es LLVAR, longitud de 9 -> '09'");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	void testBuildMessage_ThrowsException_IfNullField()
	{
		try
		{
			ISO8583Builder builder = new ISO8583Builder(mtiBase);
			Map<Integer, String> fields = new HashMap<>();
			fields.put(3, null);
			Exception e = assertThrows(IllegalArgumentException.class, () -> builder.buildMessage(fields));
			assertTrue(e.getMessage().contains("no puede ser nulo"));
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	@Test
	void testBuildMessage_ThrowsException_IfFieldExceedsLength()
	{
		try
		{
			ISO8583Builder builder = new ISO8583Builder(mtiBase);
			Map<Integer, String> fields = new HashMap<>();
			// Campo 3 tiene máximo longitud 6
			fields.put(3, "1234567");
			Exception e = assertThrows(IllegalArgumentException.class, () -> builder.buildMessage(fields));
			assertTrue(e.getMessage().contains("excede la longitud máxima"));
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	void testBuildMessageBytes_ProducesCorrectAsciiBytes()
	{
		try
		{
			// En la vida real, lo que viaja por el cable TCP es un arreglo de bytes, no texto crudo.
			ISO8583Builder builder = new ISO8583Builder(mtiBase);
			Map<Integer, String> fields = new HashMap<>();
			fields.put(3, "010000");
			String expectedString = builder.buildMessage(fields);
			byte[] rawBytes = builder.buildMessageBytes(fields);
			// Verificamos que contenga exactamente la misma cantidad de bytes ASCII que de caracteres
			Assertions.assertEquals(expectedString.length(), rawBytes.length, "Un caracter ASCII equivale a 1 byte rigurosamente");
			// Simulamos lo que haría el receptor TCP: Reconstruir usando el Encoding oficial
			String decodedString = new String(rawBytes, java.nio.charset.StandardCharsets.US_ASCII);
			Assertions.assertEquals(expectedString, decodedString, "El byte array viaja codificado en ASCII correctamente, y es capaz de recuperar la trama");
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
	
	@Test
	@DisplayName("1.2 Builder de mensaje ISO 8583 de respuesta")
	void testBuildMessage_WithRealCompleteDump()
	{
		System.out.println("ISO8583BuilderTest.testBuildMessage_WithRealCompleteDump()");
		try
		{
			ISO8583Builder builder = new ISO8583Builder("0200");
			Map<Integer, String> fields = new HashMap<>();
			
			fields.put(EIsoField.DE_2.getField(), "4111111111111111");
			fields.put(EIsoField.DE_3.getField(), "010000"); 
			fields.put(EIsoField.DE_4.getField(), "000000050000");
			fields.put(EIsoField.DE_7.getField(), "0405120000");
			fields.put(EIsoField.DE_11.getField(), "000001");
			fields.put(EIsoField.DE_12.getField(), "120000");
			fields.put(EIsoField.DE_13.getField(), "0405");
			fields.put(EIsoField.DE_22.getField(), "051");
			fields.put(EIsoField.DE_25.getField(), "00");
			fields.put(EIsoField.DE_37.getField(), "000000000001");
			fields.put(EIsoField.DE_38.getField(), "123456");
			fields.put(EIsoField.DE_39.getField(), "00");
			fields.put(EIsoField.DE_41.getField(), "BANCOM01");
			fields.put(EIsoField.DE_42.getField(), "BANCOMDIGITAL01");
			fields.put(EIsoField.DE_49.getField(), "604");
			
			String result = builder.buildMessage(fields);
			
			System.out.println("Result : " + result);
			
			assertTrue(result.startsWith("0200"), "Debe de arrancar en 0200");
			assertTrue(result.contains("164111111111111111"), "El PAN debió sumarle el LL 16 delante");
//			assertEquals("0200F23AC48128E080000210411111111111111100000005000024040500000112000004056011101BANCOMDIGITAL     BANCOM001         000000000050000604", result); //FORMATO INCORRECTO
//			assertEquals("0200F23AC48128E080000210411111111111111100000005000024040500000112000004056011101BANCOMDIGITALBANCOM001000000000050000604", result); //FORMATO INCORRECTO
			assertEquals("0200723804800EC08000164111111111111111010000000000050000040512000000000112000004050510000000000000112345600BANCOM01BANCOMDIGITAL01604", result); //FORMATO CORRECTO
		}
		catch (Exception e)
		{
			fail(e.getMessage());
		}
	}
}
