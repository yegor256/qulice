/**
 * Copyright (c) 2011-2018, Qulice.com
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions
 * are met: 1) Redistributions of source code must retain the above
 * copyright notice, this list of conditions and the following
 * disclaimer. 2) Redistributions in binary form must reproduce the above
 * copyright notice, this list of conditions and the following
 * disclaimer in the documentation and/or other materials provided
 * with the distribution. 3) Neither the name of the Qulice.com nor
 * the names of its contributors may be used to endorse or promote
 * products derived from this software without specific prior written
 * permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS
 * "AS IS" AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT
 * NOT LIMITED TO, THE IMPLIED WARRANTIES OF MERCHANTABILITY AND
 * FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO EVENT SHALL
 * THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR
 * SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION)
 * HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT,
 * STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED
 * OF THE POSSIBILITY OF SUCH DAMAGE.
 */
package com.qulice.pmd.rules;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.lang.java.ast.ASTClassOrInterfaceType;
import net.sourceforge.pmd.lang.java.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.lang.java.ast.ASTName;
import net.sourceforge.pmd.lang.java.ast.ASTPrimaryPrefix;
import net.sourceforge.pmd.lang.java.ast.ASTPrimarySuffix;
import net.sourceforge.pmd.lang.java.ast.ASTResultType;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.ClassScope;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.java.symboltable.MethodNameDeclaration;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Rule to prohibit use of String.length() when checking for empty string.
 * String.isEmpty() should be used instead.
 * @author Mihai Andronache (amihaiemil@gmail.com)
 * @version $Id$
 * @since 0.18
 * @todo #801:30min This rule currently doesn't complain if the string is
 *  prefixed with this when length is called (e.g. somestring.length() works
 *  but this.somestring.length() does not). The same happens in with a method
 *  call (this.method().length() does not work). Check if possible and if it
 *  is, extend this rule to also catch this case. More about how to write PMD
 *  rules here: http://pmd.sourceforge.net/pmd-4.3/howtowritearule.html.
 * @checkstyle MultipleStringLiteralsCheck (300 lines)
 */
public final class UseStringIsEmptyRule extends AbstractInefficientZeroCheck {

    @Override
    public boolean appliesToClassName(final String name) {
        return StringUtil.isSame(name, "String", true, true, true);
    }

    @Override
    public Map<String, List<String>> getComparisonTargets() {
        final Map<String, List<String>> rules = new HashMap<>();
        rules.put("<", Arrays.asList("1"));
        rules.put(">", Arrays.asList("0"));
        rules.put("==", Arrays.asList("0"));
        rules.put("!=", Arrays.asList("0"));
        rules.put(">=", Arrays.asList("0", "1"));
        rules.put("<=", Arrays.asList("0"));
        return rules;
    }

    @Override
    public boolean isTargetMethod(final JavaNameOccurrence occ) {
        boolean target = false;
        if (occ.getNameForWhichThisIsAQualifier() != null
            && occ.getLocation().getImage().endsWith(".length")
        ) {
            target = true;
        }
        return target;
    }

    @Override
    public Object visit(final ASTPrimarySuffix node, final Object data) {
        if (node.getImage() != null && node.getImage().endsWith("length")) {
            ASTClassOrInterfaceType type = getTypeOfPrimaryPrefix(node);
            if (type == null) {
                type = getTypeOfMethodCall(node);
            }
            if (type != null
                && this.appliesToClassName(type.getType().getSimpleName())
            ) {
                checkNodeAndReport(
                    data, node, node.jjtGetParent().jjtGetParent()
                );
            }
        }
        return data;
    }

    /**
     * Get the type returned by the method call.
     * @param node Node suffix
     * @return The type or null if it's not found
     */
    private static ASTClassOrInterfaceType getTypeOfMethodCall(
        final ASTPrimarySuffix node) {
        ASTClassOrInterfaceType type = null;
        final ASTName method = node.jjtGetParent()
            .getFirstChildOfType(ASTPrimaryPrefix.class)
            .getFirstChildOfType(ASTName.class);
        if (method != null) {
            final ClassScope scope = node.getScope()
                .getEnclosingScope(ClassScope.class);
            final Map<MethodNameDeclaration, List<NameOccurrence>> methods =
                scope.getMethodDeclarations();
            //@checkstyle LineLengthCheck (1 line)
            for (final Map.Entry<MethodNameDeclaration, List<NameOccurrence>> entry
                : methods.entrySet()) {
                if (entry.getKey().getName().equals(method.getImage())) {
                    type = entry.getKey().getNode()
                        .getFirstParentOfType(ASTMethodDeclaration.class)
                        .getFirstChildOfType(ASTResultType.class)
                        .getFirstDescendantOfType(
                            ASTClassOrInterfaceType.class
                        );
                    break;
                }
            }
        }
        return type;
    }

    /**
     * Get the type of the primary prefix.
     * @param node Node suffix
     * @return The type or null if it's not found.
     */
    private static ASTClassOrInterfaceType getTypeOfPrimaryPrefix(
        final ASTPrimarySuffix node) {
        return node.jjtGetParent()
            .getFirstChildOfType(ASTPrimaryPrefix.class)
            .getFirstDescendantOfType(ASTClassOrInterfaceType.class);
    }
}
