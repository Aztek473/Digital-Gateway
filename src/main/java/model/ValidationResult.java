package model;

import java.util.Collections;
import java.util.List;

public record ValidationResult(
    boolean isValid,
    List<String> errors
) {
    public static ValidationResult success() {
        return new ValidationResult(true, Collections.emptyList());
    }

    public static ValidationResult failure(List<String> errors) {
        return new ValidationResult(false, errors);
    }
}
