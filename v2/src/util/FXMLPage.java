package util;

import java.net.URL;

public enum FXMLPage {

    ADD("/fournisseurs/addEditFournisseur.fxml"),
    ADD_PROD("/productsaddEdit/addEdit.fxml"),
    MAIN("/dashboard/home.fxml"),
    LIST("/stock/stockUI.fxml");
    
    

    private final String location;

    FXMLPage(String location) {
        this.location = location;
    }

    public URL getPage() {
        return getClass().getResource(location);

    }

}
