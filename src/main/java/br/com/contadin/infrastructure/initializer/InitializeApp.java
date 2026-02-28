package br.com.contadin.infrastructure.initializer;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import br.com.contadin.application.port.out.UsuarioRepository;
import br.com.contadin.domain.model.Usuario;
import br.com.contadin.domain.valueobject.Email;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
@Order(1)
public class InitializeApp implements ApplicationRunner {

    // Repository
    private final UsuarioRepository usuarioRepository;

    // Environment
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

        if (usuarioRepository.existsByEmail(email.trim().toLowerCase())) {
            log.info("Usuário sysadmin já criado.");
            return;
        }

        Usuario sysadmin = Usuario.builder()
            .nome("SYSADMIN")
            .sobrenome(null)
            .email(new Email(email.trim().toLowerCase()))
            .senha(senha)
            .telefone(null)
            .ativo(true)
            .build();

        usuarioRepository.save(sysadmin);
        log.info("Usuário sysadmin criado com sucesso: {}", email);
    }
}
