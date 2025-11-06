package br.com.tp.lncr.payment.webhooks.mercadopago;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MercadoPagoCallbackDTOTest {

    private final ObjectMapper objectMapper = new ObjectMapper();

    @Test
    void testCreateCallbackDTO() {
        // Arrange & Act
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-123",
            "mp-456",
            "processed",
            "accredited",
            "100.00",
            "100.00",
            null,
            "order",
            1
        );

        MercadoPagoCallbackDTO callbackDTO = new MercadoPagoCallbackDTO(
            "payment.updated",
            "v1",
            "app-123",
            data,
            "2025-01-01T00:00:00Z",
            true,
            "payment",
            "user-123"
        );

        // Assert
        assertNotNull(callbackDTO);
        assertEquals("payment.updated", callbackDTO.action());
        assertEquals("v1", callbackDTO.apiVersion());
        assertEquals("app-123", callbackDTO.applicationId());
        assertEquals("payment", callbackDTO.type());
        assertTrue(callbackDTO.liveMode());
        assertNotNull(callbackDTO.data());
        assertEquals("ext-ref-123", callbackDTO.data().externalReference());
        assertEquals("mp-456", callbackDTO.data().id());
    }

    @Test
    void testDataRecord() {
        // Arrange & Act
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "order-789",
            "payment-123",
            "approved",
            "accredited",
            "250.50",
            "250.50",
            null,
            "merchant_order",
            2
        );

        // Assert
        assertNotNull(data);
        assertEquals("order-789", data.externalReference());
        assertEquals("payment-123", data.id());
        assertEquals("approved", data.status());
        assertEquals("accredited", data.statusDetail());
        assertEquals("250.50", data.totalAmount());
        assertEquals("250.50", data.totalPaidAmount());
        assertEquals("merchant_order", data.type());
        assertEquals(2, data.version());
    }

    @Test
    void testDataRecordWithNullExternalReference() {
        // Arrange & Act
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            null,
            "payment-456",
            "pending",
            "pending_waiting_payment",
            "150.00",
            "0.00",
            null,
            "payment",
            1
        );

        // Assert
        assertNotNull(data);
        assertNull(data.externalReference());
        assertEquals("payment-456", data.id());
        assertEquals("pending", data.status());
    }

    @Test
    void testJsonSerialization() throws Exception {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-123",
            "mp-456",
            "processed",
            "accredited",
            "100.00",
            "100.00",
            null,
            "order",
            1
        );

        MercadoPagoCallbackDTO callbackDTO = new MercadoPagoCallbackDTO(
            "payment.updated",
            "v1",
            "app-123",
            data,
            "2025-01-01T00:00:00Z",
            true,
            "payment",
            "user-123"
        );

        // Act
        String json = objectMapper.writeValueAsString(callbackDTO);

        // Assert
        assertNotNull(json);
        assertTrue(json.contains("payment.updated"));
        assertTrue(json.contains("ext-ref-123"));
        assertTrue(json.contains("api_version"));
        assertTrue(json.contains("external_reference"));
    }

    @Test
    void testJsonDeserialization() throws Exception {
        // Arrange
        String json = """
            {
                "action": "payment.updated",
                "api_version": "v1",
                "application_id": "app-123",
                "data": {
                    "external_reference": "ext-ref-123",
                    "id": "mp-456",
                    "status": "processed",
                    "status_detail": "accredited",
                    "total_amount": "100.00",
                    "total_paid_amount": "100.00",
                    "transactions": null,
                    "type": "order",
                    "version": 1
                },
                "date_created": "2025-01-01T00:00:00Z",
                "live_mode": true,
                "type": "payment",
                "user_id": "user-123"
            }
            """;

        // Act
        MercadoPagoCallbackDTO callbackDTO = objectMapper.readValue(json, MercadoPagoCallbackDTO.class);

        // Assert
        assertNotNull(callbackDTO);
        assertEquals("payment.updated", callbackDTO.action());
        assertEquals("v1", callbackDTO.apiVersion());
        assertEquals("ext-ref-123", callbackDTO.data().externalReference());
        assertEquals("mp-456", callbackDTO.data().id());
        assertTrue(callbackDTO.liveMode());
    }

    @Test
    void testRecordEquality() {
        // Arrange
        MercadoPagoCallbackDTO.Data data1 = new MercadoPagoCallbackDTO.Data(
            "ref-1", "id-1", "status-1", "detail-1", "100", "100", null, "type-1", 1
        );
        MercadoPagoCallbackDTO.Data data2 = new MercadoPagoCallbackDTO.Data(
            "ref-1", "id-1", "status-1", "detail-1", "100", "100", null, "type-1", 1
        );

        // Assert
        assertEquals(data1, data2);
        assertEquals(data1.hashCode(), data2.hashCode());
    }
}

