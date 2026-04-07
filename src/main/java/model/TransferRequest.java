package model;

// POST /api/v1/transactions/transfer
public record TransferRequest(
		String productId,
		String pan,
		long amount,
		String currency,
		String terminalId,
		String merchantId,
		String traceNumber
) {}