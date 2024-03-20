package com.myrealtrip.ohmyhotel.configuration.kafka;

import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.ContainerProperties;

import java.util.HashMap;
import java.util.Map;

@EnableKafka
@Configuration
public class KafkaConsumerConfiguration {
    @Value("${myrealtrip.kafka.common.bootstrap-servers}")
    private String bootstrapServers;

    @Value("${myrealtrip.kafka.consumer.max-poll-interval-ms}")
    private String maxPollIntervalMs;

    @Bean
    public Map<String, Object> defaultConsumerConfigs() {
        return Map.of(
            ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
            ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
            ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class
        );
    }

    @Bean
    public ConsumerFactory<String, String> defaultConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(defaultConsumerConfigs());
    }

    @Bean(name = "defaultKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> defaultKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(defaultConsumerFactory());

        return factory;
    }

    @Bean
    public Map<String, Object> manualAckConsumerConfigs() {
        Map<String, Object> props = new HashMap<>();

        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers);
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.ENABLE_AUTO_COMMIT_CONFIG, false);

        return props;
    }

    @Bean
    public ConsumerFactory<String, String> manualAckConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(manualAckConsumerConfigs());
    }

    @Bean(name = "manualAckKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> manualAckKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(manualAckConsumerFactory());
        factory.getContainerProperties().setAckMode(ContainerProperties.AckMode.MANUAL_IMMEDIATE);

        return factory;

    }

    @Bean
    public Map<String, Object> batchConsumerConfigs() {
        return Map.of(
                ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, bootstrapServers,
                ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class,
                ConsumerConfig.MAX_POLL_INTERVAL_MS_CONFIG, maxPollIntervalMs,
                ConsumerConfig.MAX_POLL_RECORDS_CONFIG, 500
        );
    }

    @Bean
    public ConsumerFactory<String, String> batchConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(batchConsumerConfigs());
    }

    @Bean(name = "batchKafkaListenerContainerFactory")
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, String>> batchKafkaListenerContainerFactory() {
        ConcurrentKafkaListenerContainerFactory<String, String> factory = new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(batchConsumerFactory());
        factory.setBatchListener(true);

        return factory;
    }
}
