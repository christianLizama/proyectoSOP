/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectosop;

/**
 *
 * @author Diego Aguilera
 */
public class Proceso {
    String nombre;
    int prioridad;
    int tamanio;
    int tiempo;

    public Proceso(String nombre, int prioridad, int tamanio, int tiempo) {
        this.nombre = nombre;
        this.prioridad = prioridad;
        this.tamanio = tamanio;
        this.tiempo = tiempo;
    }
    
    public void Imprimir(){
        System.out.println("Nombre: "+nombre);
        System.out.println("Prioridad: "+prioridad);
        System.out.println("tamanio: "+tamanio);
        System.out.println("tiempo: "+tiempo);
    }

    public int getTiempo() {
        return tiempo;
    }

    public void setTiempo(int tiempo) {
        this.tiempo = tiempo;
    }
    
    public String getNombre() {
        return nombre;
    }

    public void setNombre(String nombre) {
        this.nombre = nombre;
    }

    public int getPrioridad() {
        return prioridad;
    }

    public void setPrioridad(int prioridad) {
        this.prioridad = prioridad;
    }

    public int getTamanio() {
        return tamanio;
    }

    public void setTamanio(int tamanio) {
        this.tamanio = tamanio;
    }
    
}
