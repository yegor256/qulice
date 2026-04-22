/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.util.regex.Pattern;

/**
 * Checks that enum constant names conform to the same naming convention
 * as {@code static final} fields (i.e., the default Checkstyle
 * {@code ConstantName} pattern {@code ^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$}).
 *
 * <p>Since enum constants are effectively {@code public static final}
 * references, they must follow the same upper-case, underscore-separated
 * naming convention. Names like {@code anyName} or {@code MixedCase} are
 * forbidden.
 *
 * @since 0.24
 */
public final class EnumValueNameCheck extends AbstractCheck {

    /**
     * Required pattern for enum constant names.
     */
    private static final Pattern FORMAT = Pattern.compile(
        "^[A-Z][A-Z0-9]*(_[A-Z0-9]+)*$"
    );

    @Override
    public int[] getDefaultTokens() {
        return new int[] {TokenTypes.ENUM_CONSTANT_DEF};
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return this.getDefaultTokens();
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final DetailAST ident = ast.findFirstToken(TokenTypes.IDENT);
        final String name = ident.getText();
        if (!EnumValueNameCheck.FORMAT.matcher(name).matches()) {
            this.log(
                ident.getLineNo(),
                ident.getColumnNo(),
                String.format(
                    "Enum value %s must match pattern %s",
                    name,
                    EnumValueNameCheck.FORMAT.pattern()
                )
            );
        }
    }
}
