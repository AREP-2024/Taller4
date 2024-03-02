package edu.escuelaing.arep.ASE.app;

 import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThrows;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import edu.escuelaing.arep.ASE.app.exception.HttpException;
import edu.escuelaing.arep.ASE.app.http.Registry;

public class RegistryTest {

    @Test
    public void deberiaRegistrarGet () throws HttpException{
        Registry registry = new Registry();
        registry.get("/prueba1", (req,res)->{
            return "Hola esta es la prueba de unidad del GET";
        });
        var resultado = registry.doGet("/prueba1", null, null);

        assertTrue(registry.tieneEndPoint("/prueba1"));
        assertNotNull(resultado);
        assertEquals("Hola esta es la prueba de unidad del GET", resultado);
    }

    @Test
    public void deberiaRegistrarPost () throws HttpException{
        Registry registry = new Registry();
        registry.post("/prueba2", (req,res)->{
            return "Hola esta es la prueba de unidad del POST";
        });
        var resultado = registry.doPost("/prueba2", null, null);

        assertTrue(registry.tieneEndPoint("/prueba2"));
        assertNotNull(resultado);
        assertEquals("Hola esta es la prueba de unidad del POST", resultado);
    }
    
    @Test
    public void deberiaLanzarExcepcionSiNoTieneEndPointGet(){
        Registry registry = new Registry();
        assertThrows(HttpException.class, ()-> registry.doGet("/prueba3", null, null));
        registry.get("/prueba3", (req,res)->{
            return "Hola esta es la prueba de unidad del GET(Lanza Excepcion)";
        });
        var resultado = registry.doGet("/prueba3", null, null);

        assertTrue(registry.tieneEndPoint("/prueba3"));
        assertNotNull(resultado);
        assertEquals("Hola esta es la prueba de unidad del GET(Lanza Excepcion)", resultado);

    }

    @Test
    public void deberiaLanzarExcepcionSiNoTieneEndPointPost(){
        Registry registry = new Registry();
        assertThrows(HttpException.class, ()-> registry.doPost("/prueba4", null, null));
        registry.post("/prueba4", (req,res)->{
            return "Hola esta es la prueba de unidad del POST(Lanza Excepcion)";
        });
        var resultado = registry.doPost("/prueba4", null, null);

        assertTrue(registry.tieneEndPoint("/prueba4"));
        assertNotNull(resultado);
        assertEquals("Hola esta es la prueba de unidad del POST(Lanza Excepcion)", resultado);

    }

    @Test
    public void noDeberiaPermitirEndPointMalEstructurados(){
        Registry registry = new Registry();
        assertTrue(registry.verificarEndPoint("/",false));
        assertTrue(registry.verificarEndPoint("/prueba",false));
        assertTrue(registry.verificarEndPoint("/:prueba",true));

        assertFalse(registry.verificarEndPoint("+",false));
        assertFalse(registry.verificarEndPoint("P*ueba",false));
        assertFalse(registry.verificarEndPoint("/P#ueba",false));
        assertFalse(registry.verificarEndPoint("/:",false));
        
    }
    
}
