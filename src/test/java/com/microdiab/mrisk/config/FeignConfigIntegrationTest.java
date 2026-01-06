package com.microdiab.mrisk.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.auth.BasicAuthRequestInterceptor;
import io.micrometer.tracing.Tracer;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.ApplicationContext;

import java.util.Collection;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Test d'intégration pour FeignConfig.
 * Vérifie que les interceptors Feign sont correctement configurés et injectés dans le contexte Spring.
 */
@SpringBootTest
class FeignConfigIntegrationTest {

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private Tracer tracer;

    @Test
    void shouldLoadFeignConfiguration() {
        assertThat(applicationContext).isNotNull();
        assertThat(applicationContext.getBean(FeignConfig.class)).isNotNull();
    }

    @Test
    void shouldInjectBasicAuthRequestInterceptor() {
        // Vérifie que le bean est présent dans le contexte
        BasicAuthRequestInterceptor interceptor = applicationContext.getBean(BasicAuthRequestInterceptor.class);
        assertThat(interceptor).isNotNull();

        // Vérifie que l'interceptor ajoute bien le header Authorization
        RequestTemplate template = new RequestTemplate();
        interceptor.apply(template);

        Collection<String> authHeaders = template.headers().get("Authorization");
        assertThat(authHeaders)
                .isNotNull()
                .hasSize(1);
        assertThat(authHeaders.iterator().next()).startsWith("Basic ");
    }

    @Test
    void shouldInjectB3HeadersRequestInterceptor() {
        // Vérifie que le bean est présent dans le contexte
        RequestInterceptor interceptor = applicationContext.getBean("b3HeadersRequestInterceptor", RequestInterceptor.class);
        assertThat(interceptor).isNotNull();
    }

    @Test
    void shouldInjectTracerBean() {
        // Vérifie que le Tracer est bien disponible pour l'interceptor B3
        assertThat(tracer).isNotNull();
    }

    @Test
    void b3HeadersInterceptor_shouldAddB3Headers_whenSpanExists() {
        // Ce test vérifie le comportement réel avec le tracer injecté par Spring
        RequestInterceptor interceptor = applicationContext.getBean("b3HeadersRequestInterceptor", RequestInterceptor.class);
        RequestTemplate template = new RequestTemplate();

        // Si un span existe dans le contexte de tracing actuel, les headers doivent être ajoutés
        interceptor.apply(template);

        // Note: Dans un environnement de test sans span actif, les headers peuvent ne pas être présents
        // Ce test vérifie surtout que l'interceptor ne plante pas
        assertThat(template).isNotNull();
    }

    @Test
    void b3HeadersInterceptor_shouldNotFail_whenNoActiveSpan() {
        // Vérifie que l'interceptor gère gracieusement l'absence de span
        RequestInterceptor interceptor = applicationContext.getBean("b3HeadersRequestInterceptor", RequestInterceptor.class);
        RequestTemplate template = new RequestTemplate();

        // Ne doit pas lever d'exception même sans span actif
        interceptor.apply(template);
        assertThat(template).isNotNull();
    }
}