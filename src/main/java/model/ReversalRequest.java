package model;

// POST /api/v1/transactions/reversal
public record ReversalRequest(
		String originalTraceNumber,
		long amount
		)
{
}
