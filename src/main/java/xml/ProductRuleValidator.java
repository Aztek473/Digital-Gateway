package xml;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.fasterxml.jackson.dataformat.xml.XmlMapper;

import iso8583.enums.EIsoField;
import jakarta.annotation.PostConstruct;
import model.IsoMessage;
import model.ProductRuleDTO;
import model.ProductRulesConfig;
import model.ValidationResult;

@Service
public class ProductRuleValidator
{
	@Value("classpath:product-rules.xml")
	private Resource rulesFile;
	private final Map<String, ProductRuleDTO> ruleCache = new HashMap<>();
	private final Map<String, Long> accountDailyAccumulator = new ConcurrentHashMap<>();
	
	@PostConstruct
	public void init()
	{
		try
		{
			XmlMapper xmlMapper = new XmlMapper();
			// LECTURA DE XML Y LLENADO DE DTOs
			ProductRulesConfig root = xmlMapper.readValue(rulesFile.getInputStream(), ProductRulesConfig.class);
			for( ProductRuleDTO rule : root.products() )
			{
				if( rule.requiredFields() != null )
				{
					rule.requiredFields().fields().forEach(req -> {
						if( EIsoField.fromFieldNumber(req.de()) == null )
						{
							throw new IllegalArgumentException("Error: En el producto " + rule.id() + " el fiel DE_" + req.de() + " no existe en el estándar EIsoField.");
						}
					});
				}
				ruleCache.put(rule.processingCode(), rule);
			}
			System.out.println(ruleCache.size() + " reglas cargadas en caché.");
		}
		catch (Exception e)
		{
			e.printStackTrace();
			throw new RuntimeException("Error cargando product-rules.xml", e);
		}
	}
	
	public ValidationResult validate(IsoMessage message)
	{
		List<String> errors = new ArrayList<>();
		Map<Integer, String> fields = message.getFields();
		// 1. Validar si Processing Code esta presente
		String processingCode = fields.get(EIsoField.DE_3.getField());
		if( processingCode == null )
		{
			errors.add("El Processing Code (DE_3) es necesario.");
			return ValidationResult.failure(errors);
		}
		// 2. Obtengo el producto
		ProductRuleDTO productRuleDTO = ruleCache.get(processingCode);
		if( productRuleDTO == null )
		{
			errors.add("Processing Code '" + processingCode + "' no se encontro entre los productos.");
			return ValidationResult.failure(errors);
		}
		// 3. Validar Monto y Límites (DE_4)
		String amountStr = fields.get(EIsoField.DE_4.getField());
		if( amountStr != null && productRuleDTO.limits() != null )
		{
			try
			{
				long amount = Long.parseLong(amountStr);
				if( amount < productRuleDTO.limits().minAmount() )
				{
					errors.add("El monto " + amount + " es menor al mínimo permitido " + productRuleDTO.limits().minAmount() + ".");
				}
				if( amount > productRuleDTO.limits().maxAmount() )
				{
					errors.add("El monto " + amount + " excede al máximo permitido " + productRuleDTO.limits().maxAmount() + ".");
				}
				String pan = fields.get(EIsoField.DE_2.getField());
				// VALIDACIÓN DE LÍMITE DIARIO POR PAN
				if( pan != null )
				{
					long currentDaily = accountDailyAccumulator.getOrDefault(pan + "_" + productRuleDTO.id(), 0L);
					if( currentDaily + amount > productRuleDTO.limits().dailyLimit() )
					{
						errors.add("Monto supera el límite diario del producto " + productRuleDTO.id() + " para la cuenta enviada.");
					}
				}
			}
			catch (NumberFormatException e)
			{
				errors.add("El formato numérico del Amount (DE_4) es inválido.");
			}
		}
		// 4. Validar Campos Obligatorios (Required Fields)
		if( productRuleDTO.requiredFields() != null && productRuleDTO.requiredFields().fields() != null )
		{
			productRuleDTO.requiredFields().fields().forEach(reqField -> {
				if( !fields.containsKey(reqField.de()) )
				{
					errors.add("Campo obligatorio " + reqField.name() + " (DE_" + reqField.de() + ") para el producto " + productRuleDTO.id());
				}
			});
		}
		// 5. Validar Horario de Disponibilidad
		if( productRuleDTO.schedule() != null )
		{
			validateSchedule(productRuleDTO, errors);
		}
		if( errors.isEmpty() )
		{
			return ValidationResult.success();
		}
		return ValidationResult.failure(errors);
	}
	
	public void commitTransaction(String pan, String productId, long amount)
	{
		if( pan == null || productId == null )
		{
			return;
		}
		String key = pan + "_" + productId;
		long updatedDaily = accountDailyAccumulator.getOrDefault(key, 0L) + amount;
		accountDailyAccumulator.put(key, updatedDaily);
	}
	
	public void reverseTransaction(String pan, String productId, long amount)
	{
		if( pan == null || productId == null )
		{
			return;
		}
		String key = pan + "_" + productId;
		long currentDaily = accountDailyAccumulator.getOrDefault(key, 0L);
		long restoredDaily = Math.max(0, currentDaily - amount);
		accountDailyAccumulator.put(key, restoredDaily);
	}
	
	private void validateSchedule(ProductRuleDTO productRuleDTO, List<String> errors)
	{
		String dayEn = LocalDate.now().getDayOfWeek().name().substring(0, 3).toUpperCase();
		// Validar Días
		String allowedDays = productRuleDTO.schedule().availableDays();
		if( allowedDays != null && !allowedDays.contains(dayEn) )
		{
			errors.add("El producto " + productRuleDTO.id() + " no está habilitado en este día.");
		}
		// Validar Horario (Range bounds)
		try
		{
			DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm");
			LocalTime start = LocalTime.parse(productRuleDTO.schedule().availableFrom(), formatter);
			LocalTime end = LocalTime.parse(productRuleDTO.schedule().availableTo(), formatter);
			LocalTime nowTime = LocalTime.now();
			if( nowTime.isBefore(start) || nowTime.isAfter(end) )
			{
				errors.add("Fuera del horario permitido.");
			}
		}
		catch (Exception e)
		{
			errors.add("Horario del producto invalido.");
		}
	}
	
	// Método auxiliar para pruebas unitarias
	public Map<String, ProductRuleDTO> getRuleCache()
	{
		return ruleCache;
	}
}