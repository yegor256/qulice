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
package com.qulice.checkstyle;

import com.puppycrawl.tools.checkstyle.api.AbstractCheck;

/**
 * Checks if the classes in the file are final.
 *
 * @since 0.18
 * @todo #920:30min Qulice should prohibit any non-final class. Tests for
 *  this check have already been implemented in ProhibitNonFinalClassesCheck
 *  folder. After the implementation of this class add config.xml to folder
 *  pointing to ProhibitNonFinalClassesCheck class, add check into ChecksTest
 *  and add it to custom checks in checks.xml
 */
public final class ProhibitNonFinalClassesCheck extends AbstractCheck {
    @Override
    public int[] getDefaultTokens() {
        throw new UnsupportedOperationException(
            "getDefaultTokens() not implemented"
        );
    }

    @Override
    public int[] getAcceptableTokens() {
        throw new UnsupportedOperationException(
            "getAcceptableTokens() not implemented"
        );
    }

    @Override
    public int[] getRequiredTokens() {
        throw new UnsupportedOperationException(
            "getRequiredTokens() not implemented"
        );
    }
}
