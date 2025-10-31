#language: pt

Funcionalidade: Configuração do Mercado Pago
  Como sistema de pagamento
  Eu quero configurar corretamente a integração com Mercado Pago
  Para garantir que todos os beans e dependências estejam disponíveis

  Cenário: Carregar configurações do Mercado Pago
    Dado que possuo as propriedades de configuração do Mercado Pago
    Quando o sistema inicializar a configuração
    Então as propriedades devem ser carregadas corretamente
    E o location_prefix deve estar definido
    E a URL de OAuth deve estar definida
    E a URL de orders deve estar definida

  Cenário: Criar beans de configuração
    Dado que a configuração do Mercado Pago está carregada
    Quando os beans forem criados
    Então o bean PaymentMercadopagoQRMapper deve estar disponível
    E o bean PaymentController deve estar disponível
    E o bean PaymentMercadoPagoQrDataProxy deve estar disponível

  Cenário: Validar tempo de expiração configurado
    Dado que o tempo de expiração está configurado
    Quando eu obter o valor de expirationTime
    Então o valor não deve estar vazio

