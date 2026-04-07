package model;

import java.util.List;

public record ValidationResult(
    boolean isValid,
    List<String> errors
) {
    public static ValidationResult success() {
        return new ValidationResult(true, java.util.Collections.emptyList());
    }

    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }
}
