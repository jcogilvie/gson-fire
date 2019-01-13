package io.gsonfire.gson;

import com.gilecode.yagson.ReadContext;
import com.gilecode.yagson.WriteContext;
import com.gilecode.yagson.com.google.gson.Gson;
import com.gilecode.yagson.com.google.gson.TypeAdapter;
import com.gilecode.yagson.com.google.gson.TypeAdapterFactory;
import com.gilecode.yagson.com.google.gson.reflect.TypeToken;
import com.gilecode.yagson.com.google.gson.stream.JsonReader;
import com.gilecode.yagson.com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * Created by julio on 9/6/16.
 */
public final class EnumDefaultValueTypeAdapterFactory<T extends Enum> implements TypeAdapterFactory {

    private final Class<T> clazz;
    private final T defaultValue;

    public EnumDefaultValueTypeAdapterFactory(Class<T> clazz, T defaultValue) {
        this.clazz = clazz;
        this.defaultValue = defaultValue;
    }

    @Override
    public <T> TypeAdapter<T> create(final Gson gson, final TypeToken<T> type) {
        if(clazz.isAssignableFrom(type.getRawType())) {
            final TypeAdapter<T> originalTypeAdapter = gson.getDelegateAdapter(EnumDefaultValueTypeAdapterFactory.this, type);
            return new NullableTypeAdapter(
                new TypeAdapter<T>() {

                    @Override
                    public void write(JsonWriter jsonWriter, T o, WriteContext ctx) throws IOException {
                        originalTypeAdapter.write(jsonWriter, o, ctx);
                    }

                    @Override
                    public T read(JsonReader jsonReader, ReadContext ctx) throws IOException {
                        T result = originalTypeAdapter.read(jsonReader, ctx);
                        if(result == null) {
                            return (T) defaultValue;
                        } else {
                            return result;
                        }
                    }

                }
            );
        }
        return null;
    }



}
