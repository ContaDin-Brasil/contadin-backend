package br.com.contadin.application.validation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = SenhaForteValidator.class)
@Target({ ElementType.FIELD, ElementType.PARAMETER })
@Retention(RetentionPolicy.RUNTIME)
public @interface SenhaForte {

    String message() default "Senha não atende aos critérios de segurança";

    Class<?>[] groups() default {};

    Class<? extends Payload>[] payload() default {};
}