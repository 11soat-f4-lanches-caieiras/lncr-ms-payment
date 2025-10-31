package br.com.tp.lncr.payment.datasources.postgres;

import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadopagoQrEntity;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class JpaMercadopagoQrEntityTest {

    @Test
    void defaultConstructorCreatesEmptyEntity() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity();

        assertNotNull(entity);
        assertNull(entity.getMeliId());
        assertNull(entity.getQrData());
        assertNull(entity.getId());
        assertNull(entity.getOrderId());
    }

    @Test
    void builderSetsAllFields() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        JpaMercadopagoQrEntity entity = JpaMercadopagoQrEntity.builder()
                .id(1)
                .orderId(100)
                .status(2)
                .amount(50.0)
                .paymentProvider("mercadopago")
                .paymentMethod("qrcode")
                .externalPaymentId("ext123")
                .created(created)
                .updated(updated)
                .meliId("meli123")
                .qrData("qrdata123")
                .build();

        assertEquals(1, entity.getId());
        assertEquals(100, entity.getOrderId());
        assertEquals(2, entity.getStatusId());
        assertEquals(50.0, entity.getAmount());
        assertEquals("mercadopago", entity.getPaymentProvider());
        assertEquals("qrcode", entity.getPaymentMethod());
        assertEquals("ext123", entity.getExternalPaymentId());
        assertEquals(created, entity.getCreated());
        assertEquals(updated, entity.getUpdated());
        assertEquals("meli123", entity.getMeliId());
        assertEquals("qrdata123", entity.getQrData());
    }

    @Test
    void setMeliIdUpdatesValue() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity();

        entity.setMeliId("newMeliId");

        assertEquals("newMeliId", entity.getMeliId());
    }

    @Test
    void setQrDataUpdatesValue() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity();

        entity.setQrData("newQrData");

        assertEquals("newQrData", entity.getQrData());
    }

    @Test
    void setMeliIdWithNullValue() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity();
        entity.setMeliId("initialValue");

        entity.setMeliId(null);

        assertNull(entity.getMeliId());
    }

    @Test
    void setQrDataWithNullValue() {
        JpaMercadopagoQrEntity entity = new JpaMercadopagoQrEntity();
        entity.setQrData("initialValue");

        entity.setQrData(null);

        assertNull(entity.getQrData());
    }

    @Test
    void builderWithNullValues() {
        JpaMercadopagoQrEntity entity = JpaMercadopagoQrEntity.builder()
                .id(null)
                .orderId(null)
                .status(null)
                .amount(null)
                .paymentProvider(null)
                .paymentMethod(null)
                .externalPaymentId(null)
                .created(null)
                .updated(null)
                .meliId(null)
                .qrData(null)
                .build();

        assertNull(entity.getId());
        assertNull(entity.getOrderId());
        assertNull(entity.getStatusId());
        assertNull(entity.getAmount());
        assertNull(entity.getPaymentProvider());
        assertNull(entity.getPaymentMethod());
        assertNull(entity.getExternalPaymentId());
        assertNull(entity.getCreated());
        assertNull(entity.getUpdated());
        assertNull(entity.getMeliId());
        assertNull(entity.getQrData());
    }
}
