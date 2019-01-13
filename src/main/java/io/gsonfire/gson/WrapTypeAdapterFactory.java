package io.gsonfire.gson;

import com.gilecode.yagson.ReadContext;
import com.gilecode.yagson.WriteContext;
import com.gilecode.yagson.com.google.gson.Gson;
import com.gilecode.yagson.com.google.gson.JsonElement;
import com.gilecode.yagson.com.google.gson.JsonObject;
import com.gilecode.yagson.com.google.gson.TypeAdapter;
import com.gilecode.yagson.com.google.gson.TypeAdapterFactory;
import com.gilecode.yagson.com.google.gson.reflect.TypeToken;
import com.gilecode.yagson.com.google.gson.stream.JsonReader;
import com.gilecode.yagson.com.google.gson.stream.JsonWriter;
import io.gsonfire.util.JsonUtils;
import io.gsonfire.util.Mapper;

import java.io.IOException;
import java.util.Map;

/**
 * Created by asanchez on 19/02/16.
 */
public class WrapTypeAdapterFactory<T> implements TypeAdapterFactory {

    private final Map<Class<T>, Mapper<T, String>> wrappedClasses;

    public WrapTypeAdapterFactory(Map<Class<T>, Mapper<T, String>> wrappedClasses) {
        this.wrappedClasses = wrappedClasses;
    }

    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(this, type);
        final Mapper<T, String> mapper = (Mapper<T, String>) getMostSpecificMapper(type.getRawType());

        if (mapper == null) {
            return originalTypeAdapter;
        } else {
            return new NullableTypeAdapter<T>(new WrapperTypeAdapter(mapper, gson, originalTypeAdapter));
        }
    }

    private Mapper<T, String> getMostSpecificMapper(Class clazz) {
        Mapper<T, String> mostSpecificMapper = null;
        Class mostSpecificClass = clazz;
        while (mostSpecificClass != null) {
            mostSpecificMapper = wrappedClasses.get(mostSpecificClass);
            if (mostSpecificMapper != null) {
                return mostSpecificMapper;
            }
            mostSpecificClass = mostSpecificClass.getSuperclass();
        }
        return null;
    }

    private class WrapperTypeAdapter<T> extends TypeAdapter<T> {

        private final Mapper<T, String> mapper;
        private final Gson gson;
        private final TypeAdapter<T> originalTypeAdapter;

        public WrapperTypeAdapter(Mapper<T, String> mapper, Gson gson, TypeAdapter<T> originalTypeAdapter) {
            this.mapper = mapper;
            this.gson = gson;
            this.originalTypeAdapter = originalTypeAdapter;
        }

        @Override
        public void write(JsonWriter out, T src, WriteContext ctx) throws IOException {
            if (src == null) {
                //if src is null there is nothing for this type adapter to do, delegate it to the original type adapter
                originalTypeAdapter.write(out, src, ctx);
            } else {
                final String value = mapper.map(src);
                JsonElement unwrappedObj = JsonUtils.toJsonTree(originalTypeAdapter, out, src, ctx);
                JsonObject wrappedObj = new JsonObject();
                wrappedObj.add(value, unwrappedObj);
                gson.toJson(wrappedObj, out);
            }
        }

        @Override
        public T read(JsonReader in, ReadContext ctx) throws IOException {
            in.beginObject();
            in.nextName();
            T unwrappedObj = originalTypeAdapter.read(in, ctx);
            in.endObject();
            return unwrappedObj;
        }
    }
}
