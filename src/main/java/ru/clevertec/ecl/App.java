package ru.clevertec.ecl;

import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.http.client.ClientHttpRequestInterceptor;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.servlet.i18n.AcceptHeaderLocaleResolver;
import ru.clevertec.ecl.interceptor.RestTemplateInterceptor;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Application's start point containing main() method.
 *
 * See also {@link SpringBootApplication}
 *
 * @author Olga Mailychko
 *
 */
@SpringBootApplication
@RequiredArgsConstructor
public class App {

    private final RestTemplateInterceptor restTemplateInterceptor;

    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }

    @Bean
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasename("messages");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setUseCodeAsDefaultMessage(true);
        return messageSource;
    }

    @Bean
    public AcceptHeaderLocaleResolver localeResolver() {
        final AcceptHeaderLocaleResolver resolver = new AcceptHeaderLocaleResolver();
        resolver.setDefaultLocale(Locale.US);
        return resolver;
    }

    @Bean
    public RestTemplate restTemplate() {
        RestTemplate restTemplate = new RestTemplate();

        List<ClientHttpRequestInterceptor> interceptors
                = restTemplate.getInterceptors();
        if (CollectionUtils.isEmpty(interceptors)) {
            interceptors = new ArrayList<>();
        }
        interceptors.add(restTemplateInterceptor);
        restTemplate.setInterceptors(interceptors);
        return restTemplate;
    }

}
