package ru.clevertec.ecl.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.*;
import java.util.stream.Collectors;

/**
 * DTO class for transfer {@link ru.clevertec.ecl.entity.baseentities.Certificate} data.
 *
 * Can be converted from {@link ru.clevertec.ecl.dto.CertificateDto} DTO for providing
 * filtering logic methods. Contains only fields that requested entity can be filtered by.
 *
 * @author Olga Mailychko
 *
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CertificateFilterDto {
    private String name;
    private String description;
    private Set<String> filteringTags;

    public List<RequestedFilter> getRequestedFilters() {
        List<RequestedFilter> appliedFilters = Arrays.asList(this.getClass().getDeclaredFields()).stream()
                .map(field -> {
                    field.setAccessible(true);
                    return field;
                })
                .filter(field -> {
                    try {
                        return field.get(this) != null;
                    } catch (IllegalAccessException e) {
                       return false;
                    }
                })
                .map(field -> RequestedFilter.findByFieldName(field.getName()))
                .collect(Collectors.toList());
        appliedFilters = appliedFilters.stream().filter(Objects::nonNull).collect(Collectors.toList());
        if (appliedFilters.isEmpty()) {
            return new ArrayList<>(Collections.singletonList(RequestedFilter.NONE));
        }
        if (appliedFilters.containsAll(Arrays.asList(RequestedFilter.DESCRIPTION, RequestedFilter.NAME))) {
            appliedFilters.removeAll(Arrays.asList(RequestedFilter.DESCRIPTION, RequestedFilter.NAME));
            appliedFilters.add(RequestedFilter.NAME_AND_DESCRIPTION);
        }
        return appliedFilters;
    }

    @AllArgsConstructor
    public enum RequestedFilter {
        TAG("filteringTags"), NAME("name"), NONE(""),
        DESCRIPTION("description"), NAME_AND_DESCRIPTION("nameAndDescription");

        private String correspondingFieldName;
        private static final Map<String, RequestedFilter> map;

        static {
            map = new HashMap<>();
            for (RequestedFilter v : RequestedFilter.values()) {
                map.put(v.correspondingFieldName, v);
            }
        }

        public static RequestedFilter findByFieldName(String fieldName) {
            return map.get(fieldName);
        }
    }
}
