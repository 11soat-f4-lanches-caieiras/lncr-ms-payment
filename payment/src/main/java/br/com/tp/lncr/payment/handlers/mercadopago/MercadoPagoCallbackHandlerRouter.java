package br.com.tp.lncr.payment.handlers.mercadopago;

import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

@Component
public class MercadoPagoCallbackHandlerRouter {
    private final List<MercadoPagoCallbackHandler> handlers;

    public MercadoPagoCallbackHandlerRouter(List<MercadoPagoCallbackHandler> handlers) {
        this.handlers = handlers;
    }

    public void route(MercadoPagoCallbackDTO callbackDTO, Map<String, String> requestMap, MercadoPagoConfig mercadoPagoConfig) {
        handlers.stream()
                .filter(handler -> handler.canHandle(callbackDTO))
                .forEach(handler -> handler.handle(callbackDTO, requestMap, mercadoPagoConfig));
    }
}
