#language: pt

Funcionalidade: Webhook do Mercado Pago
  Como sistema de pagamento
  Eu quero receber notificações do Mercado Pago
  Para atualizar o status dos pagamentos em tempo real

  Cenário: Receber callback de pagamento aprovado
    Dado que o Mercado Pago enviou uma notificação de pagamento
    E a notificação contém external_reference "123"
    E a notificação contém data_id "mp-456"
    E o tipo da notificação é "payment"
    Quando o webhook processar a notificação
    Então o sistema deve retornar status HTTP 200
    E deve encaminhar a notificação para o serviço de pagamentos

  Cenário: Processar callback de forma assíncrona
    Dado que recebo um callback válido do Mercado Pago
    Quando o webhook receber a requisição
    Então deve retornar a resposta imediatamente
    E processar a notificação de forma assíncrona

