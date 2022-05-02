package ru.clevertec.ecl.util.matcherhelper;

import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.exception.UndefinedException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@Component
@NoArgsConstructor
public class MatcherBuilder<T extends AbstractModel> {

    private List<String> getAllFilters(T propertySourceObj) {
        List<String> filters = new ArrayList<>();
        Object value;
        Class clazz = propertySourceObj.getClass();
        try {
            while (clazz != AbstractModel.class) {
                for (Field field : propertySourceObj.getClass().getDeclaredFields()) {
                    String fieldName = field.getName();
                    field.setAccessible(true);
                    value = field.get(propertySourceObj);
                    if (!isObjectNullOrDefault(value, field)) {
                        filters.add(fieldName);
                    }
                }
                clazz = clazz.getSuperclass();
            }
        } catch (IllegalAccessException e) {
            throw new UndefinedException(e.getMessage());
        }
        return filters;
    }

    public ExampleMatcher buildMatcher(T o) {
        ExampleMatcher filterMatcher = ExampleMatcher.matchingAll();
        List<String> filterNames = getAllFilters(o);
        for (String filter : filterNames) {
            filterMatcher = filterMatcher.withMatcher(filter, matcher -> matcher.contains().ignoreCase());
        }
        return filterMatcher;
    }

    private static boolean isObjectNullOrDefault(Object v, Field field) {
        Class<?> t = field.getType();
        if (boolean.class.equals(t)) {
            return Boolean.FALSE.equals(v);
        } else if (char.class.equals(t)) {
            return ((Character) v) == Character.MIN_VALUE;
        } else if (t.isPrimitive()) {
            return ((Number) v).doubleValue() == 0;
        } else {
            return v == null;
        }
    }

    public boolean isEmpty(T o) {
        List<Object> notNullFields = new ArrayList<>();
        try {
            Object value;
            for (Field field : o.getClass().getDeclaredFields()) {
                String fieldName = field.getName();
                field.setAccessible(true);
                value = field.get(o);
                if (!isObjectNullOrDefault(value, field)) {
                    notNullFields.add(value);
                }
            }
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
        return notNullFields.isEmpty();
    }

}
