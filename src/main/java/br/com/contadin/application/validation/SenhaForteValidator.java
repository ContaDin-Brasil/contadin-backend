package br.com.contadin.application.validation;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.regex.Pattern;

public class SenhaForteValidator implements ConstraintValidator<SenhaForte, String> {

    private static final Pattern NUMERO = Pattern.compile("\\d");
    private static final Pattern ESPECIAL = Pattern.compile("[!@$%&]");
    private static final Pattern SEQUENCIA_NUMERICA = Pattern.compile(
            "012|123|234|345|456|567|678|789|987|876|765|654|543|432|321|210"
    );
    private static final Pattern NUMERO_REPETIDO = Pattern.compile("(\\d)\\1\\1");

    @Override
    public boolean isValid(String senha, ConstraintValidatorContext context) {

        // @NotBlank já cuida de null/vazio
        if (senha == null) {
            return true;
        }

        return senha.length() >= 8
                && NUMERO.matcher(senha).find()
                && ESPECIAL.matcher(senha).find()
                && !SEQUENCIA_NUMERICA.matcher(senha).find()
                && !NUMERO_REPETIDO.matcher(senha).find();
    }
}