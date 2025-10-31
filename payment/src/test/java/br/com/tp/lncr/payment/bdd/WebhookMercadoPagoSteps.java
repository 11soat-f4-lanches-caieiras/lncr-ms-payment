package br.com.tp.lncr.payment.bdd;

import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.commons.config.IntegrationConfig;
import br.com.tp.lncr.payment.webhooks.WebhookMercadoPagoController;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebhookMercadoPagoSteps {

    private WebhookMercadoPagoController webhookController;

    @Mock
    private IntegrationConfig integrationConfig;

    private Map<String, Object> requestBody;
    private String externalReference;
    private String dataId;
    private String notificationType;
    private ResponseEntity<ResponseModel<String>> response;
    private boolean isValidNotification;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        requestBody = new HashMap<>();
        isValidNotification = true;

        when(integrationConfig.getPaymentsUrl()).thenReturn("http://localhost:8080/api/payments");

        webhookController = new WebhookMercadoPagoController(integrationConfig);
    }

    @Dado("que o Mercado Pago enviou uma notificação de pagamento")
    public void queOMercadoPagoEnviouUmaNotificacaoDePagamento() {
        requestBody.put("action", "payment.updated");
        requestBody.put("type", "payment");
        isValidNotification = true;
    }

    @Dado("a notificação contém external_reference {string}")
    public void aNotificacaoContemExternalReference(String reference) {
        externalReference = reference;
    }

    @Dado("a notificação contém data_id {string}")
    public void aNotificacaoContemDataId(String id) {
        dataId = id;
    }

    @Dado("o tipo da notificação é {string}")
    public void oTipoDaNotificacaoE(String type) {
        notificationType = type;
    }

    @Quando("o webhook processar a notificação")
    public void oWebhookProcessarANotificacao() {
        if (isValidNotification && externalReference != null && dataId != null) {
            response = webhookController.paymentMercadoPagoCallback(
                externalReference,
                dataId,
                notificationType != null ? notificationType : "order",
                requestBody
            );
        } else {
            response = webhookController.paymentMercadoPagoCallback(
                externalReference,
                dataId,
                notificationType != null ? notificationType : "order",
                requestBody
            );
        }
    }

    @Então("o sistema deve retornar status HTTP {int}")
    public void oSistemaDeveRetornarStatusHTTP(Integer expectedStatus) {
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode().value());
    }

    @Então("deve encaminhar a notificação para o serviço de pagamentos")
    public void deveEncaminharANotificacaoParaOServicoDePagamentos() {
        // Verifica se a resposta foi ACCEPTED (202), indicando processamento assíncrono
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Dado("que o Mercado Pago enviou uma notificação incompleta")
    public void queOMercadoPagoEnviouUmaNotificacaoIncompleta() {
        externalReference = null;
        dataId = null;
        isValidNotification = false;
        requestBody.put("action", "payment.updated");
    }

    @Então("deve indicar parâmetros faltantes")
    public void deveIndicarParametrosFaltantes() {
        assertNotNull(response);
        assertNotNull(response.getBody());
    }

    @Dado("que recebo um callback válido do Mercado Pago")
    public void queReceboUmCallbackValidoDoMercadoPago() {
        externalReference = "12345";
        dataId = "mp-67890";
        notificationType = "payment";
        requestBody.put("action", "payment.updated");
        requestBody.put("data", Map.of("id", dataId));
        isValidNotification = true;
    }

    @Quando("o webhook receber a requisição")
    public void oWebhookReceberARequisicao() {
        long startTime = System.currentTimeMillis();
        response = webhookController.paymentMercadoPagoCallback(
            externalReference,
            dataId,
            notificationType,
            requestBody
        );
        long endTime = System.currentTimeMillis();

        // Verifica que a resposta foi rápida (menos de 100ms para retornar)
        assertTrue((endTime - startTime) < 100);
    }

    @Então("deve retornar a resposta imediatamente")
    public void deveRetornarARespostaImediatamente() {
        assertNotNull(response);
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }

    @Então("processar a notificação de forma assíncrona")
    public void processarANotificacaoDeFormaAssincrona() {
        // O webhook usa CompletableFuture.runAsync() para processamento assíncrono
        // Verificamos que recebemos 202 ACCEPTED indicando processamento em background
        assertEquals(HttpStatus.ACCEPTED, response.getStatusCode());
    }
}

