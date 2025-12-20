package br.com.tp.lncr.payment.handlers.mercadopago;

import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class MercadoPagoCallbackHandlerRouterTest {

    @Mock
    private MercadoPagoCallbackHandler handler1;

    @Mock
    private MercadoPagoCallbackHandler handler2;

    @Mock
    private MercadoPagoConfig mercadoPagoConfig;

    @Mock
    private HttpServletRequest request;

    private MercadoPagoCallbackHandlerRouter router;

    @BeforeEach
    void setUp() {
        List<MercadoPagoCallbackHandler> handlers = Arrays.asList(handler1, handler2);
        router = new MercadoPagoCallbackHandlerRouter(handlers);
    }

    @Test
    void testRoute_FirstHandlerCanHandle() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref",
            "mp-123",
            "processed",
            "accredited",
            "100",
            "100",
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

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("queryString", "?source_news=webhooks");
        requestMap.put("xSignature", "ts=123,v1=sig");
        requestMap.put("requestId", "req-123");

        when(handler1.canHandle(callbackDTO)).thenReturn(true);
        when(handler2.canHandle(callbackDTO)).thenReturn(false);

        // Act
        router.route(callbackDTO, requestMap, mercadoPagoConfig);

        // Assert
        verify(handler1, times(1)).canHandle(callbackDTO);
        verify(handler1, times(1)).handle(callbackDTO, requestMap, mercadoPagoConfig);
        verify(handler2, times(1)).canHandle(callbackDTO);
        verify(handler2, never()).handle(any(), any(), any());
    }

    @Test
    void testRoute_SecondHandlerCanHandle() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref",
            "mp-123",
            "pending",
            "pending_waiting_payment",
            "100",
            "0",
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

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("queryString", "?source_news=webhooks");
        requestMap.put("xSignature", "ts=123,v1=sig");
        requestMap.put("requestId", "req-123");

        when(handler1.canHandle(callbackDTO)).thenReturn(false);
        when(handler2.canHandle(callbackDTO)).thenReturn(true);

        // Act
        router.route(callbackDTO, requestMap, mercadoPagoConfig);

        // Assert
        verify(handler1, times(1)).canHandle(callbackDTO);
        verify(handler1, never()).handle(any(), any(), any());
        verify(handler2, times(1)).canHandle(callbackDTO);
        verify(handler2, times(1)).handle(callbackDTO, requestMap, mercadoPagoConfig);
    }

    @Test
    void testRoute_BothHandlersCanHandle() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref",
            "mp-123",
            "processed",
            "accredited",
            "100",
            "100",
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

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("queryString", "?source_news=webhooks");
        requestMap.put("xSignature", "ts=123,v1=sig");
        requestMap.put("requestId", "req-123");

        when(handler1.canHandle(callbackDTO)).thenReturn(true);
        when(handler2.canHandle(callbackDTO)).thenReturn(true);

        // Act
        router.route(callbackDTO, requestMap, mercadoPagoConfig);

        // Assert
        verify(handler1, times(1)).handle(callbackDTO, requestMap, mercadoPagoConfig);
        verify(handler2, times(1)).handle(callbackDTO, requestMap, mercadoPagoConfig);
    }

    @Test
    void testRoute_NoHandlerCanHandle() {
        // Arrange
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref",
            "mp-123",
            "unknown",
            "unknown_detail",
            "100",
            "100",
            null,
            "order",
            1
        );

        MercadoPagoCallbackDTO callbackDTO = new MercadoPagoCallbackDTO(
            "unknown.action",
            "v1",
            "app-123",
            data,
            "2025-01-01T00:00:00Z",
            true,
            "payment",
            "user-123"
        );

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("queryString", "?source_news=webhooks");
        requestMap.put("xSignature", "ts=123,v1=sig");
        requestMap.put("requestId", "req-123");

        when(handler1.canHandle(callbackDTO)).thenReturn(false);
        when(handler2.canHandle(callbackDTO)).thenReturn(false);

        // Act
        router.route(callbackDTO, requestMap, mercadoPagoConfig);

        // Assert
        verify(handler1, times(1)).canHandle(callbackDTO);
        verify(handler1, never()).handle(any(), any(), any());
        verify(handler2, times(1)).canHandle(callbackDTO);
        verify(handler2, never()).handle(any(), any(), any());
    }

    @Test
    void testRoute_EmptyHandlersList() {
        // Arrange
        List<MercadoPagoCallbackHandler> emptyHandlers = Collections.emptyList();
        MercadoPagoCallbackHandlerRouter emptyRouter = new MercadoPagoCallbackHandlerRouter(emptyHandlers);

        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            "ext-ref",
            "mp-123",
            "processed",
            "accredited",
            "100",
            "100",
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

        Map<String, String> requestMap = new HashMap<>();
        requestMap.put("queryString", "?source_news=webhooks");
        requestMap.put("xSignature", "ts=123,v1=sig");
        requestMap.put("requestId", "req-123");

        // Act & Assert - should not throw exception and complete without errors
        assertDoesNotThrow(() -> emptyRouter.route(callbackDTO, requestMap, mercadoPagoConfig));
    }
}

