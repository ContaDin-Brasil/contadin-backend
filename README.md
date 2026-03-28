# Contadin Backend

Backend do **Contadin** — sistema para organização financeira — construído em **Java 21** com **Spring Boot 3.4**, seguindo **Arquitetura Hexagonal** (Ports and Adapters) e princípios de **Arquitetura Limpa**.

Este documento serve como **guia de onboarding** e referência para entender como o código está organizado, por que certas pastas existem e como uma requisição atravessa as camadas até o banco e volta.

---

## 1. Introdução

### O que é este projeto

O **contadin-backend** expõe uma API REST para operações do domínio financeiro (por exemplo: categorias, transações, instituições, metas de gasto e usuários). A lógica de negócio fica protegida em **camadas internas**; detalhes de HTTP, persistência JPA e configuração Spring ficam na **periferia** (infraestrutura).

### O que é Arquitetura Hexagonal

A **Arquitetura Hexagonal** (também chamada **Ports and Adapters**) organiza o software em torno do **núcleo da aplicação** (casos de uso + domínio). Tudo que é “externo” — REST, banco de dados, filas, e-mail — entra e sai por **adaptadores** que implementam **portas** (interfaces) definidas pelo núcleo.

- **Port In**: como o mundo exterior **invoca** a aplicação (ex.: “criar categoria”).
- **Port Out**: como a aplicação **pede** algo ao mundo exterior (ex.: “salvar categoria”).
- **Adapters**: implementações concretas (controller Spring, repository JPA) que plugam nessas portas.

O formato “hexágono” é apenas uma metáfora visual: há **várias faces** (HTTP, CLI, mensageria, BD) plugadas no mesmo núcleo — não é um requisito geométrico no código.

### Por que esta arquitetura foi adotada neste projeto

1. **O negócio não fica preso ao Spring ou ao Hibernate** — facilita evoluir regras e trocar tecnologia na borda.
2. **Testes** podem exercitar casos de uso com **dublês** das portas de saída, sem subir servidor web ou banco.
3. **Pacotes e responsabilidades** ficam previsíveis para novos desenvolvedores (onboarding mais rápido).
4. **Múltiplos modelos** (DTO, domínio, entidade JPA) evitam vazar anotações de framework e detalhes de persistência para a API e para as regras.

---

## 2. Estrutura do projeto

O código principal vive sob o pacote raiz `br.com.contadin`. A árvore conceitual (alinhada ao repositório) é:

```
br.com.contadin
├── ContadinApplication.java      # Ponto de entrada Spring Boot (único @SpringBootApplication)
├── application/                  # Camada de aplicação (orquestração + contratos)
├── domain/                       # Núcleo do negócio (puro Java)
├── infrastructure/               # Adaptadores: Web, JPA, inicialização
└── config/                       # Configurações transversais (ex.: Swagger/OpenAPI)
```

### 2.1. Domain (`domain`)

**Responsabilidade**: regras de negócio e modelo conceitual do problema, **sem** Spring, JPA ou HTTP.

| Pacote        | Papel                                                                 |
|---------------|-----------------------------------------------------------------------|
| `model`       | Agregados / entidades de domínio (ex.: `Categoria`, `Transacao`).     |
| `valueobject` | Objetos de valor imutáveis quando fizer sentido no modelo.           |
| `enums`       | Enumerações do domínio.                                               |
| `exception`   | Exceções de negócio (ex.: conflito, recurso não encontrado).          |

**Regra de ouro**: se você precisar importar `org.springframework.*` ou `jakarta.persistence.*` aqui, provavelmente está no pacote errado.

### 2.2. Application (`application`)

**Responsabilidade**: **casos de uso** — um fluxo por classe ou operação coerente — e **contratos** (portas) entre o núcleo e o mundo externo. Também abriga o que é “entrada da aplicação” em termos de dados (DTOs e validação aplicada ao contorno).

| Pacote       | Papel                                                                                    |
|--------------|-------------------------------------------------------------------------------------------|
| `port.in`    | Interfaces de **entrada** que o adaptador Web chama (ex.: `CriarCategoriaInputPort`).    |
| `port.out`   | Interfaces de **saída** que os casos de uso dependem (ex.: `TransacaoRepository`).        |
| `usecase`    | Implementações dos portas de entrada; orquestram domínio + portas de saída.               |
| `dto`        | Objetos de transferência (**Request** / **Response**) para a API.                       |
| `validation` | Validações reutilizáveis no contorno da aplicação, quando aplicável.                    |
| `exception`  | Exceções que fazem sentido na camada de aplicação (se houver).                          |

Os **DTOs** vivem na aplicação porque descrevem o **contrato da API** que o caso de uso aceita ou devolve conceitualmente; o **controller** na infraestrutura apenas os utiliza.

### 2.3. Infrastructure (`infrastructure`)

**Responsabilidade**: **detalhes técnicos** — HTTP, mapeamento Web↔Domínio, JPA, implementações dos *port out*.

| Pacote / área      | Papel                                                                                       |
|--------------------|----------------------------------------------------------------------------------------------|
| `web.controller`  | `@RestController`: recebe HTTP, delega ao *port in*, monta HTTP de resposta.                |
| `web.mapper`      | Converte **DTO ↔ modelo de domínio** (ex.: `CategoriaWebMapper`).                          |
| `web.exception`   | Tratamento global de erros HTTP (`@ControllerAdvice`), tradução para status/corpo adequados. |
| `persistence.entity` | Entidades JPA (`@Entity`) — modelo **físico / ORM**.                                    |
| `persistence.mapper` | Converte **domínio ↔ entidade** para o repositório.                                     |
| `persistence.repository` | `JpaRepository` + classes que **implementam** os *port out* (adaptadores de persistência). |
| `initializer`     | Inicialização de dados (ex.: *mock* em desenvolvimento), quando habilitado por configuração. |

### 2.4. Config (`config`)

**Responsabilidade**: beans e anotações de configuração que **não** são regra de negócio.

| Pacote    | Papel                                              |
|-----------|----------------------------------------------------|
| `swagger` | OpenAPI/Springdoc — documentação e esquema Bearer JWT (`OpenApiConfig`). |

---

## 3. Fluxo de requisição (passo a passo)

### 3.1. Explicação textual

1. **Request HTTP** chega a um `@RestController` em `infrastructure.web.controller` (ex.: `POST /categorias`).
2. O **Spring** desserializa o corpo para um **DTO** de request (`application.dto`, ex.: `CategoriaRequest`), respeitando validações (`jakarta.validation`).
3. O controller usa um **mapper Web** (`infrastructure.web.mapper`) para obter um **objeto de domínio** (`domain.model`).
4. O controller chama a interface **Port In** (`application.port.in`), por exemplo `CriarCategoriaInputPort#execute`.
5. O **use case** correspondente (`application.usecase`) executa: validações de fluxo, chama regras no **domínio**, e quando precisa persistir ou buscar dados, usa apenas **Port Out** (`application.port.out`).
6. A implementação do Port Out (`infrastructure.persistence.repository`) usa **mapper de persistência** + **entidade JPA** + `JpaRepository` para falar com o banco.
7. O retorno sobe a pilha: entidade → domínio → eventualmente DTO de resposta via **mapper Web** → `ResponseEntity` / corpo JSON.

**Exemplo real** (trecho ilustrativo do fluxo de criação de categoria — padrão usado no projeto):

```java
// infrastructure — Controller
Categoria dominio = categoriaWebMapper.toDomain(request);
Categoria criada = criarCategoriaInputPort.execute(dominio);
CategoriaResponse body = categoriaWebMapper.toResponse(criada);
return ResponseEntity.status(201).body(body);
```

### 3.2. Diagrama de fluxo (ASCII)

```
┌─────────────────────────────────────────────────────────────────────────────┐
│                         CLIENTE (HTTP / JSON)                               │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  INFRASTRUCTURE.WEB                                                         │
│  Controller  →  WebMapper: Request (DTO) → Domain Model                    │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  APPLICATION                                                                 │
│  Port In (interface)  ←──  UseCase.execute(domain)                         │
│       │                                                                      │
│       ├── regras / domain.model, valueobject, enums                         │
│       │                                                                      │
│       └── chama Port Out (interface)                                        │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                                      ▼
┌─────────────────────────────────────────────────────────────────────────────┐
│  INFRASTRUCTURE.PERSISTENCE                                                 │
│  Adapter implementa Port Out → PersistenceMapper → Entity → JpaRepository → BD │
└─────────────────────────────────────────────────────────────────────────────┘
                                      │
                          (retorno no cam Inverso)
                                      │
                                      ▼
         Domain ← Entity ← BD   →   Response (DTO) ← WebMapper ← Controller
```

---

## 4. Separação de responsabilidades

### 4.1. O que cada camada **pode** fazer

| Camada           | Pode fazer                                                                 |
|------------------|-----------------------------------------------------------------------------|
| **Domain**       | Encapsular invariantes, métodos de negócio, exceções de domínio.            |
| **Application**  | Orquestrar um caso de uso; definir portas; usar DTOs como contrato da API.  |
| **Infrastructure** | Expor REST, mapear DTO↔domínio; persistir com JPA; configurar beans Spring. |

### 4.2. O que cada camada **não** deve fazer

| Camada           | Evitar                                                                                 |
|------------------|----------------------------------------------------------------------------------------|
| **Domain**       | Depender de framework, DTO de API ou entidade JPA.                                    |
| **Application**  | Anotar com `@RestController`, injetar `JpaRepository` diretamente nos use cases.      |
| **Infrastructure** | Colocar regra de negócio complexa que deveria estar em `domain` ou `usecase`.    |

### 4.3. Erros comuns (anti‑padrões)

**Controller com regra de negócio**

```java
// Ruim: cálculo ou decisão de negócio no controller
if (request.getValor().compareTo(BigDecimal.ZERO) < 0) {
    throw new IllegalArgumentException("...");
}
```

O controller deve **receber, mapear e delegar**. Invariantes profundas ficam no **domínio**; o contorno valida formato obrigatório (DTO + Bean Validation).

**Use case acessando diretamente o `JpaRepository`**

```java
// Ruim: acoplamento ao Spring Data dentro do caso de uso
private final TransacaoJpaRepository jpaRepository;
```

O caso de uso deve depender apenas de **`application.port.out.TransacaoRepository`** (interface). A classe em `infrastructure...` que implementa essa interface usa o JPA.

**Domain dependendo do framework**

```java
// Ruim: modelo de domínio como entidade JPA
@Entity
public class Categoria { ... }
```

No projeto, entidades JPA ficam em `infrastructure.persistence.entity`; o domínio permanece em `domain.model`.

---

## 5. Sobre Ports and Adapters

### 5.1. Port In (entrada)

- **O que é**: contrato que diz “**o que** a aplicação oferece” para um ator externo (normalmente a API REST).
- **Onde**: `application.port.in` (por domínio: `categoria`, `transacao`, etc.).
- **Quem implementa**: classes em `application.usecase`.
- **Quem chama**: controllers em `infrastructure.web.controller`.

Isso **inverte a dependência**: o controller depende da **abstração** (*port in*), não do detalhe interno do use case exposto de forma acoplada.

### 5.2. Port Out (saída)

- **O que é**: contrato que diz “**o que** a aplicação precisa do mundo externo” (persistência, e-mail, gateway de pagamento).
- **Onde**: `application.port.out` (ex.: `TransacaoRepository`, `InstituicaoRepository`).
- **Quem implementa**: adaptadores em `infrastructure.persistence.repository`.
- **Quem chama**: use cases.

### 5.3. Como isso desacopla o sistema

O **caso de uso** não sabe se os dados vêm de **SQL Server**, **H2** ou um arquivo CSV: ele só conhece a **interface**. Em testes, você pode fornecer uma implementação *in-memory* da mesma interface.

**Conceitualmente:**

```
[ WebAdapter ] --implements--> Port In <--- UseCase
                                    │
                                    └──depends on---> Port Out <--- [ JpaAdapter ]
```

---

## 6. Mapeamentos (DTO, Domain, Entity)

### 6.1. Por que existem múltiplos modelos

Cada representação tem um **propósito** diferente:

- **DTO** (`application.dto`): formato da API (campos expostos, validação de entrada, versionamento).
- **Domain** (`domain.model`): conceitos e regras do negócio, estáveis em relação ao transporte e ao banco.
- **Entity** (`infrastructure.persistence.entity`): mapeamento **ORM** (tabelas, lazy loading, ciclo de vida JPA).

Misturar os três costuma gerar API acoplada ao banco, *leaks* de lazy loading e mudanças perigosas em contrato público.

### 6.2. Diferenças resumidas

| Aspecto        | DTO                         | Domain                    | Entity (JPA)              |
|----------------|-----------------------------|---------------------------|---------------------------|
| Onde           | `application.dto`           | `domain.model`            | `persistence.entity`      |
| Framework      | Só validação/composição web | Nenhum                    | Hibernate/JPA             |
| Evolução       | Contrato com clientes       | Invariantes de negócio    | Esquema físico            |

### 6.3. Fluxo de conversão

**Persistência (escrita típica)**

```
Request JSON → DTO (Request) → Domain → Entity → Banco
```

**Leitura / resposta**

```
Banco → Entity → Domain → DTO (Response) → JSON
```

No código:

- **DTO ↔ Domain**: `infrastructure.web.mapper` (ex.: `CategoriaWebMapper`).
- **Domain ↔ Entity**: `infrastructure.persistence.mapper` (ex.: `InstituicaoPersistenceMapper`).

---

## 7. Integração com Spring

### 7.1. Onde o Spring Boot aparece

- **`ContadinApplication`**: bootstrap com `@SpringBootApplication`.
- **`infrastructure`**: `@RestController`, `@Service` (use cases, se anotados), implementações de repositório, `JpaRepository`, `@Configuration` em adaptadores quando necessário.
- **`config`**: configuração OpenAPI (`OpenApiConfig`).

### 7.2. O domínio não depende do Spring

Classes em `domain` e interfaces em `application.port` **não** devem referenciar Spring. Isso garante que o “coração” do Contadin possa ser compilado e testado como biblioteca Java comum.

### 7.3. Componentes típicos neste projeto

| Recurso Spring      | Uso no Contadin                                                |
|---------------------|----------------------------------------------------------------|
| Controllers         | `infrastructure.web.controller.*Controller`                  |
| Repositories        | `*JpaRepository` + classes `*RepositoryImplementations`       |
| Validação           | `@Valid` nos controllers com DTOs                            |
| Springdoc OpenAPI   | Anotações nos controllers + `config.swagger.OpenApiConfig`   |

---

## 8. Benefícios da arquitetura (neste contexto)

- **Baixo acoplamento** entre API REST, ORM e regras — mudanças em uma face não quebram todas as outras.
- **Alta testabilidade** — casos de uso testáveis com portas falsas (*fakes* / mocks).
- **Manutenção** — localizar “onde fica a regra” vs “onde fica o SQL” é direto pelos pacotes.
- **Independência de tecnologia** no núcleo — migrar de SQL Server para outro backend afeta principalmente `infrastructure.persistence`.
- **Onboarding** — a árvore de pastas documenta a intenção arquitetural sem depender só de convenção verbal.

---

## 9. Como rodar o projeto

### 9.1. Pré-requisitos

- **JDK 21** (alinhado ao `pom.xml`).
- **Maven 3.8+** (ou use o **Maven Wrapper** incluído no repositório: `./mvnw` no Linux/macOS ou `mvnw.cmd` no Windows).

Ferramentas opcionais: cliente HTTP (Insomnia, Postman), Git.

### 9.2. Banco de dados

O **`application.properties` padrão** usa **H2 em memória** (útil para subir rápido e desenvolver sem SQL Server).

Para **SQL Server** em desenvolvimento, utilize o profile **`dev`**, que espera variáveis de ambiente como `DB_URL`, `DB_USERNAME`, `DB_PASSWORD` (veja `application-dev.properties`).

Exemplo (PowerShell):

```powershell
$env:DB_PASSWORD = "sua_senha"
```

### 9.3. Comandos

Na raiz do repositório:

```bash
# Padrão (H2 conforme application.properties)
mvn spring-boot:run

# Com Maven Wrapper (Windows)
.\mvnw.cmd spring-boot:run

# Profile dev (SQL Server — configure datasource antes)
mvn spring-boot:run -Dspring-boot.run.profiles=dev
```

A aplicação sobe, por padrão, em **`http://localhost:8080`** (porta padrão do Spring Boot, salvo override em `application*.properties`).

### 9.4. Swagger / OpenAPI

Este projeto usa **springdoc-openapi**. Os caminhos configurados em `application.properties` são:

| Recurso        | URL típica                          |
|----------------|-------------------------------------|
| Swagger UI     | http://localhost:8080/api-contadin  |
| OpenAPI (JSON) | http://localhost:8080/api-contadin-json |

A API documentada inclui esquema de segurança **Bearer JWT** (configurado em `OpenApiConfig`).

### 9.5. Console H2 (apenas profile com H2)

Com H2 habilitado: **http://localhost:8080/h2-console**  
(JDBC URL, usuário e senha conforme `application.properties`.)

---

## 10. Boas práticas adotadas

- **SOLID**: especialmente **S** (responsabilidade única por use case/controller/adapter) e **D** (dependência de abstrações — *ports*).
- **Separação de responsabilidades**: domínio vs orquestração vs entrega HTTP vs persistência.
- **Interfaces como portas**: `port.in` e `port.out` estabelecem contratos estáveis.
- **Organização de pacotes** por **camada** e, dentro dela, **por contexto** (`categoria`, `transacao`, `instituicao`, …).
- **Validação no contorno**: DTOs + Bean Validation nos endpoints; regras fortes no domínio.
- **MapStruct** (onde aplicado) para mapeamentos explícitos entre representações.

---

## Stack principal (referência rápida)

| Tecnologia        | Uso                                      |
|-------------------|------------------------------------------|
| Spring Boot 3.4   | Aplicação, Web, Data JPA, Validation     |
| Java 21           | Linguagem                                 |
| H2 / SQL Server   | Banco (default vs profile `dev`)         |
| Springdoc OpenAPI | Documentação Swagger UI                  |
| MapStruct         | Mapeamento entre tipos                   |
| Lombok            | Redução de boilerplate (getters, builders) |

---

## Licença e contato

Configuração de licença e metadados da API: ver `OpenApiConfig` e o repositório oficial referenciado na documentação OpenAPI.

Para dúvidas sobre convenções de código neste monólito, use este README como mapa e, em segundo lugar, siga os padrões já presentes nos pacotes `categoria`, `transacao` e demais contextos.
