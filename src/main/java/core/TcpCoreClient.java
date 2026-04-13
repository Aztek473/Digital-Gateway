package core;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import core.ledger.exception.CoreTimeoutException;
import iso8583.builder.ISO8583Builder;
import iso8583.enums.EIsoField;
import iso8583.parser.ISO8583Parser;
import model.IsoMessage;

@Service
public class TcpCoreClient
{
	private final ISO8583Parser parser = new ISO8583Parser();
	private final Map<String, IsoMessage> as400Database = new ConcurrentHashMap<>();
	// Configuración desde application.properties
	@Value("${core.tcp.enabled:false}")
	private boolean tcpEnabled;
	@Value("${core.tcp.host:127.0.0.1}")
	private String host;
	@Value("${core.tcp.port:9090}")
	private int port;
	@Value("${core.tcp.connect-timeout:5000}")
	private int connectTimeout;
	@Value("${core.tcp.read-timeout:10000}")
	private int readTimeout;
	
	public String sendIsoTransaction(String rawIsoMessage)
	{
		String maskedMessage = maskPan(rawIsoMessage);
		// MODO 1: TCP REAL (Si está habilitado en properties)
		if( tcpEnabled )
		{
			System.out.println("[GATEWAY -> CORE TCP] Enviando: " + maskedMessage);
			try (Socket socket = new Socket())
			{
				socket.connect(new InetSocketAddress(host, port), connectTimeout);
				socket.setSoTimeout(readTimeout);
				try (PrintWriter out = new PrintWriter(socket.getOutputStream(), true); BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream())))
				{
					out.println(rawIsoMessage);
					String response = in.readLine();
					if( response == null )
						throw new CoreTimeoutException("Respuesta vacía del Core");
					System.out.println("[CORE TCP -> GATEWAY] Recibido: " + maskPan(response));
					return response;
				}
			}
			catch (SocketTimeoutException e)
			{
				throw new CoreTimeoutException("Timeout de red con el Core");
			}
			catch (Exception e)
			{
				throw new RuntimeException("Error de comunicación TCP: " + e.getMessage());
			}
		}
		// MODO 2: SIMULADOR INTERNO (Para pruebas rápidas sin levantar el Server)
		System.out.println("[GATEWAY -> CORE MOCK INTERNO] Procesando: " + maskedMessage);
		try
		{
			Thread.sleep(300);
		}
		catch (InterruptedException e)
		{
		}
		try
		{
			IsoMessage requestMessage = parser.parseMessage(rawIsoMessage);
			String mti = requestMessage.getMti();
			Map<Integer, String> requestFields = requestMessage.getFields();
			Map<Integer, String> responseFields = new HashMap<>(requestFields);
			String responseMti;
			if( "0200".equals(mti) )
			{
				responseMti = "0210";
				as400Database.put(requestFields.get(EIsoField.DE_11.getField()), requestMessage);
				responseFields.put(EIsoField.DE_38.getField(), "123456");
				responseFields.put(EIsoField.DE_39.getField(), "00");
			}
			else if( "0400".equals(mti) )
			{
				responseMti = "0410";
				String origTrace = requestFields.get(EIsoField.DE_90.getField()).substring(4, 10);
				if( as400Database.containsKey(origTrace) )
				{
					as400Database.remove(origTrace);
					responseFields.put(EIsoField.DE_39.getField(), "00");
				}
				else
				{
					responseFields.put(EIsoField.DE_39.getField(), "12");
				}
			}
			else if( "0800".equals(mti) )
			{
				responseMti = "0810";
				String traceToFind = requestFields.get(EIsoField.DE_11.getField());
				responseFields.put(EIsoField.DE_39.getField(), as400Database.containsKey(traceToFind) ? "00" : "25");
			}
			else
			{
				responseMti = "9999";
				responseFields.put(EIsoField.DE_39.getField(), "30");
			}
			if( requestFields.containsKey(EIsoField.DE_37.getField()) )
			{
				responseFields.put(EIsoField.DE_37.getField(), requestFields.get(EIsoField.DE_37.getField()));
			}
			String responseString = new ISO8583Builder(responseMti).buildMessage(responseFields);
			System.out.println("[CORE MOCK INTERNO -> GATEWAY] Respuesta: " + maskPan(responseString));
			return responseString;
		}
		catch (Exception e)
		{
			return "99990000000030000000";
		}
	}
	
	private String maskPan(String rawIso)
	{
		// Enmascara dígitos del 7 al 12 (ej: 411111XXXXXX1111)
		return rawIso.replaceAll("(\\d{6})\\d{6}(\\d{4,})", "$1XXXXXX$2");
	}
}
