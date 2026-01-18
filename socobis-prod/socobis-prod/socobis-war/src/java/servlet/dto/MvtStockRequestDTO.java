// MvtStockRequestDTO.java
package servlet.dto;

import java.util.List;

public class MvtStockRequestDTO {
    private String designation;
    private String idMagasin;
    private String idPoint;
    private String idTypeMvStock;
    private String idobjet;
    private String fabPrecedent;
    private String daty;
    private List<MvtStockFilleRequestDTO> produits;

    // Getters et Setters
    public String getDesignation() { return designation; }
    public void setDesignation(String designation) { this.designation = designation; }

    public String getIdMagasin() { return idMagasin; }
    public void setIdMagasin(String idMagasin) { this.idMagasin = idMagasin; }

    public String getIdPoint() { return idPoint; }
    public void setIdPoint(String idPoint) { this.idPoint = idPoint; }

    public String getIdTypeMvStock() { return idTypeMvStock; }
    public void setIdTypeMvStock(String idTypeMvStock) { this.idTypeMvStock = idTypeMvStock; }

    public String getIdobjet() { return idobjet; }
    public void setIdobjet(String idobjet) { this.idobjet = idobjet; }

    public String getFabPrecedent() { return fabPrecedent; }
    public void setFabPrecedent(String fabPrecedent) { this.fabPrecedent = fabPrecedent; }

    public String getDaty() { return daty; }
    public void setDaty(String daty) { this.daty = daty; }

    public List<MvtStockFilleRequestDTO> getProduits() { return produits; }
    public void setProduits(List<MvtStockFilleRequestDTO> produits) { this.produits = produits; }
}