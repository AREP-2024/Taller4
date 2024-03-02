package edu.escuelaing.arep.ASE.app.http;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.BiFunction;

import edu.escuelaing.arep.ASE.app.http.Request;
import edu.escuelaing.arep.ASE.app.http.Response;
import edu.escuelaing.arep.ASE.app.exception.HttpException;

public class Registry {

    private Map<String, Collection <HttpFunction>> registros;

    public Registry() {
        this.registros = new HashMap<>();
    }

    /*
        * Metodo que registra un endPoint con el metodo GET
        * @param endPoint el endPoint que se va a registrar
        * @param handler el manejador que se va a registrar 
     */
    public <R> void get(String endPoint, BiFunction<Request, Response, R> handler){
        if (!verificarEndPoint(endPoint, true)){
            throw new HttpException ();
        }

        if (!registros.containsKey(endPoint)) {
            registros.put(endPoint, new ArrayList<>());            
        }
        registros.get(endPoint).add(new HttpFunction("GET", handler));
    }

    
    /*
        * Metodo que ejecuta un endPoint con el metodo GET
        * @param endPoint el endPoint que se va a ejecutar
        * @param request el request que se va a ejecutar
        * @param response el response que se va a ejecutar
    */
    public <R> R doGet(String endPoint, Request request, Response response){
        String endPointFinal = buscarEndPoint(endPoint);
        if (endPointFinal.equals("")){
            throw new HttpException ();
        }
        HttpFunction funcion = registros.get(endPointFinal).stream()
            .filter((httpFunction) -> httpFunction.verificarMetodoHttp("GET"))
            .findFirst()
            .orElseThrow(HttpException::new);
        return funcion.execute(request, response);
    } 


    /*
        * Metodo que registra un endPoint con el metodo POST
        * @param endPoint el endPoint que se va a registrar
        * @param handler el manejador que se va a registrar
     */
    public <R> void post(String endPoint, BiFunction<Request, Response, R> handler){
        if (!verificarEndPoint(endPoint, true)){
            throw new HttpException ();
        }

        if (!registros.containsKey(endPoint)) {
            registros.put(endPoint, new ArrayList<>());
            
        }
        registros.get(endPoint).add(new HttpFunction("POST", handler));
    }


    /*
        * Metodo que ejecuta un endPoint con el metodo POST
        * @param endPoint el endPoint que se va a ejecutar
        * @param request el request que se va a ejecutar
        * @param response el response que se va a ejecutar
     */
    public <R> R doPost(String endPoint, Request request, Response response){
        String endPointFinal = buscarEndPoint(endPoint);
        if (endPointFinal.equals("")){
            throw new HttpException ();
        }
        HttpFunction funcion = registros.get(endPointFinal).stream()
            .filter((httpFunction) -> httpFunction.verificarMetodoHttp("POST"))
            .findFirst()
            .orElseThrow(HttpException::new);
        return funcion.execute(request, response);
    } 

    /*
        * Metodo que busca el endPoint mas largo que coincida con el endPoint que se le pasa
        * @param endPoint el endPoint que se va a buscar
        * @return el endPoint mas largo que coincida con el endPoint que se le pasa
     */
    private String buscarEndPoint (String endPoint){
        if (!verificarEndPoint(endPoint, false)) {
            throw new HttpException ();
        }
        String [] resultado = new String[0];
        String [] compararEndPoint = endPoint.substring(1).split("/");        
        String resultadoFinal = "";
        for (String key : registros.keySet()) {
            String [] compararKey = key.substring(1).split("/");
            if(compararEndPoint.length != compararKey.length){
                continue;
            }

            boolean esIgual = true;
            for (int i = 0; i < compararEndPoint.length; i++) {

                if(!compararKey[i].startsWith(":") && !compararEndPoint[i].equals(compararKey[i])) {
                    esIgual = false;
                    break;
                }
            }
            if (esIgual && compararKey.length > resultado.length) {
                resultado = compararKey;
                resultadoFinal = key;                
            }

        }
        return resultadoFinal;

    }

    /*
        * Valida si el endPoint es valido
        * @param endPoint el endPoint que se va a verificar
        * @return si el endPoint esta registrado
     */
    public boolean verificarEndPoint(String endPoint, boolean esRegistro){
        if (!endPoint.startsWith("/")) {
            return false;            
        } else if (endPoint.equals("/")) {
            return true;
        }

        Set<Character> patron = Set.of(
            '*',
            '+',
            '^',
            '$',
            '#',
            ' '
        );
    
        return Arrays.asList(endPoint.substring(1).split("/")).parallelStream()
            .allMatch((seccion)->
                seccion != null && 
                !(seccion.equals("")) && 
                patron.stream().noneMatch((caracter)->seccion.indexOf(caracter)!=-1) &&
                (esRegistro || !seccion.startsWith(":"))
    
            );


    }

    /*
        * Metodo que verifica si un endPoint esta registrado
        * @param endPoint el endPoint que se va a verificar
        * @return si el endPoint esta registrado
     */
    public boolean tieneEndPoint (String endPoint){
        String resultBuscarEndPoint = this.buscarEndPoint(endPoint);

        return resultBuscarEndPoint != null && !resultBuscarEndPoint.equals("");
    }

    @Override
    public String toString(){
        StringBuilder aux = new StringBuilder();
        this.registros.forEach((key, value)->{
            aux.append(key);
            aux.append("\n");
        });
        return aux.toString();

    }


    
}
