/**
 * Copyright (c) 2011-2015, Qulice.com
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
package com.qulice.checkstyle;

import com.google.common.base.Optional;
import com.puppycrawl.tools.checkstyle.TreeWalker;
import com.puppycrawl.tools.checkstyle.api.DetailAST;
import com.puppycrawl.tools.checkstyle.api.FileContents;
import com.puppycrawl.tools.checkstyle.api.FileText;
import com.puppycrawl.tools.checkstyle.api.TokenTypes;
import java.io.File;
import java.nio.charset.StandardCharsets;
import org.hamcrest.MatcherAssert;
import org.hamcrest.Matchers;
import org.junit.Ignore;
import org.junit.Test;

/**
 * Test case for {@link DetailAST}.
 * @author I. Sokolov (happy.neko@gmail.com)
 * @version $Id$
 * @since 0.14.2
 */
public final class DetailASTTest {

    /**
     * ELIST token should return valid line number.
     * @throws Exception If something goes wrong
     * @todo #541:30min/DEV Test BracketsStructureCheck with fixed checkstyle
     *  version.
     */
    @Ignore
    @Test
    public void expressionListTokeHasValidLineNumber() throws Exception {
        final String path = "/com/qulice/checkstyle/ExpressionList.java";
        final FileContents contents =
            new FileContents(
                new FileText(
                    new File(this.getClass().getResource(path).getFile()),
                    StandardCharsets.UTF_8.name()
                )
            );
        final DetailAST ast = TreeWalker.parse(contents);
        final DetailAST token = findToken(ast, TokenTypes.ELIST).get();
        MatcherAssert.assertThat(
            token.getLineNo(),
            Matchers.equalTo(
                token.getPreviousSibling().getLineNo()
            )
        );
    }

    /**
     * Recursive token search.
     * @param ast Tree to search
     * @param type Token type
     * @return Token node if found
     */
    private static Optional<DetailAST> findToken(final DetailAST ast,
        final int type) {
        Optional<DetailAST> token = Optional.absent();
        DetailAST child = ast.getFirstChild();
        while (child != null) {
            token = Optional.fromNullable(child.findFirstToken(type));
            if (token.isPresent()) {
                break;
            }
            token = findToken(child, type);
            if (token.isPresent()) {
                break;
            }
            child = child.getNextSibling();
        }
        return token;
    }
}
