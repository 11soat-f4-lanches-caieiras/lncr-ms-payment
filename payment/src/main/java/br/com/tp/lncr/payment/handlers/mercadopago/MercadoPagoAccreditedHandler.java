package br.com.tp.lncr.payment.handlers.mercadopago;

import br.com.tp.lncr.commons.config.IntegrationConfig;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoWebhookUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.util.Map;

@Component
public class MercadoPagoAccreditedHandler implements MercadoPagoCallbackHandler{

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoAccreditedHandler.class);

    private final IntegrationConfig integrationConfig;

    public MercadoPagoAccreditedHandler(IntegrationConfig integrationConfig) {
        this.integrationConfig = integrationConfig;
    }

    @Override
    public boolean canHandle(MercadoPagoCallbackDTO callbackDTO) {
        return "processed".equalsIgnoreCase(callbackDTO.data().status()) &&
               "accredited".equalsIgnoreCase(callbackDTO.data().statusDetail());
    }

    @Override
    public void handle(MercadoPagoCallbackDTO callbackDTO, Map<String,String> requestMap, MercadoPagoConfig mercadoPagoConfig) {
        log.info("Processando pagamento aprovado: {}", callbackDTO.data().externalReference());
        log.info("Webhook Validation Signature: {}", mercadoPagoConfig.isWebhookValidationSignature());
        if (mercadoPagoConfig.isWebhookValidationSignature()){
            MercadoPagoWebhookUtils.validateCallbackSignature(callbackDTO, requestMap, mercadoPagoConfig.getWebhookSecret());
        }
        String url = integrationConfig.getPaymentsUrl() + "/paymentReceived?" + requestMap.get("queryString");
        log.info("Roteando callback para URL interna: {}", url);
        log.info("Body: {}", callbackDTO);
        MercadoPagoWebhookUtils.routeCallback(callbackDTO, url);
    }


}
