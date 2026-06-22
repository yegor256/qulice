/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import java.util.Map;
import java.util.Set;
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTAmbiguousName;
import net.sourceforge.pmd.lang.java.ast.ASTBlock;
import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTConstructorCall;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
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
     * The initialiser is a method call on some target, and a later statement
     * (before the local is read) calls a method on that same target. Inlining
     * would read the post-mutation value.
     * @param variable The variable declarator
     * @param use The single use of the variable
     * @return True if an intervening statement calls the initialiser's target
     */
    static boolean interveningCall(
        final ASTVariableDeclarator variable,
        final ASTVariableAccess use
    ) {
        boolean found = false;
        final ASTExpression init = variable.getInitializer();
        if (init instanceof ASTMethodCall call) {
            final String target = UnnecessaryLocalSkips.qualifierImage(
                call.getQualifier()
            );
            if (target != null && !target.isEmpty()) {
                final Node decl = UnnecessaryLocalSkips.blockLevel(variable);
                final Node consumer = UnnecessaryLocalSkips.blockLevel(use);
                found = UnnecessaryLocalSkips.sameBlock(decl, consumer)
                    && UnnecessaryLocalSkips.scanBetween(decl, consumer, target);
            }
        }
        return found;
    }

    private static boolean sameBlock(final Node first, final Node second) {
        return first != null && second != null
            && first.getParent() instanceof ASTBlock
            && first.getParent().equals(second.getParent());
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

    private static boolean scanBetween(
        final Node decl,
        final Node consumer,
        final String target
    ) {
        final Node block = decl.getParent();
        final int last = consumer.getIndexInParent();
        boolean found = false;
        for (int idx = decl.getIndexInParent() + 1; idx < last && !found;
            idx += 1) {
            found = block.getChild(idx).descendants(ASTMethodCall.class)
                .crossFindBoundaries()
                .toStream()
                .anyMatch(call -> UnnecessaryLocalSkips.callsTarget(call, target));
        }
        return found;
    }

    private static boolean callsTarget(
        final ASTMethodCall call,
        final String target
    ) {
        return target.equals(
            UnnecessaryLocalSkips.qualifierImage(call.getQualifier())
        );
    }

    private static Node blockLevel(final Node node) {
        Node current = node;
        while (current != null && !(current.getParent() instanceof ASTBlock)) {
            current = current.getParent();
        }
        return current;
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
