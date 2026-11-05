package in.hamshik.Helpers;

public class VarHelpers {

    // Order MUST match ESP enum: INT=0, FLOAT=1, STRING=2, BOOL=3
    public enum Type {
        INT, FLOAT, STRING, BOOL;

        /** Human-readable label used in the UI and CardHolder comparisons. */
        public String displayName() { return name(); }
    }

    // Order MUST match ESP enum: RO=0, RW=1, WO=2
    public enum Permissions {
        RO, RW, WO;

        /** Human-readable label used in the card UI. */
        public String displayName() {
            return switch (this) {
                case RO -> "Read Only";
                case WO -> "Write Only";
                case RW -> "Read / Write";
            };
        }
    }

    public record Variable(String name, Type type, Permissions permission) {}
}
