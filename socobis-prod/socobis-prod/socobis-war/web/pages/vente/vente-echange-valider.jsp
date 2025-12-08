<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vente.EchangeService" %>
<%@ page import="user.UserEJB" %>
<%
try {
	String lien = (String) session.getAttribute("lien");
	UserEJB u = (UserEJB) session.getAttribute("u");
	String idVente = request.getParameter("idVente");
	String idVenteDetail = request.getParameter("idVenteDetail");
	String idProduitNouveau = request.getParameter("idProduitNouveau");
	System.out.println("DEBUG ECHANGE idProduitNouveau = " + idProduitNouveau);
	String qteStr = request.getParameter("qte");

	if (idVente == null || idVente.trim().isEmpty()) throw new Exception("Facture introuvable");
	if (idVenteDetail == null || idVenteDetail.trim().isEmpty()) throw new Exception("Ligne de facture manquante");
	if (idProduitNouveau == null || idProduitNouveau.trim().isEmpty()) throw new Exception("Produit de remplacement manquant");

	Double qte = null;
	if (qteStr != null && qteStr.trim().length() > 0) {
		qte = Double.valueOf(qteStr);
	}

	// Exécuter l'échange (service assure les vérifications + MVT stock + BL)
	EchangeService.echangerProduit(u.getUser().getTuppleID(), idVente, idVenteDetail, idProduitNouveau, qte);
%>
<script>
	alert('Echange effectué avec succès. Les mouvements de stock et la livraison ont été générés.');
	document.location.replace('<%=lien%>?but=vente/vente-fiche.jsp&id=<%=idVente%>');
 </script>
<%
} catch (Exception e) {
	e.printStackTrace();
%>
<script>
	alert('<%= new String(e.getMessage().getBytes(), "UTF-8") %>');
	history.back();
</script>
<%
}
%>
