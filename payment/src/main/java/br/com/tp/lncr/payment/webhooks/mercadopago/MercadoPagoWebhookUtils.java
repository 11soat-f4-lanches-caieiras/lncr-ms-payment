package br.com.tp.lncr.payment.webhooks.mercadopago;

import br.com.tp.lncr.commons.utils.IntegrationUtil;
import br.com.tp.lncr.core.utils.LoggerUtil;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.commons.codec.digest.HmacUtils;

import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;

public class MercadoPagoWebhookUtils {

    private MercadoPagoWebhookUtils() {
        throw new IllegalStateException("Utility class");
    }

    public static void routeCallback(MercadoPagoCallbackDTO callbackDTO, String url) {
            IntegrationUtil.patchForObject(url, callbackDTO);
    }

    private static HttpHeaders getHeaders() {
        HttpHeaders headers = new HttpHeaders();
        headers.set("Content-Type", "application/json");
        return headers;
    }

    private static HttpEntity<MercadoPagoCallbackDTO> createHttpEntity(MercadoPagoCallbackDTO body) {
        return new HttpEntity<>(body, getHeaders());
    }

    public static void validateCallbackSignature(MercadoPagoCallbackDTO callbackDTO, HttpServletRequest request, String secret) {
        LoggerUtil.info("Iniciando validação da assinatura do webhook do MercadoPago");
        String xSignature = request.getHeader("x-signature");
        String xRequestId = request.getHeader("x-request-id");
        // Extrai o ID dos dados do callback e converte para minúsculas regra do Mercado Pago
        // https://www.mercadopago.com.br/developers/pt/docs/your-integrations/notifications/webhooks#editor_3
        String dataId = callbackDTO.data().id().toLowerCase();

        if (xSignature == null || xSignature.isEmpty() || dataId.isEmpty() || xRequestId == null || xRequestId.isEmpty()) {
            LoggerUtil.error("Assinatura ou ID de dados ausentes no cabeçalho da solicitação");
            LoggerUtil.info("Signature: " + xSignature);
            LoggerUtil.info("RequestID: " + xRequestId);
            LoggerUtil.info("DataID: " + dataId);
            throw new IllegalArgumentException("Parâmetros de validação ausentes");

        }
        String ts = extractSignatureValue(xSignature, "ts");
        String v1 = extractSignatureValue(xSignature, "v1");

        String manifest = setManifest(dataId, xRequestId, ts);
        String sha = createHMAC256Signature(secret, manifest);

        if (!isValidSignature(v1, sha)) {
            LoggerUtil.error("Assinatura inválida detectada no webhook do MercadoPago");
            throw new IllegalArgumentException("Assinatura inválida");
        }
        LoggerUtil.info("Assinatura do webhook do MercadoPago validada com sucesso");
    }

    private static String setManifest(String dataId, String requestId, String ts) {
        return String.format("id:%s;request-id:%s;ts:%s;", dataId, requestId, ts);

    }

    private static String createHMAC256Signature(String secret, String manifest) {
        return new HmacUtils("HmacSHA256", secret).hmacHex(manifest);
    }

    private static boolean isValidSignature(String expectedSignature, String actualSignature) {
        return expectedSignature.equals(actualSignature);
    }

    private static String extractSignatureValue(String signature, String key) {
        String[] parts = signature.split(",");
        for (String part : parts) {
            if (part.trim().startsWith(key + "=")) {
                return part.trim().substring(key.length() + 1);
            }
        }
        throw new IllegalArgumentException("Parâmetro " + key + " não encontrado na assinatura");
    }

}
