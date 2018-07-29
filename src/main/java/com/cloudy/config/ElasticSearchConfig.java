package com.cloudy.config;

import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;
import org.elasticsearch.transport.client.PreBuiltTransportClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.net.InetAddress;
import java.net.UnknownHostException;

/**
 * Created by ljy_cloudy on 2018/7/28.
 */
@Configuration
public class ElasticSearchConfig {

    @Value("${elasticsearch.url}")
    private String address;

    @Bean
    public TransportClient transportClient() throws UnknownHostException {
        Settings settings = Settings.builder()
                .put("cluster.name", "elasticsearch")
                .put("client.transport.sniff", true)
                .build();

        InetSocketTransportAddress master = new InetSocketTransportAddress(InetAddress.getByName(address), 9300);
        TransportClient client = new PreBuiltTransportClient(settings).addTransportAddress(master);
        return client;
    }

}
