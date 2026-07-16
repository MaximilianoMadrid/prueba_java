package ms_orders.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import ms_orders.entity.Order;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Map;

@Service
public class OrderNotifierService {

    private final SqsClient sqsClient;
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Value("${aws.sqs.queue-name}")
    private String queueName;

    public OrderNotifierService(SqsClient sqsClient) {
        this.sqsClient = sqsClient;
    }

    public void notifyOrderCreated(Order order) {
        try {
            String queueUrl = sqsClient.getQueueUrl(
                    GetQueueUrlRequest.builder().queueName(queueName).build()
            ).queueUrl();

            Map<String, Object> payload = Map.of(
                    "orderId", order.getId(),
                    "email", order.getEmail(),
                    "nombreProducto", order.getNombreProducto(),
                    "cantidad", order.getCantidad(),
                    "estado", order.getEstado().toString(),
                    "canal", order.getCanal(),
                    "event", "ORDER_CREATED"
            );

            sqsClient.sendMessage(SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(objectMapper.writeValueAsString(payload))
                    .build());

        } catch (Exception e) {
            // No debe tumbar la creación del pedido si SQS falla momentáneamente
            System.err.println("No se pudo notificar a SQS: " + e.getMessage());
        }
    }
}