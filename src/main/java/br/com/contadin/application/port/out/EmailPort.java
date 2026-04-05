package br.com.contadin.application.port.out;

public interface EmailPort {

    void enviarPinRecuperacaoSenha(String destinatario, String nomeDestinatario, String pin);
}
