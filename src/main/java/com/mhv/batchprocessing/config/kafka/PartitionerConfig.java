package com.mhv.batchprocessing.config.kafka;

import org.apache.kafka.clients.producer.Partitioner;
import org.apache.kafka.common.Cluster;
import org.springframework.context.annotation.Configuration;

import java.io.IOException;
import java.util.Map;
import java.util.Properties;

@Configuration
public class PartitionerConfig implements Partitioner {

    private static final Properties properties = new Properties();

    public PartitionerConfig(){
        try{
            properties.load(this.getClass().getClassLoader().getResourceAsStream("classpath:kafka.properties"));
        } catch (IOException e) {
            e.printStackTrace();
            throw new RuntimeException("Kafka properties file missing");
        }
    }

    @Override
    public int partition(String topic, Object key, byte[] keyBytes, Object value, byte[] valueBytes, Cluster cluster) {
        int partitionCount = Integer.parseInt(properties.getProperty("kafka.customer.topic.partition.count"));
        int keyNumber = Integer.parseInt((String)key);
        return Math.abs(keyNumber % partitionCount);
    }

    @Override
    public void close() {
    }

    @Override
    public void configure(Map<String, ?> map) {
    }
}
