#language: pt

Funcionalidade: Integração com Mercado Pago
  Como sistema de pagamento
  Eu quero integrar com o Mercado Pago
  Para que os pedidos possam ser processados via QR Code

  Cenário: Criar ordem de pagamento com sucesso
    Dado que tenho um pedido com ID 123 e valor 50.00
    E possuo credenciais válidas do Mercado Pago
    Quando eu criar a ordem de pagamento
    Então a ordem deve ser criada com sucesso
    E devo receber o ID do Mercado Pago
    E devo receber os dados do QR Code

  Cenário: Obter token de acesso
    Dado que possuo client_id e secret_id válidos
    Quando eu solicitar um token de acesso
    Então devo receber um access_token válido

  Cenário: Cancelar ordem de pagamento
    Dado que existe uma ordem criada com ID "mp-12345"
    Quando eu cancelar a ordem "mp-12345"
    Então a solicitação de cancelamento deve ser enviada ao Mercado Pago

  Cenário: Solicitar reembolso de pagamento
    Dado que existe um pagamento aprovado com ID "mp-67890"
    Quando eu solicitar o reembolso da ordem "mp-67890"
    Então a solicitação de reembolso deve ser enviada ao Mercado Pago

  Cenário: Falha ao criar ordem sem credenciais
    Dado que não possuo credenciais válidas
    Quando eu tentar criar uma ordem de pagamento
    Então deve ocorrer um erro de integração

