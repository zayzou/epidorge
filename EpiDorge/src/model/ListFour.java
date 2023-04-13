package model;

/**
 *
 * @author sof
 */
public class ListFour {

    private String rs;
    private String adr;
    private String tel;
    private String commentaire;
    private double solde;
    private int cpt;

    public ListFour(String rs, String adr, String tel, String commentaire, double solde, int cpt) {
        this.rs = rs;
        this.adr = adr;
        this.tel = tel;
        this.commentaire = commentaire;
        this.solde = solde;
        this.cpt = cpt;
    }

    /**
     * @return the rs
     */
    public String getRs() {
        return rs;
    }

    /**
     * @param rs the rs to set
     */
    public void setRs(String rs) {
        this.rs = rs;
    }

    /**
     * @return the adr
     */
    public String getAdr() {
        return adr;
    }

    /**
     * @param adr the adr to set
     */
    public void setAdr(String adr) {
        this.adr = adr;
    }

    /**
     * @return the tel
     */
    public String getTel() {
        return tel;
    }

    /**
     * @param tel the tel to set
     */
    public void setTel(String tel) {
        this.tel = tel;
    }

    /**
     * @return the solde
     */
    public double getSolde() {
        return solde;
    }

    /**
     * @param solde the solde to set
     */
    public void setSolde(double solde) {
        this.solde = solde;
    }

    /**
     * @return the commentaire
     */
    public String getCommentaire() {
        return commentaire;
    }

    /**
     * @param commentaire the commentaire to set
     */
    public void setCommentaire(String commentaire) {
        this.commentaire = commentaire;
    }

    /**
     * @return the cpt
     */
    public int getCpt() {
        return cpt;
    }

    public void setCpt(int c) {
        this.cpt = c;
    }

}
