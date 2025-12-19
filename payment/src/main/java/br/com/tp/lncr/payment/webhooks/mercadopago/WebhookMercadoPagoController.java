package br.com.tp.lncr.payment.webhooks.mercadopago;

import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.commons.utils.ResponseEntityModelUtil;
import br.com.tp.lncr.core.utils.LoggerUtil;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.handlers.mercadopago.MercadoPagoCallbackHandlerRouter;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/webhooks")
public class WebhookMercadoPagoController {

    private final MercadoPagoConfig mercadoPagoConfig;
    private final MercadoPagoCallbackHandlerRouter webhookHandlerRouter;

    public WebhookMercadoPagoController(MercadoPagoConfig mercadoPagoConfig, MercadoPagoCallbackHandlerRouter webhookHandlerRouter) {
        this.mercadoPagoConfig = mercadoPagoConfig;
        this.webhookHandlerRouter = webhookHandlerRouter;
    }

    @PostMapping("/payments/mercadoPago/callback")
    public ResponseEntity<ResponseModel<String>> paymentMercadoPagoCallback(@RequestBody MercadoPagoCallbackDTO body, HttpServletRequest request) {

        LoggerUtil.info("========== MercadoPago Webhook Received ==========");
        LoggerUtil.info("Action: " + body.action());
        LoggerUtil.info("Type: " + body.type());
        LoggerUtil.info("External Reference: " + body.data().externalReference());
        LoggerUtil.info("Query String: " + request.getQueryString());
        LoggerUtil.info("==================================================");

        CompletableFuture.runAsync(() -> webhookHandlerRouter.route(body, request, mercadoPagoConfig));


        return ResponseEntityModelUtil.ok(null);
    }

}
