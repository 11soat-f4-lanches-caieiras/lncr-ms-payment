# lncr-ms-payment

## Descrição

Microserviço responsável pelo gerenciamento de **Pagamentos** no sistema Lanches Caieiras. Este serviço implementa as funcionalidades relacionadas ao processamento de pagamentos, integração com o gateway de pagamento MercadoPago, geração de QR Code para pagamento via PIX, e controle de status de transações.

## Funcionalidades

### Endpoints Disponíveis (`/payments/mercadoPago`)

| Método | Path | Descrição |
|--------|------|-----------|
| `POST` | `/payments/mercadoPago` | Criar cobrança de pagamento |
| `GET` | `/payments/mercadoPago/{paymentId}` | Buscar pagamento por ID |
| `GET` | `/payments/mercadoPago/customerOrder/{customerOrderId}` | Buscar pagamento por ID do pedido |
| `GET` | `/payments/mercadoPago/status/{paymentStatusList}` | Buscar pagamentos por lista de status |
| `POST` | `/payments/mercadoPago/cancel/{customerOrderId}` | Cancelar pagamento por ID do pedido |
| `GET` | `/payments/mercadoPago/process` | Processar confirmação de pagamento (webhook) |

**Parâmetros de consulta (webhook):**
- `data.external_reference`: Referência externa do pagamento
- `data.id`: ID da transação no MercadoPago
- `type`: Tipo de notificação (padrão: order)

## Tecnologias Utilizadas

- Java 21
- Spring Boot 3.4.5
- PostgreSQL
- Maven
- Cucumber (BDD)
- JUnit 5
- MercadoPago SDK

## Sonar Quality Gate

[![Quality Gate Status](https://sonarcloud.io/api/project_badges/measure?project=11soat-f4-lanches-caieiras_lncr-ms-payment&metric=alert_status&token=d48e0c482d2a8267eba87f08f90dec976262c13a)](https://sonarcloud.io/summary/new_code?id=11soat-f4-lanches-caieiras_lncr-ms-payment)

Acesse o dashboard completo: [SonarCloud - lncr-ms-payment](https://sonarcloud.io/project/overview?id=11soat-f4-lanches-caieiras_lncr-ms-payment)

## Dependências

- **lncr-core** (versão 3.0) - Biblioteca com regras de negócio e entidades de domínio
- **lncr-commons** (versão 1.0) - Biblioteca comum compartilhada com configurações e utilitários

## Guia de Download e Execução

### Pré-requisitos

- **Java 21** instalado
- **Maven 3.8+** instalado
- **PostgreSQL 13+** em execução
- **Conta no MercadoPago** (para integração de pagamentos)
- **Git** instalado

### Configuração do Banco de Dados

```sql
-- Criar database
CREATE DATABASE lncr_payment;

-- Criar usuário (opcional)
CREATE USER lncr_user WITH PASSWORD 'your_password';
GRANT ALL PRIVILEGES ON DATABASE lncr_payment TO lncr_user;
```

### Variáveis de Ambiente

Crie um arquivo `.env` ou configure as seguintes variáveis de ambiente:

```bash
# Configuração do Servidor
SERVER_PORT=8080

# PostgreSQL
POSTGRES_URL=jdbc:postgresql://localhost:5432/lncr_payment
POSTGRES_USER=lncr_user
POSTGRES_PASSWORD=your_password

# URLs da Aplicação
LNCR_INTERNAL_URL=http://localhost:8080
LNCR_EXTERNAL_URL=http://localhost:8080

# MercadoPago Configurações
MERCADOPAGO_OAUTH_URL=https://api.mercadopago.com/oauth/token
MERCADOPAGO_ORDERS_URL=https://api.mercadopago.com/instore/orders/qr/seller/collectors
MERCADOPAGO_CLIENT_ID=your_client_id
MERCADOPAGO_SECRET_ID=your_secret_id
MERCADOPAGO_POS_ID=your_pos_id
MERCADOPAGO_EXPIRATION_TIME=3600
MERCADOPAGO_WEBHOOK_SECRET=your_webhook_secret
MERCADOPAGO_WEBHOOK_VALIDATION_SIGNATURE=true
```

### Download e Instalação

```bash
# Clone o repositório
git clone https://github.com/11soat-f4-lanches-caieiras/lncr-ms-payment.git

# Entre no diretório do projeto
cd lncr-ms-payment/payment

# Configure o GitHub Packages (necessário para dependências lncr-core e lncr-commons)
# Crie o arquivo ~/.m2/settings.xml com suas credenciais do GitHub

# Compile o projeto
mvn clean install

# Execute a aplicação
mvn spring-boot:run
```

### Executando com Docker

```bash
# Build da imagem
docker build -t lncr-ms-payment:latest .

# Execute o container
docker run -p 8080:8080 \
  -e POSTGRES_URL=jdbc:postgresql://host.docker.internal:5432/lncr_payment \
  -e POSTGRES_USER=lncr_user \
  -e POSTGRES_PASSWORD=your_password \
  -e LNCR_INTERNAL_URL=http://localhost:8080 \
  -e LNCR_EXTERNAL_URL=http://localhost:8080 \
  -e MERCADOPAGO_CLIENT_ID=your_client_id \
  -e MERCADOPAGO_SECRET_ID=your_secret_id \
  lncr-ms-payment:latest
```

### Executando os Testes

```bash
# Executar todos os testes
mvn test

# Executar testes com cobertura
mvn test -Pcoverage

# Executar apenas testes BDD
mvn test -Dcucumber.filter.tags="@bdd"
```

### Verificando a Aplicação

Após iniciar a aplicação, acesse:

- **Health Check**: http://localhost:8080/actuator/health
- **API Base**: http://localhost:8080/payments/mercadoPago
