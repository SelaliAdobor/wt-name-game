package com.willowtree.namegame.util;

import io.realm.RealmQuery;

public class RealmUtil {
    /**
     * Source: https://github.com/realm/realm-java/issues/3154
     */
    public static <T> RealmQuery<T> excludeFieldValue(RealmQuery<T> query, String fieldName, String... excludedValues) {
        if (excludedValues.length > 0) {
            boolean isFirst = true;
            query = query.beginGroup();
            for (String value : excludedValues) {
                if (!isFirst) {
                    query = query.or();
                }
                query = query.notEqualTo(fieldName, value);
                isFirst = false;
            }
            query = query.endGroup();
        }
        return query;
    }
}
