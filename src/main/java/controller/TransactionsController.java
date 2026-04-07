package controller;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import model.GatewayResponse;
import model.ReversalRequest;
import model.TransferRequest;
import service.TransactionGatewayService;

@RestController
@RequestMapping("/api/v1/transactions")
public class TransactionsController
{
	private final TransactionGatewayService serviceGateway;
	
	// Inversión de dependencias nativa de Spring (IoC)
	public TransactionsController(TransactionGatewayService serviceGateway)
	{
		this.serviceGateway = serviceGateway;
	}
	
	/**
	 * 3.1 - El endpoint principal de Autorización ISO-8583 (MTI 0200 -> 0210)
	 */
	@PostMapping("/transfer")
	public ResponseEntity<GatewayResponse> executeTransfer(@RequestBody TransferRequest requestBody)
	{
		GatewayResponse response = serviceGateway.processTransfer(requestBody);
		// Retornamos 400 Bad Request si la validación comercial en el validator explotó
		if( "30".equals(response.responseCode()) )
		{
			return ResponseEntity.badRequest().body(response);
		}
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 3.2 - Flujo para las famosas Anulaciones de Caja / Extornos (MTI 0400 -> 0410)
	 */
	@PostMapping("/reversal")
	public ResponseEntity<GatewayResponse> executeReversal(@RequestBody ReversalRequest requestBody)
	{
		GatewayResponse response = serviceGateway.processReversal(requestBody);
		return ResponseEntity.ok(response);
	}
	
	/**
	 * 3.3 - Endpoint de Echo Test (Network Management) para auditar estado activo de la base (MTI 0800)
	 */
	@GetMapping("/{traceNumber}/status")
	public ResponseEntity<GatewayResponse> consultStatus(@PathVariable("traceNumber") String traceNumber)
	{
		GatewayResponse response = serviceGateway.checkStatus(traceNumber);
		return ResponseEntity.ok(response);
	}
}
