package io.gsonfire.gson;

import com.gilecode.yagson.ReadContext;
import com.gilecode.yagson.WriteContext;
import com.gilecode.yagson.com.google.gson.TypeAdapter;
import com.gilecode.yagson.com.google.gson.stream.JsonReader;
import com.gilecode.yagson.com.google.gson.stream.JsonToken;
import com.gilecode.yagson.com.google.gson.stream.JsonWriter;

import java.io.IOException;

/**
 * @autor: julio
 */
public final class NullableTypeAdapter<T> extends TypeAdapter<T> {

    private final TypeAdapter<T> nullable;

    public NullableTypeAdapter(TypeAdapter<T> nullable) {
        this.nullable = nullable;
    }

    @Override
    public void write(JsonWriter out, T value, WriteContext ctx) throws IOException {
        if(value == null){
            out.nullValue();
        } else {
            nullable.write(out, value, ctx);
        }
    }

    @Override
    public T read(JsonReader in, ReadContext ctx) throws IOException {
        if(in.peek() == JsonToken.NULL){
            in.nextNull();
            return null;
        } else {
            return nullable.read(in, ctx);
        }
    }
}
