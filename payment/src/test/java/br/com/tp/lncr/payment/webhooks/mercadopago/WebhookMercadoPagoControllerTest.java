package br.com.tp.lncr.payment.webhooks.mercadopago;

import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.handlers.mercadopago.MercadoPagoCallbackHandlerRouter;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;


import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebhookMercadoPagoControllerTest {

    @Mock
    private MercadoPagoConfig mercadoPagoConfig;

    @Mock
    private MercadoPagoCallbackHandlerRouter handlerRouter;

    @Mock
    private HttpServletRequest httpServletRequest;

    private WebhookMercadoPagoController controller;

    @BeforeEach
    void setUp() {
        controller = new WebhookMercadoPagoController(mercadoPagoConfig, handlerRouter);
    }

    @Test
    void testPaymentMercadoPagoCallback_Success() {
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

        when(httpServletRequest.getQueryString()).thenReturn("?source_news=webhooks");
        when(httpServletRequest.getHeader("x-signature")).thenReturn("ts=123,v1=sig");
        when(httpServletRequest.getHeader("x-request-id")).thenReturn("req-123");

        // Act
        ResponseEntity<ResponseModel<String>> response = controller.paymentMercadoPagoCallback(callbackDTO, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(handlerRouter, timeout(1000).times(1)).route(eq(callbackDTO), argThat(map ->
            map.get("queryString").equals("?source_news=webhooks") &&
            map.get("xSignature").equals("ts=123,v1=sig") &&
            map.get("requestId").equals("req-123")
        ), eq(mercadoPagoConfig));
    }

    @Test
    void testPaymentMercadoPagoCallback_WithNullExternalReference() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            null,
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

        when(httpServletRequest.getQueryString()).thenReturn("?source_news=webhooks");
        when(httpServletRequest.getHeader("x-signature")).thenReturn("ts=123,v1=sig");
        when(httpServletRequest.getHeader("x-request-id")).thenReturn("req-123");

        // Act
        ResponseEntity<ResponseModel<String>> response = controller.paymentMercadoPagoCallback(callbackDTO, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Test
    void testPaymentMercadoPagoCallback_AsyncProcessing() {
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

        when(httpServletRequest.getQueryString()).thenReturn("?source_news=webhooks");
        when(httpServletRequest.getHeader("x-signature")).thenReturn("ts=123,v1=sig");
        when(httpServletRequest.getHeader("x-request-id")).thenReturn("req-123");

        // Act
        long startTime = System.currentTimeMillis();
        ResponseEntity<ResponseModel<String>> response = controller.paymentMercadoPagoCallback(callbackDTO, httpServletRequest);
        long endTime = System.currentTimeMillis();

        // Assert
        assertTrue((endTime - startTime) < 100, "Response should be returned immediately (< 100ms)");
        assertEquals(HttpStatus.OK, response.getStatusCode());

        // Verify async processing happens
        verify(handlerRouter, timeout(2000).atLeastOnce()).route(any(), any(), any());
    }

    @Test
    void testPaymentMercadoPagoCallback_DifferentAction() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref-789",
            "mp-999",
            "pending",
            "pending_waiting_payment",
            "200.00",
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

        when(httpServletRequest.getQueryString()).thenReturn("?source_news=webhooks");
        when(httpServletRequest.getHeader("x-signature")).thenReturn("ts=456,v1=sig2");
        when(httpServletRequest.getHeader("x-request-id")).thenReturn("req-456");

        // Act
        ResponseEntity<ResponseModel<String>> response = controller.paymentMercadoPagoCallback(callbackDTO, httpServletRequest);

        // Assert
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(handlerRouter, timeout(1000).times(1)).route(any(), any(), any());
    }
}

