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
import com.badlogic.gdx.utils.Array;
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
		if (jsonValue == null) {
			Gdx.app.log("ApiResponse::fromJson", jsonString);
			throw new Error("Invalid JSON string");
		}

		ApiResponse<T> response = new ApiResponse<>();

		JsonValue okValue = jsonValue.get("ok");
		if (okValue != null) {
			response.setOk(okValue.asBoolean());
		}

		JsonValue messageValue = jsonValue.get("message");
		if (messageValue != null) {
			response.setMessage(messageValue.asString());
		}

		if (dataClass != null) {
			JsonValue dataValue = jsonValue.get("data");
			if (dataValue != null) {
				T data = json.readValue(dataClass, dataValue);
				response.setData(data);
			}
		}

		return response;
	}

	public static <T> ApiResponse<Array<T>> fromJson(String jsonString, Class<T> dataClass,
	                                                 boolean isArray) {
		Json json = new Json();
		json.setOutputType(OutputType.json);
		json.setIgnoreUnknownFields(true);
		json.setTypeName(null);
		json.setUsePrototypes(false);

		JsonValue jsonValue = new JsonReader().parse(jsonString);
		ApiResponse<Array<T>> response = new ApiResponse<>();

		JsonValue okValue = jsonValue.get("ok");
		if (okValue != null) {
			response.setOk(okValue.asBoolean());
		}

		JsonValue messageValue = jsonValue.get("message");
		if (messageValue != null) {
			response.setMessage(messageValue.asString());
		}

		if (dataClass != null) {
			JsonValue dataValue = jsonValue.get("data");
			if (dataValue != null) {
				if (dataValue.isArray()) {
					//noinspection rawtypes
					Array data = json.readValue(Array.class, dataClass, dataValue);
					//noinspection unchecked
					response.setData(data);
				} else {
					throw new Error("expected array data type");
				}
			}
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
