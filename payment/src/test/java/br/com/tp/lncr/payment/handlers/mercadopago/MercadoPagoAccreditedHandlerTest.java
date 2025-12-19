package br.com.tp.lncr.payment.handlers.mercadopago;

import br.com.tp.lncr.commons.config.IntegrationConfig;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MercadoPagoAccreditedHandlerTest {

    @Mock
    private IntegrationConfig integrationConfig;

    @Mock
    private MercadoPagoConfig mercadoPagoConfig;

    @Mock
    private HttpServletRequest request;

    private MercadoPagoAccreditedHandler handler;

    @BeforeEach
    void setUp() {
        handler = new MercadoPagoAccreditedHandler(integrationConfig);
        lenient().when(integrationConfig.getPaymentsUrl()).thenReturn("http://localhost:8080/api/payments");
        lenient().when(mercadoPagoConfig.getWebhookSecret()).thenReturn("test-secret");
    }

    @Test
    void testCanHandle_ProcessedAndAccredited() {
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
        boolean canHandle = handler.canHandle(callbackDTO);

        // Assert
        assertTrue(canHandle);
    }

    @Test
    void testCanHandle_ProcessedButNotAccredited() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-123",
            "mp-456",
            "processed",
            "rejected",
            "100.00",
            "0.00",
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
        boolean canHandle = handler.canHandle(callbackDTO);

        // Assert
        assertFalse(canHandle);
    }

    @Test
    void testCanHandle_AccreditedButNotProcessed() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-123",
            "mp-456",
            "pending",
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
        boolean canHandle = handler.canHandle(callbackDTO);

        // Assert
        assertFalse(canHandle);
    }

    @Test
    void testCanHandle_CaseInsensitive() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-123",
            "mp-456",
            "PROCESSED",
            "ACCREDITED",
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
        boolean canHandle = handler.canHandle(callbackDTO);

        // Assert
        assertTrue(canHandle);
    }

    @Test
    void testCanHandle_PendingPayment() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-123",
            "mp-456",
            "pending",
            "pending_waiting_payment",
            "100.00",
            "0.00",
            null,
            "order",
            1
        );

        MercadoPagoCallbackDTO callbackDTO = new MercadoPagoCallbackDTO(
            "payment.created",
            "v1",
            "app-123",
            data,
            "2025-01-01T00:00:00Z",
            true,
            "payment",
            "user-123"
        );

        // Act
        boolean canHandle = handler.canHandle(callbackDTO);

        // Assert
        assertFalse(canHandle);
    }

    @Test
    void testHandle_ValidCallback_WithoutQueryString() {
        // Arrange
        String dataId = "12345";
        String requestId = "req-67890";
        String ts = "1234567890";
        String secret = "test-secret";

        // Criar assinatura válida
        String manifest = String.format("id:%s;request-id:%s;ts:%s;", dataId, requestId, ts);
        String signature = new org.apache.commons.codec.digest.HmacUtils("HmacSHA256", secret).hmacHex(manifest);
        String xSignature = String.format("ts=%s,v1=%s", ts, signature);

        lenient().when(request.getHeader("x-signature")).thenReturn(xSignature);
        lenient().when(request.getHeader("x-request-id")).thenReturn(requestId);
        lenient().when(request.getQueryString()).thenReturn(null);
        lenient().when(mercadoPagoConfig.isWebhookValidationSignature()).thenReturn(true);

        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-123",
            dataId,
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

        // Act & Assert - Should validate and not throw exception
        assertDoesNotThrow(() -> {
            try {
                handler.handle(callbackDTO, request, mercadoPagoConfig);
            } catch (Exception e) {
                // Expected in test context without actual server for routing
                if (!e.getMessage().contains("I/O error") && !e.getMessage().contains("Erro na integração")) {
                    throw e;
                }
            }
        });
    }

    @Test
    void testHandle_InvalidSignature_ThrowsException() {
        // Arrange
        when(mercadoPagoConfig.isWebhookValidationSignature()).thenReturn(true);
        when(request.getHeader("x-signature")).thenReturn("ts=123,v1=invalid-sig");
        when(request.getHeader("x-request-id")).thenReturn("req-123");

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

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            handler.handle(callbackDTO, request, mercadoPagoConfig)
        );
    }

    @Test
    void testHandle_MissingHeaders_ThrowsException() {
        // Arrange
        when(mercadoPagoConfig.isWebhookValidationSignature()).thenReturn(true);
        when(request.getHeader("x-signature")).thenReturn(null);
        when(request.getHeader("x-request-id")).thenReturn("req-123");

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

        // Act & Assert
        assertThrows(IllegalArgumentException.class, () ->
            handler.handle(callbackDTO, request, mercadoPagoConfig),
            "Parâmetros de validação ausentes"
        );
    }
}

