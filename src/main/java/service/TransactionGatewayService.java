package service;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Service;

import core.TcpCoreClient;
import iso8583.builder.ISO8583Builder;
import iso8583.enums.EIsoField;
import iso8583.parser.ISO8583Parser;
import model.GatewayResponse;
import model.IsoMessage;
import model.ReversalRequest;
import model.TransferRequest;
import model.ValidationResult;
import xml.ProductRuleValidator;

@Service
public class TransactionGatewayService
{
	private final ProductRuleValidator validator;
	private final TcpCoreClient tcpClient;
	private final ISO8583Parser parser = new ISO8583Parser();
	
	public TransactionGatewayService(ProductRuleValidator validator, TcpCoreClient tcpClient)
	{
		this.validator = validator;
		this.tcpClient = tcpClient;
	}
	
	public GatewayResponse processTransfer(TransferRequest request)
	{
		try
		{
			String procCode = getProcessingCode(request.productId());
			Map<Integer, String> fields = new HashMap<>();
			fields.put(EIsoField.DE_3.getField(), procCode);
			fields.put(EIsoField.DE_2.getField(), request.pan());
			fields.put(EIsoField.DE_4.getField(), String.valueOf(request.amount()));
			fields.put(EIsoField.DE_7.getField(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss")));
			fields.put(EIsoField.DE_11.getField(), request.traceNumber());
			fields.put(EIsoField.DE_41.getField(), request.terminalId());
			fields.put(EIsoField.DE_42.getField(), request.merchantId());
			fields.put(EIsoField.DE_49.getField(), request.currency());
			// 1. Gateway XML Validation (Reglas y Límite Diario Por Cuenta)
			IsoMessage tempMessage = new IsoMessage("0200", fields);
			ValidationResult validation = validator.validate(tempMessage);
			if( !validation.isValid() )
			{
				return GatewayResponse.fail("30", "Error en reglas comerciales o límites", validation.errors());
			}
			// 2. Transmisión ISO
			String isoStringMessage = new ISO8583Builder("0200").buildMessage(fields);
			String rawIsoResponse = tcpClient.sendIsoTransaction(isoStringMessage);
			// 3. Parsea respuesta de vuelta
			IsoMessage responseBank = parser.parseMessage(rawIsoResponse);
			String responseCode = responseBank.getFields().get(EIsoField.DE_39.getField());
			if( "00".equals(responseCode) )
			{
				// Notificar éxito al Límite Diario (accountDailyAccumulator)
				validator.commitTransaction(request.pan(), request.productId(), request.amount());
				return new GatewayResponse("00", "Aprobado", responseBank.getFields().get(EIsoField.DE_38.getField()), "000000000001", // Retornamos el Retrieval Reference constante de prueba
						request.amount(), request.currency(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")), request.traceNumber(), null);
			}
			else
			{
				return GatewayResponse.fail(responseCode, "Rechazado por el Core", List.of("Código Banco: " + responseCode));
			}
		}
		catch (Exception e)
		{
			return GatewayResponse.fail("99", "Error interno Gateway", List.of(e.getMessage()));
		}
	}
	
	public GatewayResponse processReversal(ReversalRequest request)
	{
		try
		{
			Map<Integer, String> fields = new HashMap<>();
			fields.put(EIsoField.DE_4.getField(), String.valueOf(request.amount()));
			fields.put(EIsoField.DE_7.getField(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss")));
			fields.put(EIsoField.DE_11.getField(), "99" + request.originalTraceNumber().substring(2)); // Nuevo Trace ID
			fields.put(EIsoField.DE_90.getField(), "0200" + request.originalTraceNumber() + "0000000000" + "00000000000" + "00000000000");
			String isoStringMessage = new ISO8583Builder("0400").buildMessage(fields);
			String rawIsoResponse = tcpClient.sendIsoTransaction(isoStringMessage);
			IsoMessage responseBank = parser.parseMessage(rawIsoResponse);
			String responseCode = responseBank.getFields().get(EIsoField.DE_39.getField());
			if( "00".equals(responseCode) )
			{
				return new GatewayResponse("00", "Anulación Banco exitosa", null, "000000001000", request.amount(), "604",
						LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd'T'HH:mm:ss'Z'")), request.originalTraceNumber(), null);
			}
			else
			{
				return GatewayResponse.fail(responseCode, "Anulación Inválida", List.of("La transacción original no se encontró en el Core."));
			}
		}
		catch (Exception e)
		{
			return GatewayResponse.fail("99", "Error generando Reverso ISO", List.of(e.getMessage()));
		}
	}
	
	public GatewayResponse checkStatus(String traceNumber)
	{
		try
		{
			Map<Integer, String> fields = new HashMap<>();
			fields.put(EIsoField.DE_7.getField(), LocalDateTime.now().format(DateTimeFormatter.ofPattern("MMddHHmmss")));
			fields.put(EIsoField.DE_11.getField(), traceNumber);
			fields.put(EIsoField.DE_70.getField(), "301"); // Echo
			String isoStringMessage = new ISO8583Builder("0800").buildMessage(fields);
			String rawIsoResponse = tcpClient.sendIsoTransaction(isoStringMessage);
			IsoMessage responseBank = parser.parseMessage(rawIsoResponse);
			String responseCode = responseBank.getFields().get(EIsoField.DE_39.getField());
			if( "00".equals(responseCode) )
			{
				return new GatewayResponse("00", "Transacción Vigente (Aprobada)", null, null, null, null, null, traceNumber, null);
			}
			else if( "25".equals(responseCode) )
			{
				return GatewayResponse.fail("25", "Transacción No Encontrada (O fue Extornada)", null);
			}
			else
			{
				return GatewayResponse.fail(responseCode, "Estado de transacción desconocido", null);
			}
		}
		catch (Exception e)
		{
			return GatewayResponse.fail("99", "Error comprobando estatus", List.of(e.getMessage()));
		}
	}
	
	private String getProcessingCode(String productId)
	{
		if( "TRANSF_INTRA".equals(productId) )
			return "400000";
		if( "TRANSF_INTER".equals(productId) )
			return "401000";
		if( "PAGO_SERVICIO".equals(productId) )
			return "180000";
		return "999999";
	}
}
