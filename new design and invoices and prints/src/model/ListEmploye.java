package model;

import java.sql.Date;

/**
 *
 * @author sof
 */
public class ListEmploye {

    private String rs;
    private String adr;
    private String tel;
    private String commentaire;
    private double solde;
    private int id;
    private Date date;
    private String type;

    public ListEmploye(String rs, String adr, String tel, String commentaire, double solde, int id, Date date, String type) {
        this.rs = rs;
        this.adr = adr;
        this.tel = tel;
        this.commentaire =  commentaire;
        this.solde = solde;
        this.id = id;
        this.date = date;
        this.type = type;
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
     * @return the id
     */
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    /**
     * @return the date
     */
    public Date getDate() {
        return date;
    }

    /**
     * @param date the date to set
     */
    public void setDate(Date date) {
        this.date = date;
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

}
