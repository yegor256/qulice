package emp;

class EmptyCatchBlock {
    public void bar() {
        try {
            final int x = 1;
        } catch (Exception ioe) {
            // not good
        }
    }
}
