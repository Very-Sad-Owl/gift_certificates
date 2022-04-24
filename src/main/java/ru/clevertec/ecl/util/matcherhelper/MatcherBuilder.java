package ru.clevertec.ecl.util.matcherhelper;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import org.springframework.data.domain.ExampleMatcher;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.exception.crud.UndefinedException;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@NoArgsConstructor
public abstract class MatcherBuilder<T extends AbstractModel> {

    protected List<String> excludedFieldNames;

    public List<String> getAllFilters(T propertySourceObj) {
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

    public abstract ExampleMatcher buildMatcher(List<String> required, T o);
    public abstract List<String> getRequiredFilters(T propertySourceObj);
    protected abstract void configurePermanentlyIgnoredFields();

    protected static boolean isObjectNullOrDefault(Object v, Field field) {
        Class<?> t = field.getType();
        if (boolean.class.equals(t)) {
            return Boolean.FALSE.equals(v);
        } else if (char.class.equals(t)) {
            return ((Character) v) != Character.MIN_VALUE;
        } else if (t.isPrimitive()) {
            return ((Number) v).doubleValue() == 0;
        } else {
            return v == null;
        }
    }

    protected static List<String> getFieldNames(Object o) {
        List<String> names = new ArrayList<>();
        Class<?> c = o.getClass();
        Field[] fields = c.getDeclaredFields();
        for (Field field : fields) {
            names.add(field.getName());
        }
        return names;
    }
}
