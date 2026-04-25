/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTArgumentList;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.rule.AbstractJavaRulechainRule;
import net.sourceforge.pmd.lang.java.types.InvocationMatcher;

/**
 * Rule to flag {@code Arrays.asList} invocations that are passed a single
 * non-array argument. {@code Arrays.asList(x)} where {@code x} is a scalar
 * (e.g. an {@code int}, a {@code String} or any non-array reference) ends
 * up allocating a backing one-element array via varargs and a wrapper list
 * around it. {@code Collections.singletonList(x)} expresses the same intent
 * with a single, immutable, fixed-size list and no implicit array
 * allocation. A single array argument is not flagged because that form
 * either spreads the array's elements (for reference arrays) or wraps the
 * array itself in a one-element list (for primitive arrays), and either
 * behaviour may be intentional.
 * @since 0.26.0
 */
public final class UseCollectionsSingletonListRule
    extends AbstractJavaRulechainRule {

    /**
     * Matcher for any invocation of {@code java.util.Arrays.asList}.
     */
    private static final InvocationMatcher AS_LIST =
        InvocationMatcher.parse("java.util.Arrays#asList(_*)");

    public UseCollectionsSingletonListRule() {
        super(ASTMethodCall.class);
    }

    @Override
    public Object visit(final ASTMethodCall call, final Object data) {
        if (UseCollectionsSingletonListRule.shouldUseSingletonList(call)) {
            this.asCtx(data).addViolation(call);
        }
        return data;
    }

    private static boolean shouldUseSingletonList(final ASTMethodCall call) {
        boolean result = false;
        if (UseCollectionsSingletonListRule.AS_LIST.matchesCall(call)) {
            final ASTArgumentList args = call.getArguments();
            result = args.size() == 1
                && !args.get(0).getTypeMirror().isArray();
        }
        return result;
    }
}
