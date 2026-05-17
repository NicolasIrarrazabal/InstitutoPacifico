package com.proyecto.ms_notas.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

// @Configuration: esta clase define "beans" (componentes que Spring administra)
// Un Bean es un objeto que Spring crea y gestiona automáticamente
@Configuration
public class RestTemplateConfig {

    // @Bean: le dice a Spring que este método crea un objeto que puede inyectarse
    // en otros componentes con @RequiredArgsConstructor o @Autowired
    @Bean
    public RestTemplate restTemplate() {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        // Tiempo máximo para establecer conexión: 5 segundos
        factory.setConnectTimeout(5000);
        // Tiempo máximo para esperar respuesta: 10 segundos
        factory.setReadTimeout(10000);
        return new RestTemplate(factory);
    }
}
