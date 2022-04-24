package ru.clevertec.ecl.util.matcherhelper;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.TagDto;
import ru.clevertec.ecl.exception.UnsupportedFilterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class TagMatcherBuilder extends MatcherBuilder<TagDto> {

    public TagMatcherBuilder() {
        configurePermanentlyIgnoredFields();
    }

    @Override
    public ExampleMatcher buildMatcher(List<String> required, TagDto o) {
        if (required.stream().anyMatch(excludedFieldNames::contains)) {
            throw new UnsupportedFilterException();
        }
        List<String> all = getFieldNames(o);
        all.removeAll(required);
        all.addAll(excludedFieldNames);
        return ExampleMatcher.matchingAny()
                .withMatcher("name", matcher -> matcher.contains().ignoreCase())
                .withIgnoreNullValues()
                .withIgnorePaths(all.toArray(new String[0]));
    }

    @Override
    public List<String> getRequiredFilters(TagDto propertySourceObj) {
        return getAllFilters(propertySourceObj);
    }

    @Override
    protected void configurePermanentlyIgnoredFields() {
        excludedFieldNames = new ArrayList<>(Arrays.asList("id", "name"));
    }
}
