package model;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonInclude;

@JsonInclude(JsonInclude.Include.NON_NULL) 
public record GatewayResponse(
		String responseCode,
		String responseDescription,
		String approvalCode,
		String retrievalRef,
		Long amount,
		String currency,
		String timestamp,
		String isoMessageId,
		List<String> errors
	) 
	{
		// Si tienes un fallo de validación lanzas este JSON directo:
		public static GatewayResponse fail(String code, String msg, List<String> errorList) {
			return new GatewayResponse(code, msg, null, null, null, null, null, null, errorList);
		}
	}

