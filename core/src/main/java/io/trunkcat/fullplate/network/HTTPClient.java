package io.trunkcat.fullplate.network;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.Net;
import com.badlogic.gdx.net.HttpRequestBuilder;
import com.badlogic.gdx.utils.Json;
import com.badlogic.gdx.utils.JsonValue;
import com.badlogic.gdx.utils.JsonWriter;

import java.util.HashMap;
import java.util.Map;

public class HTTPClient {
    String basePath;
    String authSessionToken = null;
    Json jsonParser;

    public HTTPClient(String basePath) {
        this.basePath = basePath;

        jsonParser = new Json();
        jsonParser.setOutputType(JsonWriter.OutputType.json);
        jsonParser.setTypeName(null);
        jsonParser.setUsePrototypes(false);
        jsonParser.setSerializer(HashMap.class, new Json.Serializer<HashMap>() {
            @Override
            public void write(Json json, HashMap map, Class knownType) {
                json.writeObjectStart();
                for (Object entry : map.entrySet()) {
                    Map.Entry mapEntry = (Map.Entry) entry;
                    String key = mapEntry.getKey().toString();
                    json.writeValue(key, mapEntry.getValue());
                }
                json.writeObjectEnd();
            }

            @Override
            public HashMap read(Json json, JsonValue jsonData, Class type) {
                return null; // read isn't required as we only send these data
            }
        });
    }

    public boolean hasAuthSessionToken() {
        return authSessionToken != null && !authSessionToken.isEmpty();
    }

    public void setAuthSessionToken(String token) {
        this.authSessionToken = token;
    }

    void request(Net.HttpRequest httpRequest, Net.HttpResponseListener httpResponseListener) {
        Gdx.net.sendHttpRequest(httpRequest, httpResponseListener);
    }

    HttpRequestBuilder makeBaseRequest(String method, String path) {
        HttpRequestBuilder request = new HttpRequestBuilder()
            .newRequest()
            .method(method)
            .url(basePath + path)
            .timeout(5000);

        if (authSessionToken != null && !authSessionToken.isEmpty()) {
            request.header("Authorization", "Bearer " + authSessionToken);
        }

        return request;
    }

    public void get(String path, Net.HttpResponseListener httpResponseListener) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.GET, path);
        request(request.build(), httpResponseListener);
    }

    public void post(String path, Object json, Net.HttpResponseListener httpResponseListener) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.POST, path);
        Net.HttpRequest req = request.build();
        req.setHeader("Content-Type", "application/json");
        req.setContent(jsonParser.toJson(json));
        request(req, httpResponseListener);
    }
}
