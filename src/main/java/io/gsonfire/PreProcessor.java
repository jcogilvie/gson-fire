package io.gsonfire;

import com.gilecode.yagson.com.google.gson.Gson;
import com.gilecode.yagson.com.google.gson.JsonElement;

/**
 * @autor: julio
 */
public interface PreProcessor<T> {

    void preDeserialize(Class<? extends T> clazz, JsonElement src, Gson gson);

}
