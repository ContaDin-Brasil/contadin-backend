package br.com.contadin.domain.valueobject;

public record Telefone(String numero) {

    public Telefone {
        if (numero == null || numero.isBlank()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio");
        }

        numero = removerMascara(numero);

        if (!telefoneValido(numero)) {
            throw new IllegalArgumentException("Telefone inválido");
        }
    }

    /**
     * Telefone formatado para exibição
     */
    public String formatado() {
        return numero.length() == 11
                ? formatarCelular()
                : formatarFixo();
    }

    // ======================
    // Regras internas
    // ======================

    private static String removerMascara(String valor) {
        if (valor == null || valor.isBlank()) {
            throw new IllegalArgumentException("Telefone não pode ser vazio");
        }
        return valor.replaceAll("\\D", "");
    }

    private static boolean telefoneValido(String numero) {
        return numero.length() == 10 || numero.length() == 11;
    }

    private String formatarCelular() {
        return String.format("(%s) %s-%s",
                numero.substring(0, 2),
                numero.substring(2, 7),
                numero.substring(7));
    }

    private String formatarFixo() {
        return String.format("(%s) %s-%s",
                numero.substring(0, 2),
                numero.substring(2, 6),
                numero.substring(6));
    }
}
