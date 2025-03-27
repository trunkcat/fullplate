/*
 * Copyright (c) 2024-2025 Trunk Cat Studios
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

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
    final String basePath;
    String authSessionToken = null;
    final Json jsonParser;

    public HTTPClient(String basePath) {
        this.basePath = basePath;

        jsonParser = new Json();
        jsonParser.setOutputType(JsonWriter.OutputType.json);
        jsonParser.setTypeName(null);
        jsonParser.setUsePrototypes(false);
        //noinspection rawtypes
        jsonParser.setSerializer(HashMap.class, new Json.Serializer<HashMap>() {
            @Override
            public void write(Json json, HashMap map, Class knownType) {
                json.writeObjectStart();
                for (Object entry : map.entrySet()) {
                    Map.Entry<?, ?> mapEntry = (Map.Entry<?, ?>) entry;
                    String key = mapEntry.getKey().toString();
                    json.writeValue(key, mapEntry.getValue());
                }
                json.writeObjectEnd();
            }

            @Override
            public HashMap<?, ?> read(Json json, JsonValue jsonData, Class type) {
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

    <T> void request(Net.HttpRequest httpRequest, ResponseHandler<T> responseHandler, Class<T> tClass) {
        Net.HttpResponseListener httpResponseListener = new Net.HttpResponseListener() {
            public void handleHttpResponse(Net.HttpResponse httpResponse) {
                final String result = httpResponse.getResultAsString();
                final ApiResponse<T> response;
                try {
                    response = ApiResponse.fromJson(result, tClass);
                    Gdx.app.postRunnable(() -> {
                        if (response.isOk()) {
                            responseHandler.success(response.getData());
                        } else {
                            responseHandler.failure(response.getMessage());
                        }
                    });
                } catch (Exception e) {
                    Gdx.app.error("HTTP", "Failed to parse response: " + e.getMessage());
                    Gdx.app.postRunnable(() -> responseHandler.failure("Invalid response from server"));
                }
            }

            public void failed(Throwable t) {
                responseHandler.failure("Failed to connect");
            }

            public void cancelled() {
                Gdx.app.log("HTTP", "Request was cancelled");
            }
        };

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

    public void get(String path, ResponseHandler<?> responseHandler) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.GET, path);
        request(request.build(), responseHandler, null);
    }


    public <T> void get(String path, ResponseHandler<T> responseHandler, Class<T> tClass) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.GET, path);
        request(request.build(), responseHandler, tClass);
    }


    public void post(String path, Object json, ResponseHandler<?> responseHandler) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.POST, path);
        Net.HttpRequest req = request.build();
        req.setHeader("Content-Type", "application/json");
        req.setContent(jsonParser.toJson(json));
        request(req, responseHandler, null);
    }

    public <T> void post(String path, Object json, ResponseHandler<T> responseHandler, Class<T> tClass) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.POST, path);
        Net.HttpRequest req = request.build();
        req.setHeader("Content-Type", "application/json");
        req.setContent(jsonParser.toJson(json));
        request(req, responseHandler, tClass);
    }

    public void delete(String path, ResponseHandler<?> responseHandler) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.DELETE, path);
        request(request.build(), responseHandler, null);
    }

    public <T> void delete(String path, ResponseHandler<T> responseHandler, Class<T> tClass) {
        HttpRequestBuilder request = makeBaseRequest(Net.HttpMethods.DELETE, path);
        request(request.build(), responseHandler, tClass);
    }
}
