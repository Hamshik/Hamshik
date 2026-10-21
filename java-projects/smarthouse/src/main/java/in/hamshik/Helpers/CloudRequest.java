package in.hamshik.Helpers;

import com.google.gson.JsonObject;

enum ReqTy {
    GET_VAR(0),
    SET_VAR(1),
    GET_ALL_VAR(2);

    private final int value;

    ReqTy(int value) {
        this.value = value;
    }

    public int value() {
        return value;
    }
}

public final class CloudRequest {

    private CloudRequest() {}

    public static JsonObject getVar(String varName) {
        JsonObject request = new JsonObject();

        request.addProperty("msg", ReqTy.GET_VAR.value());
        request.addProperty("VarName", varName);

        return request;
    }

    public static JsonObject setVar(String varName, Object value) {
        JsonObject request = new JsonObject();

        request.addProperty("msg", ReqTy.SET_VAR.value());
        request.addProperty("VarName", varName);

        if (value instanceof Number n) {
            request.addProperty("Value", n);
        } else if (value instanceof Boolean b) {
            request.addProperty("Value", b);
        } else if (value instanceof String s) {
            request.addProperty("Value", s);
        } else if (value == null) {
            request.add("Value", com.google.gson.JsonNull.INSTANCE);
        } else {
            throw new IllegalArgumentException(
                "Unsupported value type: " + value.getClass()
            );
        }

        return request;
    }

    public static JsonObject getAllVar(){
        JsonObject json = new JsonObject();
        json.addProperty("msg", ReqTy.GET_ALL_VAR.value());
        return json;
    }
}