package org.dromara.search.config;

import org.apache.http.HttpHost;
import org.apache.http.auth.AuthScope;
import org.apache.http.auth.UsernamePasswordCredentials;
import org.apache.http.client.CredentialsProvider;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.elasticsearch.client.RestClient;
import org.elasticsearch.client.RestClientBuilder;
import org.elasticsearch.client.RestHighLevelClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ElasticsearchConfig {

    @Value("${spring.elasticsearch.rest.uris}")
    private String uris;

    @Value("${spring.elasticsearch.rest.username}")
    private String username;

    @Value("${spring.elasticsearch.rest.password}")
    private String password;

    @Value("${spring.elasticsearch.rest.connection-timeout:5000}")
    private int connectTimeout;

    @Value("${spring.elasticsearch.rest.read-timeout:5000}")
    private int socketTimeout;

    @Value("${spring.elasticsearch.rest.max-conn-total:30}")
    private int maxConnTotal;

    @Value("${spring.elasticsearch.rest.max-conn-per-route:10}")
    private int maxConnPerRoute;

    @Bean
    public RestHighLevelClient elasticsearchClient() {
        RestClientBuilder builder = RestClient.builder(HttpHost.create(uris))
            .setRequestConfigCallback(requestConfigBuilder -> 
                requestConfigBuilder
                    .setConnectTimeout(connectTimeout)
                    .setSocketTimeout(socketTimeout))
            .setHttpClientConfigCallback(httpClientBuilder -> {
                // Set credentials if username and password are provided
                if (username != null && !username.isEmpty()) {
                    CredentialsProvider credentialsProvider = new BasicCredentialsProvider();
                    credentialsProvider.setCredentials(AuthScope.ANY,
                        new UsernamePasswordCredentials(username, password));
                    httpClientBuilder.setDefaultCredentialsProvider(credentialsProvider);
                }
                
                return httpClientBuilder
                    .setMaxConnTotal(maxConnTotal)
                    .setMaxConnPerRoute(maxConnPerRoute);
            });

        return new RestHighLevelClient(builder);
    }
} 