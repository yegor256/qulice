package foo;

class SingletonClassReturningNewInstance {
    private static Singleton instance = null;

    public static Singleton getInstance() {
        synchronized(Singleton.class) {
            return new Singleton();
        }
    }
}
