package util;

/**
 *
 * @author sof
 */
import database.DatabaseUtil;
import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;
import javafx.animation.FadeTransition;
import javafx.animation.Interpolator;
import javafx.event.Event;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.image.Image;
import javafx.scene.layout.AnchorPane;
import javafx.stage.DirectoryChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.util.Duration;
import model.ListApprov;
import model.ListEmploye;
import model.ListFour;
import model.ListPaiement;
import model.ListProd;
import model.ListUnit;
import model.ListVente;

public class BaseController {

    protected Alert alert;

    public static ListFour editFour = null;
    public static ListProd editProd = null;
    public static ListUnit editUnit = null;
    public static ListApprov editApprov = null;
    public static ListVente editVente = null;
    public static ListEmploye editEmploye = null;
    public static ListPaiement editPaiement = null;
    public static boolean isClient = false;
    public final static Locale FR = Locale.FRANCE;
    public static Calendar cal = new GregorianCalendar(FR);
    public PreparedStatement statement;
    public ResultSet resultSet;
    public final Connection con = DatabaseUtil.DBCON;
    public static String date ;
    
    public ArrayList<String> typeData = new ArrayList<>();
    /**
     * method used for main system navigation
     *
     * @param event
     * @param fxmlDocName
     * @param title
     * @throws IOException
     */

    public void navigate(Event event, URL fxmlDocName, String title) throws IOException {

        Parent pageParent = FXMLLoader.load(fxmlDocName);
        Scene scene = new Scene(pageParent);
        Stage appStage = new Stage(StageStyle.DECORATED);
        appStage.setResizable(false);
        appStage.getIcons().add(new Image("/resources/icon.png"));
        appStage.setScene(scene);
        appStage.setTitle(title);
        appStage.show();
    }

    static public String customFormat(double value) {
        String pattern = "0.00 DA";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        //System.out.println(value + "  " + pattern + "  " + output);
        return output;
    }
    static public String customPercent(double value) {
        String pattern = "0.00";
        DecimalFormat myFormatter = new DecimalFormat(pattern);
        String output = myFormatter.format(value);
        //System.out.println(value + "  " + pattern + "  " + output);
        return output+"%";
    }

    public static String[] getMonths() {
        String[] months = new String[12];
        
        for (int i = 0; i < 12; i++) {
            cal.set(getYear(), i, 1);
            months[i] = cal.getDisplayName(Calendar.MONTH, Calendar.LONG, FR);
        }
        return months;
    }
    public static int getYear(){
        return LocalDate.now().getYear();
    }
    
    public  void animationFade(Node e) {
        FadeTransition x = new FadeTransition(new Duration(3000), e);
        x.setFromValue(0);
        x.setToValue(100);
        x.setCycleCount(1);
        x.setInterpolator(Interpolator.LINEAR);
        x.play();
    }
   
    public void initTypeDate(){
        typeData.clear();
        typeData.add("Biere");
        typeData.add("Vin");
        typeData.add("Liqueur");
    }
     public File chooseDir(AnchorPane rootPane) {
        Stage stage = (Stage) rootPane.getScene().getWindow();
        DirectoryChooser chooser = new DirectoryChooser();
        File file = chooser.showDialog(stage);

        return file;
     }

}
