/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.pmd.rules;

import net.sourceforge.pmd.lang.java.ast.ASTClassType;
import net.sourceforge.pmd.lang.java.ast.ASTCompilationUnit;
import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;

/**
 * A supertype recognised by the name it is given in an {@code extends} or
 * an {@code implements} clause. Qulice runs PMD without an auxiliary
 * classpath, so a supertype that lives outside the file being analyzed
 * never resolves to a type and only the name written in the clause is
 * available. That name is trusted when it is fully qualified, or when the
 * file imports the type, either directly or on demand from its package.
 * @since 1.0
 */
final class NamedSupertype {

    /**
     * Canonical name of the type, e.g. {@code com.sun.jna.Library}.
     */
    private final String canonical;

    /**
     * Constructor.
     * @param name Canonical name of the type
     */
    NamedSupertype(final String name) {
        this.canonical = name;
    }

    /**
     * Does the given clause name this very type?
     * @param clause Type named in an extends or implements clause,
     *  may be NULL when the clause is absent
     * @return TRUE if the clause names this type
     */
    boolean matches(final ASTClassType clause) {
        return clause != null
            && this.simple().equals(clause.getSimpleName())
            && (this.pack().equals(clause.getPackageQualifier())
                || this.imported(clause.getRoot()));
    }

    /**
     * Simple name of the type.
     * @return Name without the package
     */
    private String simple() {
        return this.canonical.substring(this.canonical.lastIndexOf('.') + 1);
    }

    /**
     * Package of the type.
     * @return Name without the simple name
     */
    private String pack() {
        return this.canonical.substring(0, this.canonical.lastIndexOf('.'));
    }

    /**
     * Does the given file import this type?
     * @param unit File being analyzed
     * @return TRUE if the type is imported, directly or on demand
     */
    private boolean imported(final ASTCompilationUnit unit) {
        return unit.children(ASTImportDeclaration.class).any(this::brings);
    }

    /**
     * Does the given import bring this type in?
     * @param imported Import declaration
     * @return TRUE if the import names this type or its package
     */
    private boolean brings(final ASTImportDeclaration imported) {
        return this.canonical.equals(imported.getImportedName())
            || imported.isImportOnDemand()
            && this.pack().equals(imported.getImportedName());
    }
}
