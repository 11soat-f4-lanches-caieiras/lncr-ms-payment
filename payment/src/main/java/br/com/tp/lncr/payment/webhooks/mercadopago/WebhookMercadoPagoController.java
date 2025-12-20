package br.com.tp.lncr.payment.webhooks.mercadopago;

import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.commons.utils.ResponseEntityModelUtil;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.handlers.mercadopago.MercadoPagoCallbackHandlerRouter;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/webhooks")
public class WebhookMercadoPagoController {

    private static final Logger log = LoggerFactory.getLogger(WebhookMercadoPagoController.class);

    private final MercadoPagoConfig mercadoPagoConfig;
    private final MercadoPagoCallbackHandlerRouter webhookHandlerRouter;

    public WebhookMercadoPagoController(MercadoPagoConfig mercadoPagoConfig, MercadoPagoCallbackHandlerRouter webhookHandlerRouter) {
        this.mercadoPagoConfig = mercadoPagoConfig;
        this.webhookHandlerRouter = webhookHandlerRouter;
    }

    @PostMapping("/payments/mercadoPago/callback")
    public ResponseEntity<ResponseModel<String>> paymentMercadoPagoCallback(@RequestBody MercadoPagoCallbackDTO body, HttpServletRequest request) {

        log.info("========== MercadoPago Webhook Received ==========");
        log.info("Action: {}", body.action());
        log.info("Type: {}", body.type());
        log.info("External Reference: {}", body.data().externalReference());
        Map<String,String> requestMap = new HashMap();
        requestMap.put("queryString", request.getQueryString());
        log.info("Query String: {}", requestMap.get("queryString"));
        requestMap.put("xSignature", request.getHeader("x-signature"));
        log.info("X-Signature: {}", requestMap.get("xSignature"));
        requestMap.put("requestId", request.getHeader("x-request-id"));
        log.info("RequestId: {}", requestMap.get("requestId"));
        log.info("==================================================");

        CompletableFuture.runAsync(() -> webhookHandlerRouter.route(body, requestMap, mercadoPagoConfig));


        return ResponseEntityModelUtil.ok(null);
    }

}
