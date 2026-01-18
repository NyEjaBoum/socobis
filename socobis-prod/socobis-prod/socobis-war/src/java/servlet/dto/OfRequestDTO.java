package servlet.dto;

import java.util.Date;
import java.util.List;

public class OfRequestDTO {
    private String cible;
    private String remarque;
    private String libelle;
    private String idBc;
    private String besoin;
    private String daty;
    private String lancePar;
    private List<OfFilleRequestDTO> produits;

    // Getters et setters
    public String getCible() { return cible; }
    public void setCible(String cible) { this.cible = cible; }

    public String getRemarque() { return remarque; }
    public void setRemarque(String remarque) { this.remarque = remarque; }

    public String getLibelle() { return libelle; }
    public void setLibelle(String libelle) { this.libelle = libelle; }

    public String getIdBc() { return idBc; }
    public void setIdBc(String idBc) { this.idBc = idBc; }

    public String getBesoin() { return besoin; }
    public void setBesoin(String besoin) { this.besoin = besoin; }

    public String getDaty() { return daty; }
    public void setDaty(String daty) { this.daty = daty; }

    public String getLancePar() { return lancePar; }
    public void setLancePar(String lancePar) { this.lancePar = lancePar; }

    public List<OfFilleRequestDTO> getProduits() { return produits; }
    public void setProduits(List<OfFilleRequestDTO> produits) { this.produits = produits; }
}

