/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectosop;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 *
 * @author ckill
 */
public class ProcesosCreados {
    
    ArrayList<Proceso> procesosCreados = new ArrayList<Proceso>();
    ArrayList<Proceso> procesosEsperando = new ArrayList<Proceso>();
    ArrayList<Proceso> procesosEjecutandose = new ArrayList<Proceso>();
    
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
    
    public void restarEjecutados(){
        ArrayList<Proceso> aux = new ArrayList<>();
        for (Proceso proceso : procesosEjecutandose) {
            proceso.restarUnTiempo();
            if(proceso.getTiempo()!=0){
                aux.add(proceso);
            }
        }
        procesosEjecutandose.clear();
        for (Proceso proceso : aux) {
            procesosEjecutandose.add(proceso);
        }
        
    }
    
    public int agregarProcesosRam(int ram){
        double k=Math.floor(Math.random()*procesosCreados.size());
        int numeroRandom=(int)k;
        if(procesosCreados.get(numeroRandom).tamanio==ram){
            ram=ram-procesosCreados.get(numeroRandom).getTamanio();
            procesosEjecutandose.add(procesosCreados.get(numeroRandom));
            procesosCreados.remove(numeroRandom);
            for (Proceso procesosCreado : procesosCreados) {
                procesosEsperando.add(procesosCreado);
            }
            procesosCreados.clear();
        }
        
        else if(procesosCreados.get(numeroRandom).tamanio<ram){
            ram=ram-procesosCreados.get(numeroRandom).getTamanio();
            procesosEjecutandose.add(procesosCreados.get(numeroRandom));
            procesosCreados.remove(numeroRandom);
            agregarProcesosRam(ram);
        }
        else if(procesosCreados.get(numeroRandom).tamanio>ram && ram!=0){
            procesosEsperando.add(procesosCreados.get(numeroRandom));
            procesosCreados.remove(numeroRandom);
            if(validadProcesosDisp(ram)){
                agregarProcesosRam(ram);
            }
            else{
                if(procesosCreados.size()>0){
                    for (Proceso procesosCreado : procesosCreados) {
                        procesosEsperando.add(procesosCreado);
                    }
                    procesosCreados.clear();
                }
                return ram;
            }
            
        }
        else if(ram==0){//ram llena
            return ram;
        }
        return -1;
        
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
