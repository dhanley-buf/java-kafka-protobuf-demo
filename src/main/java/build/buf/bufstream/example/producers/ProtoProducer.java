package build.buf.bufstream.example.producers;

import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializer;
import io.confluent.kafka.serializers.protobuf.KafkaProtobufSerializerConfig;
import org.apache.kafka.clients.producer.*;
import org.apache.kafka.common.serialization.StringSerializer;
import java.util.Properties;
import java.util.logging.Logger;
import java.util.logging.Level;
import java.util.Random;
import java.util.List;
import java.util.Arrays;

import dev.buf.bufdemo.gen.bufstream.example.invoice.v1.*;

public class ProtoProducer {
    private static final Logger logger = Logger.getLogger(ProtoProducer.class.getName());
    private static volatile boolean running = true;
    
    private static final List<String> CUSTOMER_POOL = Arrays.asList(
        "CUST001", "CUST002", "CUST003", "CUST004", "CUST005", "CUST006", "CUST007", "CUST008", "CUST009", "CUST010",
        "CUST011", "CUST012", "CUST013", "CUST014", "CUST015", "CUST016", "CUST017", "CUST018", "CUST019", "CUST020",
        "CUST021", "CUST022", "CUST023", "CUST024", "CUST025", "CUST026", "CUST027", "CUST028", "CUST029", "CUST030",
        "CUST031", "CUST032", "CUST033", "CUST034", "CUST035", "CUST036", "CUST037", "CUST038", "CUST039", "CUST040",
        "CUST041", "CUST042", "CUST043", "CUST044", "CUST045", "CUST046", "CUST047", "CUST048", "CUST049", "CUST050"
    );
    
    private static final List<LineItemTemplate> LINE_ITEM_POOL = Arrays.asList(
        new LineItemTemplate("L001", "P001"),
        new LineItemTemplate("L002", "P002"),
        new LineItemTemplate("L003", "P003"),
        new LineItemTemplate("L004", "P004"),
        new LineItemTemplate("L005", "P005"),
        new LineItemTemplate("L006", "P006"),
        new LineItemTemplate("L007", "P007"),
        new LineItemTemplate("L008", "P008"),
        new LineItemTemplate("L009", "P009"),
        new LineItemTemplate("L010", "P010"),
        new LineItemTemplate("L011", "P011"),
        new LineItemTemplate("L012", "P012"),
        new LineItemTemplate("L013", "P013"),
        new LineItemTemplate("L014", "P014"),
        new LineItemTemplate("L015", "P015")
    );
    
    private static class LineItemTemplate {
        final String lineItemId;
        final String productId;
        
        LineItemTemplate(String lineItemId, String productId) {
            this.lineItemId = lineItemId;
            this.productId = productId;
        }
    }
    
    private static Invoice generateInvoice(int invoiceNumber, Random random) {
        String customerId = CUSTOMER_POOL.get(random.nextInt(CUSTOMER_POOL.size()));
        
        int numLineItems = random.nextInt(3) + 1;
        Invoice.Builder invoiceBuilder = Invoice.newBuilder()
                .setInvoiceId(String.format("INV%06d", invoiceNumber))
                .setCustomerId(customerId);
        
        for (int i = 0; i < numLineItems; i++) {
            LineItemTemplate template = LINE_ITEM_POOL.get(random.nextInt(LINE_ITEM_POOL.size()));
            invoiceBuilder.addLineItems(LineItem.newBuilder()
                    .setLineItemId(template.lineItemId)
                    .setProductId(template.productId)
            );
        }
        
        return invoiceBuilder.build();
    }

    public static void main(String[] args) {
        Properties props = new Properties();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class.getName());
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, KafkaProtobufSerializer.class.getName());
        props.put(KafkaProtobufSerializerConfig.AUTO_REGISTER_SCHEMAS, false);
        props.put(KafkaProtobufSerializerConfig.SCHEMA_REGISTRY_URL_CONFIG, "https://bufdemo.buf.dev/integrations/confluent/bufstream-examples");
        props.put(KafkaProtobufSerializerConfig.BASIC_AUTH_CREDENTIALS_SOURCE, "USER_INFO");
        props.put(KafkaProtobufSerializerConfig.USER_INFO_CONFIG, "username:password");
        
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Shutdown requested");
            running = false;
        }));

        String key = "key";
        int invoiceNumber = 1;
        Random random = new Random();
        
        try (Producer<String, Invoice> protoProducer = new KafkaProducer<>(props)) {
            while (running) {
                Invoice invoice = generateInvoice(invoiceNumber, random);
                
                protoProducer.send(new ProducerRecord<>("invoice", key + invoiceNumber, invoice));
                logger.info("Sent invoice record: " + invoiceNumber++);
                
                Thread.sleep(1000);
            }
        } catch (InterruptedException e) {
            logger.log(Level.WARNING, "Producer interrupted", e);
            Thread.currentThread().interrupt();
        } catch (Exception e) {
            logger.log(Level.SEVERE, "Proto producer error", e);
        }
    }
}