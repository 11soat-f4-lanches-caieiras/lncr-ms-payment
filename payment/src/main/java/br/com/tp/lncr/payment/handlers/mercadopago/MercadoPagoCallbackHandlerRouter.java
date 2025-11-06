package br.com.tp.lncr.payment.handlers.mercadopago;

import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class MercadoPagoCallbackHandlerRouter {
    private final List<MercadoPagoCallbackHandler> handlers;

    public MercadoPagoCallbackHandlerRouter(List<MercadoPagoCallbackHandler> handlers) {
        this.handlers = handlers;
    }

    public void route(MercadoPagoCallbackDTO callbackDTO, HttpServletRequest request, MercadoPagoConfig mercadoPagoConfig) {
        handlers.stream()
                .filter(handler -> handler.canHandle(callbackDTO))
                .forEach(handler -> handler.handle(callbackDTO, request, mercadoPagoConfig));
    }
}
