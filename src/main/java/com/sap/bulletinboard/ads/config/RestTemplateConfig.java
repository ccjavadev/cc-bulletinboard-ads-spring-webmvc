package com.sap.bulletinboard.ads.config;

import org.apache.http.HttpHost;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.ClientHttpRequestFactory;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

/**
 * Makes use of Apache HttpComponents HttpClient to create requests. It shows how to overwrite the default HTTP client,
 * potentially with authentication, HTTP connection pooling, etc.
 * 
 * See <a href="http://hc.apache.org/httpcomponents-client-ga/">Apache HTTPClient</a>
 *
 */
@Configuration
public class RestTemplateConfig {

    @Value("${http.proxyHost:#{null}}") // proxy.wdf.sap.corp
    private String proxyHost;

    @Value("${http.proxyPort:#0}") // defaults to 0
    private int proxyPort;

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate(getClientHttpRequestFactory());
    };

    private ClientHttpRequestFactory getClientHttpRequestFactory() {

        HttpClientBuilder clientBuilder = HttpClientBuilder.create();
        clientBuilder.setDefaultRequestConfig(RequestConfig.custom().build());

        if (proxyHost != null && !proxyHost.trim().isEmpty() && proxyPort != 0) {
            // sets default proxy, this value can be overridden by the setRoutePlanner method
            LoggerFactory.getLogger(getClass()).info("set proxy: " + proxyHost + ":" + String.valueOf(proxyPort));
            clientBuilder.setProxy(new HttpHost(proxyHost, proxyPort, HttpHost.DEFAULT_SCHEME_NAME));
        }

        CloseableHttpClient client = clientBuilder.build();
        return new HttpComponentsClientHttpRequestFactory(client);
    }
}
