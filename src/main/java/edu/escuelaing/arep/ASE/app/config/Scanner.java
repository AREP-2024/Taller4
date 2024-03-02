package edu.escuelaing.arep.ASE.app.config;

import java.io.File;
import java.io.FileNotFoundException;
import edu.escuelaing.arep.ASE.app.annotation.Controller;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Map;
import java.util.function.BiFunction;
import java.lang.reflect.Type;
import java.security.NoSuchAlgorithmException;

import edu.escuelaing.arep.ASE.app.http.Metodo;
import edu.escuelaing.arep.ASE.app.annotation.RequestMapping;
import edu.escuelaing.arep.ASE.app.http.Registry;
import edu.escuelaing.arep.ASE.app.http.Request;
import edu.escuelaing.arep.ASE.app.http.Response;

public class Scanner {
    private Registry registry;

    /*
        * Constructor de Scanner que inicializa el escaneo de un directorio en especifico
        * @param controlador la ruta del controlador para iniciar el escaneo
        * @throws FileNotFoundException si no puede encontrar un archivo o directorio especifico.
        * @throws ClassNotFoundException si la clase no se puede cargar durante el escaneo.
     */

    public Scanner(String controlador) throws FileNotFoundException, ClassNotFoundException{
        String rutaRaiz = System.getProperty("user.dir");
        String rutaInicio = rutaRaiz + "/target/classes/edu";

    
        this.registry = new Registry();
        try {
            buscarDirectorios(new File(rutaInicio));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    /*
        * Constructor de Scanner que llama al constructor sin argumentos.
        * @throws FileNotFoundException si no puede encontrar un archivo o directorio especifico.
        * @throws ClassNotFoundException si la clase no se puede cargar durante el escaneo.
    */
    public Scanner() throws FileNotFoundException, ClassNotFoundException{
        this("");        
    }

    public Registry getRegistry(){
        return registry;
    }

    private void buscarDirectorios(File fileRaiz) throws FileNotFoundException, ClassNotFoundException, NoSuchMethodException, SecurityException{
        
        if (fileRaiz== null) throw new  FileNotFoundException();                 
        
        if (fileRaiz.isDirectory()){
            
            for(File file: fileRaiz.listFiles()){
                buscarDirectorios(file);
            }

        }else{
            String fileName = fileRaiz.getAbsolutePath();
            
           
            
            String nombre = extractFullyQualifiedClassName(fileName);
            System.out.println("nombre "+nombre);

            Class c = Class.forName(nombre);

            try{
                registrarMetodo(c);

            }catch(NoSuchMethodException e){
            }          

        }        

    }

    private String extractFullyQualifiedClassName(String classFilePath) {
        
        String separator = File.separator.replace("\\","\\\\");

        String[] secciones = classFilePath.split(separator);
        int indice = 0;
        boolean loEncontro = false;
        while (indice<secciones.length && !loEncontro) {
            if (secciones[indice].equals("target")) {
                if(indice+1 < secciones.length && secciones[indice+1].equals("classes")){
                    loEncontro = true;
                    indice++;
                }
            }
            indice++;
            
        }
        return String.join(".", Arrays.copyOfRange(secciones, indice, secciones.length))
                    .replace(".class", "");

        
    }


    private<T> void registrarMetodo(Class<T> c) throws NoSuchMethodException {

        Constructor<T> constructor = c.getConstructor();
            
        if (c.isAnnotationPresent(Controller.class)) {
           
            for(Method method: c.getDeclaredMethods()){
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    
                    RequestMapping requestMapping = method.getAnnotation(RequestMapping.class);
                    Class<?> retorno = method.getReturnType();
                    
                    BiFunction<Request, Response, ?> funcion = (req, res)-> {
                        try {
                            Object result = method.invoke(constructor.newInstance(), (Object[])method.getParameterTypes());
                            return retorno.cast(result);
                        } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException
                                | InstantiationException e) {
                            e.printStackTrace();
                            return null;
                        }
                       
                    };
                    switch (requestMapping.metodo()) {
                        case GET -> registry.get(requestMapping.ruta(), funcion);

                        case POST -> registry.post(requestMapping.ruta(), funcion);

                        default -> throw new NoSuchMethodException();
                    }
                    
                }
            }                
        }
        
    }   

}
