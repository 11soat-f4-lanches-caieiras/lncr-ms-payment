package br.com.tp.lncr.payment.dataproxy;

import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadoPagoQrRepositoryImpl;
import br.com.tp.lncr.commons.integrations.customerorder.CustomerOrderIntegration;
import br.com.tp.lncr.commons.integrations.notifcation.NotificationIntegraionImpl;
import br.com.tp.lncr.payment.integration.mercadopago.MercadoPagoIntegrationImpl;
import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import br.com.tp.lncr.core.interfaces.payment.PaymentDatabase;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public class PaymentMercadoPagoQrDataProxy implements PaymentDatabase<PaymentMercadopagoQrDTO> {

    private final JpaMercadoPagoQrRepositoryImpl jpaMercadoPagoQrPostgresDatabase;
    private final MercadoPagoIntegrationImpl mercadoPagoIntegration;
    private final CustomerOrderIntegration customerOrderIntegration;
    private final NotificationIntegraionImpl notificationIntegration;

    public PaymentMercadoPagoQrDataProxy(JpaMercadoPagoQrRepositoryImpl jpaMercadoPagoQrPostgresDatabase, MercadoPagoIntegrationImpl mercadoPagoIntegration, CustomerOrderIntegration customerOrderIntegration, NotificationIntegraionImpl notificationIntegration) {
        this.jpaMercadoPagoQrPostgresDatabase = jpaMercadoPagoQrPostgresDatabase;
        this.mercadoPagoIntegration = mercadoPagoIntegration;
        this.customerOrderIntegration = customerOrderIntegration;
        this.notificationIntegration = notificationIntegration;
    }

    @Override
    public void cancelPaymentOrder(String meliId) {
        this.mercadoPagoIntegration.cancelOrder(meliId);
    }

    @Override
    public PaymentMercadopagoQrDTO createPaymentCharge(PaymentMercadopagoQrDTO paymentDTO) {
        paymentDTO = this.jpaMercadoPagoQrPostgresDatabase.save(paymentDTO);
        paymentDTO = this.mercadoPagoIntegration.createOrder(paymentDTO);
        paymentDTO = this.jpaMercadoPagoQrPostgresDatabase.save(paymentDTO);
        return paymentDTO;
    }

    @Transactional(readOnly = true)
    @Override
    public List<PaymentMercadopagoQrDTO> findByStatusList(List<Integer> paymentStatusIdList) {
        return this.jpaMercadoPagoQrPostgresDatabase.findByStatusList(paymentStatusIdList);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentMercadopagoQrDTO findPaymentByCustomerOrderId(Integer customerOrderId) {
        return Optional.ofNullable(this.jpaMercadoPagoQrPostgresDatabase.findByCustomerOrderId(customerOrderId)).orElse(null);
    }

    @Transactional(readOnly = true)
    @Override
    public PaymentMercadopagoQrDTO findPaymentById(Integer paymentId) {
        return this.jpaMercadoPagoQrPostgresDatabase.findById(paymentId);
    }

    @Override
    public void refundPaymentOrder(String meliId) {
        this.mercadoPagoIntegration.refundOrder(meliId);
    }

    @Override
    public PaymentMercadopagoQrDTO save(PaymentMercadopagoQrDTO paymentDTO) {
        return this.jpaMercadoPagoQrPostgresDatabase.save(paymentDTO);
    }

    @Override
    public void sendNotification(String notificationType, Integer artefactId, String message) {
        this.notificationIntegration.sendNotification(notificationType, artefactId, message);
    }

    @Override
    public void updateCustomerOrderStatus(Integer customerOrderId, String newStatus) {
        this.customerOrderIntegration.updateCustomerOrderStatus(customerOrderId,newStatus);
    }
}
