package com.alipay.sofa.boot.compatibility;

import org.springframework.core.style.ToStringCreator;

import java.util.Objects;

/**
 * @author huzijie
 * @version VerificationResult.java, v 0.1 2023年08月03日 4:08 PM huzijie Exp $
 */
public class VerificationResult {

    private final boolean compatible;

    private final String description;

    private final String action;

    // if OK
    private VerificationResult() {
        this.compatible = true;
        this.description = "";
        this.action = "";
    }

    // if not OK
    private VerificationResult(String errorDescription, String action) {
        this.compatible = false;
        this.description = errorDescription;
        this.action = action;
    }

    public static VerificationResult compatible() {
        return new VerificationResult();
    }

    public static VerificationResult notCompatible(String errorDescription, String action) {
        return new VerificationResult(errorDescription, action);
    }

    public boolean isNotCompatible() {
        return !compatible;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof VerificationResult)) {
            return false;
        }
        VerificationResult that = (VerificationResult) o;
        return compatible == that.compatible && description.equals(that.description) && action.equals(that.action);
    }

    @Override
    public int hashCode() {
        return Objects.hash(compatible, description, action);
    }

    @Override
    public String toString() {
        ToStringCreator toStringCreator = new ToStringCreator(this);
        toStringCreator.append("compatible", compatible);
        toStringCreator.append("description", description);
        toStringCreator.append("action", action);
        return toStringCreator.toString();
    }

}

