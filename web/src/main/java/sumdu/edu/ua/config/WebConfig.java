package sumdu.edu.ua.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        // Налаштування роздачі статичного файлу index.html з папки resources/public/
        registry.addResourceHandler("/**")
                .addResourceLocations("classpath:/public/", "classpath:/static/", "/");
    }
}