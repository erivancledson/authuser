package com.ead.authuser.validation;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.*;

@Documented
@Constraint(validatedBy = UsernameConstraintImpl.class) //qual a classe que vai conter a anotação especifica
@Target({ElementType.METHOD, ElementType.FIELD}) //aonde que podemos usar a anotação, metodo e em campo
@Retention(RetentionPolicy.RUNTIME) //quando a validação vai ocorrer em tempo de execução
public @interface UsernameConstraint {
    String message() default "Invalid username"; //são parametros padrão do bean validation
    Class<?>[] groups() default{}; //são parametros padrão do bean validation
    Class<? extends Payload>[] payload() default{}; //são parametros padrão do bean validation
}
