package br.com.tp.lncr.payment.configs;

import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import br.com.tp.lncr.payment.dataproxy.PaymentMercadoPagoQrDataProxy;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaMercadoPagoQrRepositoryImpl;
import br.com.tp.lncr.payment.datasources.postgres.mercadopago.JpaPaymentMercadopagoQRMapper;
import br.com.tp.lncr.commons.integrations.customerorder.CustomerOrderIntegrationImpl;
import br.com.tp.lncr.commons.integrations.notifcation.NotificationIntegraionImpl;

import br.com.tp.lncr.core.adapters.payment.mercadopago.PaymentMercadoPagoQrControllerImpl;
import br.com.tp.lncr.core.adapters.payment.mercadopago.PaymentMercadopagoQRMapper;
import br.com.tp.lncr.core.interfaces.payment.PaymentController;
import br.com.tp.lncr.payment.integration.mercadopago.MercadoPagoIntegrationImpl;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "lncr.mercado-pago")
public class MercadoPagoConfig {
    private String locationPrefix;
    private String oAuthUrl;
    private String ordersUrl;
    private String clientId;
    private String secretId;
    private String posId;
    private String expirationTime;
    private String webhookSecret;

    public String getLocationPrefix() {
        return locationPrefix;
    }

    public void setLocationPrefix(String locationPrefix) {
        this.locationPrefix = locationPrefix;
    }

    public String getoAuthUrl() {
        return oAuthUrl;
    }

    public void setoAuthUrl(String oAuthUrl) {
        this.oAuthUrl = oAuthUrl;
    }

    public String getOrdersUrl() {
        return ordersUrl;
    }

    public void setOrdersUrl(String ordersUrl) {
        this.ordersUrl = ordersUrl;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getSecretId() {
        return secretId;
    }

    public void setSecretId(String secretId) {
        this.secretId = secretId;
    }

    public String getPosId() {
        return posId;
    }

    public void setPosId(String posId) {
        this.posId = posId;
    }

    public String getExpirationTime() {
        return expirationTime;
    }

    public void setExpirationTime(String expirationTime) {
        this.expirationTime = expirationTime;
    }

    public String getWebhookSecret() {
        return webhookSecret;
    }

    public void setWebhookSecret(String webhookSecret) {
        this.webhookSecret = webhookSecret;
    }

    @Bean
    public PaymentMercadopagoQRMapper paymentMercadopagoQRMapper(){
        return new PaymentMercadopagoQRMapper();
    }

    @Bean
    public PaymentController<PaymentMercadopagoQrDTO> paymentMercadoPagoQrController(PaymentMercadopagoQRMapper paymentMercadopagoQRMapper){
        return new PaymentMercadoPagoQrControllerImpl(paymentMercadopagoQRMapper);
    }

    @Bean
    public PaymentMercadoPagoQrDataProxy paymentMercadoPagoQrDataProxy(JpaMercadoPagoQrRepositoryImpl jpaMercadoPagoQrPostgresDatabase, MercadoPagoIntegrationImpl mercadoPagoIntegration, CustomerOrderIntegrationImpl customerOrderIntegration, NotificationIntegraionImpl notificationIntegration){
        return new PaymentMercadoPagoQrDataProxy(jpaMercadoPagoQrPostgresDatabase,mercadoPagoIntegration,customerOrderIntegration, notificationIntegration);
    }

    @Bean
    public JpaPaymentMercadopagoQRMapper jpaPaymentMercadopagoQRMapper(){
        return new JpaPaymentMercadopagoQRMapper();
    }



}
