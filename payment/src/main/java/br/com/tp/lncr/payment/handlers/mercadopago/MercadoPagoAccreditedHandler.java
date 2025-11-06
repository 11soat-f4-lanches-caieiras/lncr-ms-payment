package br.com.tp.lncr.payment.handlers.mercadopago;

import br.com.tp.lncr.commons.config.IntegrationConfig;
import br.com.tp.lncr.core.utils.LoggerUtil;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoWebhookUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Component;

@Component
public class MercadoPagoAccreditedHandler implements MercadoPagoCallbackHandler{

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
    public void handle(MercadoPagoCallbackDTO callbackDTO, HttpServletRequest request, MercadoPagoConfig mercadoPagoConfig) {
        LoggerUtil.info("Processando pagamento aprovado: " + callbackDTO.data().externalReference());
        MercadoPagoWebhookUtils.validateCallbackSignature(callbackDTO, request, mercadoPagoConfig.getWebhookSecret());
        String url = integrationConfig.getPaymentsUrl() + "/paymentReceived" + request.getQueryString();
        MercadoPagoWebhookUtils.routeCallback(callbackDTO, url);
    }


}
