package edu.escuelaing.arep.ASE.app.controller;

import edu.escuelaing.arep.ASE.app.annotation.Controller;
import edu.escuelaing.arep.ASE.app.annotation.RequestMapping;
import edu.escuelaing.arep.ASE.app.http.Metodo;

@Controller
public class ControladorBase {

    @RequestMapping(ruta = "/Luisa", metodo = Metodo.GET)
    public String getPostman(){
        return "GET, Luisa Fernanda Bermudez Giron";
    }

    @RequestMapping(ruta = "/Luisa", metodo = Metodo.POST)
    public String postPostman(){
        return "POST, Luisa Fernanda Bermudez Giron";
    }


    
}
