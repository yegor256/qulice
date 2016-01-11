package emp;

class EmptySynchronizedBlock {
    public void bar() {
        synchronized (this) {
            // empty!
        }
    }
}
