package emp;

class EmptyStatementBlock {
    private int baz;

    public void setBar(int bar) {
        { baz = bar; } // Why not?
        {} // But remove this.
    }
}
