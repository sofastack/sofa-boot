package com.alipay.sofa.boot.compatibility;

import com.alipay.sofa.boot.compatibility.VerificationResult;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;

public class VerificationResultTests {

    @Test
    public void testEquals_SameDescriptionAndAction_ReturnsTrue() {
        VerificationResult result1 = VerificationResult.notCompatible("Error", "Take action");
        VerificationResult result2 = VerificationResult.notCompatible("Error", "Take action");
        assertThat(result1).isEqualTo(result2);
    }

    @Test
    public void testEquals_DifferentDescriptions_ReturnsFalse() {
        VerificationResult result1 = VerificationResult.notCompatible("Error 1", "Take action");
        VerificationResult result2 = VerificationResult.notCompatible("Error 2", "Take action");
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    public void testEquals_DifferentActions_ReturnsFalse() {
        VerificationResult result1 = VerificationResult.notCompatible("Error", "Take action 1");
        VerificationResult result2 = VerificationResult.notCompatible("Error", "Take action 2");
        assertThat(result1).isNotEqualTo(result2);
    }

    @Test
    public void testEquals_ComparingWithNull_ReturnsFalse() {
        VerificationResult result1 = VerificationResult.notCompatible("Error", "Take action");
        assertThat(result1).isNotEqualTo(null);
    }
}