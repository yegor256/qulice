/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2025 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import com.puppycrawl.tools.checkstyle.utils.ScopeUtil;
import java.util.ArrayDeque;
import java.util.Deque;
import org.cactoos.text.Joined;
import org.cactoos.text.UncheckedText;

/**
 * Checks that classes are declared as final. Doesn't check for classes nested
 *  in interfaces or annotations, as they are always {@code final} there.
 * <p>
 * An example of how to configure the check is:
 * </p>
 * <pre>
 * &lt;module name="ProhibitNonFinalClassesCheck"/&gt;
 * </pre>
 *
 * @since 0.19
 */
public final class ProhibitNonFinalClassesCheck extends AbstractCheck {

    /**
     * Character separate package names in qualified name of java class.
     */
    private static final String PACKAGE_SEPARATOR = ".";

    /**
    * Keeps ClassDesc objects for stack of declared classes.
    */
    private Deque<ClassDesc> classes = new ArrayDeque<>();

    /**
    * Full qualified name of the package.
    */
    private String pack;

    @Override
    public int[] getDefaultTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getAcceptableTokens() {
        return this.getRequiredTokens();
    }

    @Override
    public int[] getRequiredTokens() {
        return new int[] {TokenTypes.CLASS_DEF};
    }

    @Override
    public void beginTree(final DetailAST root) {
        this.classes = new ArrayDeque<>();
        this.pack = "";
    }

    @Override
    public void visitToken(final DetailAST ast) {
        final DetailAST modifiers = ast.findFirstToken(TokenTypes.MODIFIERS);
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            final boolean isfinal =
                modifiers.findFirstToken(TokenTypes.FINAL) != null;
            final boolean isabstract =
                modifiers.findFirstToken(TokenTypes.ABSTRACT) != null;
            final String qualified = this.qualifiedClassName(ast);
            this.classes.push(
                new ClassDesc(qualified, isfinal, isabstract)
            );
        }
    }

    @Override
    public void leaveToken(final DetailAST ast) {
        if (ast.getType() == TokenTypes.CLASS_DEF) {
            final ClassDesc desc = this.classes.pop();
            if (!desc.isDeclaredAsAbstract()
                && !desc.isAsfinal()
                && !ScopeUtil.isInInterfaceOrAnnotationBlock(ast)) {
                final String qualified = desc.getQualified();
                final String name =
                    ProhibitNonFinalClassesCheck.getClassNameFromQualifiedName(
                        qualified
                    );
                log(ast.getLineNo(), "Classes should be final", name);
            }
        }
    }

    /**
     * Get qualified class name from given class Ast.
     * @param classast Class to get qualified class name
     * @return Qualified class name of a class
    */
    private String qualifiedClassName(final DetailAST classast) {
        final String name = classast.findFirstToken(
            TokenTypes.IDENT
        ).getText();
        String outer = null;
        if (!this.classes.isEmpty()) {
            outer = this.classes.peek().getQualified();
        }
        return ProhibitNonFinalClassesCheck.getQualifiedClassName(
            this.pack,
            outer,
            name
        );
    }

    /**
     * Calculate qualified class name(package + class name) laying inside given
     * outer class.
     * @param pack Package name, empty string on default package
     * @param outer Qualified name(package + class) of outer
     *  class, null if doesn't exist
     * @param name Class name
     * @return Qualified class name(package + class name)
    */
    private static String getQualifiedClassName(
        final String pack,
        final String outer,
        final String name) {
        final String qualified;
        if (outer == null) {
            if (pack.isEmpty()) {
                qualified = name;
            } else {
                qualified =
                    new UncheckedText(
                        new Joined(
                            ProhibitNonFinalClassesCheck.PACKAGE_SEPARATOR,
                            pack,
                            name
                        )
                    ).asString();
            }
        } else {
            qualified =
                new UncheckedText(
                    new Joined(
                        ProhibitNonFinalClassesCheck.PACKAGE_SEPARATOR,
                        outer,
                        name
                    )
                ).asString();
        }
        return qualified;
    }

    /**
     * Get class name from qualified name.
     * @param qualified Qualified class name
     * @return Class Name
     */
    private static String getClassNameFromQualifiedName(
        final String qualified
    ) {
        return qualified.substring(
            qualified.lastIndexOf(
                ProhibitNonFinalClassesCheck.PACKAGE_SEPARATOR
            ) + 1
        );
    }

    /**
     * Maintains information about class' ctors.
     *
     * @since 0.1
     */
    private static final class ClassDesc {

        /**
         * Qualified class name(with package).
        */
        private final String qualified;

        /**
         * Is class declared as final.
        */
        private final boolean asfinal;

        /**
         * Is class declared as abstract.
        */
        private final boolean asabstract;

        /**
         * Create a new ClassDesc instance.
         *
         * @param qualified Qualified class name(with package)
         * @param asfinal Indicates if the class declared as final
         * @param asabstract Indicates if the class declared as
         *  abstract
         */
        ClassDesc(final String qualified, final boolean asfinal,
            final boolean asabstract
        ) {
            this.qualified = qualified;
            this.asfinal = asfinal;
            this.asabstract = asabstract;
        }

        /**
         * Get qualified class name.
         * @return Qualified class name
         */
        private String getQualified() {
            return this.qualified;
        }

        /**
         * Is class declared as final.
         * @return True if class is declared as final
         */
        private boolean isAsfinal() {
            return this.asfinal;
        }

        /**
         * Is class declared as abstract.
         * @return True if class is declared as final
         */
        private boolean isDeclaredAsAbstract() {
            return this.asabstract;
        }
    }
}
