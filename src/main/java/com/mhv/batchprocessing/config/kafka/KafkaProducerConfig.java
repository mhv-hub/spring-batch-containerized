package com.mhv.batchprocessing.config.kafka;

import com.mhv.batchprocessing.entity.Customer;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.support.serializer.JsonSerializer;

import java.util.HashMap;
import java.util.Map;

@Configuration
@EnableKafka
public class KafkaProducerConfig {

    @Value("${spring.kafka.bootstrap-servers}")
    private String kafkaServer;

    @Bean(name = "customerDataProducerConfigBean")
    public ProducerFactory<String, Customer> customerDataProducerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        config.put(ProducerConfig.PARTITIONER_CLASS_CONFIG, PartitionerConfig.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "customerCtlProducerConfigBean")
    public ProducerFactory<String, String> customerCtlProducerFactory(){
        Map<String, Object> config = new HashMap<>();
        config.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaServer);
        config.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        config.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        return new DefaultKafkaProducerFactory<>(config);
    }

    @Bean(name = "customerDataKafkaTemplateBean")
    public KafkaTemplate<String, Customer> customerDataKafkaTemplate(){
        return new KafkaTemplate<>(customerDataProducerFactory());
    }

    @Bean(name = "customerCtlKafkaTemplateBean")
    public KafkaTemplate<String, String> customerCtlKafkaTemplate(){
        return new KafkaTemplate<>(customerCtlProducerFactory());
    }
}
