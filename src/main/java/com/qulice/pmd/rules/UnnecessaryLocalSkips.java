/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.Map;
import java.util.Set;
import java.util.stream.IntStream;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTFieldAccess;
import net.sourceforge.pmd.lang.java.ast.ASTMethodCall;
import net.sourceforge.pmd.lang.java.ast.ASTTypeExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableAccess;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclarator;

/**
 * Heuristics that veto an {@link UnnecessaryLocalRule} report when the local
 * is semantically required (closure capture, clock snapshot, or capture
 * before a destructive call). See issue #1607.
 * @since 0.27.7
 */
final class UnnecessaryLocalSkips {

    /**
     * Simple class names from {@code java.time} whose {@code now()} factory
     * returns a fresh snapshot of the system clock.
     */
    private static final Set<String> TIME_TYPES = Set.of(
        "Instant", "LocalDate", "LocalDateTime", "LocalTime",
        "ZonedDateTime", "OffsetDateTime", "OffsetTime",
        "Year", "YearMonth", "MonthDay", "Clock"
    );

    /**
     * Other (non-{@code now()}) calls that return fresh state on each
     * invocation, keyed by the qualifier's simple name.
     */
    private static final Map<String, Set<String>> FRESH_STATE_CALLS = Map.of(
        "System", Set.of("currentTimeMillis", "nanoTime"),
        "UUID", Set.of("randomUUID"),
        "Math", Set.of("random")
    );

    private UnnecessaryLocalSkips() {
    }

    /**
     * The single use of the local is reachable only by crossing a lambda or
     * anonymous-class boundary, so the local exists to carry the value into
     * a different exception scope.
     * @param block The block enclosing the declaration
     * @param name The variable name
     * @param crossings Number of usages found when crossing find boundaries
     * @return True when at least one usage sits behind a find boundary
     */
    static boolean acrossBoundary(
        final ASTBlock block,
        final String name,
        final int crossings
    ) {
        return crossings != block
            .descendants(ASTVariableAccess.class)
            .filter(ref -> name.equals(ref.getName()))
            .count();
    }

    /**
     * The initialiser snapshots mutable global state - a clock, a randomness
     * source, or {@code new Date()} - so inlining would change <em>when</em>
     * the value is taken.
     * @param init The initialiser expression
     * @return True if the initialiser captures fresh state
     */
    static boolean freshState(final ASTExpression init) {
        final boolean result;
        if (init instanceof ASTMethodCall call) {
            result = UnnecessaryLocalSkips.freshStateCall(call);
        } else if (init instanceof ASTConstructorCall call) {
            final ASTClassType type = call.getTypeNode();
            result = type != null && "Date".equals(type.getSimpleName());
        } else {
            result = false;
        }
        return result;
    }

    /**
     * A statement intervenes between the local's declaration and its single
     * use, so the local is pinning evaluation order and must stay. When the
     * initialiser is a method call or a constructor call, any intervening
     * statement is enough - the call's result cannot be read out of order
     * without reordering side effects. When the initialiser is a field access
     * (e.g. {@code System.out}), only an intervening call on the <em>same</em>
     * qualifier (e.g. {@code System.setOut(...)}) counts, since such a call may
     * reassign the field before it is read; an unrelated statement leaves the
     * read inlinable. See issues #1607, #1699, #1700 and #1710.
     * @param variable The variable declarator
     * @param use The single use of the variable
     * @return True if a statement intervenes between init and its use
     */
    static boolean interveningCall(
        final ASTVariableDeclarator variable,
        final ASTVariableAccess use
    ) {
        final ASTExpression init = variable.getInitializer();
        final boolean found;
        if (init instanceof ASTMethodCall
            || init instanceof ASTConstructorCall) {
            found = UnnecessaryLocalSkips.intervenes(variable, use);
        } else if (init instanceof ASTFieldAccess access) {
            found = UnnecessaryLocalSkips.callsQualifier(variable, use, access);
        } else {
            found = false;
        }
        return found;
    }

    /**
     * A statement sits between the declaration and its use inside the block
     * that encloses the declaration. The use is measured through its nearest
     * ancestor statement in that common block, so a use buried in an {@code
     * if} or loop body (whose immediate block differs from the declaration's)
     * is still compared against the intervening statements rather than being
     * treated as adjacent. See issue #1700.
     * @param variable The variable declarator
     * @param use The single use of the variable
     * @return True if a statement intervenes between init and its use
     */
    private static boolean intervenes(
        final ASTVariableDeclarator variable,
        final ASTVariableAccess use
    ) {
        boolean found = false;
        final ASTBlock block = variable.ancestors(ASTBlock.class).first();
        if (block != null) {
            final Node decl = UnnecessaryLocalSkips.childOf(block, variable);
            final Node consumer = UnnecessaryLocalSkips.childOf(block, use);
            found = decl != null && consumer != null
                && consumer.getIndexInParent() > decl.getIndexInParent() + 1;
        }
        return found;
    }

    private static boolean callsQualifier(
        final ASTVariableDeclarator variable,
        final ASTVariableAccess use,
        final ASTFieldAccess access
    ) {
        boolean found = false;
        final ASTBlock block = variable.ancestors(ASTBlock.class).first();
        final String qualifier = UnnecessaryLocalSkips.qualifierImage(
            access.getQualifier()
        );
        if (block != null && qualifier != null) {
            final Node decl = UnnecessaryLocalSkips.childOf(block, variable);
            final Node consumer = UnnecessaryLocalSkips.childOf(block, use);
            if (decl != null && consumer != null) {
                found = IntStream.range(
                    decl.getIndexInParent() + 1, consumer.getIndexInParent()
                ).anyMatch(
                    idx -> UnnecessaryLocalSkips.calls(
                        block.getChild(idx), qualifier
                    )
                );
            }
        }
        return found;
    }

    private static boolean calls(final Node stmt, final String qualifier) {
        return stmt.descendants(ASTMethodCall.class).toStream().anyMatch(
            call -> qualifier.equals(
                UnnecessaryLocalSkips.qualifierImage(call.getQualifier())
            )
        );
    }

    private static Node childOf(final ASTBlock block, final Node inner) {
        Node current = inner;
        while (current != null && !block.equals(current.getParent())) {
            current = current.getParent();
        }
        return current;
    }

    private static boolean freshStateCall(final ASTMethodCall call) {
        final String name = call.getMethodName();
        final String qualifier = UnnecessaryLocalSkips.qualifierImage(
            call.getQualifier()
        );
        boolean fresh = false;
        if (qualifier != null) {
            final boolean known = UnnecessaryLocalSkips.FRESH_STATE_CALLS
                .getOrDefault(qualifier, Set.of()).contains(name);
            final boolean clock = "now".equals(name)
                && UnnecessaryLocalSkips.TIME_TYPES.contains(qualifier);
            fresh = known || clock;
        }
        return fresh;
    }

    private static String qualifierImage(final ASTExpression expr) {
        final String result;
        if (expr instanceof ASTAmbiguousName name) {
            result = name.getName();
        } else if (expr instanceof ASTVariableAccess access) {
            result = access.getName();
        } else if (expr instanceof ASTTypeExpression type
            && type.getTypeNode() instanceof ASTClassType klass) {
            result = klass.getSimpleName();
        } else {
            result = null;
        }
        return result;
    }
}
