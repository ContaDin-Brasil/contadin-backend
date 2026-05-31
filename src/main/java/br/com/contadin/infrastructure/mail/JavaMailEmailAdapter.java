package br.com.contadin.infrastructure.mail;

import br.com.contadin.application.port.out.email.EmailPort;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class JavaMailEmailAdapter implements EmailPort {

    private static final String LOGO_CID = "logoContadin";
    private static final String LOGO_PATH = "assets/logo-contadin-azul.svg";

    private final JavaMailSender mailSender;

    @Value("${app.mail.from}")
    private String fromAddress;

    @Async
    @Override
    public void enviarPinRecuperacaoSenha(String destinatario, String nomeDestinatario, String pin) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    message, MimeMessageHelper.MULTIPART_MODE_MIXED_RELATED, "UTF-8"
            );

            helper.setFrom(fromAddress, "Contadin");
            helper.setTo(destinatario);
            helper.setSubject("Seu PIN de recuperação de senha - Contadin");
            helper.setText(buildHtml(nomeDestinatario, pin), true);
            
            ClassPathResource logo = new ClassPathResource(LOGO_PATH);
            if (logo.exists()) {
                helper.addInline(LOGO_CID, logo, "image/svg+xml");
            }

            mailSender.send(message);
            log.info("[MAIL] PIN de recuperação enviado para {}", destinatario);

        } catch (MessagingException | java.io.UnsupportedEncodingException e) {
            log.error("[MAIL] Falha ao enviar e-mail de recuperação para {}: {}", destinatario, e.getMessage());
        }
    }

    private String buildHtml(String nome, String pin) {
        return """
                <!DOCTYPE html>
                <html lang="pt-BR">
                <head>
                  <meta charset="UTF-8">
                  <meta name="viewport" content="width=device-width, initial-scale=1.0">
                  <title>Recuperação de Senha - Contadin</title>
                </head>
                <body style="margin:0;padding:0;background-color:#f0f2f5;font-family:Arial,Helvetica,sans-serif;">
                  <table width="100%%" cellpadding="0" cellspacing="0" style="background-color:#f0f2f5;padding:48px 0;">
                    <tr>
                      <td align="center">
                        <table width="600" cellpadding="0" cellspacing="0"
                               style="background-color:#ffffff;border-radius:16px;overflow:hidden;
                                      box-shadow:0 8px 24px rgba(0,0,0,0.10);max-width:600px;">

                          <!-- ===== HEADER ===== -->
                          <tr>
                            <td style="background:linear-gradient(135deg,#1565c0,#1e88e5);
                                        padding:36px 48px 32px;text-align:center;">
                              <!-- Logo em card branco para contraste com o fundo azul -->
                              <table cellpadding="0" cellspacing="0" style="margin:0 auto 16px;">
                                <tr>
                                  <td style="background:#ffffff;border-radius:18px;
                                              padding:10px;line-height:0;">
                                    <img src="cid:logoContadin"
                                         alt="Contadin"
                                         width="80" height="80"
                                         style="display:block;border-radius:10px;">
                                  </td>
                                </tr>
                              </table>
                              <p style="margin:0;color:rgba(255,255,255,0.85);font-size:13px;
                                         letter-spacing:1px;">Organização Financeira Inteligente</p>
                            </td>
                          </tr>

                          <!-- ===== BODY ===== -->
                          <tr>
                            <td style="padding:48px 48px 32px;">

                              <h2 style="margin:0 0 12px;color:#1a1a2e;font-size:22px;font-weight:bold;">
                                Recuperação de Senha 🔐
                              </h2>
                              <p style="margin:0 0 28px;color:#555;font-size:15px;line-height:1.7;">
                                Olá, <strong>%s</strong>!<br>
                                Recebemos uma solicitação para redefinir a senha da sua conta Contadin.
                                Use o PIN abaixo para continuar com o processo de recuperação.
                              </p>

                              <!-- ===== PIN CARD ===== -->
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td align="center" style="padding-bottom:32px;">
                                    <table cellpadding="0" cellspacing="0"
                                           style="background:#eef4ff;border:2px dashed #1e88e5;
                                                  border-radius:16px;">
                                      <tr>
                                        <td style="padding:28px 56px;text-align:center;">
                                          <p style="margin:0 0 8px;color:#888;font-size:11px;
                                                     text-transform:uppercase;letter-spacing:2px;">
                                            Seu PIN de recuperação
                                          </p>
                                          <p style="margin:0;color:#1565c0;font-size:48px;
                                                     font-weight:bold;letter-spacing:14px;
                                                     font-variant-numeric:tabular-nums;">
                                            %s
                                          </p>
                                        </td>
                                      </tr>
                                    </table>
                                  </td>
                                </tr>
                              </table>

                              <!-- ===== AVISO DE EXPIRAÇÃO ===== -->
                              <table width="100%%" cellpadding="0" cellspacing="0"
                                     style="margin-bottom:24px;">
                                <tr>
                                  <td style="background:#fff8e1;border-left:4px solid #f9a825;
                                              border-radius:0 8px 8px 0;padding:14px 16px;">
                                    <p style="margin:0;color:#5a4000;font-size:13px;line-height:1.6;">
                                      ⏱️ Este PIN é válido por <strong>10 minutos</strong> e pode ser
                                      utilizado <strong>somente uma vez</strong>. Após esse prazo,
                                      você precisará solicitar um novo PIN.
                                    </p>
                                  </td>
                                </tr>
                              </table>

                              <!-- ===== AVISO DE SEGURANÇA ===== -->
                              <table width="100%%" cellpadding="0" cellspacing="0">
                                <tr>
                                  <td style="background:#fce4ec;border-left:4px solid #e53935;
                                              border-radius:0 8px 8px 0;padding:14px 16px;">
                                    <p style="margin:0;color:#7b0000;font-size:13px;line-height:1.6;">
                                      🔒 Se você <strong>não solicitou</strong> a recuperação de senha,
                                      ignore este e-mail. Sua senha permanecerá a mesma e nenhuma
                                      alteração será feita na sua conta.
                                    </p>
                                  </td>
                                </tr>
                              </table>

                            </td>
                          </tr>

                          <!-- ===== FOOTER ===== -->
                          <tr>
                            <td style="background:#f9f9f9;border-top:1px solid #eeeeee;
                                        padding:24px 48px;text-align:center;">
                              <p style="margin:0 0 4px;color:#aaa;font-size:12px;">
                                © 2026 Contadin · Todos os direitos reservados.
                              </p>
                              <p style="margin:0;color:#bbb;font-size:11px;">
                                Este é um e-mail automático. Por favor, não responda.
                              </p>
                            </td>
                          </tr>

                        </table>
                      </td>
                    </tr>
                  </table>
                </body>
                </html>
                """.formatted(nome, pin);
    }
}
