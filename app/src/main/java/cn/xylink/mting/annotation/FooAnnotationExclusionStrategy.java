package cn.xylink.mting.annotation;

import com.google.gson.ExclusionStrategy;
import com.google.gson.FieldAttributes;

public class FooAnnotationExclusionStrategy implements ExclusionStrategy {
    @Override
    public boolean shouldSkipField(FieldAttributes f) {
        return f.getAnnotation(FooAnnotation.class) != null;
    }

    @Override
    public boolean shouldSkipClass(Class<?> clazz) {
        return clazz.getAnnotation(FooAnnotation.class) != null;
    }
}
