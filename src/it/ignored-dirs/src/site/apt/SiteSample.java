/*
 * SPDX-FileCopyrightText: Copyright (c) 2011-2026 Yegor Bugayenko
 * SPDX-License-Identifier: MIT
 */
class SiteSample {
    public int Y;
    public SiteSample() { }
    public int Do_It(int  a,int b) {
        String s = "" + a;
        if (s.length() == 0) return b;
        return a;
    }
}
