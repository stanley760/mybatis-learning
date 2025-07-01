package org.apache.ibatis.copyright.type;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * @author ywb
 * @description:
 * @datetime 2025-06-30 11:45
 * @version: 1.0
 */
public class TypeAliasRegistry {

    private final Map<String, Class<?>> typeAliases = new HashMap<>();

    public TypeAliasRegistry() {
        registerTypeAlias("string", String.class);
        registerTypeAlias("int", Integer.class);
        registerTypeAlias("long", Long.class);
        registerTypeAlias("double", Double.class);
        registerTypeAlias("boolean", Boolean.class);
        registerTypeAlias("byte", Byte.class);
        registerTypeAlias("short", Short.class);
        registerTypeAlias("float", Float.class);
    }

    public void registerTypeAlias(String alias, Class<?> type) {
        String key = alias.toLowerCase(Locale.ENGLISH);
        typeAliases.put(key, type);
    }

    public <T> Class<T> resolveAlias(String typeAlias) {
        String key = typeAlias.toLowerCase(Locale.ENGLISH);
        return (Class<T>) typeAliases.get(key);
    }
}
