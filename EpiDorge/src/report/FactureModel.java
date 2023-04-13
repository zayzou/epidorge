/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package report;

/**
 *
 * @author soffi
 */
public class FactureModel {
    private String des;
  private  String qte;
  private  String prix_unit;
  private  String total;

    public FactureModel(String des, String qte, String prix_unit, String total) {
        this.des = des;
        this.qte = qte;
        this.prix_unit = prix_unit;
        this.total = total;
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
     * @return the prix_unit
     */
    public String getPrix_unit() {
        return prix_unit;
    }

    /**
     * @param prix_unit the prix_unit to set
     */
    public void setPrix_unit(String prix_unit) {
        this.prix_unit = prix_unit;
    }

    /**
     * @return the total
     */
    public String getTotal() {
        return total;
    }

    /**
     * @param total the total to set
     */
    public void setTotal(String total) {
        this.total = total;
    }

   
          
    
}
