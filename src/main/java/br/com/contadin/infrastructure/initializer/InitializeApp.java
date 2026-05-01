package br.com.contadin.infrastructure.initializer;

import br.com.contadin.application.port.out.security.PasswordEncoderPort;
import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.UUID;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class InitializeApp implements ApplicationRunner {

    private final MockDb mockDb;

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoderPort passwordEncoderPort;

    @Value("${app.sysadmin.email}")
    private String email;
    @Value("${app.sysadmin.senha}")
    private String senha;

    @Override
    public void run(ApplicationArguments args) {

        if (email == null || email.isBlank() || senha == null || senha.isBlank()) {
            log.info("SYSADMIN não configurado, variáveis de ambiente não definidas.");
            return;
        }

        UUID sysadminId;

        if (usuarioRepository.existsByEmail(email.trim().toLowerCase())) {
            log.info("Usuário sysadmin já criado.");
            sysadminId = usuarioRepository.findByEmail(email.trim().toLowerCase())
                    .map(Usuario::getId)
                    .orElse(null);
        } else {
            Usuario sysadmin = Usuario.builder()
                    .nome("SYSADMIN")
                    .sobrenome(null)
                    .email(new Email(email.trim().toLowerCase()))
                    .senha(passwordEncoderPort.encode(senha))
                    .telefone(null)
                    .ativo(true)
                    .build();

            Usuario saved = usuarioRepository.save(sysadmin);
            sysadminId = saved.getId();
            log.info("Usuário sysadmin criado com sucesso: {}", email);
        }

        if (sysadminId != null) {
            mockDb.initializeMockData(sysadminId);
        }
    }
}
