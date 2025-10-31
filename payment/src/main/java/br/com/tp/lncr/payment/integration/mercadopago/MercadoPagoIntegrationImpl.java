package br.com.tp.lncr.payment.integration.mercadopago;

import br.com.tp.lncr.commons.utils.IntegrationUtil;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.commons.integrations.IntegrationException;
import br.com.tp.lncr.core.dtos.payment.PaymentMercadopagoQrDTO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;

import java.util.UUID;


@Service
public class MercadoPagoIntegrationImpl implements MercadoPagoIntegration {

    private static final Logger log = LoggerFactory.getLogger(MercadoPagoIntegrationImpl.class);
    public final MercadoPagoConfig mercadoPagoConfig;

    public MercadoPagoIntegrationImpl(MercadoPagoConfig mercadoPagoConfig) {
        this.mercadoPagoConfig = mercadoPagoConfig;
    }

    @Override
    public void cancelOrder(String meliId) {
        String url = mercadoPagoConfig.getOrdersUrl() + "/" + meliId + "/cancel";
        HttpHeaders headers = setOrderHeaders();
        try {
            log.info("Cancelando ordem no Mercado Pago: {}", url);
            String response =  IntegrationUtil.postForObjectWithReturn(url, null, headers);
            log.info("Response: {}", response);
        } catch (Exception e) {
            log.error("Erro ao cancelar ordem no Mercado Pago: {}", e.getMessage());
            throw new IntegrationException("Erro ao cancelar ordem no Mercado Pago", 500);
        }
    }

    @Override
    public PaymentMercadopagoQrDTO createOrder(PaymentMercadopagoQrDTO paymentMercadopagoQrDTO) {
        String url = mercadoPagoConfig.getOrdersUrl();
        String requestBody = setCreateOrderRequestBody(paymentMercadopagoQrDTO.getOrderId(), paymentMercadopagoQrDTO.getAmount());
        log.info("URL: {}", url);
        log.info("RequestBody: {}", requestBody);
        String response = IntegrationUtil.postForObjectWithReturn(url,requestBody, setOrderHeaders());
        log.info("Response: {}", response);
        if (response == null || response.isEmpty()) {
            throw new IntegrationException("Erro ao criar pedido no Mercado Pago", 500);
        }
        return setOrderValuesInPaymentMercadoPagoDTO(paymentMercadopagoQrDTO,response);
    }

    @Override
    public String getAccessToken(){
        String url = mercadoPagoConfig.getoAuthUrl();
        String accessTokenResponse = IntegrationUtil.postForObjectWithReturn(url, setAccessTokenRequestyBody(),null);
        return getAccessTokenValue(accessTokenResponse);
    }

    @Override
    public void refundOrder(String meliId) {
        String url = mercadoPagoConfig.getOrdersUrl() + "/" + meliId + "/refund";
        HttpHeaders headers = setOrderHeaders();
        try {
            log.info("Solicitando estordo para ordem no Mercado Pago: {}", url);
            String response =  IntegrationUtil.postForObjectWithReturn(url, null, headers);
            log.info("Response: {}", response);
        } catch (Exception e) {
            log.error("Erro ao solicitar estorno no Mercado Pago: {}", e.getMessage());
            throw new IntegrationException("Erro ao solicitar estorno no Mercado Pago", 500);
        }
    }


    private HttpHeaders setOrderHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        headers.set("Authorization", "Bearer " + getAccessToken());
        headers.set("X-Idempotency-Key", UUID.randomUUID().toString());
        return headers;
    }

    private String getAccessTokenValue(String response) {
        if (response != null) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode;
                jsonNode = mapper.readTree(response);
                return jsonNode.path("access_token").asText();
            } catch (Exception e) {
                throw new IntegrationException("Erro ao extrair access_token do Mercado Pago\n"+ e.getMessage(),500);
            }
        }
        throw new IntegrationException("Erro ao obter token de acesso",500);
    }

    private PaymentMercadopagoQrDTO setOrderValuesInPaymentMercadoPagoDTO(PaymentMercadopagoQrDTO paymentMercadopagoQrDTO, String response) {
        if (response != null && !response.isEmpty()) {
            try {
                ObjectMapper mapper = new ObjectMapper();
                JsonNode jsonNode = mapper.readTree(response);
                String meliId = jsonNode.path("id").asText();
                String qrData = jsonNode.path("type_response").path("qr_data").asText();
                paymentMercadopagoQrDTO.setMeliId(meliId);
                paymentMercadopagoQrDTO.setQrData(qrData);
                return paymentMercadopagoQrDTO;
            } catch (Exception e) {
                throw new IntegrationException("Erro ao extrair valores de pagamento no Mercado Pago\n"+ e.getMessage(),500);
            }
        }
        throw new IntegrationException("Erro ao obter ordem de pagamento",500);
    }

    private String setAccessTokenRequestyBody(){
        return "{\"client_id\": \"" + mercadoPagoConfig.getClientId()+ "\"," +
                "\"client_secret\": \"" + mercadoPagoConfig.getSecretId() + "\"," +
                "\"grant_type\": \"client_credentials\"}";
    }

    private String setCreateOrderRequestBody(Integer customerOrderId, Double amount) {
        return "{"
                + "\"type\": \"qr\","
                + "\"external_reference\": \"" + customerOrderId + "\","
                + "\"expiration_time\": \"" + mercadoPagoConfig.getExpirationTime() + "\","
                + "\"config\": {"
                + "\"qr\": {"
                + "\"external_pos_id\": \"" + mercadoPagoConfig.getPosId() + "\","
                + "\"mode\": \"dynamic\""
                + "}"
                + "},"
                + "\"transactions\": {"
                + "\"payments\":[{"
                + "\"amount\": \"" + amount + "\"}]"
                + "}"
                + "}";
    }

}
