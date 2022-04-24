package ru.clevertec.ecl.util.matcherhelper;

import org.springframework.data.domain.ExampleMatcher;
import org.springframework.stereotype.Component;
import ru.clevertec.ecl.dto.AbstractModel;
import ru.clevertec.ecl.dto.CertificateDto;
import ru.clevertec.ecl.exception.UnsupportedFilterException;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
public class CertificateMatcherBuilder extends MatcherBuilder<CertificateDto>{

    public CertificateMatcherBuilder() {
        configurePermanentlyIgnoredFields();
    }

    @Override
    public ExampleMatcher buildMatcher(List<String> required, CertificateDto o) {
        if (required.stream().anyMatch(excludedFieldNames::contains)) {
            throw new UnsupportedFilterException();
        }
        List<String> all = getFieldNames(o);
        all.removeAll(required);
//        all.addAll(excludedFieldNames);
        return ExampleMatcher.matchingAny()
                .withMatcher("name", matcher -> matcher.contains().ignoreCase())
                .withMatcher("description", matcher -> matcher.contains().ignoreCase())
//                .withMatcher("price", matcher -> matcher.contains().ignoreCase())
//                .withMatcher("duration", matcher -> matcher.contains().ignoreCase())
//                .withMatcher("createDate", matcher -> matcher.contains().ignoreCase())
//                .withMatcher("lastUpdateDate", matcher -> matcher.contains().ignoreCase())
//                .withMatcher("tags", matcher -> matcher.contains().ignoreCase())
                .withIgnoreNullValues()
//                .withIgnorePaths(all.toArray(new String[0]))
                ;
    }

    @Override
    public List<String> getRequiredFilters(CertificateDto propertySourceObj) {
        return getAllFilters(propertySourceObj);
    }

    @Override
    protected void configurePermanentlyIgnoredFields() {
        excludedFieldNames = new ArrayList<>(Arrays.asList("id", "createDate", "lastUpdateDate", "tags", "duration", "price"));
    }
}
