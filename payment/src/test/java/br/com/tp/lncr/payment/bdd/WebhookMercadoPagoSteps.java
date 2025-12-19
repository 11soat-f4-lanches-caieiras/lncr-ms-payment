package br.com.tp.lncr.payment.bdd;

import br.com.tp.lncr.commons.model.ResponseModel;
import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.payment.handlers.mercadopago.MercadoPagoCallbackHandlerRouter;
import br.com.tp.lncr.payment.webhooks.mercadopago.MercadoPagoCallbackDTO;
import br.com.tp.lncr.payment.webhooks.mercadopago.WebhookMercadoPagoController;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;
import jakarta.servlet.http.HttpServletRequest;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class WebhookMercadoPagoSteps {

    private WebhookMercadoPagoController webhookController;

    @Mock
    private MercadoPagoConfig mercadoPagoConfig;

    @Mock
    private MercadoPagoCallbackHandlerRouter handlerRouter;

    @Mock
    private HttpServletRequest httpServletRequest;

    private MercadoPagoCallbackDTO callbackDTO;
    private ResponseEntity<ResponseModel<String>> response;
    private String externalReference;
    private String dataId;
    private String action;
    private String type;

    @Before
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        when(mercadoPagoConfig.getWebhookSecret()).thenReturn("test-secret");
        webhookController = new WebhookMercadoPagoController(mercadoPagoConfig, handlerRouter);
    }

    @Dado("que o Mercado Pago enviou uma notificação de pagamento")
    public void queOMercadoPagoEnviouUmaNotificacaoDePagamento() {
        action = "payment.updated";
        type = "payment";
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
    public void oTipoDaNotificacaoE(String notificationType) {
        type = notificationType;
    }

    @Quando("o webhook processar a notificação")
    public void oWebhookProcessarANotificacao() {
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            externalReference,
            dataId,
            "processed",
            "accredited",
            "100.00",
            "100.00",
            null,
            "order",
            1
        );

        callbackDTO = new MercadoPagoCallbackDTO(
            action,
            "v1",
            "app-123",
            data,
            "2025-01-01T00:00:00Z",
            true,
            type,
            "user-123"
        );

        when(httpServletRequest.getQueryString()).thenReturn("?source_news=webhooks");

        response = webhookController.paymentMercadoPagoCallback(callbackDTO, httpServletRequest);
    }

    @Então("o sistema deve retornar status HTTP {int}")
    public void oSistemaDeveRetornarStatusHTTP(Integer expectedStatus) {
        assertNotNull(response);
        assertEquals(expectedStatus, response.getStatusCode().value());
    }

    @Então("deve encaminhar a notificação para o serviço de pagamentos")
    public void deveEncaminharANotificacaoParaOServicoDePagamentos() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(handlerRouter, timeout(1000).times(1)).route(any(), any(), any());
    }

    @Dado("que recebo um callback válido do Mercado Pago")
    public void queReceboUmCallbackValidoDoMercadoPago() {
        externalReference = "12345";
        dataId = "mp-67890";
        action = "payment.updated";
        type = "payment";
    }

    @Quando("o webhook receber a requisição")
    public void oWebhookReceberARequisicao() {
        MercadoPagoCallbackDTO.Data data = new MercadoPagoCallbackDTO.Data(
            externalReference,
            dataId,
            "processed",
            "accredited",
            "100.00",
            "100.00",
            null,
            "order",
            1
        );

        callbackDTO = new MercadoPagoCallbackDTO(
            action,
            "v1",
            "app-123",
            data,
            "2025-01-01T00:00:00Z",
            true,
            type,
            "user-123"
        );

        when(httpServletRequest.getQueryString()).thenReturn("?source_news=webhooks");

        long startTime = System.currentTimeMillis();
        response = webhookController.paymentMercadoPagoCallback(callbackDTO, httpServletRequest);
        long endTime = System.currentTimeMillis();

        assertTrue((endTime - startTime) < 100, "Resposta deve ser retornada em menos de 100ms");
    }

    @Então("deve retornar a resposta imediatamente")
    public void deveRetornarARespostaImediatamente() {
        assertNotNull(response);
        assertEquals(HttpStatus.OK, response.getStatusCode());
    }

    @Então("processar a notificação de forma assíncrona")
    public void processarANotificacaoDeFormaAssincrona() {
        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(handlerRouter, timeout(2000).atLeastOnce()).route(any(), any(), any());
    }
}

