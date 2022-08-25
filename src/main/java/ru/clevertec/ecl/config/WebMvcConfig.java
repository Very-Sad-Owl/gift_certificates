package ru.clevertec.ecl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.clevertec.ecl.interceptor.ClusterInterceptor;
import ru.clevertec.ecl.interceptor.NonClusterInterceptor;
import ru.clevertec.ecl.interceptor.ReplicaInterceptor;

/**
 * This configuration class customizes default Spring MVC logic.
 *
 * Please see the {@link org.springframework.web.servlet.config.annotation.WebMvcConfigurer} class
 * @author Olga Mailychko
 *
 */
@Configuration
@RequiredArgsConstructor
public class WebMvcConfig implements WebMvcConfigurer {

    private final ClusterInterceptor clusterInterceptor;
    private final NonClusterInterceptor nonClusterInterceptor;
    private final ReplicaInterceptor replicaInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(clusterInterceptor).addPathPatterns("/orders/**");
        registry.addInterceptor(nonClusterInterceptor)
                .addPathPatterns("/tags/**", "/users/**", "/certificates/**")
                .excludePathPatterns("/**/find/", "/**/findAll/");
        registry.addInterceptor(replicaInterceptor)
                .excludePathPatterns("*/status/*", "/error/*");
    }
}
