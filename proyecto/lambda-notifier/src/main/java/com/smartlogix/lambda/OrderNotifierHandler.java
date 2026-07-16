package com.smartlogix.lambda;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.SQSEvent;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

public class OrderNotifierHandler implements RequestHandler<SQSEvent, String> {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public String handleRequest(SQSEvent event, Context context) {
        int procesados = 0;

        for (SQSEvent.SQSMessage message : event.getRecords()) {
            try {
                JsonNode body = objectMapper.readTree(message.getBody());

                Long orderId = body.get("orderId").asLong();
                String email = body.get("email").asText();
                String producto = body.get("nombreProducto").asText();
                String estado = body.get("estado").asText();

                context.getLogger().log(
                    String.format(
                        "[SmartLogix] Nuevo pedido #%d | Cliente: %s | Producto: %s | Estado: %s%n"
                        + "  -> Simulando envio de correo de confirmacion al cliente...%n",
                        orderId, email, producto, estado
                    )
                );

                procesados++;
            } catch (Exception e) {
                context.getLogger().log("Error procesando mensaje: " + e.getMessage());
            }
        }

        return "Procesados " + procesados + " mensaje(s)";
    }
}