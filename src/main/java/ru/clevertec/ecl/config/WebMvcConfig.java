package ru.clevertec.ecl.config;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.AntPathMatcher;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import ru.clevertec.ecl.interceptor.ClusterInterceptor;
import ru.clevertec.ecl.interceptor.NonClusterInterceptor;
import ru.clevertec.ecl.interceptor.ReplicaInterceptor;

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
                .excludePathPatterns("*/status/*", "*/error/*");
    }
}
