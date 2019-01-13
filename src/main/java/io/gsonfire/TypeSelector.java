package io.gsonfire;

import com.gilecode.yagson.com.google.gson.JsonElement;

/**
 * @autor: julio
 */
public interface TypeSelector<T> {

    Class<? extends T> getClassForElement(JsonElement readElement);

}
