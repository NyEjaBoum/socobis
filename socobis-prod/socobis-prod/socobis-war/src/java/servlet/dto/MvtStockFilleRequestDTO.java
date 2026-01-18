// MvtStockFilleRequestDTO.java
package servlet.dto;

public class MvtStockFilleRequestDTO {
    private String idProduit;
    private String designation;
    private Double entree;
    private Double sortie;
    private Double pu;
    private String mvtSrc;

    // Getters et Setters
    public String getIdProduit() { return idProduit; }
    public void setIdProduit(String idProduit) { this.idProduit = idProduit; }

    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public Double getEntree() { return entree; }
    public void setEntree(Double entree) { this.entree = entree; }

    public Double getSortie() { return sortie; }
    public void setSortie(Double sortie) { this.sortie = sortie; }

    public Double getPu() { return pu; }
    public void setPu(Double pu) { this.pu = pu; }

    public String getMvtSrc() { return mvtSrc; }
    public void setMvtSrc(String mvtSrc) { this.mvtSrc = mvtSrc; }
}