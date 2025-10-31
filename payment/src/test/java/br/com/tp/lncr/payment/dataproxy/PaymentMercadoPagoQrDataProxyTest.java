package br.com.tp.lncr.payment.dataproxy;

import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadoPagoQrRepositoryImpl;
import br.com.tp.lncr.commons.integrations.customerorder.CustomerOrderIntegration;
import br.com.tp.lncr.commons.integrations.notifcation.NotificationIntegraionImpl;
import br.com.tp.lncr.payment.integration.mercadopago.MercadoPagoIntegrationImpl;
import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PaymentMercadoPagoQrDataProxyTest {

    @Mock
    private JpaMercadoPagoQrRepositoryImpl jpaMercadoPagoQrPostgresDatabase;
    @Mock
    private MercadoPagoIntegrationImpl mercadoPagoIntegration;
    @Mock
    private CustomerOrderIntegration customerOrderIntegration;
    @Mock
    private NotificationIntegraionImpl notificationIntegration;

    private PaymentMercadoPagoQrDataProxy paymentDataProxy;

    @BeforeEach
    void setUp() {
        paymentDataProxy = new PaymentMercadoPagoQrDataProxy(
                jpaMercadoPagoQrPostgresDatabase,
                mercadoPagoIntegration,
                customerOrderIntegration,
                notificationIntegration
        );
    }

    @Test
    void shouldCancelPaymentOrderWithValidMeliId() {
        String meliId = "12345";

        paymentDataProxy.cancelPaymentOrder(meliId);

        verify(mercadoPagoIntegration).cancelOrder(meliId);
    }

    @Test
    void shouldCreatePaymentChargeSuccessfully() {
        PaymentMercadopagoQrDTO paymentDTO = createPaymentDTO();
        PaymentMercadopagoQrDTO savedPayment = createPaymentDTO();
        savedPayment.setId(1);
        PaymentMercadopagoQrDTO integrationPayment = createPaymentDTO();
        integrationPayment.setId(1);
        integrationPayment.setMeliId("meli123");
        PaymentMercadopagoQrDTO finalSavedPayment = createPaymentDTO();
        finalSavedPayment.setId(1);
        finalSavedPayment.setMeliId("meli123");

        when(jpaMercadoPagoQrPostgresDatabase.save(paymentDTO)).thenReturn(savedPayment);
        when(mercadoPagoIntegration.createOrder(savedPayment)).thenReturn(integrationPayment);
        when(jpaMercadoPagoQrPostgresDatabase.save(integrationPayment)).thenReturn(finalSavedPayment);

        PaymentMercadopagoQrDTO result = paymentDataProxy.createPaymentCharge(paymentDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("meli123", result.getMeliId());
        verify(jpaMercadoPagoQrPostgresDatabase, times(2)).save(any(PaymentMercadopagoQrDTO.class));
        verify(mercadoPagoIntegration).createOrder(savedPayment);
    }

    @Test
    void shouldFindPaymentsByStatusList() {
        List<Integer> statusIds = Arrays.asList(1, 2, 3);
        List<PaymentMercadopagoQrDTO> expectedPayments = Arrays.asList(
                createPaymentDTO(),
                createPaymentDTO()
        );

        when(jpaMercadoPagoQrPostgresDatabase.findByStatusList(statusIds)).thenReturn(expectedPayments);

        List<PaymentMercadopagoQrDTO> result = paymentDataProxy.findByStatusList(statusIds);

        assertNotNull(result);
        assertEquals(2, result.size());
        verify(jpaMercadoPagoQrPostgresDatabase).findByStatusList(statusIds);
    }

    @Test
    void shouldFindPaymentByCustomerOrderIdWhenExists() {
        Integer customerOrderId = 1;
        PaymentMercadopagoQrDTO expectedPayment = createPaymentDTO();
        expectedPayment.setOrderId(customerOrderId);

        when(jpaMercadoPagoQrPostgresDatabase.findByCustomerOrderId(customerOrderId)).thenReturn(expectedPayment);

        PaymentMercadopagoQrDTO result = paymentDataProxy.findPaymentByCustomerOrderId(customerOrderId);

        assertNotNull(result);
        assertEquals(customerOrderId, result.getOrderId());
        verify(jpaMercadoPagoQrPostgresDatabase).findByCustomerOrderId(customerOrderId);
    }

    @Test
    void shouldReturnNullWhenPaymentByCustomerOrderIdNotFound() {
        Integer customerOrderId = 999;

        when(jpaMercadoPagoQrPostgresDatabase.findByCustomerOrderId(customerOrderId)).thenReturn(null);

        PaymentMercadopagoQrDTO result = paymentDataProxy.findPaymentByCustomerOrderId(customerOrderId);

        assertNull(result);
        verify(jpaMercadoPagoQrPostgresDatabase).findByCustomerOrderId(customerOrderId);
    }

    @Test
    void shouldFindPaymentByIdWhenExists() {
        Integer paymentId = 1;
        PaymentMercadopagoQrDTO expectedPayment = createPaymentDTO();
        expectedPayment.setId(paymentId);

        when(jpaMercadoPagoQrPostgresDatabase.findById(paymentId)).thenReturn(expectedPayment);

        PaymentMercadopagoQrDTO result = paymentDataProxy.findPaymentById(paymentId);

        assertNotNull(result);
        assertEquals(paymentId, result.getId());
        verify(jpaMercadoPagoQrPostgresDatabase).findById(paymentId);
    }

    @Test
    void shouldRefundPaymentOrderWithValidMeliId() {
        String meliId = "12345";

        paymentDataProxy.refundPaymentOrder(meliId);

        verify(mercadoPagoIntegration).refundOrder(meliId);
    }

    @Test
    void shouldSavePaymentSuccessfully() {
        PaymentMercadopagoQrDTO paymentDTO = createPaymentDTO();
        PaymentMercadopagoQrDTO savedPayment = createPaymentDTO();
        savedPayment.setId(1);

        when(jpaMercadoPagoQrPostgresDatabase.save(paymentDTO)).thenReturn(savedPayment);

        PaymentMercadopagoQrDTO result = paymentDataProxy.save(paymentDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(jpaMercadoPagoQrPostgresDatabase).save(paymentDTO);
    }

    @Test
    void shouldSendNotificationWithValidParameters() {
        String notificationType = "PAYMENT_APPROVED";
        Integer artefactId = 1;
        String message = "Payment has been approved";

        paymentDataProxy.sendNotification(notificationType, artefactId, message);

        verify(notificationIntegration).sendNotification(notificationType, artefactId, message);
    }

    @Test
    void shouldUpdateCustomerOrderStatusSuccessfully() {
        Integer customerOrderId = 1;
        String newStatus = "PAID";

        paymentDataProxy.updateCustomerOrderStatus(customerOrderId, newStatus);

        verify(customerOrderIntegration).updateCustomerOrderStatus(customerOrderId, newStatus);
    }

    @Test
    void shouldHandleNullParametersGracefully() {
        assertDoesNotThrow(() -> {
            paymentDataProxy.cancelPaymentOrder(null);
            paymentDataProxy.refundPaymentOrder(null);
            paymentDataProxy.sendNotification(null, null, null);
            paymentDataProxy.updateCustomerOrderStatus(null, null);
        });

        verify(mercadoPagoIntegration).cancelOrder(null);
        verify(mercadoPagoIntegration).refundOrder(null);
        verify(notificationIntegration).sendNotification(null, null, null);
        verify(customerOrderIntegration).updateCustomerOrderStatus(null, null);
    }

    @Test
    void shouldCreatePaymentChargeEvenWhenFirstSaveFails() {
        PaymentMercadopagoQrDTO paymentDTO = createPaymentDTO();
        PaymentMercadopagoQrDTO integrationResult = createPaymentDTO();
        integrationResult.setMeliId("meli123");
        PaymentMercadopagoQrDTO finalResult = createPaymentDTO();
        finalResult.setId(1);
        finalResult.setMeliId("meli123");

        when(jpaMercadoPagoQrPostgresDatabase.save(paymentDTO)).thenReturn(null);
        when(mercadoPagoIntegration.createOrder(null)).thenReturn(integrationResult);
        when(jpaMercadoPagoQrPostgresDatabase.save(integrationResult)).thenReturn(finalResult);

        PaymentMercadopagoQrDTO result = paymentDataProxy.createPaymentCharge(paymentDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        assertEquals("meli123", result.getMeliId());
        verify(jpaMercadoPagoQrPostgresDatabase, times(2)).save(any());
        verify(mercadoPagoIntegration).createOrder(null);
    }

    @Test
    void shouldCreatePaymentChargeEvenWhenIntegrationFails() {
        PaymentMercadopagoQrDTO paymentDTO = createPaymentDTO();
        PaymentMercadopagoQrDTO savedPayment = createPaymentDTO();
        savedPayment.setId(1);
        PaymentMercadopagoQrDTO finalResult = createPaymentDTO();
        finalResult.setId(1);

        when(jpaMercadoPagoQrPostgresDatabase.save(paymentDTO)).thenReturn(savedPayment);
        when(mercadoPagoIntegration.createOrder(savedPayment)).thenReturn(null);
        when(jpaMercadoPagoQrPostgresDatabase.save(null)).thenReturn(finalResult);

        PaymentMercadopagoQrDTO result = paymentDataProxy.createPaymentCharge(paymentDTO);

        assertNotNull(result);
        assertEquals(1, result.getId());
        verify(jpaMercadoPagoQrPostgresDatabase, times(2)).save(any());
        verify(mercadoPagoIntegration).createOrder(savedPayment);
    }

    private PaymentMercadopagoQrDTO createPaymentDTO() {
        PaymentMercadopagoQrDTO dto = new PaymentMercadopagoQrDTO();
        dto.setOrderId(1);
        dto.setAmount(25.50);
        return dto;
    }
}
