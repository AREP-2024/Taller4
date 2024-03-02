package edu.escuelaing.arep.ASE.app;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

import edu.escuelaing.arep.ASE.app.config.Scanner;
import edu.escuelaing.arep.ASE.app.controller.MySparkController;
import edu.escuelaing.arep.ASE.app.http.Registry;
import edu.escuelaing.arep.ASE.app.http.Request;
import edu.escuelaing.arep.ASE.app.http.Response;
import edu.escuelaing.arep.ASE.app.exception.HttpException;

public class HttpServer {

    private static final String GET_URL="https://www.omdbapi.com/";
    private static final String GET_KEY="926dbc03";

    private static final String MI_RUTA= "static/";
    public static void main(String[] args) throws IOException, ClassNotFoundException {

        Scanner escaner = new Scanner();
        Registry  registry = escaner.getRegistry();

        System.out.println("registry " +registry);
        /*MySparkController mySparkController = new MySparkController();

        mySparkController.get("/Luisa", (req, res) -> {
            return "Luisa Fernanda Bermudez Giron";
        });

        mySparkController.post("/Luisa", (req,res)->{
            return "POST Luisa Giron";
        });*/
 
        ServerSocket serverSocket = null;
        try {
            serverSocket = new ServerSocket(35000);
        } catch (IOException e) {
            System.err.println("Could not listen on port: 35000.");
            System.exit(1);
        }
        boolean running = true;
        while(running){
            Socket clientSocket = null;
            try {
                System.out.println("Listo para recibir ...");
                clientSocket = serverSocket.accept();
            } catch (IOException e) {                
                System.err.println("Accept failed.");
                System.exit(1);
            }

            BufferedReader in = new BufferedReader(
                    new InputStreamReader(
                            clientSocket.getInputStream()));
            String inputLine;

            String esLaPrimera=null;
    
            while ((inputLine = in.readLine()) != null) {
                if (esLaPrimera==null) {
                    esLaPrimera = inputLine;                    
                }                                
                System.out.println("Received: " + inputLine);
                if (!in.ready()) {
                    break;
                }
            }
            if(esLaPrimera==null){
                continue;
            }           

            String metodoHttp = esLaPrimera.split(" ")[0];
            String recurso = esLaPrimera.split(" ")[1];

            System.out.println("metodoHttp " +metodoHttp);
            System.out.println("recurso" + recurso);


            if (registry.tieneEndPoint(recurso)) {
                resolverController(recurso, clientSocket, registry , metodoHttp);
                
            } else if(recurso.equals("/favicon.ico")){
                resolverImagen(clientSocket, recurso);
            }
            else if (recurso.endsWith(".png")  ) {
                
                resolverImagen(clientSocket,recurso);
                
            }else{
                resolverTextoPlano(clientSocket,recurso);
            }
            
            in.close();
            clientSocket.close();
        }

        serverSocket.close();
    }


    /*
        * Metodo que resuelve la peticion de una imagen
        * @param clientSocket socket del cliente
        * @param recurso nombre de la imagen
        * @throws IOException si hay un error en la conexion
        * @throws FileNotFoundException si no se encuentra la imagen
     */
    private static void resolverImagen(Socket clientSocket, String recurso) throws IOException{        

        try{
            FileInputStream fileInputStream = new FileInputStream(MI_RUTA + recurso.split("/")[1]);
            long tamanoArchivo = fileInputStream.available();
            byte[] imagenBytes = new byte[(int) tamanoArchivo];
            fileInputStream.read(imagenBytes);
            fileInputStream.close();
    
            OutputStream out = clientSocket.getOutputStream();
    
            out.write("HTTP/1.1 200 OK\r\n".getBytes());
            out.write("Content-Length: ".getBytes());
            out.write(String.valueOf(imagenBytes.length).getBytes());
            out.write("\r\n".getBytes());
            out.write("Content-Type: image/png\r\n".getBytes());
            out.write("\r\n".getBytes());
    
            out.write(imagenBytes);
            out.flush();
            out.close();

        }catch(FileNotFoundException e){
            resolverError(clientSocket);
        }


    }  

    /*
        * Metodo que resuelve la peticion de un texto plano
        * @param clientSocket socket del cliente
        * @param recurso nombre del recurso
        * @throws IOException si hay un error en la conexion
        * @throws FileNotFoundException si no se encuentra el recurso     
     */
    private static void resolverTextoPlano(Socket clientSocket, String recurso) throws IOException{
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        String outputLine = "";
        try{            
            HttpConnection httpConnection = new HttpConnection(GET_URL,GET_KEY);     
            
            if (recurso.startsWith("/Peliculas")) {
                outputLine = mostrarPelicula(httpConnection,recurso.split("/")[2]);                
            }else if (recurso.equals("/")){                
                outputLine = mostrarPagina("/pagina.html");
            }else if(recurso.endsWith(".css")){
                outputLine = mostrarEstilos(recurso);
            }else if(recurso.endsWith(".html") || recurso.endsWith(".js")){
                outputLine = mostrarPagina(recurso);            
            }else{
                throw new FileNotFoundException("No se encontro el recurso solicitado");
            }
            out.println(outputLine);
            out.close(); 

        }catch(FileNotFoundException e){
            resolverError(clientSocket);
        }      

    }

    /*
        * Metodo que muetra la pagina principal
        * @return String con la pagina principal
     */
    public static String mostrarPagina(String path) throws IOException{

        StringBuilder pagina = new StringBuilder();

        String outputLine;

        pagina.append("HTTP/1.1 200 OK\r\n");
        pagina.append("Content-Type: text/html\r\n");
        pagina.append("\r\n");
        File file = new File(MI_RUTA+path.split("/")[1]);
        BufferedReader reader = new BufferedReader(new FileReader(file));  
        while((outputLine = reader.readLine()) != null){
            pagina.append(outputLine);
            pagina.append("\n");
        }

        reader.close();
        return pagina.toString();    

    }


    /*
        * Metodo que muetra los estilos de la pagina
        * @param path ruta del archivo de estilos
        * @return String con los estilos
        * @throws IOException si hay un error en la conexion
        * @throws FileNotFoundException si no se encuentra el archivo de estilos
     */
    public static String mostrarEstilos(String path) throws IOException{
        
        StringBuilder pagina = new StringBuilder();
        String outputLine;
        pagina.append("HTTP/1.1 200 OK\r\n");
        pagina.append("Content-Type: text/css\r\n");
        pagina.append("\r\n");
        File file = new File(MI_RUTA+path.split("/")[1]);
        BufferedReader reader = new BufferedReader(new FileReader(file));  
        while((outputLine = reader.readLine()) != null){
            pagina.append(outputLine);
            pagina.append("\n");
        }

        reader.close();
        return pagina.toString();

    }


    
    /*
        * Metodo que muetra la informacion de una pelicula
        * @param httpConnection conexion con la api
        * @param nameMovie nombre de la pelicula
        * @return String con la informacion de la pelicula
     */
    public static String mostrarPelicula(HttpConnection httpConnection, String nameMovie) throws IOException{
        StringBuilder pagina = new StringBuilder();

        String outputLine = httpConnection.infoMovieWithCache(nameMovie);

        pagina.append("HTTP/1.1 200 OK\r\n");
        pagina.append("Content-Type: text/json\r\n");
        pagina.append("\r\n");
        pagina.append(outputLine);

        return pagina.toString();

    }


    /*
        * Metodo que resuelve un error 404
        * @param clientSocket socket del cliente
        * @throws IOException si hay un error en la conexion 
     */
    private static void  resolverError(Socket clientSocket) throws IOException{
        PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
        
        String outputLine = "HTTP/1.1 404 Not Found\r\n"
        + "Content-Type: text/html\r\n"
        + "\r\n"
        + "<!DOCTYPE html>\n"
        + "<html>\n"
        + "<head>\n"
        + "<meta charset=\"UTF-8\">\n"
        + "<title>Error</title>\n"
        + "</head>\n"
        + "<body>\n"
        + "404 NOT FOUND \n"
        + "</body>\n"
        + "</html>\n";
        out.println(outputLine);
        out.close(); 
    }


    /*
        * Metodo que resuelve un controlador
        * @param endPoint el endPoint que se va a resolver
        * @param clientSocket socket del cliente
        * @param mySparkController controlador de spark
        * @param metodoHttp metodo http de la peticion
        * @throws IOException si hay un error en la conexion
     */
    public static void resolverController(String endPoint, Socket clientSocket, Registry registro, String metodoHttp) throws IOException{

        try( PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true)){
            var respuesta = switch (metodoHttp){
                case "GET" -> registro.doGet(endPoint, new Request(), new Response());
                case "POST" -> registro.doPost(endPoint, new Request(), new Response());
                default -> throw new HttpException();
            };        
            
            System.out.println(respuesta.toString());
            StringBuilder outputLine = new StringBuilder();
            
            outputLine.append("HTTP/1.1 200 OK\r\n");
            outputLine.append("Content-Type: text/json\r\n");
            outputLine.append("\r\n");
            outputLine.append(respuesta.toString());
            out.println(outputLine.toString());         

        }catch(HttpException e){
            resolverError(clientSocket);         
        }
        

    }
    
}
