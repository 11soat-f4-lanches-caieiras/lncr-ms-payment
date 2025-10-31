package br.com.tp.lncr.payment.integration.mercadopago;

import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;

public interface MercadoPagoIntegration {

    void cancelOrder(String meliId);

    PaymentMercadopagoQrDTO createOrder(PaymentMercadopagoQrDTO paymentMercadopagoQrDTO);

    String getAccessToken();

    void refundOrder(String meliId);
}
