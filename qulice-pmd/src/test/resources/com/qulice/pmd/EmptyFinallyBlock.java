package emp;

class EmptyFinallyBlock {
    public void bar() {
        try {
            final int x = 1;
            x += 5;
            x++;
        } finally {
            // not good
        }
    }
}
