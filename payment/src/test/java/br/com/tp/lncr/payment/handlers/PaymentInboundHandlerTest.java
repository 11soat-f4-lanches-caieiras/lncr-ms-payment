package br.com.tp.lncr.payment.handlers;

import br.com.tp.lncr.core.exceptions.PaymentException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;

class PaymentInboundHandlerTest {

    private PaymentInboundHandler handler;

    @BeforeEach
    void setUp() {
        handler = new PaymentInboundHandler();
    }

    @ParameterizedTest(name = "Handle PaymentException with status code {0} - {1}")
    @CsvSource({
            "400, 'Payment failed', BAD_REQUEST",
            "404, 'Payment not found', NOT_FOUND",
            "500, 'Internal error', INTERNAL_SERVER_ERROR",
            "409, 'Payment conflict', CONFLICT"
    })
    void handlePaymentExceptionWithDifferentStatusCodes(int statusCode, String message, String statusName) {
        PaymentException exception = new PaymentException(message, statusCode);

        ResponseEntity<Object> response = handler.handleKitchenOrderException(exception);

        assertNotNull(response);
        assertEquals(statusCode, response.getStatusCode().value(),
            "Status code should be " + statusCode + " for " + statusName);
    }

    @ParameterizedTest(name = "Handle PaymentException with message: {0}")
    @ValueSource(strings = {"", "Payment failed: $%^&*()"})
    void handlePaymentExceptionWithDifferentMessages(String message) {
        PaymentException exception = new PaymentException(message, 400);

        ResponseEntity<Object> response = handler.handleKitchenOrderException(exception);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
    }

    @ParameterizedTest(name = "Handle PaymentException with null message")
    @ValueSource(ints = {400})
    void handlePaymentExceptionWithNullMessage(int statusCode) {
        PaymentException exception = new PaymentException(null, statusCode);

        ResponseEntity<Object> response = handler.handleKitchenOrderException(exception);

        assertNotNull(response);
        assertEquals(statusCode, response.getStatusCode().value());
    }

    @ParameterizedTest(name = "Handle PaymentException with long message of {0} characters")
    @ValueSource(ints = {1000})
    void handlePaymentExceptionWithLongMessage(int messageLength) {
        String longMessage = "A".repeat(messageLength);
        PaymentException exception = new PaymentException(longMessage, 400);

        ResponseEntity<Object> response = handler.handleKitchenOrderException(exception);

        assertNotNull(response);
        assertEquals(400, response.getStatusCode().value());
    }
}

