package model;

import javafx.scene.image.ImageView;

/**
 *
 * @author soffi
 */
public class Rupture {

    private ImageView icon;
    private String ref;
    private int qte;
    private String fournisseur;

    public Rupture(ImageView icon, String ref, int qte, String fournisseur) {
        this.icon = icon;
        this.ref = ref;
        this.qte = qte;
        this.fournisseur = fournisseur;
    }

    /**
     * @return the ref
     */
    public String getRef() {
        return ref;
    }

    /**
     * @param ref the ref to set
     */
    public void setRef(String ref) {
        this.ref = ref;
    }

    /**
     * @return the qte
     */
    public int getQte() {
        return qte;
    }

    /**
     * @param qte the qte to set
     */
    public void setQte(int qte) {
        this.qte = qte;
    }

    /**
     * @return the fournisseur
     */
    public String getFournisseur() {
        return fournisseur;
    }

    /**
     * @param fournisseur the fournisseur to set
     */
    public void setFournisseur(String fournisseur) {
        this.fournisseur = fournisseur;
    }

    /**
     * @return the icon
     */
    public ImageView getIcon() {
        return icon;
    }

    /**
     * @param icon the icon to set
     */
    public void setIcon(ImageView icon) {
        this.icon = icon;
    }

}
