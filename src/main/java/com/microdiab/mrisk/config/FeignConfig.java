package com.microdiab.mrisk.config;

import feign.RequestInterceptor;
import feign.RequestTemplate;
import feign.auth.BasicAuthRequestInterceptor;
import io.micrometer.tracing.Span;
import io.micrometer.tracing.Tracer;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for Feign clients in the MicroDiab project.
 * This class provides beans necessary for Feign client customization,
 * including basic authentication and distributed tracing headers.
 */
@Configuration
public class FeignConfig {

    /**
     * Tracer used to propagate distributed tracing context across Feign requests.
     */
    @Autowired
    private Tracer tracer;

    /**
     * Creates a {@link BasicAuthRequestInterceptor} bean for Feign clients.
     * This interceptor adds HTTP Basic Authentication headers to outgoing requests.
     *
     * @return A configured {@link BasicAuthRequestInterceptor} instance.
     */
    @Bean
    public BasicAuthRequestInterceptor basicAuthRequestInterceptor() {
        return new BasicAuthRequestInterceptor("username", "user");
    }

    /**
     * Creates a {@link RequestInterceptor} bean for Feign clients.
     * This interceptor propagates B3 tracing headers (TraceId and SpanId) to outgoing requests,
     * enabling distributed tracing across microservices.
     *
     * @return A configured {@link RequestInterceptor} instance.
     */
    @Bean
    public RequestInterceptor b3HeadersRequestInterceptor() {
        return new RequestInterceptor() {
            @Override
            public void apply(RequestTemplate requestTemplate) {
                Span currentSpan = tracer.currentSpan();
                if (currentSpan != null) {
                    // Propagate B3 headers for distributed tracing
                    requestTemplate.header("X-B3-TraceId", currentSpan.context().traceId());
                    requestTemplate.header("X-B3-SpanId", currentSpan.context().spanId());
                    requestTemplate.header("X-B3-Sampled", "1");
                }
            }
        };
    }
}
