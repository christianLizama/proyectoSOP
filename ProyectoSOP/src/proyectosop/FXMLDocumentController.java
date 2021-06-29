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
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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
    private GridPane pagina;
    @FXML
    private Button Ejecutar;
    @FXML
    private Button crear;
    
    int columna=0;
    int ramOcupada=8;
    int estadosEjecutar=0;

    ProcesosCreados procesos = new ProcesosCreados();
    @FXML
    private GridPane listaCreados;
    
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
            ramOcupada=procesos.agregarProcesosRam(ramOcupada);
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
        }
        else{
            procesos.restarEjecutados();
            System.out.println("Nuevos procesos ejecutandose: ");
            procesos.imprimir(procesos.procesosEjecutandose);
            System.out.println("------------------------");
            //Meter los en espera
        }
        //Si hay un proceso de prioridad alta a espera,
        //si hay un proceso de prioridad baja ese cumple un ciclo y se sale, restandole a su tiempo
        
        //Si hay solo procesos de prioridad alta se elige el primero que se encuentra
        //si hay solo procesos de prioridad baja cumplen su iteracion y luego entran los otros de baja
     
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
            
            listaCreados.add(new Text(" "+p.getNombre()), 0, columna);
            columna++;
            
            procesos.procesosCreados.add(p);
            //p.Imprimir();
        }
    }
    
}
