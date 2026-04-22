/*
 * Test file.
 */
package com.qulice.plugin.violations;

/**
 * Indentation checks.
 */
public final class IndentationChecks {

    /**
     * This should not produce any violations.
     */
    public void triggersNoViolation() {
        new String()
            .substring(
                0, 2
            )
            .substring(
                0, 1
            );
    }

}
