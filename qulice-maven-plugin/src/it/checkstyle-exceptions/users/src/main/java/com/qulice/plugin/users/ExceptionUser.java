/**
 * Copyright (c) 2011-2014, Qulice.com
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
package com.qulice.plugin.users;

import com.qulice.plugin.exceptions.FooException;
import com.qulice.plugin.exceptions.ModuleException;
import com.qulice.plugin.users.sub.SubException;

/**
 * Exception user class.
 *
 * @author Krzysztof Krason (Krzysztof.Krason@gmail.com)
 * @version $Id$
 */
public final class ExceptionUser {
    /**
     * Utility constructor.
     * @throws BarException in case of error.
     */
    private ExceptionUser() throws BarException {
        // do nothing
    }

    /**
     * Might throw exception.
     * @throws FooException In case of problem.
     */
    public static void user() throws FooException {
        throw new FooException();
    }

    /**
     * Test.
     */
    public static void catcher() {
        try {
            ExceptionUser.user();
        } catch (final FooException ex) {
            throw new IllegalStateException(ex);
        }
    }

    /**
     * Might throw exception.
     * @throws SubException In case of problem.
     */
    public static void subuser() throws SubException {
        throw new SubException();
    }

    /**
     * Might throw exception.
     * @throws ModuleException In case of problem.
     */
    public static void moduleuser() throws ModuleException {
        throw new ModuleException();
    }
}
