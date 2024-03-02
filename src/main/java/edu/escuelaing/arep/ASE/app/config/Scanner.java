package edu.escuelaing.arep.ASE.app.config;

import java.io.File;
import java.io.FileNotFoundException;
import edu.escuelaing.arep.ASE.app.annotation.Controller;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
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

    public Scanner(String controlador) throws FileNotFoundException, ClassNotFoundException{
        String rutaRaiz = System.getProperty("user.dir");
        String rutaInicio = rutaRaiz + "/target/classes/edu";

        System.out.println();
        this.registry = new Registry();
        try {
            buscarDirectorios(new File(rutaInicio));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

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
            
            String classpath = System.getProperty("java.class.path");
            
            Class c = Class.forName(extractFullyQualifiedClassName(fileName, classpath));

            try{
                registrarMetodo(c);

            }catch(NoSuchMethodException e){
            }          

        }        

    }

    private String extractFullyQualifiedClassName(String classFilePath, String classpath) {
        String separator = File.separator;

        String relativePath = classFilePath.substring(classpath.length() + 1);

        return relativePath.replace(separator, ".").replace(".class", "");
    }


    private<T> void registrarMetodo(Class<T> c) throws NoSuchMethodException {

        Constructor<T> constructor = c.getConstructor();
            
        if (c.isAnnotationPresent(Controller.class)) {
            System.out.println(":)");
            for(Method method: c.getDeclaredMethods()){
                if (method.isAnnotationPresent(RequestMapping.class)) {
                    System.out.println(":) :)");
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
