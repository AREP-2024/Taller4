package edu.escuelaing.arep.ASE.app.http;

import java.util.function.BiFunction;

public class HttpFunction {
    private String metodoHttp;
    private BiFunction<Request, Response, ?> handler;

    public HttpFunction(String metodoHttp, BiFunction<Request, Response, ?> handler) {
        this.metodoHttp = metodoHttp;
        this.handler = handler;
    }

    /*
        * Metodo que ejecuta el hadler del endPoint
        * @param request el request que se va a ejecutar
        * @param response el response que se va a ejecutar
        * @return el resultado de la ejecucion
     */
    public <R> R execute(Request request, Response response) {
        return (R) handler.apply(request, response);
    }


    /*
        * se utiliza para determinar si el método HTTP asociado con ese objeto es igual al método HTTP proporcionado como argumento
        * @param metodoHttp el metodoHttp que se va a verificar
        * @return si el metodoHttp es igual al metodoHttp del objeto
     */
    public boolean verificarMetodoHttp(String metodoHttp) {
        return this.metodoHttp.equals(metodoHttp);
    }
    
}
