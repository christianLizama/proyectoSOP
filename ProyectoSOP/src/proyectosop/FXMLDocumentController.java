/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package proyectosop;

import java.net.URL;
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
    private AnchorPane prioridadTitulo;
    @FXML
    private ColumnConstraints enEjecucion;
    @FXML
    private ChoiceBox<String> tiempo;
    @FXML
    private GridPane procesoEnEjecucion;
    @FXML
    private GridPane procesosEnEspera;
    @FXML
    private GridPane pagina;
    @FXML
    private Button Ejecutar;
    @FXML
    private Button crear;
    
    
    PriorityQueue<Proceso> procesos;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // TODO
        procesos = new PriorityQueue<>(new Comparator<Proceso>() {
            @Override
            public int compare(Proceso t, Proceso t1) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });
        
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
            p.Imprimir();
        }
    }
    
}
