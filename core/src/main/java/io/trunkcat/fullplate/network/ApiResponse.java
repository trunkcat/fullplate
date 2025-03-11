package io.trunkcat.fullplate.network;

import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonReader;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter.OutputType;

public class ApiResponse<T> {
    private boolean ok;
    private String message;
    private T data;

    public ApiResponse() {
    }

    public ApiResponse(boolean ok, String message, T data) {
        this.ok = ok;
        this.message = message;
        this.data = data;
    }

    public boolean isOk() {
        return ok;
    }

    public void setOk(boolean ok) {
        this.ok = ok;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public static <T> ApiResponse<T> fromJson(String jsonString, Class<T> dataClass) {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        json.setIgnoreUnknownFields(true);
        json.setTypeName(null);
        json.setUsePrototypes(false);

        JsonValue jsonValue = new JsonReader().parse(jsonString);
        ApiResponse<T> response = new ApiResponse<>();

        JsonValue okValue = jsonValue.get("ok");
        if (okValue != null) response.setOk(okValue.asBoolean());

        JsonValue messageValue = jsonValue.get("message");
        if (messageValue != null) response.setMessage(messageValue.asString());

        JsonValue dataValue = jsonValue.get("data");
        if (dataValue != null) {
            T data = json.readValue(dataClass, dataValue);
            response.setData(data);
        }

        return response;
    }

    public String toJson() {
        Json json = new Json();
        json.setOutputType(OutputType.json);
        json.setTypeName(null);
        json.setUsePrototypes(false);
        return json.toJson(this);
    }
}
