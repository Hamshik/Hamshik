package in.hamshik.Helpers;

import java.util.HashMap;

public class VarHelpers {

    // Order MUST match ESP enum: INT=0, FLOAT=1, STRING=2, BOOL=3
    public enum Type {
        INT, FLOAT, STRING, BOOL, ANY, CHAR;

        /** Human-readable label used in the UI and CardHolder comparisons. */
        public String displayName() { return name(); }
        public static HashMap<String, Type> datatype = new HashMap<>();
        static {
            for (var i : Type.values())
                datatype.put(i.name().toLowerCase(), i);
        }
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
