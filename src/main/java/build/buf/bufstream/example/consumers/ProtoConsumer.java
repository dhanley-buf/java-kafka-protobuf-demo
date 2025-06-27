package build.buf.bufstream.example.consumers;

import com.google.protobuf.DynamicMessage;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufDeserializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import org.apache.kafka.clients.consumer.Consumer;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.clients.consumer.KafkaConsumer;
import org.apache.kafka.common.serialization.StringDeserializer;

import java.time.Duration;
import java.util.Collections;
import java.util.Properties;
import java.util.logging.Logger;

public class ProtoConsumer {
    private static final Logger logger = Logger.getLogger(ProtoConsumer.class.getName());

    public static void main(String[] args) {
            Properties props = new Properties();
            props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
            props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class.getName());
            props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, KafkaProtobufDeserializer.class.getName());
            props.put(ConsumerConfig.GROUP_ID_CONFIG, "demo");
            props.put(ConsumerConfig.AUTO_OFFSET_RESET_CONFIG, "earliest");
            props.put(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "https://bufdemo.buf.dev/integrations/confluent/bufstream-examples");
            props.put(KafkaProtobufSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
            //user info config takes the form 'user:token' - read from environment variable
            String userInfo = System.getenv("USER_INFO_CONFIG");
            if (userInfo == null || userInfo.isEmpty()) {
                logger.severe("USER_INFO_CONFIG environment variable is required but not set");
                System.exit(1);
            }
            props.put(KafkaProtobufSerializerConfig.USER_INFO_CONFIG, userInfo);

            try (Consumer<String, DynamicMessage> consumer = new KafkaConsumer<>(props)) {
                String topic = "invoice";
                consumer.subscribe(Collections.singletonList(topic));
                while (true) {
                    ConsumerRecords<String, DynamicMessage> records = consumer.poll(Duration.ofMillis(1000));
                    for (ConsumerRecord<String, DynamicMessage> record : records) {
                        System.out.printf("Consumed event from topic %s: key %s -> value %s%n", topic, record.key(), record.value());
                    }
                }
            }
        }
    }

