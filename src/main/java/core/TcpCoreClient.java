package core;

import iso8583.builder.ISO8583Builder;
import iso8583.enums.EIsoField;
import iso8583.parser.ISO8583Parser;
import model.IsoMessage;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Service
public class TcpCoreClient {

    private final ISO8583Parser parser = new ISO8583Parser();
    private final Map<String, IsoMessage> as400Database = new ConcurrentHashMap<>();

	public String sendIsoTransaction(String rawIsoMessage) {
        System.out.println("\n[TCP OUT ->] Enviando trama al Core Legacy: " + rawIsoMessage);
        try { Thread.sleep(600); } catch (InterruptedException e) {}

        try {
            IsoMessage requestMessage = parser.parseMessage(rawIsoMessage);
            String mti = requestMessage.getMti();
            Map<Integer, String> requestFields = requestMessage.getFields();
            
            Map<Integer, String> responseFields = new HashMap<>(requestFields);
            String responseMti;

            if ("0200".equals(mti)) {
                responseMti = "0210";
                
                String trace = requestFields.get(EIsoField.DE_11.getField());
                as400Database.put(trace, requestMessage);

                responseFields.put(EIsoField.DE_38.getField(), "123456"); 
                responseFields.put(EIsoField.DE_39.getField(), "00"); 

                if(requestFields.containsKey(EIsoField.DE_37.getField())) {
                   responseFields.put(EIsoField.DE_37.getField(), requestFields.get(EIsoField.DE_37.getField()));
                }
                
            } else if ("0400".equals(mti)) {
                responseMti = "0410";
                
                if(requestFields.containsKey(EIsoField.DE_37.getField())) {
                   responseFields.put(EIsoField.DE_37.getField(), requestFields.get(EIsoField.DE_37.getField()));
                }

                String traceToRevertStr = requestFields.get(EIsoField.DE_90.getField()); 
                String origTrace = traceToRevertStr.substring(4, 10); 
                
                if (as400Database.containsKey(origTrace)) {
                    as400Database.remove(origTrace); 
                    responseFields.put(EIsoField.DE_39.getField(), "00"); 
                } else {
                    responseFields.put(EIsoField.DE_39.getField(), "12"); 
                }
                
            } else if ("0800".equals(mti)) {
                responseMti = "0810";
                
                // La tarea pide usar el 0800 con el STAN para averiguar el ESTADO de esa transacción
                String traceToFind = requestFields.get(EIsoField.DE_11.getField());
                if (as400Database.containsKey(traceToFind)) {
                    responseFields.put(EIsoField.DE_39.getField(), "00"); // 00 = Aprobada y existe
                } else {
                    responseFields.put(EIsoField.DE_39.getField(), "25"); // 25 = Transaction Not Found (Unable to locate)
                }
            } else {
                responseMti = "9999";
                responseFields.put(EIsoField.DE_39.getField(), "30"); 
            }

            ISO8583Builder responseBuilder = new ISO8583Builder(responseMti);
            String responseString = responseBuilder.buildMessage(responseFields);
            
            System.out.println("[TCP IN <-]  Respuesta del Core (Mock): " + responseString + "\n");
            return responseString;

        } catch (Exception e) {
            e.printStackTrace();
            return "99990000000000000000"; 
        }
    }
}
