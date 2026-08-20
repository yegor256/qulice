/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
class Sample {
    public int Z;
    public Sample() { }
    public int Give_Me(int  a,int b) {
        String s = "" + a;
        if (s.length() == 0) return b;
        return a;
    }
}
