/**
 * Copyright (c) 2011-2016, Qulice.com
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

import net.sourceforge.pmd.lang.java.ast.ASTImportDeclaration;
import net.sourceforge.pmd.lang.java.rule.junit.AbstractJUnitRule;

/**
 * Rule to check old style assertions in JUnit tests.
 * @author Viktor Kuchyn (kuchin.victor@gmail.com)
 * @version $Id$
 */
public final class ProhibitOldStyleAssertionsRule extends AbstractJUnitRule {

    /**
     * Mask of prohibited imports.
     */
    private static final String[] PROHIBITED = {
        "org.junit.Assert.assert",
        "junit.framework.Assert.assert",
    };

    @Override
    public Object visit(final ASTImportDeclaration imp, final Object data) {
        if (ProhibitOldStyleAssertionsRule.isArrayMatchesValue(
            imp.getImportedName(), ProhibitOldStyleAssertionsRule.PROHIBITED
        )) {
            this.addViolation(data, imp);
        }
        return super.visit(imp, data);
    }

    /**
     * Checks if string matches at least one element in array.
     * @param value String value to be matched
     * @param array Array of templates
     * @return False if no matches found, true if at least one
     */
    private static boolean isArrayMatchesValue(final String value,
        final String... array) {
        boolean matches = false;
        for (final String element : array) {
            if (value.contains(element)) {
                matches = true;
                break;
            }
        }
        return matches;
    }

}
