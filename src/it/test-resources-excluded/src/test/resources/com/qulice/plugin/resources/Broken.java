class Broken {
    public int X;
    public Broken() { }
    public int Get_It(int  a,int b) {
        String s = "" + a;
        if (s.length() == 0) return b;
        return a;
    }
}
