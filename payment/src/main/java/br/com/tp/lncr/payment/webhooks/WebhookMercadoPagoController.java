package br.com.tp.lncr.payment.webhooks;

import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.commons.utils.ResponseEntityModelUtil;
import br.com.tp.lncr.commons.config.IntegrationConfig;
import br.com.tp.lncr.core.utils.LoggerUtil;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;

import java.util.Map;
import java.util.concurrent.CompletableFuture;

@RestController
@RequestMapping("/webhooks")
public class WebhookMercadoPagoController {

    private final IntegrationConfig integrationConfig;
    private final RestTemplate restTemplate;

    public WebhookMercadoPagoController(IntegrationConfig integrationConfig) {
        this.integrationConfig = integrationConfig;
        this.restTemplate = new RestTemplate(new HttpComponentsClientHttpRequestFactory());
    }

    @PostMapping("/payments/mercadoPago/callback")
    public ResponseEntity<ResponseModel<String>> paymentMercadoPagoCallback(
                    @RequestParam(name = "data.external_reference", required = false) String externalReference,
                    @RequestParam(name = "data.id",required = false) String dataId,
                    @RequestParam(name = "type", defaultValue = "order", required = false) String type,
                    @RequestBody Map<String, Object> body) {

        LoggerUtil.info("Received MercadoPago webhook:");
        LoggerUtil.info("externalReference = "+ externalReference);
        LoggerUtil.info("dataId = "+ dataId);
        LoggerUtil.info("type =" + type);
        LoggerUtil.info("body = " + body.toString());

        if (externalReference == null || externalReference.isEmpty() || dataId == null || dataId.isEmpty()) {
            LoggerUtil.error("Invalid request: missing required parameters.");
            return ResponseEntityModelUtil.badRequest("Missing required parameters");
        }

        CompletableFuture.runAsync(() -> {
            HttpEntity<Map<String, Object>> requestEntity = createHttpEntity(body);
            restTemplate.exchange(getUrl(externalReference,dataId,type), HttpMethod.PATCH, requestEntity, new ParameterizedTypeReference<ResponseModel<String>>() {});
        });
        return ResponseEntityModelUtil.accepted(null);
    }

    private String getUrl(String externalReference, String dataId, String type) {
        return integrationConfig.getPaymentsUrl() + "/paymentReceived?data.external_reference="+ externalReference +"&data.id="+dataId+"&type="+type;
    }

    private HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private HttpEntity<Map<String, Object>> createHttpEntity(Map<String, Object> body) {
        return new HttpEntity<>(body, getHeaders());
    }



}
