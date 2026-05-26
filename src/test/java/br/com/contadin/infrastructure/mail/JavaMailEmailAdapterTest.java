package br.com.contadin.infrastructure.mail;

import jakarta.mail.Session;
import jakarta.mail.internet.MimeMessage;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.util.ReflectionTestUtils;

import java.util.Properties;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("JavaMailEmailAdapter")
class JavaMailEmailAdapterTest {

    @Mock
    private JavaMailSender mailSender;

    @InjectMocks
    private JavaMailEmailAdapter adapter;

    private MimeMessage mimeMessage;

    @BeforeEach
    void setUp() {
        ReflectionTestUtils.setField(adapter, "fromAddress", "noreply@contadin.com.br");
        mimeMessage = new MimeMessage(Session.getDefaultInstance(new Properties()));
    }

    @Nested
    @DisplayName("enviarPinRecuperacaoSenha() - happy path")
    class HappyPath {

        @Test
        @DisplayName("deve criar MimeMessage, configurar e enviar e-mail com sucesso")
        void deveEnviarEmailComSucesso() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            assertDoesNotThrow(() ->
                    adapter.enviarPinRecuperacaoSenha("usuario@email.com", "João", "123456"));

            verify(mailSender).createMimeMessage();
            verify(mailSender).send(mimeMessage);
        }

        @Test
        @DisplayName("deve chamar mailSender.send() exatamente uma vez")
        void deveChamarSendUmaVez() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            adapter.enviarPinRecuperacaoSenha("destino@email.com", "Maria", "654321");

            verify(mailSender, times(1)).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("deve criar a MimeMessage antes de enviar")
        void deveCriarMimeMessageAntesDeEnviar() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            adapter.enviarPinRecuperacaoSenha("a@b.com", "Ana", "000000");

            InOrder ordem = inOrder(mailSender);
            ordem.verify(mailSender).createMimeMessage();
            ordem.verify(mailSender).send(any(MimeMessage.class));
        }
    }

    @Nested
    @DisplayName("enviarPinRecuperacaoSenha() - tratamento de erros")
    class TratamentoErros {

        @Test
        @DisplayName("deve propagar MailSendException pois não é capturada pelo adapter")
        void devePropagaMailSendException() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doThrow(new org.springframework.mail.MailSendException("Falha SMTP"))
                    .when(mailSender).send(any(MimeMessage.class));

            assertThrows(org.springframework.mail.MailSendException.class, () ->
                    adapter.enviarPinRecuperacaoSenha("x@x.com", "Teste", "999999"));
        }

        @Test
        @DisplayName("deve propagar RuntimeException pois não é capturada pelo adapter")
        void devePropagaRuntimeException() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);
            doThrow(new RuntimeException("Erro inesperado"))
                    .when(mailSender).send(any(MimeMessage.class));

            assertThrows(RuntimeException.class, () ->
                    adapter.enviarPinRecuperacaoSenha("destino@test.com", "Bob", "112233"));
        }
    }

    @Nested
    @DisplayName("enviarPinRecuperacaoSenha() - parâmetros variados")
    class ParametrosVariados {

        @Test
        @DisplayName("deve funcionar com pin de diferentes tamanhos")
        void deveFuncionarComPinDiferenteTamanho() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            assertDoesNotThrow(() ->
                    adapter.enviarPinRecuperacaoSenha("a@b.com", "Usuário", "12345678"));
            verify(mailSender).send(any(MimeMessage.class));
        }

        @Test
        @DisplayName("deve funcionar com caracteres especiais no nome do destinatário")
        void deveFuncionarComCaracteresEspeciaisNoNome() {
            when(mailSender.createMimeMessage()).thenReturn(mimeMessage);

            assertDoesNotThrow(() ->
                    adapter.enviarPinRecuperacaoSenha("a@b.com", "José Açúcar & Cia", "000111"));
            verify(mailSender).send(any(MimeMessage.class));
        }
    }
}
