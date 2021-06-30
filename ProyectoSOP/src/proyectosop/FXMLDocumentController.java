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
    
    String[] pizarra = {"0","0","0","0","0","0","0","0"};
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
            ramDisp=procesos.agregarProcesosRam(ramDisp);
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
            System.out.println("Nuevos procesos ejecutandose: ");
            procesos.imprimir(procesos.procesosEjecutandose);
            System.out.println("------------------------");
            
            
            //Meter los en espera
        
            //Si hay un proceso de prioridad alta a la espera,
            if(!procesos.procesosEspAlta.isEmpty()){
                System.out.println("Hay uno a la espera");
                
                for (Proceso p : procesos.procesosEspAlta) {
                    meterAltaEjecutar(p);
                    System.out.println("Ram disponible y entre");
                }
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
                
                /*
                for (Proceso ejecutando : procesos.procesosEjecutandose) {
                    if(ejecutando.getPrioridad()==0){//Si existe un proceso de prioridad baja ejecutandose
                        if(ejecutando.getTamanio()>=procesos.procesosEspAlta.get(0).getTamanio()){
                            //Hacemos swap de los procesos en espera y ejecutando
                            Proceso procesoIn = procesos.procesosEspAlta.get(0);
                            Proceso procesoOut = ejecutando;
                            procesos.procesosEjecutandose.remove(ejecutando);
                            
                        }
                    }
                }*/
            }
            //si hay un proceso de prioridad baja ese cumple un ciclo y se sale, restandole a su tiempo

            System.out.println("Nuevos procesos esperando: ");
            procesos.imprimir(procesos.procesosEsperando);
            System.out.println("------------------------");
            System.out.println("Nuevos procesos esperando alta: ");
            procesos.imprimir(procesos.procesosEspAlta);
            System.out.println("------------------------");

            //Si hay solo procesos de prioridad alta se elige el primero que se encuentra
            //si hay solo procesos de prioridad baja cumplen su iteracion y luego entran los otros de baja
        }
    }
    
    public void meterAltaEjecutar(Proceso p){
        System.out.println("ram disponible: "+ramDisp);
        System.out.println("Tamaño proceso nuevo: "+p.getTamanio());
        if(ramDisp>=p.getTamanio()){
            
            //Se agrega el proceso en espera a la ejecucion
            procesos.procesosEjecutandose.add(p);
            //Lo eliminamos de los procesos en espera alta
            //procesos.procesosEspAlta.remove(p);
            for (int i=0;i<procesos.procesosEspAlta.size();i++) {
                if(!procesos.procesosEspAlta.get(i).getNombre().equals(p.getNombre())){
                    auxEspAlta.add(procesos.procesosEspAlta.get(i));
                }
            }
            //Lo eliminamos de los procesos en espera general
            //procesos.procesosEsperando.remove(p);
            for (int i=0;i<procesos.procesosEsperando.size();i++) {
                if(!procesos.procesosEsperando.get(i).getNombre().equals(p.getNombre())){
                    auxEsp.add(procesos.procesosEsperando.get(i));
                }
            }
            Node node = memoriaRam.getChildren().get(0); 
            memoriaRam.getChildren().clear(); 
            memoriaRam.getChildren().add(0,node); 
            
            
            
            
            ramDisp = ramDisp - p.getTamanio();
            //agregarProcesosRam();
            pintarEncima(p);
        }
    }
    
    public void pintarEncima(Proceso p){
        
        for (int i = 0; i < 8; i++) {
            for(int j=0;j<procesos.procesosBorrados.size();j++){
                String prioridadT="";
                if(procesos.procesosBorrados.get(j).getPrioridad()==0){
                     prioridadT="Baja";
                }
                else{
                    prioridadT="Alta";
                }
                String comparacion = " "+procesos.procesosBorrados.get(j).getNombre()+" Prioridad: "+prioridadT;
                System.out.println("ej: "+comparacion);
                
                String[] pizzaraCortada = pizarra[i].split(" Tiempo");
                String part1 = pizzaraCortada[0];
                System.out.println("parte1: "+part1);
                if(comparacion.equals(part1)){
                    pizarra[i]="0";
                }
            }
        }
        
        for (int i = 0; i < 8; i++) {
            System.out.println(pizarra[i]);
        }
        
        
        int cantidadP=p.getTamanio();
        for (int i = 0; i < 8; i++) {
            if(pizarra[i]=="0" && cantidadP>0){
                String prioridadT="";
                if(p.getPrioridad()==0){
                     prioridadT="Baja";
                }
                else{
                    prioridadT="Alta";
                }
                pizarra[i]=" "+p.getNombre()+" Prioridad: "+prioridadT+" Tiempo: "+p.getTiempo();
                cantidadP--;
            }
        }
        
        for (int i = 0; i < 8; i++) {
            memoriaRam.add(new Text(pizarra[i]),0,i);
        }
        
        
    }
    
    
    private Node getNodeFromGridPane(GridPane gridPane, int col, int row) {
        ObservableList<Node> children = gridPane.getChildren();
        for (Node node : children) {
            Integer columnIndex = GridPane.getColumnIndex(node);
            Integer rowIndex = GridPane.getRowIndex(node);

            if (columnIndex == null)
                columnIndex = 0;
            if (rowIndex == null)
                rowIndex = 0;

            if (columnIndex == col && rowIndex == row) {
                return node;
            }
        }
        return null;
    }
    
    
    
    
    public void agregarProcesosAlmacen(){
        for (Proceso p : procesos.procesosEsperando) {
            String prioridadT="";
            if(p.getPrioridad()==0){
                 prioridadT="Baja";
            }
            else{
                prioridadT="Alta";
            }
            procesosEnEspera.add(new Text(" "+p.getNombre()+" Prioridad: "+prioridadT+" Tiempo: "+p.getTiempo()), 0, filaEspera);
            filaEspera++;
        }
    }
    
    public void agregarProcesosRam(){
        int filaRam=0;
        int k=0;
        for (Proceso p : procesos.procesosEjecutandose) {
            k=p.getTamanio();
            for (int i = 0; i < k; i++) {
                String prioridadT="";
                if(p.getPrioridad()==0){
                     prioridadT="Baja";
                }
                else{
                    prioridadT="Alta";
                }
                memoriaRam.add(new Text(" "+p.getNombre()+" Prioridad: "+prioridadT+" Tiempo: "+p.getTiempo()), 0, filaRam);
                pizarra[filaRam]=" "+p.getNombre()+" Prioridad: "+prioridadT+" Tiempo: "+p.getTiempo();
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
            String prioridadT="";
            if(p.getPrioridad()==0){
                 prioridadT="Baja";
            }
            else{
                prioridadT="Alta";
            }
            listaCreados.add(new Text(" "+p.getNombre()+" Prioridad: "+prioridadT+" Tiempo: "+p.getTiempo()), 0, fila);
            fila++;
            
            procesos.procesosCreados.add(p);
            //p.Imprimir();
        }
    }
}
