/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectosop;

import java.net.URL;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.PriorityQueue;
import java.util.ResourceBundle;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.Label;
import javafx.scene.control.SplitMenuButton;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.text.Text;

/**
 *
 * @author ckill
 */
public class FXMLDocumentController implements Initializable {
    
    @FXML
    private TextField nombre;
    @FXML
    private ChoiceBox<String> tamanno;
    @FXML
    private Text nombreTitulo;
    @FXML
    private ChoiceBox<String> prioridad;
    @FXML
    private ColumnConstraints enEjecucion;
    @FXML
    private ChoiceBox<String> tiempo;
    @FXML
    private GridPane procesosEnEspera;
    @FXML
    private Button Ejecutar;
    @FXML
    private Button crear;
    
    static String[] pizarra = {"0","0","0","0","0","0","0","0"};
    
    int fila=0;
    int ramDisp=8;
    int estadosEjecutar=0;
    int filaEspera=0;
    ArrayList<Proceso> auxEspAlta = new ArrayList<>();
    ArrayList<Proceso> auxEsp = new ArrayList<>();
    
    ProcesosCreados procesos = new ProcesosCreados();
    @FXML
    private GridPane listaCreados;
    @FXML
    private GridPane memoriaRam;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        prioridad.getItems().add("Baja");
        prioridad.getItems().add("Alta");
        Ejecutar.setDisable(true);
        for(int i=0; i<8;i++){
            tamanno.getItems().add(i+1+"");
        }
        
        for (int i=0;i<10;i++) {
            tiempo.getItems().add(i+1+"");
        }
        
    }    

    @FXML
    private void ejecutar(ActionEvent event) {
        
        if(estadosEjecutar==0){
            procesos.agregarProcesosRam();
            ramDisp=procesos.getRamActual();
            System.out.println("Ram ACTUAL:"+ramDisp);
            System.out.println("Procesos ejecutandose: ");
            procesos.imprimir(procesos.procesosEjecutandose);
            System.out.println("------------------------");
            System.out.println("Procesos creados: ");
            procesos.imprimir(procesos.procesosCreados);
            System.out.println("------------------------");
            System.out.println("Procesos esperando: ");
            procesos.imprimir(procesos.procesosEsperando);
            System.out.println("------------------------");
            System.out.println("Ordenando procesos en espera por prioridad");
            procesos.ordenar(procesos.procesosEsperando);
            estadosEjecutar=1;
            agregarProcesosAlmacen(); //Añadimos los procesos a la espera
            agregarProcesosRam();//Añadimos los procesos que se estan ejecutando
            crear.setDisable(true);
            
        }
        else{
            System.out.println("Ram ACTUAL:"+ramDisp);
            ramDisp=procesos.restarEjecutados(ramDisp);
            //ramDisp=procesos.getRamActual();
            
            int marca=0;
            //Si hay un proceso de prioridad alta a la espera,
            if(!procesos.procesosEspAlta.isEmpty()){
                System.out.println("Hay uno a la espera");
                int flag=0;
              
                flag=meterAltaEjecutar();
                System.out.println("Ram disponible y entre");
                if(flag==1){
                    procesos.procesosEspAlta.clear();
                    for (Proceso proceso : auxEspAlta) {
                        procesos.procesosEspAlta.add(proceso);
                    }
                    procesos.procesosEsperando.clear();
                    for (Proceso proceso : auxEsp) {
                        procesos.procesosEsperando.add(proceso);
                    }
                    auxEsp.clear();
                    auxEspAlta.clear();
                    marca=1;
                }
                //Si hace un swaping la flag es 2 y dejamos la marca para que no se agreguen mas procesos
                else if(flag==2){
                    marca=1;
                }
                
            }
            if(!procesos.procesosEspBaja.isEmpty() && marca==0){
                meterBajaEjecutar();
            }
            
            repintarEspera();
            repintarRam();
            //si hay un proceso de prioridad baja ese cumple un ciclo y se sale, restandole a su tiempo
            System.out.println("Nuevos procesos ejecutandose: ");
            procesos.imprimir(procesos.procesosEjecutandose);
            System.out.println("------------------------");
            System.out.println("Nuevos procesos esperando: ");
            procesos.imprimir(procesos.procesosEsperando);
            System.out.println("------------------------");
            System.out.println("Nuevos procesos esperando alta: ");
            procesos.imprimir(procesos.procesosEspAlta);
            System.out.println("------------------------");
        }
        //recorrer procesos ejecutandose y guardar en la pizara
        actualizarPizarra();
        repintarRam();
    }
    //Actualizamos la pizarra con los tiempos actuales de cada proceso en ejecucion
    public void actualizarPizarra(){
        for (Proceso p : procesos.procesosEjecutandose) {
            for (int i = 0; i < 8; i++) {
                String comparacion = " "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad();
                //Comparamos los que son iguales al que se necesita borrar
                String[] pizzaraCortada = pizarra[i].split(" Tiempo");
                String part1 = pizzaraCortada[0];
                if(comparacion.equals(part1)){
                    pizarra[i]=" "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo();
                }
            }
        }
    }
    
    //Buscamos si hay un proceso que cabe en la memoria ram
    public int buscarProcesoDisp(ArrayList<Proceso> procesosx){
        for (int i = 0; i < procesosx.size(); i++) {
            if(procesosx.get(i).getTamanio()<=ramDisp){
                return i;
            }
        }
        return -1;
    }
    //Metemos un proceso de prioridad baja en caso de que sea posible
    public void meterBajaEjecutar(){
        int posProceso = buscarProcesoDisp(procesos.procesosEspBaja);
        //Revisamos si es posible introducir un proceso de prioridad baja que tenga espacio para entrar
        if(posProceso!=-1){
            Proceso p = procesos.procesosEspBaja.get(posProceso);
            int tamanioP = p.getTamanio();
            for (int i = 0; i < 8; i++) {
                if(pizarra[i].equals("0") && tamanioP>0){
                    pizarra[i]=" "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo();
                    tamanioP--;
                }
            }
            procesos.procesosEsperando.remove(p);//Quitamos el proceso de los procesos en espera generales
            procesos.procesosEspBaja.remove(p);//Quitamos el proceso de los procesos en espera de prioridad baja
            procesos.procesosEjecutandose.add(p);//Agregamos el nuevo proceso en los procesos que se encuentran ejecutandose
            ramDisp = ramDisp - p.getTamanio();//Actualizamos la ram disponible segun lo que utiliza el nuevo proceso
        }
    }
    
    //Metemos un proceso de prioridad alta en ejecucion cuando hay espacio disponible
    public int meterAltaEjecutar(){
        int flag=0;
        int posProceso = buscarProcesoDisp(procesos.procesosEspAlta);
        
        //Si se comprueba que hay espacios disponibles puede entrar
        if(posProceso!=-1){
            Proceso p = procesos.procesosEspAlta.get(posProceso);
            System.out.println("ram disponible: "+ramDisp);
            System.out.println("Tamaño proceso nuevo: "+p.getTamanio());
            flag=1;
            //Se agrega el proceso de prioridad alta en espera a la ejecucion (ram)
            procesos.procesosEjecutandose.add(p);
            procesos.imprimir(procesos.procesosEjecutandose);
            
            //Lo eliminamos de los procesos en espera de priorirdad alta
            for (int i=0;i<procesos.procesosEspAlta.size();i++) {
                if(!procesos.procesosEspAlta.get(i).getNombre().equals(p.getNombre())){
                    auxEspAlta.add(procesos.procesosEspAlta.get(i));
                }
            }
            //Lo eliminamos de los procesos en espera general
            for (int i=0;i<procesos.procesosEsperando.size();i++) {
                if(!procesos.procesosEsperando.get(i).getNombre().equals(p.getNombre())){
                    auxEsp.add(procesos.procesosEsperando.get(i));
                }
            }
            Node node = memoriaRam.getChildren().get(0); 
            memoriaRam.getChildren().clear(); 
            memoriaRam.getChildren().add(0,node); 
            ramDisp = ramDisp - p.getTamanio();//Marcamos ocupada la ram que utilizo el nuevo proceso
            //agregarProcesosRam();
            pintarEncimaBorrados(p);
            //repintarEspera();
            procesos.procesosBorrados.clear();
        }
        //De lo contrario al caso anterior intentamos realizar un swapping
        //Hay un proceso de alta en espera y uno baja en ejecucion el cual puede ceder su lugar(swapping)
        else{
            int resultadoSwap=intentoSwap();
            if(resultadoSwap==1){
                flag=2;
                System.out.println("Se pudo realizar el swap");
            }
            else{
                System.out.println("No se pudo hacer swap");
            }
        }
        
        return flag;
    }
    
    //Metodo que se encarga de intentar realizar el swapping
    public int intentoSwap(){
        
        int i=0;
        int tamannio=procesos.procesosEspAlta.size();
        int estado=0;
        
        while(i<tamannio){
            //Revisamos que exista un proceso que permita realizar un swapping
            if(revisarEjecucion(procesos.procesosEspAlta.get(i))!=-1){
                int procesoOut=revisarEjecucion(procesos.procesosEspAlta.get(i));//Obtenemos el proceso que entrara al almacen
                Proceso procesoInt=procesos.procesosEspAlta.get(i);//Obtenemos el proceso que entrara a la memoria
                Proceso pOut=procesos.procesosEjecutandose.get(procesoOut);//Lo guardamos en un aux
                procesos.procesosEjecutandose.remove(procesoOut);//Lo eliminamos de los procesos ejecutandose
                procesos.procesosEjecutandose.add(procesoInt);//Agregamos el proceso de alta a la ejecucion
                procesos.procesosEsperando.add(pOut);//Agregamos el proceso a la espera
                procesos.procesosEspBaja.add(pOut);//Agregamos al proceso a la espera baja
                procesos.procesosEsperando.remove(procesoInt);//Eliminamos el proceso de alta de la espera
                procesos.procesosEspAlta.remove(procesoInt);//Eliminamos el proceso de la espera alta
                pintarSwapping(pOut, procesoInt);//Pintamos el cambio realizado en la matriz
                //Actualizamos la ram segun el tamaño del nuevo proceso que entro
                ramDisp=ramDisp+pOut.getTamanio();
                ramDisp=ramDisp-procesoInt.getTamanio();
                estado=1;
                break;//Salimos del ciclo ya que solo se efectua el swapping una vez por tiempo
            }
            i++;
        }
        return estado;
    }
    
    //Nos permite revisar si existe un proceso de prioridad baja que ceda su lugar para el swapping
    public int revisarEjecucion(Proceso p){
        int tamanio=procesos.procesosEjecutandose.size();
        int k=0;
        while(k<tamanio){
            //Verificamos si hay un proceso de baja
            if(procesos.procesosEjecutandose.get(k).getPrioridad()==0){
                if(procesos.procesosEjecutandose.get(k).getTamanio()+ramDisp>=p.getTamanio()){
                    //Procedemos a realizar el swapping
                    return k;
                }
            }
            k++;
        }
        return -1;
    }
    //Repintamos la memoria ram en caso de que exita un cambio
    public void repintarRam(){
        Node node = memoriaRam.getChildren().get(0); 
        memoriaRam.getChildren().clear(); 
        memoriaRam.getChildren().add(0,node); 
        for (int i = 0; i < 8; i++) {
            if(!pizarra[i].equals("0")){
                memoriaRam.add(new Text(pizarra[i]),0,i);
            }
            else{
                memoriaRam.add(new Text(),0,i);
            }
        }
    }
    //Repintamos el almacen de respaldo en caso de que exista un cambio
    public void repintarEspera(){
        Node node = procesosEnEspera.getChildren().get(0); 
        procesosEnEspera.getChildren().clear(); 
        procesosEnEspera.getChildren().add(0,node); 
        for (int i = 0; i < procesos.procesosEsperando.size(); i++) {
            Proceso p = procesos.procesosEsperando.get(i);
            String procesosString = " "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo();
            procesosEnEspera.add(new Text(procesosString), 0, i);
        }
    }
    //Pintamos la matriz de la memoria ram cuando se realiza un swapping
    public void pintarSwapping(Proceso out, Proceso in){
        
        int ProcesoTamannio=in.getTamanio();
        
        System.out.println("Tamaño de proceso que entrara: "+ProcesoTamannio);
        for (int i = 0; i < 8; i++) {
            String comparacion = " "+out.getNombre()+" Prioridad: "+out.obtenerPrioridad();
            //Comparamos los que son iguales al que se necesita borrar
            String[] pizzaraCortada = pizarra[i].split(" Tiempo");
            String part1 = pizzaraCortada[0];
            if(comparacion.equals(part1) && ProcesoTamannio>0){
                pizarra[i]=" "+in.getNombre()+" Prioridad: "+in.obtenerPrioridad()+" Tiempo: "+in.getTiempo();
                ProcesoTamannio--;
            }
        }
        for (int i = 0; i < 8; i++) {
            String comparacion = " "+out.getNombre()+" Prioridad: "+out.obtenerPrioridad();
            //Comparamos los que son iguales al que se necesita borrar
            String[] pizzaraCortada = pizarra[i].split(" Tiempo");
            String part1 = pizzaraCortada[0];
            if(comparacion.equals(part1)){
                pizarra[i]="0";
            }
        }
        //Si quedan partes por pintar
        if(ProcesoTamannio>0){
            for (int i = 0; i < 8; i++) {
                if(pizarra[i].equals("0") && ProcesoTamannio>0){
                    pizarra[i]=" "+in.getNombre()+" Prioridad: "+in.obtenerPrioridad()+" Tiempo: "+in.getTiempo();
                    ProcesoTamannio--;
                }
            }
        }
        //Aplicamos los cambios en la matriz de la memoria ram (interfaz)
        repintarRam();
        repintarEspera();
    }
    //Pintamos encima de un proceso que fue eliminado
    public void pintarEncimaBorrados(Proceso p){
        
        for (int i = 0; i < 8; i++) {
            for(int j=0;j<procesos.procesosBorrados.size();j++){
                String comparacion = " "+procesos.procesosBorrados.get(j).getNombre()+" Prioridad: "+procesos.procesosBorrados.get(j).obtenerPrioridad();
            
                //Comparamos los que son iguales al que se necesita borrar
                String[] pizzaraCortada = pizarra[i].split(" Tiempo");
                String part1 = pizzaraCortada[0];
                if(comparacion.equals(part1)){
                    pizarra[i]="0";
                }
            }
        }

        int cantidadP=p.getTamanio();
        
        for (int i = 0; i < 8; i++) {
            if(pizarra[i]=="0" && cantidadP>0){
                pizarra[i]=" "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo();
                cantidadP--;
            }
        }
        
        for (int i = 0; i < 8; i++) {
            if(!pizarra[i].equals("0")){
                memoriaRam.add(new Text(pizarra[i]),0,i);
            }
        }
        
    }
    
    public void agregarProcesosAlmacen(){
        for (Proceso p : procesos.procesosEsperando) {
            String prioridadT="";
            procesosEnEspera.add(new Text(" "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo()), 0, filaEspera);
            filaEspera++;
        }
    }
    
    public void agregarProcesosRam(){
        int filaRam=0;
        int k=0;
        for (Proceso p : procesos.procesosEjecutandose) {
            k=p.getTamanio();
            for (int i = 0; i < k; i++) {
                memoriaRam.add(new Text(" "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo()), 0, filaRam);
                pizarra[filaRam]=" "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo();
                filaRam++;
            }
        }
    }
    
    @FXML
    private void crearProceso(ActionEvent event) {
        
        if(prioridad.getValue()!=null && tamanno.getValue()!=null && tiempo.getValue()!=null && !nombre.getText().equals("")){
            System.out.println("Proceso creado");
            String aux=prioridad.getValue();
            int prioridadP=0;
            if(aux.equals("Alta")){
                prioridadP=1;
            }
            aux=tamanno.getValue();
            int tamanioP=Integer.parseInt(aux);
            aux=tiempo.getValue();
            int tiempoP=Integer.parseInt(aux);
            Proceso p = new Proceso(nombre.getText(),prioridadP,tamanioP,tiempoP);
            listaCreados.add(new Text(" "+p.getNombre()+" Prioridad: "+p.obtenerPrioridad()+" Tiempo: "+p.getTiempo()), 0, fila);
            fila++;
            
            procesos.procesosCreados.add(p);
            Ejecutar.setDisable(false);
            //p.Imprimir();
        }
    }
}
