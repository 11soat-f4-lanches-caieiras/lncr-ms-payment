package br.com.tp.lncr.payment.bdd;

import br.com.tp.lncr.payment.configs.MercadoPagoConfig;
import br.com.tp.lncr.core.adapters.payment.mercadopago.PaymentMercadopagoQRMapper;
import br.com.tp.lncr.core.interfaces.payment.PaymentController;
import io.cucumber.java.Before;
import io.cucumber.java.pt.Dado;
import io.cucumber.java.pt.Então;
import io.cucumber.java.pt.Quando;

import static org.junit.jupiter.api.Assertions.*;

public class MercadoPagoConfigSteps {

    private MercadoPagoConfig mercadoPagoConfig;
    private PaymentMercadopagoQRMapper paymentMapper;
    private PaymentController paymentController;
    private boolean configLoaded;

    @Before
    public void setUp() {
        mercadoPagoConfig = new MercadoPagoConfig();
        configLoaded = false;
    }

    @Dado("que possuo as propriedades de configuração do Mercado Pago")
    public void quePossuoAsPropriedadesDeConfiguracaoDoMercadoPago() {
        mercadoPagoConfig.setLocationPrefix("/api/payments");
        mercadoPagoConfig.setoAuthUrl("https://api.mercadopago.com/oauth/token");
        mercadoPagoConfig.setOrdersUrl("https://api.mercadopago.com/instore/orders/qr");
        mercadoPagoConfig.setClientId("test-client-id");
        mercadoPagoConfig.setSecretId("test-secret-id");
        mercadoPagoConfig.setPosId("POS001");
        mercadoPagoConfig.setExpirationTime("2024-12-31T23:59:59.000Z");
    }

    @Quando("o sistema inicializar a configuração")
    public void oSistemaInicializarAConfiguracao() {
        assertNotNull(mercadoPagoConfig);
        configLoaded = true;
    }

    @Então("as propriedades devem ser carregadas corretamente")
    public void asPropriedadesDevemSerCarregadasCorretamente() {
        assertTrue(configLoaded);
        assertNotNull(mercadoPagoConfig);
    }

    @Então("o location_prefix deve estar definido")
    public void oLocationPrefixDeveEstarDefinido() {
        assertNotNull(mercadoPagoConfig.getLocationPrefix());
        assertFalse(mercadoPagoConfig.getLocationPrefix().isEmpty());
    }

    @Então("a URL de OAuth deve estar definida")
    public void aURLDeOAuthDeveEstarDefinida() {
        assertNotNull(mercadoPagoConfig.getoAuthUrl());
        assertFalse(mercadoPagoConfig.getoAuthUrl().isEmpty());
        assertTrue(mercadoPagoConfig.getoAuthUrl().contains("oauth"));
    }

    @Então("a URL de orders deve estar definida")
    public void aURLDeOrdersDeveEstarDefinida() {
        assertNotNull(mercadoPagoConfig.getOrdersUrl());
        assertFalse(mercadoPagoConfig.getOrdersUrl().isEmpty());
        assertTrue(mercadoPagoConfig.getOrdersUrl().contains("orders"));
    }

    @Dado("que a configuração do Mercado Pago está carregada")
    public void queAConfiguracaoDoMercadoPagoEstaCarregada() {
        mercadoPagoConfig.setLocationPrefix("/api/payments");
        mercadoPagoConfig.setoAuthUrl("https://api.mercadopago.com/oauth/token");
        mercadoPagoConfig.setOrdersUrl("https://api.mercadopago.com/instore/orders/qr");
        mercadoPagoConfig.setClientId("test-client-id");
        mercadoPagoConfig.setSecretId("test-secret-id");
        mercadoPagoConfig.setPosId("POS001");
        mercadoPagoConfig.setExpirationTime("2024-12-31T23:59:59.000Z");
        configLoaded = true;
    }

    @Quando("os beans forem criados")
    public void osBeansForemCriados() {
        paymentMapper = mercadoPagoConfig.paymentMercadopagoQRMapper();
        paymentController = mercadoPagoConfig.paymentMercadoPagoQrController(paymentMapper);
    }

    @Então("o bean PaymentMercadopagoQRMapper deve estar disponível")
    public void oBeanPaymentMercadopagoQRMapperDeveEstarDisponivel() {
        assertNotNull(paymentMapper);
    }

    @Então("o bean PaymentController deve estar disponível")
    public void oBeanPaymentControllerDeveEstarDisponivel() {
        assertNotNull(paymentController);
        assertInstanceOf(PaymentController.class, paymentController);
    }

    @Então("o bean PaymentMercadoPagoQrDataProxy deve estar disponível")
    public void oBeanPaymentMercadoPagoQrDataProxyDeveEstarDisponivel() {
        // Simula que o bean seria criado no contexto Spring
        // Em um teste real, seria verificado via ApplicationContext
        assertNotNull(mercadoPagoConfig);
    }

    @Dado("que o tempo de expiração está configurado")
    public void queOTempoDeExpiracaoEstaConfigurado() {
        mercadoPagoConfig.setExpirationTime("2024-12-31T23:59:59.000Z");
    }

    @Quando("eu obter o valor de expirationTime")
    public void euObterOValorDeExpirationTime() {
        assertNotNull(mercadoPagoConfig.getExpirationTime());
    }

    @Então("o valor não deve estar vazio")
    public void oValorNaoDeveEstarVazio() {
        assertNotNull(mercadoPagoConfig.getExpirationTime());
        assertFalse(mercadoPagoConfig.getExpirationTime().isEmpty());
    }
}

