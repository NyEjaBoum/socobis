// FabricationRequestDTO.java
package servlet.dto;

import java.util.List;

public class FabricationRequestDTO {
    private String idOf;
    private String idOffille;
    private String lancePar;
    private String cible;
    private String remarque;
    private String libelle;
    private String idBc;
    private String besoin;
    private String daty;
    private String fabricationPrec;
    private String fabricationSuiv;
    private String equipe;
    private Double nbPetris;
    private List<FabricationFilleRequestDTO> produits;

    // Getters et Setters
    public String getIdOf() { return idOf; }
    public void setIdOf(String idOf) { this.idOf = idOf; }

    public String getIdOffille() { return idOffille; }
    public void setIdOffille(String idOffille) { this.idOffille = idOffille; }

    public String getLancePar() { return lancePar; }
    public void setLancePar(String lancePar) { this.lancePar = lancePar; }

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

    public String getFabricationPrec() { return fabricationPrec; }
    public void setFabricationPrec(String fabricationPrec) { this.fabricationPrec = fabricationPrec; }

    public String getFabricationSuiv() { return fabricationSuiv; }
    public void setFabricationSuiv(String fabricationSuiv) { this.fabricationSuiv = fabricationSuiv; }

    public String getEquipe() { return equipe; }
    public void setEquipe(String equipe) { this.equipe = equipe; }

    public Double getNbPetris() { return nbPetris; }
    public void setNbPetris(Double nbPetris) { this.nbPetris = nbPetris; }

    public List<FabricationFilleRequestDTO> getProduits() { return produits; }
    public void setProduits(List<FabricationFilleRequestDTO> produits) { this.produits = produits; }
}