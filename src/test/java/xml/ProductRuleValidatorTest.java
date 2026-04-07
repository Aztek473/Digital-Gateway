package xml;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.util.HashMap;
import java.util.Map;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.test.util.ReflectionTestUtils;

import model.IsoMessage;
import model.ValidationResult;

class ProductRuleValidatorTest
{
	private ProductRuleValidator validator;
	
	@BeforeEach
	void setUp()
	{
		validator = new ProductRuleValidator();
		// 1. INYECTAMOS UN XML SIMULADO DE PRUEBA (MOCK HARDCODEADO)
		String mockXml = """
				<?xml version="1.0" encoding="UTF-8"?>
				<productRules>
				  <product id="TRANSF_MOCK">
				    <processingCode>200000</processingCode>
				    <limits currency="PEN"><minAmount>100</minAmount><maxAmount>5000000</maxAmount></limits>
				    <requiredFields><field de="2" name="PAN"/><field de="4" name="amount"/></requiredFields>
				    <schedule><availableFrom>00:00</availableFrom><availableTo>23:59</availableTo><availableDays>MON TUE WED THU FRI SAT SUN</availableDays></schedule>
				  </product>
				</productRules>
				""";
		// Inyectamos el Mock en crudo sin depender de archivos del disco duro
		ReflectionTestUtils.setField(validator, "rulesFile", new ByteArrayResource(mockXml.getBytes()));
		validator.init();
	}
	
	@Test
	@DisplayName("2.2.1 - Validar carga de Cache XML correcta")
	void testInit_xmlCargadoCorrectamente()
	{
		assertFalse(validator.getRuleCache().isEmpty(), "El caché no debe estar vacío.");
		assertTrue(validator.getRuleCache().containsKey("200000"), "Debe haber cargado el código '200000' del XML duro.");
	}
	
	@Test
	@DisplayName("2.2.2 - Validar fallo por campo DE_3 ausente")
	void testValidate_FallaSinProcessingCode()
	{
		Map<Integer, String> fields = new HashMap<>();
		IsoMessage message = new IsoMessage("0200", fields);
		ValidationResult result = validator.validate(message);
		assertFalse(result.isValid(), "Debe fallar al no ubicar DE_3");
		assertTrue(result.errors().get(0).contains("El Processing Code (DE_3) es necesario."), "Debe apuntar a que falta el Proc Code");
	}
	
	@Test
	@DisplayName("2.2.3 - Validar fallo por límite inferior de Monto")
	void testValidate_FallaPorMontoInferior()
	{
		Map<Integer, String> fields = new HashMap<>();
		fields.put(3, "200000"); // Código del XML duro
		fields.put(2, "411111111111");
		fields.put(4, "50"); // 50 céntimos (exigía mínimo 100)
		IsoMessage message = new IsoMessage("0200", fields);
		ValidationResult result = validator.validate(message);
		assertFalse(result.isValid());
		assertTrue(result.errors().stream().anyMatch(e -> e.contains("es menor al mínimo permitido")));
	}
	
	@Test
	@DisplayName("2.2.4 - Validar fallo al faltar campo obligatorio exigido en XML")
	void testValidate_FallaPorFaltaDeCampoObligatorio()
	{
		Map<Integer, String> fields = new HashMap<>();
		fields.put(3, "200000"); // Código del XML duro
		fields.put(4, "5000");
		// Falta intencionalmente el DE_2 (PAN) para forzar falla de Required Fields
		IsoMessage message = new IsoMessage("0200", fields);
		ValidationResult result = validator.validate(message);
		assertFalse(result.isValid());
		assertTrue(result.errors().stream().anyMatch(e -> e.contains("Campo obligatorio PAN (DE_2)")));
	}
	
	@Test
	@DisplayName("2.2.5 - Transacción Valida Flujo Perfecto")
	void testValidate_TransaccionExitosa()
	{
		Map<Integer, String> fields = new HashMap<>();
		fields.put(3, "200000"); // Código correcto
		fields.put(2, "4111111111111111"); // PAN presente y válido
		fields.put(4, "1500"); // Monto intermedio perfecto(>100 y <5000000)
		IsoMessage message = new IsoMessage("0200", fields);
		ValidationResult result = validator.validate(message);
		assertTrue(result.isValid(), "No debió emitir fallas, la trama encaja perfecto en las reglas DTO hardcodeadas.");
	}
}
