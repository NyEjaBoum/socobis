package vente;

import annexe.Produit;
import bean.CGenUtil;
import constante.ConstanteEtat;
import java.sql.Connection;
import stock.MvtStock;
import stock.MvtStockFille;
import utilitaire.UtilDB;
import utilitaire.Utilitaire;
import utils.ConstanteStation;

/**
 * Service d'échange de produits pour une facture validée.
 * Règle: le produit de remplacement doit avoir un prix de vente unitaire >= celui du produit échangé.
 * Effets: crée une entrée de stock pour l'ancien produit et génère un bon de livraison pour le nouveau produit
 * (ce qui provoque une sortie de stock lors de la validation du BL).
 */
public class EchangeService {

	public static void echangerProduit(String userId,
									   String idVente,
									   String idVenteDetail,
									   String idProduitNouveau,
									   Double qteParam) throws Exception {
		Connection c = null;
		boolean opened = false;
		try {
			c = new UtilDB().GetConn();
			opened = true;
			c.setAutoCommit(false);

			// Charger la vente et vérifier l'état
			Vente vente = new Vente();
			vente.setId(idVente);
			vente = (Vente) CGenUtil.rechercher(vente, null, null, c, " ")[0];
			if (vente.getEtat() < ConstanteEtat.getEtatValider()) {
				throw new Exception("Impossible d'échanger: facture non validée");
			}

			// Charger la ligne originale
			VenteDetailsLib vdSearch = new VenteDetailsLib();
			vdSearch.setNomTable("VENTE_DETAILS_CPL");
			vdSearch.setId(idVenteDetail);
			VenteDetailsLib[] vds = (VenteDetailsLib[]) CGenUtil.rechercher(vdSearch, null, null, c, " ");
			if (vds == null || vds.length == 0) {
				throw new Exception("Ligne de facture introuvable");
			}
			VenteDetailsLib original = vds[0];
			if (!idVente.equalsIgnoreCase(original.getIdVente())) {
				throw new Exception("La ligne ne correspond pas à la facture");
			}

			double qte = (qteParam == null || qteParam <= 0) ? original.getQte() : qteParam;
			if (qte > original.getQte()) {
				throw new Exception("Quantité d'échange supérieure à la quantité de la ligne");
			}

			// Charger le nouveau produit et appliquer la règle de prix
			Produit np = new Produit(); 
				np.setId(idProduitNouveau);
				Produit[] nps = (Produit[]) CGenUtil.rechercher(np, null, null, c, " ");
			if (nps == null || nps.length == 0) {
				throw new Exception("Produit de remplacement introuvable");
			}
			np = nps[0];
			if (np.getPuVente() < original.getPu()) {
				throw new Exception("Prix du produit de remplacement inférieur au produit échangé");
			}

			// 1) Entrée de stock pour le produit retourné
			MvtStock mvtEntree = new MvtStock();
			mvtEntree.setDaty(Utilitaire.dateDuJourSql());
			mvtEntree.setDesignation("Retour échange facture " + idVente + " (" + original.getLibelle() + ")");
			mvtEntree.setIdTransfert(idVente);
			mvtEntree.setIdTypeMvStock(ConstanteStation.TYPEMVTSTOCKENTREE);
			mvtEntree.setIdMagasin(vente.getIdMagasin());
			MvtStockFille entF = new MvtStockFille();
			entF.setIdProduit(original.getIdProduit());
			entF.setEntree(qte);
			mvtEntree.setFille(new MvtStockFille[]{entF});
			mvtEntree.createObject(userId, c);
			mvtEntree.validerObject(userId, c);

			// 2) Générer un BL pour livrer le nouveau produit (puis MVT sortie via BL)
			As_BondeLivraisonClient bl = new As_BondeLivraisonClient();
			bl.setMode("modif");
			bl.setIdvente(idVente);
			bl.setIdclient(vente.getIdClient());
			bl.setMagasin(vente.getIdMagasin());
			bl.setDaty(Utilitaire.dateDuJourSql());
			bl.setEtat(1);
			bl.setRemarque("Livraison dans le cadre d'un échange depuis la facture " + idVente);
			bl.createObject(userId, c);

			As_BondeLivraisonClientFille blf = new As_BondeLivraisonClientFille();
			blf.setMode("modif");
			blf.setNumbl(bl.getId());
			blf.setProduit(np.getId());
			// sécuriser l'unité pour passer les contrôles
			if (np.getIdUnite() == null || np.getIdUnite().trim().isEmpty()) {
				throw new Exception("Le produit de remplacement n'a pas d'unité définie");
			}
			blf.setUnite(np.getIdUnite());
			blf.setQuantite(qte);
			blf.createObject(userId, c);

			// Générer et valider le mouvement de stock (sortie) lié au BL
			bl.genererMvtStockPersist(userId);

			c.commit();
		} catch (Exception e) {
			if (c != null && opened) c.rollback();
			throw e;
		} finally {
			if (c != null && opened) c.close();
		}
	}
}
