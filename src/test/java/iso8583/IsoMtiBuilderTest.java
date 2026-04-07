package iso8583;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import iso8583.builder.IsoMTIBuilder;
import iso8583.enums.EIsoMessageClass;
import iso8583.enums.EIsoMessageFunction;
import iso8583.enums.EIsoMessageOrigin;
import iso8583.enums.EIsoVersion;

class IsoMtiBuilderTest
{
	private IsoMTIBuilder mtiBuilder;
	
	@BeforeEach
	void setUp()
	{
		mtiBuilder = new IsoMTIBuilder();
	}
	
	@Test
	@DisplayName("MTI 0100")
	void testBuild_Success_Mti_0100() throws Exception
	{
		mtiBuilder.version(EIsoVersion.V_1987) //
				.messageClass(EIsoMessageClass.AUTHORIZATION) //
				.function(EIsoMessageFunction.REQUEST) //
				.origin(EIsoMessageOrigin.ACQUIRER);
		String mti = mtiBuilder.build();
		assertNotNull(mti, "El MTI no debería ser nulo");
		assertEquals("0100", mti, "El MTI generado debería ser 0100");
	}
	
	@Test
	@DisplayName("MTI 0110")
	void testBuild_Success_Mti_0110() throws Exception
	{
		mtiBuilder.version(EIsoVersion.V_1987) //
				.messageClass(EIsoMessageClass.AUTHORIZATION) //
				.function(EIsoMessageFunction.REQUEST_RESPONSE) //
				.origin(EIsoMessageOrigin.ACQUIRER);
		String mti = mtiBuilder.build();
		assertNotNull(mti, "El MTI no debería ser nulo");
		assertEquals("0110", mti, "El MTI generado debería ser 0110");
	}
	
	@Test
	@DisplayName("MTI 0111")
	void testBuild_Success_Mti_0111() throws Exception
	{
		mtiBuilder.version(EIsoVersion.V_1987) //
				.messageClass(EIsoMessageClass.AUTHORIZATION) //
				.function(EIsoMessageFunction.REQUEST_RESPONSE) //
				.origin(EIsoMessageOrigin.ACQUIRER_REPEAT);
		String mti = mtiBuilder.build();
		assertNotNull(mti, "El MTI no debería ser nulo");
		assertEquals("0111", mti, "El MTI generado debería ser 0111");
	}
	
	@Test
	@DisplayName("MTI 0210")
	void testBuild_Success_Mti_0210() throws Exception
	{
		// Ejecución
		mtiBuilder.version(EIsoVersion.V_1987) //
				.messageClass(EIsoMessageClass.FINANCIAL) //
				.function(EIsoMessageFunction.REQUEST_RESPONSE) //
				.origin(EIsoMessageOrigin.ACQUIRER);
		String mti = mtiBuilder.build();
		assertNotNull(mti, "El MTI no debería ser nulo");
		assertEquals("0210", mti, "El MTI construido debería ser exactamente '0210'");
	}
	
	@Test
	@DisplayName("MTI 1304")
	void testBuild_Success_Mti_0300() throws Exception
	{
		// Ejecución
		mtiBuilder.version(EIsoVersion.V_1993) //
				.messageClass(EIsoMessageClass.FILE_ACTIONS) //
				.function(EIsoMessageFunction.REQUEST) //
				.origin(EIsoMessageOrigin.OTHER);
		String mti = mtiBuilder.build();
		assertNotNull(mti, "El MTI no debería ser nulo");
		assertEquals("1304", mti, "El MTI construido debería ser exactamente '1304'");
	}
	
	@Test
	@DisplayName("MTI 0800")
	void testBuild_Success_Mti_0800() throws Exception
	{
		mtiBuilder.version(EIsoVersion.V_1987) //
				.messageClass(EIsoMessageClass.NETWORK_MANAGEMENT) //
				.function(EIsoMessageFunction.REQUEST) //
				.origin(EIsoMessageOrigin.ACQUIRER);
		String mti = mtiBuilder.build();
		assertEquals("0800", mti, "El MTI construido debería ser exactamente '0800'");
	}
	
	@Test
	@DisplayName("Test Exception Null")
	void testSetters_ThrowException_WhenNull()
	{
		Exception e1 = assertThrows(Exception.class, () -> mtiBuilder.version(null));
		assertEquals("ISO Version is null", e1.getMessage());
		Exception e2 = assertThrows(Exception.class, () -> mtiBuilder.messageClass(null));
		assertEquals("ISO MessageClass is null", e2.getMessage());
		Exception e3 = assertThrows(Exception.class, () -> mtiBuilder.function(null));
		assertEquals("ISO Function is null", e3.getMessage());
		Exception e4 = assertThrows(Exception.class, () -> mtiBuilder.origin(null));
		assertEquals("ISO Origin is null", e4.getMessage());
	}
	
	@Test
	@DisplayName("Test Exception WhenVersionIsMissing")
	void testBuild_ThrowsException_WhenVersionIsMissing() throws Exception
	{
		mtiBuilder.messageClass(EIsoMessageClass.FINANCIAL).function(EIsoMessageFunction.REQUEST_RESPONSE).origin(EIsoMessageOrigin.ACQUIRER);
		Exception e = assertThrows(Exception.class, () -> mtiBuilder.build());
		assertEquals("Faltan inicializar las siguientes propiedades del MTI: Version", e.getMessage());
	}
	
	@Test
	@DisplayName("Test Exception WhenMessageClassIsMissing")
	void testBuild_ThrowsException_WhenMessageClassIsMissing() throws Exception
	{
		mtiBuilder.version(EIsoVersion.V_1987).function(EIsoMessageFunction.REQUEST_RESPONSE).origin(EIsoMessageOrigin.ACQUIRER);
		Exception e = assertThrows(Exception.class, () -> mtiBuilder.build());
		assertEquals("Faltan inicializar las siguientes propiedades del MTI: MessageClass", e.getMessage());
	}
	
	@Test
	@DisplayName("Test Exception WhenFunctionIsMissing")
	void testBuild_ThrowsException_WhenFunctionIsMissing() throws Exception
	{
		mtiBuilder.version(EIsoVersion.V_1987).messageClass(EIsoMessageClass.FINANCIAL).origin(EIsoMessageOrigin.ACQUIRER);
		Exception e = assertThrows(Exception.class, () -> mtiBuilder.build());
		assertEquals("Faltan inicializar las siguientes propiedades del MTI: Function", e.getMessage());
	}
	
	@Test
	@DisplayName("Test Exception WhenOriginIsMissing")
	void testBuild_ThrowsException_WhenOriginIsMissing() throws Exception
	{
		mtiBuilder.version(EIsoVersion.V_1987).messageClass(EIsoMessageClass.FINANCIAL).function(EIsoMessageFunction.REQUEST_RESPONSE);
		Exception e = assertThrows(Exception.class, () -> mtiBuilder.build());
		assertEquals("Faltan inicializar las siguientes propiedades del MTI: Origin", e.getMessage());
	}
	
	@Test
	@DisplayName("Test Exception WhenNothingIsInitialized")
	void testSetters_ThrowException_WhenNothingIsInitialized()
	{
		Exception e1 = assertThrows(Exception.class, () -> mtiBuilder.build());
		assertEquals("Faltan inicializar las siguientes propiedades del MTI: Version, MessageClass, Function, Origin", e1.getMessage());
	}
}
