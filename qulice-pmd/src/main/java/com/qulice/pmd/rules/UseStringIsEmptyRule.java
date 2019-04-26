/*
 * Copyright (c) 2011-2019, Qulice.com
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
import net.sourceforge.pmd.lang.ast.Node;
import net.sourceforge.pmd.lang.java.ast.ASTExpression;
import net.sourceforge.pmd.lang.java.ast.ASTVariableDeclaratorId;
import net.sourceforge.pmd.lang.java.ast.JavaNode;
import net.sourceforge.pmd.lang.java.rule.AbstractInefficientZeroCheck;
import net.sourceforge.pmd.lang.java.symboltable.JavaNameOccurrence;
import net.sourceforge.pmd.lang.symboltable.NameOccurrence;
import net.sourceforge.pmd.util.StringUtil;

/**
 * Rule to prohibit use of String.length() when checking for empty string.
 * String.isEmpty() should be used instead.
 * @since 0.18
 * @todo #950:30min Correct this class so it  complains if the string is
 *  prefixed with this when length is called (e.g. somestring.length() works
 *  but this.somestring.length() does not). The same happens in with a method
 *  call (this.method().length() does not work). More about how to write PMD
 *  rules here: http://pmd.sourceforge.net/pmd-4.3/howtowritearule.html. Then
 *  ignore the tests on UseStringIsEmptyRuleTest.
 */
@SuppressWarnings("PMD.AvoidDuplicateLiterals")
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
        final NameOccurrence name = occ.getNameForWhichThisIsAQualifier();
        return name != null && "length".equals(name.getImage());
    }

    @Override
    public Object visit(final ASTVariableDeclaratorId node, final Object data) {
        final Node type = node.getTypeNameNode();
        if (type != null
            || this.appliesToClassName(node.getNameDeclaration().getTypeImage())
        ) {
            final List<NameOccurrence> declars = node.getUsages();
            for (final NameOccurrence occ : declars) {
                final JavaNameOccurrence jocc = (JavaNameOccurrence) occ;
                if (this.isTargetMethod(jocc)) {
                    final JavaNode location = jocc.getLocation();
                    final Node expr = location.getFirstParentOfType(
                        ASTExpression.class
                    );
                    this.checkNodeAndReport(
                        data, jocc.getLocation(), expr.jjtGetChild(0)
                    );
                }
            }
        }
        return data;
    }
}
