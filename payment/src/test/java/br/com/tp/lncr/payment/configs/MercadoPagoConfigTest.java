package br.com.tp.lncr.payment.configs;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MercadoPagoConfigTest {

    private MercadoPagoConfig config;

    @BeforeEach
    void setUp() {
        config = new MercadoPagoConfig();
    }

    @Test
    void defaultConstructorCreatesEmptyConfig() {
        assertNotNull(config);
        assertNull(config.getLocationPrefix());
        assertNull(config.getoAuthUrl());
        assertNull(config.getOrdersUrl());
        assertNull(config.getClientId());
        assertNull(config.getSecretId());
        assertNull(config.getPosId());
        assertNull(config.getExpirationTime());
    }

    @Test
    void setLocationPrefixUpdatesValue() {
        config.setLocationPrefix("https://api.mercadopago.com");
        assertEquals("https://api.mercadopago.com", config.getLocationPrefix());
    }

    @Test
    void setoAuthUrlUpdatesValue() {
        config.setoAuthUrl("https://oauth.mercadopago.com");
        assertEquals("https://oauth.mercadopago.com", config.getoAuthUrl());
    }

    @Test
    void setOrdersUrlUpdatesValue() {
        config.setOrdersUrl("https://api.mercadopago.com/orders");
        assertEquals("https://api.mercadopago.com/orders", config.getOrdersUrl());
    }

    @Test
    void setClientIdUpdatesValue() {
        config.setClientId("client123");
        assertEquals("client123", config.getClientId());
    }

    @Test
    void setSecretIdUpdatesValue() {
        config.setSecretId("secret456");
        assertEquals("secret456", config.getSecretId());
    }

    @Test
    void setPosIdUpdatesValue() {
        config.setPosId("pos789");
        assertEquals("pos789", config.getPosId());
    }

    @Test
    void setExpirationTimeUpdatesValue() {
        config.setExpirationTime("3600");
        assertEquals("3600", config.getExpirationTime());
    }

    @Test
    void setLocationPrefixWithNullValue() {
        config.setLocationPrefix("initial");
        config.setLocationPrefix(null);
        assertNull(config.getLocationPrefix());
    }

    @Test
    void setoAuthUrlWithNullValue() {
        config.setoAuthUrl("initial");
        config.setoAuthUrl(null);
        assertNull(config.getoAuthUrl());
    }

    @Test
    void setOrdersUrlWithNullValue() {
        config.setOrdersUrl("initial");
        config.setOrdersUrl(null);
        assertNull(config.getOrdersUrl());
    }

    @Test
    void setClientIdWithNullValue() {
        config.setClientId("initial");
        config.setClientId(null);
        assertNull(config.getClientId());
    }

    @Test
    void setSecretIdWithNullValue() {
        config.setSecretId("initial");
        config.setSecretId(null);
        assertNull(config.getSecretId());
    }

    @Test
    void setPosIdWithNullValue() {
        config.setPosId("initial");
        config.setPosId(null);
        assertNull(config.getPosId());
    }

    @Test
    void setExpirationTimeWithNullValue() {
        config.setExpirationTime("initial");
        config.setExpirationTime(null);
        assertNull(config.getExpirationTime());
    }

    @Test
    void setLocationPrefixWithEmptyString() {
        config.setLocationPrefix("");
        assertEquals("", config.getLocationPrefix());
    }

    @Test
    void setoAuthUrlWithEmptyString() {
        config.setoAuthUrl("");
        assertEquals("", config.getoAuthUrl());
    }

    @Test
    void setOrdersUrlWithEmptyString() {
        config.setOrdersUrl("");
        assertEquals("", config.getOrdersUrl());
    }

    @Test
    void setClientIdWithEmptyString() {
        config.setClientId("");
        assertEquals("", config.getClientId());
    }

    @Test
    void setSecretIdWithEmptyString() {
        config.setSecretId("");
        assertEquals("", config.getSecretId());
    }

    @Test
    void setPosIdWithEmptyString() {
        config.setPosId("");
        assertEquals("", config.getPosId());
    }

    @Test
    void setExpirationTimeWithEmptyString() {
        config.setExpirationTime("");
        assertEquals("", config.getExpirationTime());
    }

    @Test
    void paymentMercadopagoQRMapperBeanCreation() {
        assertNotNull(config.paymentMercadopagoQRMapper());
    }

    @Test
    void jpaPaymentMercadopagoQRMapperBeanCreation() {
        assertNotNull(config.jpaPaymentMercadopagoQRMapper());
    }

    @Test
    void setAllPropertiesAndVerifyValues() {
        config.setLocationPrefix("https://location.com");
        config.setoAuthUrl("https://oauth.com");
        config.setOrdersUrl("https://orders.com");
        config.setClientId("client123");
        config.setSecretId("secret456");
        config.setPosId("pos789");
        config.setExpirationTime("7200");

        assertEquals("https://location.com", config.getLocationPrefix());
        assertEquals("https://oauth.com", config.getoAuthUrl());
        assertEquals("https://orders.com", config.getOrdersUrl());
        assertEquals("client123", config.getClientId());
        assertEquals("secret456", config.getSecretId());
        assertEquals("pos789", config.getPosId());
        assertEquals("7200", config.getExpirationTime());
    }

    @Test
    void multipleSettersOnSameProperty() {
        config.setClientId("first");
        assertEquals("first", config.getClientId());

        config.setClientId("second");
        assertEquals("second", config.getClientId());

        config.setClientId("third");
        assertEquals("third", config.getClientId());
    }

    @Test
    void setExpirationTimeWithNumericString() {
        config.setExpirationTime("12345");
        assertEquals("12345", config.getExpirationTime());
    }

    @Test
    void setExpirationTimeWithNonNumericString() {
        config.setExpirationTime("not-a-number");
        assertEquals("not-a-number", config.getExpirationTime());
    }
}

