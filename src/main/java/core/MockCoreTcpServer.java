package core;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

import iso8583.builder.ISO8583Builder;
import iso8583.enums.EIsoField;
import iso8583.parser.ISO8583Parser;
import model.IsoMessage;

public class MockCoreTcpServer
{
	private static final ISO8583Parser parser = new ISO8583Parser();
	private static final Random random = new Random();
	
	public static void main(String[] args)
	{
		int port = 9090;
		System.out.println("[MOCK CORE] Iniciando servidor en puerto " + port + "...");
		try (ServerSocket serverSocket = new ServerSocket(port))
		{
			while (true)
			{
				try (Socket clientSocket = serverSocket.accept();
						PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
						BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream())))
				{
					String inputLine = in.readLine();
					if( inputLine != null )
					{
						System.out.println("[MOCK CORE] Recibido: " + inputLine);
						String response = processMessage(inputLine);
						System.out.println("[MOCK CORE] Enviando: " + response);
						out.println(response);
					}
				}
				catch (Exception e)
				{
					System.err.println("[MOCK CORE] Error procesando conexión: " + e.getMessage());
				}
			}
		}
		catch (Exception e)
		{
			e.printStackTrace();
		}
	}
	
	private static String processMessage(String rawIso) throws Exception
	{
		IsoMessage request = parser.parseMessage(rawIso);
		String mti = request.getMti();
		Map<Integer, String> reqFields = request.getFields();
		Map<Integer, String> resFields = new HashMap<>(reqFields);
		String resMti = mti.substring(0, 2) + "1" + mti.substring(3);
		String rc = "00";
		if( "0200".equals(mti) )
		{
			long amount = Long.parseLong(reqFields.get(EIsoField.DE_4.getField()));
			String pan = reqFields.get(EIsoField.DE_2.getField());
			if( amount > 99999900 )
			{
				rc = "51";
			}
			else
			{
				char lastDigit = pan.charAt(pan.length() - 1);
				if( Character.getNumericValue(lastDigit) % 2 == 0 )
				{
					rc = "00";
				}
				else
				{
					rc = "05";
				}
			}
			resFields.put(EIsoField.DE_38.getField(), String.format("%06d", random.nextInt(1000000)));
		}
		else if( "0400".equals(mti) )
		{
			rc = "00";
		}
		resFields.put(EIsoField.DE_39.getField(), rc);
		resFields.put(EIsoField.DE_37.getField(), reqFields.getOrDefault(EIsoField.DE_37.getField(), "000000000000"));
		return new ISO8583Builder(resMti).buildMessage(resFields);
	}
}
