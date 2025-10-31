package br.com.tp.lncr.payment.datasources.postgres;

import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadopagoQrEntity;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

class AbstractJpaPaymentEntityTest {

    private JpaMercadopagoQrEntity entity;

    @BeforeEach
    void setUp() {
        entity = new JpaMercadopagoQrEntity();
    }

    @Test
    void defaultConstructorCreatesEmptyEntity() {
        assertNotNull(entity);
        assertNull(entity.getId());
        assertNull(entity.getOrderId());
        assertNull(entity.getStatusId());
        assertNull(entity.getAmount());
        assertNull(entity.getPaymentProvider());
        assertNull(entity.getPaymentMethod());
        assertNull(entity.getExternalPaymentId());
        assertNull(entity.getCreated());
        assertNull(entity.getUpdated());
    }

    @Test
    void settersAndGettersWorkCorrectly() {
        entity.setId(1);
        entity.setOrderId(100);
        entity.setStatusId(2);
        entity.setAmount(150.50);
        entity.setPaymentProvider("mercadopago");
        entity.setPaymentMethod("qrcode");
        entity.setExternalPaymentId("ext123");

        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();
        entity.setCreated(created);
        entity.setUpdated(updated);

        assertEquals(1, entity.getId());
        assertEquals(100, entity.getOrderId());
        assertEquals(2, entity.getStatusId());
        assertEquals(150.50, entity.getAmount());
        assertEquals("mercadopago", entity.getPaymentProvider());
        assertEquals("qrcode", entity.getPaymentMethod());
        assertEquals("ext123", entity.getExternalPaymentId());
        assertEquals(created, entity.getCreated());
        assertEquals(updated, entity.getUpdated());
    }

    @Test
    void setIdUpdatesValue() {
        entity.setId(5);
        assertEquals(5, entity.getId());
    }

    @Test
    void setOrderIdUpdatesValue() {
        entity.setOrderId(200);
        assertEquals(200, entity.getOrderId());
    }

    @Test
    void setStatusIdUpdatesValue() {
        entity.setStatusId(3);
        assertEquals(3, entity.getStatusId());
    }

    @Test
    void setAmountUpdatesValue() {
        entity.setAmount(99.99);
        assertEquals(99.99, entity.getAmount());
    }

    @Test
    void setPaymentProviderUpdatesValue() {
        entity.setPaymentProvider("paypal");
        assertEquals("paypal", entity.getPaymentProvider());
    }

    @Test
    void setPaymentMethodUpdatesValue() {
        entity.setPaymentMethod("credit_card");
        assertEquals("credit_card", entity.getPaymentMethod());
    }

    @Test
    void setExternalPaymentIdUpdatesValue() {
        entity.setExternalPaymentId("external456");
        assertEquals("external456", entity.getExternalPaymentId());
    }

    @Test
    void setCreatedUpdatesValue() {
        LocalDateTime now = LocalDateTime.now();
        entity.setCreated(now);
        assertEquals(now, entity.getCreated());
    }

    @Test
    void setUpdatedUpdatesValue() {
        LocalDateTime now = LocalDateTime.now();
        entity.setUpdated(now);
        assertEquals(now, entity.getUpdated());
    }

    @Test
    void prePersistSetsCreatedDate() {
        assertNull(entity.getCreated());

        entity.prePersist();

        assertNotNull(entity.getCreated());
        assertTrue(entity.getCreated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void preUpdateSetsUpdatedDate() {
        assertNull(entity.getUpdated());

        entity.preUpdate();

        assertNotNull(entity.getUpdated());
        assertTrue(entity.getUpdated().isBefore(LocalDateTime.now().plusSeconds(1)));
    }

    @Test
    void builderSetsAllBaseFields() {
        LocalDateTime created = LocalDateTime.now();
        LocalDateTime updated = LocalDateTime.now();

        JpaMercadopagoQrEntity builtEntity = JpaMercadopagoQrEntity.builder()
                .id(10)
                .orderId(500)
                .statusId(1)
                .amount(250.75)
                .paymentProvider("stripe")
                .paymentMethod("pix")
                .externalPaymentId("ext789")
                .created(created)
                .updated(updated)
                .build();

        assertEquals(10, builtEntity.getId());
        assertEquals(500, builtEntity.getOrderId());
        assertEquals(1, builtEntity.getStatusId());
        assertEquals(250.75, builtEntity.getAmount());
        assertEquals("stripe", builtEntity.getPaymentProvider());
        assertEquals("pix", builtEntity.getPaymentMethod());
        assertEquals("ext789", builtEntity.getExternalPaymentId());
        assertEquals(created, builtEntity.getCreated());
        assertEquals(updated, builtEntity.getUpdated());
    }

    @Test
    void setIdWithNullValue() {
        entity.setId(1);
        entity.setId(null);
        assertNull(entity.getId());
    }

    @Test
    void setOrderIdWithNullValue() {
        entity.setOrderId(100);
        entity.setOrderId(null);
        assertNull(entity.getOrderId());
    }

    @Test
    void setStatusIdWithNullValue() {
        entity.setStatusId(2);
        entity.setStatusId(null);
        assertNull(entity.getStatusId());
    }

    @Test
    void setAmountWithNullValue() {
        entity.setAmount(100.0);
        entity.setAmount(null);
        assertNull(entity.getAmount());
    }

    @Test
    void setPaymentProviderWithNullValue() {
        entity.setPaymentProvider("provider");
        entity.setPaymentProvider(null);
        assertNull(entity.getPaymentProvider());
    }

    @Test
    void setPaymentMethodWithNullValue() {
        entity.setPaymentMethod("method");
        entity.setPaymentMethod(null);
        assertNull(entity.getPaymentMethod());
    }

    @Test
    void setExternalPaymentIdWithNullValue() {
        entity.setExternalPaymentId("id");
        entity.setExternalPaymentId(null);
        assertNull(entity.getExternalPaymentId());
    }

    @Test
    void setCreatedWithNullValue() {
        entity.setCreated(LocalDateTime.now());
        entity.setCreated(null);
        assertNull(entity.getCreated());
    }

    @Test
    void setUpdatedWithNullValue() {
        entity.setUpdated(LocalDateTime.now());
        entity.setUpdated(null);
        assertNull(entity.getUpdated());
    }

    @Test
    void prePersistDoesNotOverwriteExistingCreatedDate() {
        LocalDateTime originalCreated = LocalDateTime.of(2025, 1, 1, 12, 0);
        entity.setCreated(originalCreated);

        entity.prePersist();

        assertNotEquals(originalCreated, entity.getCreated());
        assertNotNull(entity.getCreated());
    }

    @Test
    void preUpdateDoesNotOverwriteExistingUpdatedDate() {
        LocalDateTime originalUpdated = LocalDateTime.of(2025, 1, 1, 12, 0);
        entity.setUpdated(originalUpdated);

        entity.preUpdate();

        assertNotEquals(originalUpdated, entity.getUpdated());
        assertNotNull(entity.getUpdated());
    }

    @Test
    void builderWithPartialFields() {
        JpaMercadopagoQrEntity partialEntity = JpaMercadopagoQrEntity.builder()
                .orderId(300)
                .amount(75.50)
                .build();

        assertNull(partialEntity.getId());
        assertEquals(300, partialEntity.getOrderId());
        assertEquals(75.50, partialEntity.getAmount());
        assertNull(partialEntity.getStatusId());
        assertNull(partialEntity.getPaymentProvider());
        assertNull(partialEntity.getPaymentMethod());
    }

    @Test
    void setAmountWithZeroValue() {
        entity.setAmount(0.0);
        assertEquals(0.0, entity.getAmount());
    }

    @Test
    void setAmountWithNegativeValue() {
        entity.setAmount(-10.0);
        assertEquals(-10.0, entity.getAmount());
    }

    @Test
    void setIdWithZeroValue() {
        entity.setId(0);
        assertEquals(0, entity.getId());
    }

    @Test
    void setOrderIdWithZeroValue() {
        entity.setOrderId(0);
        assertEquals(0, entity.getOrderId());
    }

    @Test
    void setStatusIdWithZeroValue() {
        entity.setStatusId(0);
        assertEquals(0, entity.getStatusId());
    }

    @Test
    void setPaymentProviderWithEmptyString() {
        entity.setPaymentProvider("");
        assertEquals("", entity.getPaymentProvider());
    }

    @Test
    void setPaymentMethodWithEmptyString() {
        entity.setPaymentMethod("");
        assertEquals("", entity.getPaymentMethod());
    }

    @Test
    void setExternalPaymentIdWithEmptyString() {
        entity.setExternalPaymentId("");
        assertEquals("", entity.getExternalPaymentId());
    }

    @Test
    void multiplePrePersistCallsUpdateCreatedDate() {
        entity.prePersist();
        LocalDateTime firstCreated = entity.getCreated();

        entity.prePersist();
        LocalDateTime secondCreated = entity.getCreated();

        assertNotNull(firstCreated);
        assertNotNull(secondCreated);
        assertTrue(secondCreated.isAfter(firstCreated) || secondCreated.isEqual(firstCreated));
    }

    @Test
    void multiplePreUpdateCallsUpdateUpdatedDate() {
        entity.preUpdate();
        LocalDateTime firstUpdated = entity.getUpdated();

        entity.preUpdate();
        LocalDateTime secondUpdated = entity.getUpdated();

        assertNotNull(firstUpdated);
        assertNotNull(secondUpdated);
        assertTrue(secondUpdated.isAfter(firstUpdated) || secondUpdated.isEqual(firstUpdated));
    }
}

