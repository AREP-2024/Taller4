package edu.escuelaing.arep.ASE.app.controller;

import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;

import edu.escuelaing.arep.ASE.app.http.Registry;
import edu.escuelaing.arep.ASE.app.http.Request;
import edu.escuelaing.arep.ASE.app.http.Response;

import edu.escuelaing.arep.ASE.app.exception.HttpException;


public class MySparkController {

    private Registry registros = new Registry();

    /*
        * Metodo que registra un endPoint con el metodo GET
        * @param endPoint el endPoint que se va a registrar
        * @param handler el manejador que se va a registrar
     */    
    public void get(String endPoint, BiFunction<Request, Response, ?> handler){
        registros.get(endPoint, handler);
    }

    /*
        * Metodo que registra un endPoint con el metodo POST
        * @param endPoint el endPoint que se va a registrar
        * @param handler el manejador que se va a registrar
     */
    public void post(String endPoint, BiFunction<Request, Response, ?> handler){
        registros.post(endPoint, handler);

    }

    /*
        * Metodo que ejecuta un endPoint con el metodo GET
        * @param endPoint el endPoint que se va a ejecutar
        * @param request el request que se va a ejecutar
        * @param response el response que se va a ejecutar
        * @return el resultado de la ejecucion
     */
    public <R> R doGet(String endPoint, Request request, Response response){
        return registros.doGet(endPoint, request, response);
    }


    /*
        * Metodo que ejecuta un endPoint con el metodo POST
        * @param endPoint el endPoint que se va a ejecutar
        * @param request el request que se va a ejecutar
        * @param response el response que se va a ejecutar
        * @return el resultado de la ejecucion
     */
    public <R> R doPost(String endPoint, Request request, Response response){
        return registros.doPost(endPoint, request, response);
    }

    /*
        * Metodo que verifica si un endPoint esta registrado
        * @param endPoint el endPoint que se va a verificar
        * @return si el endPoint esta registrado
     */
    public boolean tieneEndPoint(String endPoint){
        return registros.tieneEndPoint(endPoint);
    }
    
}
