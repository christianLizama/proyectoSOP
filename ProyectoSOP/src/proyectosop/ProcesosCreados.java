/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectosop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import javafx.scene.text.Text;
import static proyectosop.FXMLDocumentController.pizarra;

/**
 *
 * @author ckill
 */
public class ProcesosCreados {
    
    ArrayList<Proceso> procesosCreados = new ArrayList<Proceso>();
    ArrayList<Proceso> procesosEsperando = new ArrayList<Proceso>();
    ArrayList<Proceso> procesosEspAlta = new ArrayList<Proceso>();
    ArrayList<Proceso> procesosEspBaja = new ArrayList<Proceso>();
    ArrayList<Proceso> procesosEjecutandose = new ArrayList<Proceso>();
    ArrayList<Proceso> procesosBorrados = new ArrayList<Proceso>();
    
    int ramActual=8;

    public int getRamActual() {
        return ramActual;
    }
    
    public ArrayList<Proceso> getProcesosCreados() {
        return procesosCreados;
    }

    public void setProcesosCreados(ArrayList<Proceso> procesosCreados) {
        this.procesosCreados = procesosCreados;
    }

    public ArrayList<Proceso> getProcesosEsperando() {
        return procesosEsperando;
    }

    public void setProcesosEsperando(ArrayList<Proceso> procesosEsperando) {
        this.procesosEsperando = procesosEsperando;
    }

    public ArrayList<Proceso> getProcesosEjecutandose() {
        return procesosEjecutandose;
    }

    public void setProcesosEjecutandose(ArrayList<Proceso> procesosEjecutandose) {
        this.procesosEjecutandose = procesosEjecutandose;
    }
    
    public void ordenar(ArrayList<Proceso> p){
        Collections.sort(p, (Proceso t, Proceso t1) -> {
            if( t.getPrioridad() > t1.getPrioridad()){
                return 1;
            }
            if( t.getPrioridad() < t1.getPrioridad()){
                return -1;
            }
            return 0;
        });
    }
    
    public int restarEjecutados(int ramDisp){
        ArrayList<Proceso> aux = new ArrayList<>();
        for (Proceso proceso : procesosEjecutandose) {
            proceso.restarUnTiempo();
            if(proceso.getTiempo()!=0){
                aux.add(proceso);
            }
            //Si el tiempo es igual a 0
            else{
                
                //FXMLDocumentController.pizarra[0]="ss";
                for (int i = 0; i < 8; i++) {
                    String prioridadT="";
                    if(proceso.getPrioridad()==0){
                         prioridadT="Baja";
                    }
                    else{
                        prioridadT="Alta";
                    }
                    String comparacion = " "+proceso.getNombre()+" Prioridad: "+prioridadT;
                    String[] pizzaraCortada = FXMLDocumentController.pizarra[i].split(" Tiempo");
                    String part1 = pizzaraCortada[0];
                    if(comparacion.equals(part1)){
                        FXMLDocumentController.pizarra[i]="0";
                    }
                    for (int j = 0; j < 8; j++) {
                        System.out.println(FXMLDocumentController.pizarra[j]);
                    }
                }
                
                
                
                procesosBorrados.add(proceso);
                //System.out.println("Ram disponible antes de borrar un proceso: "+ramDisp);
                //System.out.println("Tamaño Proceso eliminado: "+proceso.getTamanio());
                ramDisp+=proceso.getTamanio();
                //System.out.println("Ram disponible despues de borrar: "+ramDisp);
            }
        }
        procesosEjecutandose.clear();
        for (Proceso proceso : aux) {
            procesosEjecutandose.add(proceso);
        }
        return ramDisp;
    }
    //Agregamos los procesos a la memoria Ram
    public int agregarProcesosRam(){
        double k=Math.floor(Math.random()*procesosCreados.size());
        int numeroRandom=(int)k;
        //Cuando el proceso ocupa toda la memoria
        if(procesosCreados.get(numeroRandom).tamanio==ramActual){
            ramActual=ramActual-procesosCreados.get(numeroRandom).getTamanio();
            System.out.println("Nueva Ram caso1: "+ramActual);
            procesosEjecutandose.add(procesosCreados.get(numeroRandom));
            procesosCreados.remove(numeroRandom);
            for (Proceso procesosCreado : procesosCreados) {
                procesosEsperando.add(procesosCreado);
                if(procesosCreado.getPrioridad()==1){
                    procesosEspAlta.add(procesosCreado);
                }
                else{
                    procesosEspBaja.add(procesosCreado);
                }
            }
            procesosCreados.clear();
            return 0;
        }
        //Cuando el proceso cabe en la memoria
        else if(procesosCreados.get(numeroRandom).tamanio<ramActual){
            ramActual=ramActual-procesosCreados.get(numeroRandom).getTamanio();
            System.out.println("Nueva Ram caso 2: "+ramActual);
            procesosEjecutandose.add(procesosCreados.get(numeroRandom));
            procesosCreados.remove(numeroRandom);
            if(validadProcesosDisp(ramActual)){
                agregarProcesosRam();
            }
            else{
                if(procesosCreados.size()>0){
                    for (Proceso procesosCreado : procesosCreados) {
                        procesosEsperando.add(procesosCreado);
                        if(procesosCreado.getPrioridad()==1){
                            procesosEspAlta.add(procesosCreado);
                        }
                        else{
                            procesosEspBaja.add(procesosCreado);
                        }
                    }
                    procesosCreados.clear();
                }
                
                return 0;
            }
        }
        //Queda memoria pero los procesos no caben
        else if(procesosCreados.get(numeroRandom).tamanio>ramActual && ramActual!=0){
            procesosEsperando.add(procesosCreados.get(numeroRandom));
            if(procesosCreados.get(numeroRandom).getPrioridad()==1){
                procesosEspAlta.add(procesosCreados.get(numeroRandom));
            }
            else{
                procesosEspBaja.add(procesosCreados.get(numeroRandom));
            }
            procesosCreados.remove(numeroRandom);
            if(validadProcesosDisp(ramActual)){
                agregarProcesosRam();
            }
            else{
                if(procesosCreados.size()>0){
                    for (Proceso procesosCreado : procesosCreados) {
                        procesosEsperando.add(procesosCreado);
                        if(procesosCreado.getPrioridad()==1){
                            procesosEspAlta.add(procesosCreado);
                        }
                        else{
                            procesosEspBaja.add(procesosCreado);
                        }
                    }
                    procesosCreados.clear();
                }
                return 0;
            }
            
        }
        return 0;
    }
    
    public boolean validadProcesosDisp(int ramDisp){
        for (Proceso procesosCreado : procesosCreados) {
            if(procesosCreado.getTamanio()<=ramDisp){
                return true;
            }
        }
        return false;
    }
    
    public void imprimir(ArrayList<Proceso> procesoX){
        
        for (Proceso procesoImpreso : procesoX) {
            procesoImpreso.Imprimir();
        }
    }
    
}
