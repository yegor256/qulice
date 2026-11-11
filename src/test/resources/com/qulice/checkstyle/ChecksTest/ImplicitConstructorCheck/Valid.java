/*
 * This is not a real Java class. It won't be compiled ever. It is used
 * only as a text resource in integration.ChecksIT.
 */
public class Valid {
    public Valid() {
    }
    public static class Nested {
        private Nested() {
        }
        public static int one() {
            return 1;
        }
    }
    private static class Hidden {
        public int two() {
            return 2;
        }
    }
    static class Neighbour {
        public int three() {
            return 3;
        }
        public static class Deep {
            public int five() {
                return 5;
            }
        }
    }
    public void run() {
        class Local {
            int four() {
                return 4;
            }
        }
        new Local();
    }
    public Object anonymous() {
        return new Object() {
            public String toString() {
                return "";
            }
        };
    }
}
