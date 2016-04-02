package net.klakegg.clips.utils;

import net.klakegg.clips.lang.ClassException;

public class Classes {

    public static Class<?> get(String className) {
        try {
            return Class.forName(className);
        } catch (ClassNotFoundException e) {
            throw new ClassException(String.format("Unable to load class '%s'.", className), e);
        }
    }

    public static Class<?> get(String className, String fallback) {
        try {
            return get(className);
        } catch (ClassException e) {
            return get(fallback);
        }
    }

    public static Class<?> get(String className, Class<?> fallback) {
        try {
            return get(className);
        } catch (ClassException e) {
            return fallback;
        }
    }
}
