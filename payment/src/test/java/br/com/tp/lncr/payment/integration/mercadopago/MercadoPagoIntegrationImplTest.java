package br.com.tp.lncr.payment.integration.mercadopago;

import br.com.tp.lncr.commons.integrations.IntegrationException;
import br.com.tp.lncr.commons.utils.IntegrationUtil;
import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.http.HttpHeaders;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MercadoPagoIntegrationImplTest {

    @Mock
    private MercadoPagoConfig mercadoPagoConfig;

    private MercadoPagoIntegrationImpl mercadoPagoIntegration;

    private static final String ACCESS_TOKEN = "test-access-token-12345";
    private static final String ORDERS_URL = "https://api.mercadopago.com/orders";
    private static final String OAUTH_URL = "https://api.mercadopago.com/oauth/token";
    private static final String CLIENT_ID = "client-id-12345";
    private static final String SECRET_ID = "secret-id-12345";
    private static final String POS_ID = "pos-id-12345";
    private static final String EXPIRATION_TIME = "3600";

    @BeforeEach
    void setUp() {
        mercadoPagoIntegration = new MercadoPagoIntegrationImpl(mercadoPagoConfig);

        when(mercadoPagoConfig.getOrdersUrl()).thenReturn(ORDERS_URL);
        when(mercadoPagoConfig.getoAuthUrl()).thenReturn(OAUTH_URL);
        when(mercadoPagoConfig.getClientId()).thenReturn(CLIENT_ID);
        when(mercadoPagoConfig.getSecretId()).thenReturn(SECRET_ID);
        when(mercadoPagoConfig.getPosId()).thenReturn(POS_ID);
        when(mercadoPagoConfig.getExpirationTime()).thenReturn(EXPIRATION_TIME);
    }

    @Test
    void getAccessTokenSuccess() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\",\"token_type\":\"Bearer\"}";

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            String result = mercadoPagoIntegration.getAccessToken();

            assertEquals(ACCESS_TOKEN, result);
            verify(mercadoPagoConfig).getoAuthUrl();
            verify(mercadoPagoConfig).getClientId();
            verify(mercadoPagoConfig).getSecretId();
        }
    }

    @Test
    void getAccessTokenWithNullResponse() {
        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(null);

            IntegrationException exception = assertThrows(IntegrationException.class,
                () -> mercadoPagoIntegration.getAccessToken());

            assertEquals("Erro ao obter token de acesso", exception.getMessage());
            assertEquals(500, exception.getCode());
        }
    }

    @Test
    void getAccessTokenWithInvalidJson() {
        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn("invalid json");

            IntegrationException exception = assertThrows(IntegrationException.class,
                () -> mercadoPagoIntegration.getAccessToken());

            assertTrue(exception.getMessage().contains("Erro ao extrair access_token do Mercado Pago"));
            assertEquals(500, exception.getCode());
        }
    }

    @Test
    void createOrderSuccess() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String orderResponse = "{\"id\":\"meli-123\",\"type_response\":{\"qr_data\":\"qr-data-test\"}}";

        PaymentMercadopagoQrDTO inputDTO = new PaymentMercadopagoQrDTO();
        inputDTO.setOrderId(100);
        inputDTO.setAmount(150.50);

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL),
                anyString(),
                any(HttpHeaders.class)
            )).thenReturn(orderResponse);

            PaymentMercadopagoQrDTO result = mercadoPagoIntegration.createOrder(inputDTO);

            assertNotNull(result);
            assertEquals("meli-123", result.getMeliId());
            assertEquals("qr-data-test", result.getQrData());
            assertEquals(100, result.getOrderId());
            assertEquals(150.50, result.getAmount());
        }
    }

    @Test
    void createOrderWithNullResponse() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";

        PaymentMercadopagoQrDTO inputDTO = new PaymentMercadopagoQrDTO();
        inputDTO.setOrderId(100);
        inputDTO.setAmount(150.50);

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL),
                anyString(),
                any(HttpHeaders.class)
            )).thenReturn(null);

            IntegrationException exception = assertThrows(IntegrationException.class,
                () -> mercadoPagoIntegration.createOrder(inputDTO));

            assertEquals("Erro ao criar pedido no Mercado Pago", exception.getMessage());
            assertEquals(500, exception.getCode());
        }
    }

    @Test
    void createOrderWithEmptyResponse() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";

        PaymentMercadopagoQrDTO inputDTO = new PaymentMercadopagoQrDTO();
        inputDTO.setOrderId(100);
        inputDTO.setAmount(150.50);

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL),
                anyString(),
                any(HttpHeaders.class)
            )).thenReturn("");

            IntegrationException exception = assertThrows(IntegrationException.class,
                () -> mercadoPagoIntegration.createOrder(inputDTO));

            assertEquals("Erro ao criar pedido no Mercado Pago", exception.getMessage());
            assertEquals(500, exception.getCode());
        }
    }

    @Test
    void createOrderWithInvalidJsonResponse() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";

        PaymentMercadopagoQrDTO inputDTO = new PaymentMercadopagoQrDTO();
        inputDTO.setOrderId(100);
        inputDTO.setAmount(150.50);

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL),
                anyString(),
                any(HttpHeaders.class)
            )).thenReturn("invalid json response");

            IntegrationException exception = assertThrows(IntegrationException.class,
                () -> mercadoPagoIntegration.createOrder(inputDTO));

            assertTrue(exception.getMessage().contains("Erro ao extrair valores de pagamento no Mercado Pago"));
            assertEquals(500, exception.getCode());
        }
    }

    @Test
    void cancelOrderSuccess() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String cancelResponse = "{\"status\":\"cancelled\"}";
        String meliId = "meli-12345";

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL + "/" + meliId + "/cancel"),
                isNull(),
                any(HttpHeaders.class)
            )).thenReturn(cancelResponse);

            assertDoesNotThrow(() -> mercadoPagoIntegration.cancelOrder(meliId));

            verify(mercadoPagoConfig, atLeastOnce()).getOrdersUrl();
        }
    }

    @Test
    void cancelOrderWithException() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String meliId = "meli-12345";

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL + "/" + meliId + "/cancel"),
                isNull(),
                any(HttpHeaders.class)
            )).thenThrow(new RuntimeException("Connection error"));

            IntegrationException exception = assertThrows(IntegrationException.class,
                () -> mercadoPagoIntegration.cancelOrder(meliId));

            assertEquals("Erro ao cancelar ordem no Mercado Pago", exception.getMessage());
            assertEquals(500, exception.getCode());
        }
    }

    @Test
    void refundOrderSuccess() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String refundResponse = "{\"status\":\"refunded\"}";
        String meliId = "meli-67890";

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL + "/" + meliId + "/refund"),
                isNull(),
                any(HttpHeaders.class)
            )).thenReturn(refundResponse);

            assertDoesNotThrow(() -> mercadoPagoIntegration.refundOrder(meliId));

            verify(mercadoPagoConfig, atLeastOnce()).getOrdersUrl();
        }
    }

    @Test
    void refundOrderWithException() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String meliId = "meli-67890";

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL + "/" + meliId + "/refund"),
                isNull(),
                any(HttpHeaders.class)
            )).thenThrow(new RuntimeException("Network error"));

            IntegrationException exception = assertThrows(IntegrationException.class,
                () -> mercadoPagoIntegration.refundOrder(meliId));

            assertEquals("Erro ao solicitar estorno no Mercado Pago", exception.getMessage());
            assertEquals(500, exception.getCode());
        }
    }

    @Test
    void createOrderVerifyRequestBodyFormat() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String orderResponse = "{\"id\":\"meli-123\",\"type_response\":{\"qr_data\":\"qr-data-test\"}}";

        PaymentMercadopagoQrDTO inputDTO = new PaymentMercadopagoQrDTO();
        inputDTO.setOrderId(999);
        inputDTO.setAmount(250.75);

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            ArgumentCaptor<String> requestBodyCaptor = ArgumentCaptor.forClass(String.class);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL),
                requestBodyCaptor.capture(),
                any(HttpHeaders.class)
            )).thenReturn(orderResponse);

            mercadoPagoIntegration.createOrder(inputDTO);

            String requestBody = requestBodyCaptor.getValue();
            assertTrue(requestBody.contains("\"external_reference\": \"999\""));
            assertTrue(requestBody.contains("\"amount\": \"250.75\""));
            assertTrue(requestBody.contains("\"type\": \"qr\""));
            assertTrue(requestBody.contains("\"expiration_time\": \"" + EXPIRATION_TIME + "\""));
            assertTrue(requestBody.contains("\"external_pos_id\": \"" + POS_ID + "\""));
        }
    }

    @Test
    void getAccessTokenVerifyRequestBodyFormat() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            ArgumentCaptor<String> requestBodyCaptor = ArgumentCaptor.forClass(String.class);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                requestBodyCaptor.capture(),
                isNull()
            )).thenReturn(tokenResponse);

            mercadoPagoIntegration.getAccessToken();

            String requestBody = requestBodyCaptor.getValue();
            assertTrue(requestBody.contains("\"client_id\": \"" + CLIENT_ID + "\""));
            assertTrue(requestBody.contains("\"client_secret\": \"" + SECRET_ID + "\""));
            assertTrue(requestBody.contains("\"grant_type\": \"client_credentials\""));
        }
    }

    @Test
    void createOrderVerifyHeadersContainAuthorization() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String orderResponse = "{\"id\":\"meli-123\",\"type_response\":{\"qr_data\":\"qr-data-test\"}}";

        PaymentMercadopagoQrDTO inputDTO = new PaymentMercadopagoQrDTO();
        inputDTO.setOrderId(100);
        inputDTO.setAmount(150.50);

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            ArgumentCaptor<HttpHeaders> headersCaptor = ArgumentCaptor.forClass(HttpHeaders.class);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL),
                anyString(),
                headersCaptor.capture()
            )).thenReturn(orderResponse);

            mercadoPagoIntegration.createOrder(inputDTO);

            HttpHeaders headers = headersCaptor.getValue();
            assertNotNull(headers);
            assertTrue(headers.containsKey("Authorization"));
            assertTrue(headers.getFirst("Authorization").contains("Bearer"));
            assertTrue(headers.containsKey("Content-Type"));
            assertEquals("application/json", headers.getFirst("Content-Type"));
            assertTrue(headers.containsKey("X-Idempotency-Key"));
            assertNotNull(headers.getFirst("X-Idempotency-Key"));
        }
    }

    @Test
    void constructorShouldSetMercadoPagoConfig() {
        assertNotNull(mercadoPagoIntegration.mercadoPagoConfig);
        assertEquals(mercadoPagoConfig, mercadoPagoIntegration.mercadoPagoConfig);
    }

    @Test
    void createOrderWithDifferentAmounts() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String orderResponse = "{\"id\":\"meli-123\",\"type_response\":{\"qr_data\":\"qr-data-test\"}}";

        PaymentMercadopagoQrDTO inputDTO = new PaymentMercadopagoQrDTO();
        inputDTO.setOrderId(100);
        inputDTO.setAmount(0.01);

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL),
                anyString(),
                any(HttpHeaders.class)
            )).thenReturn(orderResponse);

            PaymentMercadopagoQrDTO result = mercadoPagoIntegration.createOrder(inputDTO);

            assertNotNull(result);
            assertEquals(0.01, result.getAmount());
        }
    }

    @Test
    void cancelOrderWithDifferentMeliIds() {
        String tokenResponse = "{\"access_token\":\"" + ACCESS_TOKEN + "\"}";
        String cancelResponse = "{\"status\":\"cancelled\"}";
        String meliId = "meli-xyz-999";

        try (MockedStatic<IntegrationUtil> mockedUtil = mockStatic(IntegrationUtil.class)) {
            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(OAUTH_URL),
                anyString(),
                isNull()
            )).thenReturn(tokenResponse);

            mockedUtil.when(() -> IntegrationUtil.postForObjectWithReturn(
                eq(ORDERS_URL + "/" + meliId + "/cancel"),
                isNull(),
                any(HttpHeaders.class)
            )).thenReturn(cancelResponse);

            assertDoesNotThrow(() -> mercadoPagoIntegration.cancelOrder(meliId));
        }
    }
}

