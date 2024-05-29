# Projeto: Curso Udemy - Arquitetura de Microsserviços: Padrão Saga Orquestrado
Repositório contendo o projeto desenvolvido do curso Arquitetura de Microsserviços: Padrão Saga Orquestrado

<p align="center">
<img loading="lazy" src="http://img.shields.io/static/v1?label=STATUS&message=CONCLUIDO&color=GREEN&style=for-the-badge"/>
</p>Créditos ao professor, Victor Hugo Negrisoli: https://github.com/vhnegrisoli

# Arquitetura
<img src="https://github.com/FreitassPedro/Projeto-padrao-saga-orquestrado/blob/main/Conteudos/Arquitetura.png">


# Tecnologias
- Java 17
- Spring Boot 3
- Apache Kafka
- API REST
- PostgreSQL
- MongoDB
- Docker
- Redpanda Console

# Ferramentas utilizadas

IntelliJ IDEA Community Edition
Docker Desktop
DBeaver (Visualizando de Banco de Dados)
Gradle

Arquitetura Proposta
Voltar ao início

No curso, desenvolveremos a seguinte aquitetura:

# Serviços da arquitetura

Teremos 5 serviços:
- **Orchestrator-Service:** microsserviço responsável por orquestrar todo o fluxo de execução da Saga, ele que saberá qual microsserviço foi executado e em qual estado, e para qual será o próximo microsserviço a ser enviado, este microsserviço também irá salvar o processo dos eventos. Este serviço não possui banco de dados.
- **Order-Service:** microsserviço responsável apenas por gerar um pedido inicial, e receber uma notificação. Aqui que teremos endpoints REST para inciar o processo e recuperar os dados dos eventos.

- **Product-Validation-Service:** microsserviço responsável por validar se o produto informado no pedido existe e está válido. Este microsserviço guardará a validação de um produto para o ID de um pedido. O banco de dados utilizado será o PostgreSQL.
Execução do projeto
- **Payment-Service:** microsserviço responsável por realizar um pagamento com base nos valores unitários e quantidades informadas no pedido. Este microsserviço guardará a informação de pagamento de um pedido. O banco de dados utilizado será o PostgreSQL.
- **Inventory-Service:** microsserviço responsável por realizar a baixa do estoque dos produtos de um pedido. Este microsserviço guardará a informação da baixa de um produto para o ID de um pedido. O banco de dados utilizado será o PostgreSQL.


# Execução do projeto
*Enquanto desenvolvi, tive alguns problemas na aplicação, então caso o projeto não funcionar do jeito que deveria, provavelmente está ocorrendo algo com o MONGODB*. 
*O **payment-service** pode enfrentar problemas, dessa forma, tente mexer no no comando rodando ```NET STOP MONGODB``` ou ```NET START MONGODB```*

- Abra o **terminal** ou **cmd** na pasta do do projeto
- Execute o arquivo python através do comando ```python .\build.py
- Aguarde o processo, você verá os containers rodando dentro do programa *docker desktop*

# Acessar a aplicação
Para acessar as aplicações e realizar um pedido, basta acessar a URL:
http://localhost:3000/swagger-ui.html

Você chegará na página gerada automaticamente pelo Swagger, confira:
<img src="https://github.com/FreitassPedro/Projeto-padrao-saga-orquestrado/blob/main/Conteudos/Documentacao.png">
As aplicações executarão nas seguintes portas:

- Order-Service: 3000
- Orchestrator-Service: 8080
- Product-Validation-Service: 8090
- Payment-Service: 8091
- Inventory-Service: 8092
- Apache Kafka: 9092
- Redpanda Console: 8081
- PostgreSQL (Payment-DB): 5433
- PostgreSQL (Inventory-DB): 5434
- PostgreSQL (Product-DB): 5435
- MongoDB (Order-DB): 27017

# Acessar tópicos com Redpanda
Acesse: http://localhost:8081
<img src="https://github.com/FreitassPedro/Projeto-padrao-saga-orquestrado/blob/main/Conteudos/Redpanda%20Kafka.png">

# API Endpoints

- Criar Tópico
**POST** http://localhost:3000/api/order
```
{
  "products": [
    {
      "product": {
        "code": "COMIC_BOOKS",
        "unitValue": 15.50
      },
      "quantity": 3
    },
    {
      "product": {
        "code": "BOOKS",
        "unitValue": 9.90
      },
      "quantity": 1
    }
  ]
}
```

É possível recuperar os dados da saga pelo orderId ou pelo transactionId, o resultado será o mesmo:

GET http://localhost:3000/api/event?orderId= ```{ORDER-ID}```

GET http://localhost:3000/api/event?transactionId= ```{TRANSACTIONID}```

- **Corpo de Resposta**
```
{
  "id": "64429e9a7a8b646915b37360",
  "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519",
  "orderId": "64429e987a8b646915b3735f",
  "payload": {
    "id": "64429e987a8b646915b3735f",
    "products": [
      {
        "product": {
          "code": "COMIC_BOOKS",
          "unitValue": 15.5
        },
        "quantity": 3
      },
      {
        "product": {
          "code": "BOOKS",
          "unitValue": 9.9
        },
        "quantity": 1
      }
    ],
    "totalAmount": 56.40,
    "totalItems": 4,
    "createdAt": "2023-04-21T14:32:56.335943085",
    "transactionId": "1682087576536_99d2ca6c-f074-41a6-92e0-21700148b519"
  },
  "source": "ORCHESTRATOR",
  "status": "SUCCESS",
  "eventHistory": [
    {
      "source": "ORCHESTRATOR",
      "status": "SUCCESS",
      "message": "Saga started!",
      "createdAt": "2023-04-21T14:32:56.78770516"
    },
    {
      "source": "PRODUCT_VALIDATION_SERVICE",
      "status": "SUCCESS",
      "message": "Products are validated successfully!",
      "createdAt": "2023-04-21T14:32:57.169378616"
    },
    {
      "source": "PAYMENT_SERVICE",
      "status": "SUCCESS",
      "message": "Payment realized successfully!",
      "createdAt": "2023-04-21T14:32:57.617624655"
    },
    {
      "source": "INVENTORY_SERVICE",
      "status": "SUCCESS",
      "message": "Inventory updated successfully!",
      "createdAt": "2023-04-21T14:32:58.139176809"
    },
    {
      "source": "ORCHESTRATOR",
      "status": "SUCCESS",
      "message": "Saga finished successfully!",
      "createdAt": "2023-04-21T14:32:58.248630293"
    }
  ],
  "createdAt": "2023-04-21T14:32:58.28"
}
```
