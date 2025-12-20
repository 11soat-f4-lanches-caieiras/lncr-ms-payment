package br.com.tp.lncr.payment.handlers.mercadopago;

import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import jakarta.servlet.http.HttpServletRequest;

import java.util.Map;

public interface MercadoPagoCallbackHandler {
    boolean canHandle(MercadoPagoCallbackDTO callbackDTO);
    void handle(MercadoPagoCallbackDTO callbackDTO, Map<String,String> requestMap, MercadoPagoConfig mercadoPagoConfig);
}
