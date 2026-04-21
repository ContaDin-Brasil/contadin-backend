# UNIT_TEST_GUIDE.md

Guia padrão para criação de testes unitários neste projeto. Todos os testes devem seguir as convenções descritas aqui.

---

## Localização e estrutura de pacotes

Os testes espelham a estrutura do código principal:

```
src/test/java/br/com/contadin/
└── application/
    └── usecase/
        └── transacao/
            └── BuscarTransacaoUseCaseTest.java
```

Regra: o pacote do teste é idêntico ao pacote da classe testada.

---

## O que testar

Testes unitários neste projeto cobrem **use cases** (`application/usecase/`).

- Controllers, repositories e mappers **não são alvo de testes unitários**.
- O use case é o sistema sob teste (`@InjectMocks`).
- As dependências injetadas no use case (ports/out) são **sempre mockadas** (`@Mock`).

---

## Estrutura da classe de teste

```java
@ExtendWith(MockitoExtension.class)
class CriarCategoriaUseCaseTest {

    @Mock
    private CategoriaRepository categoriaRepository; // port/out mockado

    @InjectMocks
    private CriarCategoriaUseCase useCase; // sistema sob teste

    @Test
    void deveCriarCategoriaQuandoDadosValidos() {
        // Arrange
        ...
        // Act
        ...
        // Assert
        ...
    }
}
```

- **Nunca** usar `@SpringBootTest` em testes unitários — não deve subir contexto Spring.
- **Nunca** mockar o próprio use case.

---

## Nomenclatura

| Elemento | Padrão | Exemplo |
|----------|--------|---------|
| Classe | `[UseCase]Test` | `BuscarTransacaoUseCaseTest` |
| Método (caminho feliz) | `deve[Comportamento]Quando[Condição]` | `deveBuscarTransacaoPorIdQuandoUuidValido` |
| Método (erro) | `deveLancarErroQuando[Condição]` | `deveLancarErroQuandoUuidNulo` |

Nomes em português, descritivos — o nome do teste deve ser legível como uma frase.

---

## Padrão Arrange / Act / Assert

Todo método de teste deve seguir as três seções, separadas por linha em branco:

```java
@Test
void deveBuscarTransacaoPorIdQuandoUuidValido() {
    // Arrange
    UUID id = UUID.randomUUID();
    Transacao transacao = Transacao.builder().id(id).build();
    when(transacaoRepository.findById(id)).thenReturn(Optional.of(transacao));

    // Act
    Transacao resultado = useCase.executeBuscarPorId(id);

    // Assert
    assertNotNull(resultado);
    assertSame(transacao, resultado);
    verify(transacaoRepository).findById(id);
}
```

---

## Testando cenários de erro

Exceções de domínio são testadas com `assertThrows`:

```java
@Test
void deveLancarErroQuandoBuscarPorIdComUuidNulo() {
    assertThrows(TransacaoInvalidaException.class,
        () -> useCase.executeBuscarPorId(null));
}

@Test
void deveLancarErroQuandoTransacaoNaoForEncontradaPorId() {
    UUID id = UUID.randomUUID();
    when(transacaoRepository.findById(id)).thenReturn(Optional.empty());

    assertThrows(TransacaoNaoEncontradaException.class,
        () -> useCase.executeBuscarPorId(id));
}
```

Regra: cada cenário de erro tem seu próprio método `@Test`.

---

## Verificando argumentos com ArgumentCaptor

Quando o use case monta um objeto (ex: `Pageable`, filtros) e passa para o repository, use `ArgumentCaptor` para verificar os valores internos:

```java
@Test
void deveUsarOrdenacaoDefaultDataTransacaoDesc() {
    // Arrange
    when(transacaoRepository.findAll(any(), any())).thenReturn(Page.empty());
    TransacaoConsultaParams params = new TransacaoConsultaParams();

    // Act
    useCase.execute(params);

    // Assert
    ArgumentCaptor<Pageable> pageableCaptor = ArgumentCaptor.forClass(Pageable.class);
    verify(transacaoRepository).findAll(any(), pageableCaptor.capture());

    Pageable pageable = pageableCaptor.getValue();
    assertEquals(0, pageable.getPageNumber());
    assertEquals(10, pageable.getPageSize());
    assertEquals(Sort.Direction.DESC, pageable.getSort().getOrderFor("dataTransacao").getDirection());
}
```

---

## Cobertura mínima esperada por use case

Para cada use case, criar testes que cubram:

- [ ] Caminho feliz (execução normal com dados válidos)
- [ ] Validações de entrada nula ou inválida → exceção de domínio
- [ ] Recurso não encontrado → exceção de não encontrado
- [ ] Combinações de filtros/parâmetros opcionais (quando aplicável)
- [ ] Comportamento de paginação/ordenação default (quando aplicável)

---

## Exceções de domínio deste projeto

| Exceção | Quando usar |
|---------|------------|
| `*InvalidaException` (ex: `TransacaoInvalidaException`) | Parâmetro nulo, campo inválido, regra de negócio violada |
| `*NaoEncontradaException` (ex: `TransacaoNaoEncontradaException`) | Busca por ID sem resultado no repository |

As exceções ficam em `domain/exception/` — referencie as classes corretas do domínio correspondente.

---

## Exemplo completo de referência

`src/test/java/br/com/contadin/application/usecase/transacao/BuscarTransacaoUseCaseTest.java`
