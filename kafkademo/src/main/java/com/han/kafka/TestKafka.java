package com.han.kafka;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.clients.producer.KafkaProducer;
import org.apache.kafka.clients.producer.ProducerRecord;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Arrays;
import java.util.Properties;

public class TestKafka {
    @org.junit.Test
    public void producer() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(TestKafka.class.getResource("/producer.properties").getPath()));

        KafkaProducer<String,String> kafkaProducer =new KafkaProducer<String,String>(properties);
        ProducerRecord<String,String> record = new ProducerRecord<String, String>("person","hanjunyu");
        kafkaProducer.send(record);

        kafkaProducer.close();
    }

    @org.junit.Test
    public void conuser() throws IOException {
        Properties properties = new Properties();
        properties.load(new FileInputStream(TestKafka.class.getResource("/consumer.properties").getPath()));

        KafkaConsumer<String,String> kafkaConsumer =new KafkaConsumer<String,String>(properties);
        kafkaConsumer.subscribe(Arrays.asList("person"));
        while (true){
            ConsumerRecords<String,String> consumerRecord =  kafkaConsumer.poll(10000);
            for(ConsumerRecord record :consumerRecord){
                System.out.println(record.value());
            }
        }

    }
}
