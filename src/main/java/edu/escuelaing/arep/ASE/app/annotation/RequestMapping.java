package edu.escuelaing.arep.ASE.app.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import edu.escuelaing.arep.ASE.app.http.Metodo;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface RequestMapping {

    String ruta();

    Metodo metodo();
    
}
