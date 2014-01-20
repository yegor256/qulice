/**
 * Copyright (c) 2011-2013, Qulice.com
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

import java.util.List;
import java.util.Map;
import net.sourceforge.pmd.AbstractJavaRule;
import net.sourceforge.pmd.ast.ASTMethodDeclaration;
import net.sourceforge.pmd.ast.ASTName;
import net.sourceforge.pmd.ast.ASTReturnStatement;
import net.sourceforge.pmd.symboltable.NameOccurrence;
import net.sourceforge.pmd.symboltable.VariableNameDeclaration;

/**
 * Rule to check unnecessary local variables.
 *
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @version $Id$
 */
public final class UnnecessaryLocalRule extends AbstractJavaRule {
    @Override
    public Object visit(final ASTMethodDeclaration meth, final Object data) {
        Object ndata = data;
        if (!meth.isVoid() && !meth.isAbstract() && !meth.isNative()) {
            ndata = super.visit(meth, data);
        }
        return ndata;
    }

    @SuppressWarnings({ "PMD.AvoidInstantiatingObjectsInLoops",
        "PMD.UseConcurrentHashMap" })
    @Override
    public Object visit(final ASTReturnStatement rtn, final Object data) {
        final ASTName name = rtn.getFirstChildOfType(ASTName.class);
        if (name != null) {
            final Map<VariableNameDeclaration, List<NameOccurrence>> vars = name
                .getScope().getVariableDeclarations();
            for (Map.Entry<VariableNameDeclaration, List<NameOccurrence>> entry
                : vars.entrySet()) {
                final List<NameOccurrence> usages = entry.getValue();
                if (usages.size() > 1) {
                    continue;
                }
                for (NameOccurrence occ: usages) {
                    if (occ.getLocation().equals(name)) {
                        addViolation(data, rtn, new Object[] {name.getImage()});
                    }
                }
            }
        }
        return data;
    }
}
