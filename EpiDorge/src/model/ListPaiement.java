
package model;

import java.sql.Date;

/**
 *
 * @author sof
 */
public class ListPaiement {

    private double solde;
    private Date  date;
    private int id;
    
    public ListPaiement(int id,double solde, Date date) {
        this.solde = solde;
        this.date = date;
        this.id = id;
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
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    public void setId(int id) {
        this.id = id;
    }
    
    
    
    
}
