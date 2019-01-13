package io.gsonfire;

import com.gilecode.yagson.com.google.gson.Gson;
import com.gilecode.yagson.com.google.gson.JsonElement;

/**
 * @autor: julio
 */
public interface PostProcessor<T> {

    void postDeserialize(T result, JsonElement src, Gson gson);

    void postSerialize(JsonElement result, T src, Gson gson);
}
