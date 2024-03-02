let ataqueJugador;
let ataqueEnemigo;

let vidasJugador = 3;
let vidasEnemigo = 3;

function iniciarJuego(){

    let sectionReiniciar =document.getElementById('reiniciar');
    sectionReiniciar.style.display = 'none';

    let sectionSeleccionarAtaque =document.getElementById('seleccionar-ataque');
    sectionSeleccionarAtaque.style.display = 'none';

    let botonMascotaJugador = document.getElementById('boton-mascota');
    botonMascotaJugador.addEventListener('click', seleccionarMascotaJugador);

    let botonFuego = document.getElementById('boton-fuego');
    botonFuego.addEventListener('click', ataqueFuego);

    let botonAgua = document.getElementById('boton-agua');
    botonAgua.addEventListener('click', ataqueAgua);

    let botonTierra = document.getElementById('boton-tierra');
    botonTierra.addEventListener('click', ataqueTierra);
    
    let botonReiniciar = document.getElementById('boton-reiniciar');
    botonReiniciar.addEventListener('click', reiniciarJuego);
}

function seleccionarMascotaJugador(){

    let sectionSeleccionarMascota =document.getElementById('seleccionar-mascota');
    sectionSeleccionarMascota.style.display = 'none';

    let sectionSeleccionarAtaque =document.getElementById('seleccionar-ataque');
    sectionSeleccionarAtaque.style.display = 'block';

    let inputHipodoge = document.getElementById('hipodoge');
    let inputCapipepo = document.getElementById('capipepo');
    let inputRatigueya = document.getElementById('ratigueya');

    let spanMascotaJuagor = document.getElementById('mascota-jugador');

    if(inputHipodoge.checked){
        spanMascotaJuagor.innerHTML = 'Hipodoge';
    } else if (inputCapipepo.checked){
        spanMascotaJuagor.innerHTML = 'Capipepo';
           
    } else if (inputRatigueya.checked){
        spanMascotaJuagor.innerHTML = 'Ratigueya';
    }else{
        alert('Selecciona una mascota');
    }

    seleccionarMascotaEnemigo();
}

function seleccionarMascotaEnemigo(){
    let mascotaAleatorio = aleatorio(1,3);

    let spanMascotaEnemigo = document.getElementById('mascota-enemigo');

    if(mascotaAleatorio==1){
        //Hipodoge
        spanMascotaEnemigo.innerHTML = 'Hipodoge';
    }else if(mascotaAleatorio==2){
        //Capipepo
        spanMascotaEnemigo.innerHTML= 'Capipepo';
    }else{
        //Ratigueya
        spanMascotaEnemigo.innerHTML = 'Ratigueya';
    
    }

}

function ataqueFuego(){
    ataqueJugador = 'FUEGO';
    ataqueAleatorioEnemigo();
}

function ataqueAgua(){
    ataqueJugador = 'AGUA';
    ataqueAleatorioEnemigo();
}

function ataqueTierra(){
    ataqueJugador = 'TIERRA';
    ataqueAleatorioEnemigo();
}

function ataqueAleatorioEnemigo(){
    let ataqueAleatorio = aleatorio(1,3);

    if(ataqueAleatorio ==1){
        ataqueEnemigo = 'FUEGO';        
    }else if(ataqueAleatorio ==2){
        ataqueEnemigo = 'AGUA';
    } else{
        ataqueEnemigo = 'TIERRA';
    }
    combate();

}

function combate(){

    let spanVidasJugador = document.getElementById('vidas-jugador');
    let spanVidasEnemigo = document.getElementById('vidas-enemigo');

    if(ataqueEnemigo == ataqueJugador){
        crearMensaje('EMPATE');
    }else if (ataqueJugador == 'FUEGO '&& ataqueEnemigo =='TIERRA'){
        crearMensaje('GANASTE');
        vidasEnemigo --;
        spanVidasEnemigo.innerHTML = vidasEnemigo;

    }else if (ataqueJugador == 'AGUA' && ataqueEnemigo == 'FUEGO'){
        crearMensaje('GANASTE');

        vidasEnemigo --;
        spanVidasEnemigo.innerHTML = vidasEnemigo;


    }else if (ataqueJugador == 'TIERRA' && ataqueEnemigo == 'AGUA'){
        crearMensaje('GANASTE');

        vidasEnemigo --;
        spanVidasEnemigo.innerHTML = vidasEnemigo;

    }else{
        crearMensaje('PERDISTE');

        vidasJugador --;
        spanVidasJugador.innerHTML = vidasJugador;
    }

    //Revisar las vidas
    revisarVidas();

}

function revisarVidas(){
    if(vidasEnemigo == 0){
        crearMensajeFinal(' FELICITACIONES GANASTE 🥳');

    }else if(vidasJugador == 0){
        crearMensajeFinal('PERDISTE 😭');

    }
}


function crearMensaje(resultado){
    let seccionMensajes = document.getElementById('mensajes');

    let parrafo = document.createElement('p');

    parrafo.innerHTML = 'Tu mascota atacó  ' + ataqueJugador + ', la mascota del enemigo atacó con ' + ataqueEnemigo +  '- '+ resultado;

    seccionMensajes.appendChild(parrafo);    
}

function crearMensajeFinal(resultadoFinal){
    let seccionMensajes = document.getElementById('mensajes');

    let parrafo = document.createElement('p');

    parrafo.innerHTML = resultadoFinal;

    seccionMensajes.appendChild(parrafo);

    let botonFuego = document.getElementById('boton-fuego');
    botonFuego.disabled = true;

    let botonAgua = document.getElementById('boton-agua');
    botonAgua.disabled = true;

    let botonTierra = document.getElementById('boton-tierra');
    botonTierra.disabled = true;
    
    let sectionReiniciar =document.getElementById('reiniciar');
    sectionReiniciar.style.display = 'block';
    
}

function reiniciarJuego(){
    location.reload();
}

function aleatorio(min, max){
    return Math.floor(Math.random() * (max - min + 1) + min);    
}

window.addEventListener('load', iniciarJuego);