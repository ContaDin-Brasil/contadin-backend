package br.com.contadin.application.port.out.email;

public interface EmailPort {

    void enviarPinRecuperacaoSenha(String destinatario, String nomeDestinatario, String pin);
}
