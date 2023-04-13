package report;

/**
 *
 * @author soffi
 */
public class StockModel {

    private String des;
    private String qte;
    private String prix_achat;
    private String prix_vente;
    private String unite;
    private String type;
    private String cpt;
    private String fournisseur;

    public StockModel(String cpt, String des, String qte, String prix_achat, String prix_vente, String unite, String type, String fournisseur) {

        this.cpt = cpt;
        this.des = des;
        this.qte = qte;
        this.prix_achat = prix_achat;
        this.prix_vente = prix_vente;
        this.unite = unite;
        this.type = type;

        this.fournisseur = fournisseur;

    }

    /**
     * @return the des
     */
    public String getDes() {
        return des;
    }

    /**
     * @param des the des to set
     */
    public void setDes(String des) {
        this.des = des;
    }

    /**
     * @return the qte
     */
    public String getQte() {
        return qte;
    }

    /**
     * @param qte the qte to set
     */
    public void setQte(String qte) {
        this.qte = qte;
    }

    /**
     * @return the prix_achat
     */
    public String getPrix_achat() {
        return prix_achat;
    }

    /**
     * @param prix_achat the prix_achat to set
     */
    public void setPrix_achat(String prix_achat) {
        this.prix_achat = prix_achat;
    }

    /**
     * @return the prix_vente
     */
    public String getPrix_vente() {
        return prix_vente;
    }

    /**
     * @param prix_vente the prix_vente to set
     */
    public void setPrix_vente(String prix_vente) {
        this.prix_vente = prix_vente;
    }

    /**
     * @return the unite
     */
    public String getUnite() {
        return unite;
    }

    /**
     * @param unite the unite to set
     */
    public void setUnite(String unite) {
        this.unite = unite;
    }

    /**
     * @return the type
     */
    public String getType() {
        return type;
    }

    /**
     * @param type the type to set
     */
    public void setType(String type) {
        this.type = type;
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
     * @return the cpt
     */
    public String getCpt() {
        return cpt;
    }

    /**
     * @param cpt the cpt to set
     */
    public void setCpt(String cpt) {
        this.cpt = cpt;
    }

}
