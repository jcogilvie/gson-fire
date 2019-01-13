package io.gsonfire.gson;

import com.gilecode.yagson.ReadContext;
import com.gilecode.yagson.WriteContext;
import com.gilecode.yagson.adapters.DelegatingTypeAdapter;
import com.gilecode.yagson.com.google.gson.Gson;
import com.gilecode.yagson.com.google.gson.JsonElement;
import com.gilecode.yagson.com.google.gson.JsonParser;
import com.gilecode.yagson.com.google.gson.TypeAdapter;
import com.gilecode.yagson.com.google.gson.internal.Streams;
import com.gilecode.yagson.com.google.gson.internal.bind.JsonTreeReader;
import com.gilecode.yagson.com.google.gson.stream.JsonReader;
import com.gilecode.yagson.com.google.gson.stream.JsonWriter;
import io.gsonfire.ClassConfig;
import io.gsonfire.PostProcessor;
import io.gsonfire.PreProcessor;
import io.gsonfire.util.JsonUtils;

import java.io.IOException;

/**
 * @autor: julio
 */
public final class HooksTypeAdapter<T> extends DelegatingTypeAdapter<T> {

    private final Class<T> clazz;
    private final ClassConfig<? super T> classConfig;
    private final Gson gson;
    private final TypeAdapter<T> originalTypeAdapter;
    private final HooksInvoker hooksInvoker = new HooksInvoker();

    public HooksTypeAdapter(Class<T> classAdapter, ClassConfig<? super T> classConfig, TypeAdapter<T> originalTypeAdapter, Gson gson) {
        this.classConfig = classConfig;
        this.gson = gson;
        this.originalTypeAdapter = originalTypeAdapter;
        this.clazz = classAdapter;

        // yagson change
        this.delegate = originalTypeAdapter;
    }

    @Override
    public void write(JsonWriter out, T value, WriteContext ctx) throws IOException {
        if(classConfig.isHooksEnabled()){
            hooksInvoker.preSerialize(value);
        }

        JsonElement res = JsonUtils.toJsonTree(originalTypeAdapter, out, value, ctx);

        //Run all the post serializers
        runPostSerialize(res, value);

        gson.toJson(res, out);
    }

    @Override
    public T read(JsonReader in, ReadContext ctx) throws IOException {
        JsonElement json = new JsonParser().parse(in);

        runPreDeserialize(json);
        T result = deserialize(json, in.isLenient(), ctx);

        //Run all the post deserializers
        if (classConfig.isHooksEnabled()) {
            hooksInvoker.postDeserialize(result, json, gson);
        }
        runPostDeserialize(result, json);

        return result;
    }

    private void runPostSerialize(JsonElement json, T src){
        for(PostProcessor<? super T> postProcessor: classConfig.getPostProcessors()){
            postProcessor.postSerialize(json, src, gson);
        }
    }

    private void runPostDeserialize(T res, JsonElement src){
        for(PostProcessor<? super T> postProcessor: classConfig.getPostProcessors()){
            postProcessor.postDeserialize(res, src, gson);
        }
    }

    private void runPreDeserialize(JsonElement json){
        for(PreProcessor<? super T> preProcessor: classConfig.getPreProcessors()){
            preProcessor.preDeserialize(clazz, json, gson);
        }
    }

    private T deserialize(JsonElement json, boolean lenient, ReadContext ctx) throws IOException{
        JsonReader jsonReader = new JsonTreeReader(json);
        jsonReader.setLenient(lenient);
        T deserialized = originalTypeAdapter.read(jsonReader, ctx);
        return deserialized;
    }

}
