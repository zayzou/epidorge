package stock;

import dialogue.AlertMaker;
import com.jfoenix.controls.JFXTextField;
import database.DatabaseUtil;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import static java.util.Objects.nonNull;
import java.util.ResourceBundle;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Tab;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.StackPane;
import model.ListFour;
import util.BaseController;
import static util.FXMLPage.ADD;

/**
 *
 * @author sof
 */
public class stockController extends BaseController implements Initializable {

    @FXML
    private JFXTextField tfSearch;
    @FXML
    private Button bntRefresh;
    @FXML
    private TableView<ListFour> tableFour;
    @FXML
    private TableColumn<?, ?> colID;
    @FXML
    private TableColumn<?, ?> colRs;
    @FXML
    private TableColumn<?, ?> colAdr;
    @FXML
    private TableColumn<?, ?> colTel;
    @FXML
    private TableColumn<?, ?> colSolde;
    @FXML
    private TableColumn<?, ?> colCommentaire;
    
    ObservableList<ListFour> data;
    FilteredList<ListFour> filteredData;
    @FXML
    private Tab testTab;
    @FXML
    private AnchorPane testPane;
    @FXML
    private Tab tabUnite;
    @FXML
    private AnchorPane paneUnite;
    @FXML
    private StackPane rootPane;
    @FXML
    private AnchorPane mainContainer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        initData();
        setCells();
        loadData();
    }

    private void initData() {
        System.out.println("init data ");
        data = FXCollections.observableArrayList();
        filteredData = new FilteredList<>(data, n -> true);
        tfSearch.setOnKeyReleased((KeyEvent e) -> {
            filteredData.setPredicate((ListFour n) -> {
                String term = tfSearch.getText().trim();
                if (term == null || term.isEmpty()) {
                    return true;
                }
                return n.getRs().contains(term) || n.getTel().contains(term) || n.getAdr().contains(term);
            });
        });
        tableFour.setItems(filteredData);
    }

    private void setCells() {
        System.out.println("set cells");
        /*column tablefour        */
        colID.setCellValueFactory(new PropertyValueFactory<>("cpt"));
        colRs.setCellValueFactory(new PropertyValueFactory<>("rs"));
        colAdr.setCellValueFactory(new PropertyValueFactory<>("adr"));
        colTel.setCellValueFactory(new PropertyValueFactory<>("tel"));
        colSolde.setCellValueFactory(new PropertyValueFactory<>("solde"));
        colCommentaire.setCellValueFactory(new PropertyValueFactory<>("commentaire"));
        /* col tableprod */
        

    }

    private void loadData() {
        System.out.println("load data");
        data.clear();
        try {
            statement = con.prepareStatement("SELECT * FROM fournisseurs");
            resultSet = statement.executeQuery();
           
            while (resultSet.next()) {
                data.add(new ListFour(
                        resultSet.getString("raison_sociale_four"),
                        resultSet.getString("adr_four"),
                        resultSet.getString("tel_four"),
                        resultSet.getString("commentaire_four"),
                        resultSet.getDouble("solde_four"),
                        resultSet.getInt("id_four")
                ));
            }
        } catch (SQLException ex) {
        }
        tableFour.setItems(filteredData);
    }

    @FXML
    private void ajouter(ActionEvent event) {
        editFour = null;
        try {
            navigate(event, ADD.getPage(), "Ajouter Fournisseur");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }
    }

    @FXML
    private void modifier(ActionEvent event) {
        editFour = ListFour.class.cast(tableFour.getSelectionModel().getSelectedItem());
        if (!nonNull(editFour)) {
            return;
        }
        try {
            navigate(event, ADD.getPage(), "Modifier Fournisseur");
        } catch (IOException ex) {
            System.err.println(ex.getMessage());
        }

    }

    @FXML
    private void supprimer(ActionEvent event) {
        editFour = ListFour.class.cast(tableFour.getSelectionModel().getSelectedItem());
        if (!nonNull(editFour)) {
            return;
        }
        boolean ok = AlertMaker.showDialog("Supprimer", "Voulez vous vraiment supprimer " + editFour.getRs() + " ?");
        if (ok) {
            try {
                int i = DatabaseUtil.execDel(editFour.getRs());
                if (i > 0) {
                        AlertMaker.showInfo("Supprimer", "Données supprimé avec succés !");
                    }
                loadData();
            } catch (SQLException ex) {
                AlertMaker.showInfo("Error", ex.getMessage());
            }
        }

    }

    @FXML
    private void refresh(ActionEvent event) {
        loadData();
    }

    @FXML
    private void changeUI(Event event) {
       change("/products/prodUI.fxml",testTab);
    }
    
    private void change(String ui,Tab tab){
         try {
            AnchorPane pane = FXMLLoader.load(getClass().getResource(ui));
           
            tab.setContent(pane);

        } catch (IOException ex) {
            System.err.println("Error : " + ex.getMessage());

        }
    }

    @FXML
    private void loadUnite(Event event) {
        change("/units/unitUI.fxml",tabUnite);
        
    }
}
