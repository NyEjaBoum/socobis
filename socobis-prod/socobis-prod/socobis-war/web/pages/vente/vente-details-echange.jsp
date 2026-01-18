<%--
    Document   : avance-modif
    Created on : 10 d�c. 2020, 15:44:38
    Author     : mihary
--%>
<%@page import="paie.log.LogPersonnel" %>
<%@page import="paie.avance.Avance" %>
<%@page import="affichage.PageUpdate" %>
<%@page import="user.UserEJB" %>
<%@page import="utilitaire.Utilitaire" %>
<%@page import="affichage.Liste" %>
<%@page import="bean.TypeObjet" %>
<%@ page import="vente.VenteDetails" %>
<%@ page import="vente.Vente" %>
<%
    try {
        String autreparsley = "data-parsley-range='[8, 40]' required";
        UserEJB u = (user.UserEJB) session.getValue("u");
        String mapping = "vente.VenteDetails",
                nomtable = "Vente_Details",
                apres = "vente/vente-liste.jsp",
                titre = "Echange de produit : ";

        String idMereVenteDetail = request.getParameter("idMereVenteDetail");




        VenteDetails objet = new VenteDetails();

        objet.setNomTable(nomtable);
        PageUpdate pi = new PageUpdate(objet, request, u);
        if (idMereVenteDetail == null) {
            VenteDetails vd = (VenteDetails) pi.getBase();
            Vente v = (Vente) vd.findMere("VENTE", null);
            idMereVenteDetail = v.getId();
        }

        pi.setLien((String) session.getValue("lien"));

        String table = "PRODUIT_LIB_MGA";

        pi.getFormu().getChamp("idProduit").setPageAppelComplete("annexe.ProduitLib", "id", table, "puVente;puAchat;taux;val;id;compte", "pu;puAchat;tauxDeChange;designation;idProduit;comptelibelle");

//        affichage.Champ.setPageAppelComplete(pi.getFormu().getChampFille("idProduit"), "annexe.ProduitLib", "id", table, "puVente;puAchat;taux;val;id;compte", "pu;puAchat;tauxDeChange;designation;idProduit;comptelibelle");

        pi.getFormu().getChamp("tva").setLibelle("TVA");
        pi.getFormu().getChamp("remise").setLibelle("remise");
        pi.getFormu().getChamp("idOrigine").setLibelle("Origine");
        pi.getFormu().getChamp("qte").setLibelle("Quantit&eacute;");
        pi.getFormu().getChamp("pu").setLibelle("Prix unitaire");
        pi.getFormu().getChamp("idDevise").setLibelle("Devise");
        pi.getFormu().getChamp("designation").setLibelle("D&eacute;signation");
        pi.getFormu().getChamp("puVente").setVisible(false);
        pi.getFormu().getChamp("puAchat").setVisible(false);
        pi.getFormu().getChamp("puRevient").setVisible(false);
        pi.getFormu().getChamp("idbcfille").setVisible(false);

        pi.getFormu().getChamp("compte").setPageAppelComplete("mg.cnaps.compta.ComptaCompte", "compte", "compta_compte");

//        affichage.Champ.setPageAppelComplete(pi.getFormu().getChampFille("compte"), "mg.cnaps.compta.ComptaCompte", "compte", "compta_compte", "", "");

        pi.getFormu().getChamp("compte").setLibelle("Compte");
        pi.getFormu().getChamp("tauxDeChange").setLibelle("Taux de change");
        pi.getFormu().getChamp("idProduit").setLibelle("Produit");

        pi.preparerDataFormu();


        pi.getFormu().getChamp("idDevise").setAutre("readonly");
        pi.getFormu().getChamp("tauxDeChange").setAutre("readonly");


//        String idpersonnel= pi.getFormu().getChamp("idpersonnel").getValeur();
//        System.out.println("idpersonnel=====>"+idpersonnel);
//
//        affichage.Champ[] liste = new affichage.Champ[1];
//        TypeObjet liste1 = new TypeObjet();
//        liste1.setNomTable("typeavance");
//        liste[0] = new Liste("idtypeavance", liste1, "val", "id");
//        pi.getFormu().changerEnChamp(liste);
//
//        pi.getFormu().getChamp("etat").setVisible(false);
//        pi.getFormu().getChamp("id").setVisible(false);
//        pi.getFormu().getChamp("daty").setVisible(false);
//
//
//        String idpers = pi.getFormu().getChamp("idpersonnel").getValeur();
//        LogPersonnel ls = new LogPersonnel();
//        ls.setId(idpers);
//        String lib_pers = ls.getMotClesString(ls);
//        System.out.println("lib_pers===>"+lib_pers);
//        pi.getFormu().getChamp("remarque").setLibelle("Remarque");
//        pi.getFormu().getChamp("idpersonnel").setLibelle("Personnel");
//        pi.getFormu().getChamp("idpersonnel").setAutre("placeholder='"+lib_pers+"'readonly");
//        pi.getFormu().getChamp("idtypeavance").setLibelle("Type");
//        pi.getFormu().getChamp("montant").setLibelle("Montant");
//        pi.getFormu().getChamp("nbremboursement").setLibelle("Nombre de remboursement");
//        pi.getFormu().getChamp("dateavance").setLibelle("Date de l'avance");
//
//        String[] colOrdre = {"dateavance","remarque","idtypeavance","montant","nbremboursement"};
//        pi.getFormu().setOrdre(colOrdre);
//
//        pi.preparerDataFormu();
%>
<div class="content-wrapper">
    <h1><%=titre%>
    </h1>

    <form action="<%=pi.getLien()%>?but=apresTarif.jsp" method="post" name="<%=nomtable%>" id="<%=nomtable%>">
<%--        <h1>Tsy payer</h1>--%>
        <%
            pi.getFormu().makeHtmlInsertTabIndex();
            out.println(pi.getFormu().getHtmlInsert());
        %>
        <div class="btn-group" role="group">
            <button type="submit"
                    name="acte"
                    value="update"
                    class="btn btn-primary btn-lg">
                <i class="fa fa-check"></i> Valider
            </button>

            <br>

            <button type="submit"
                    name="acte"
                    value="update_payer"
                    class="btn btn-success btn-lg"
                    onclick="return confirm('Voulez-vous valider la facture ET encaisser immédiatement le montant total ?');">
                <i class="fa fa-money"></i> Valider et encaisser
            </button>
        </div>
        <input name="bute" type="hidden" id="bute" value="<%=apres%>">
        <input name="classe" type="hidden" id="classe" value="<%=mapping%>">
        <input name="nomtable" type="hidden" id="nomtable" value="<%=nomtable%>">
    </form>
</div>

<script>

    document.addEventListener("DOMContentLoaded", function () {
        const champidFournisseur = document.getElementById("idClient");
        const observer = new MutationObserver(function (mutationsList) {
            for (let mutation of mutationsList) {
                if (mutation.type === "attributes" && mutation.attributeName === "value") {
                    console.log("Nouvelle valeur :", champidFournisseur.value);
                    changerValeur();
                    // Ton code ici
                }
            }
        });

        observer.observe(champidFournisseur, {attributes: true});
    });


    champ.addEventListener("input", function () {
        console.log("La valeur a changé :", this.value);
    });

    function sanitizeNumber(str) {
        return parseFloat(str.replace(/\s/g, '').replace(',', '.')) || 0;
    }

    changerValeur();

    function changerValeur() {
        const champDaty = document.getElementById("datyPrevu");

        let jour, mois, annee;

        const today = new Date();
        jour = today.getDate();
        mois = today.getMonth() + 1;
        annee = today.getFullYear();
        const date = new Date(annee, mois - 1, jour);

        if (isNaN(date.getTime())) {
            alert("Date invalide !");
            return;
        }

        const nbJours = sanitizeNumber(champ.value);
        date.setDate(date.getDate() + nbJours);

        const formattedDate = [
            String(date.getDate()).padStart(2, '0'),
            String(date.getMonth() + 1).padStart(2, '0'),
            date.getFullYear()
        ].join("/");

        champDaty.value = formattedDate;
        champ.dispatchEvent(new Event("input"));
    }

    function calculerMontant(indice) {
        $('input[id^="qte_"]').each(function () {
            var quantite = parseFloat($("#" + $(this).attr('id').replace("qte", "pu")).val());
            var montant = parseFloat($(this).val());
            if (!isNaN(quantite) && !isNaN(montant)) {
                var value = quantite * montant;
                val += value;
            }
        });
        $("#montanttotal").html(Intl.NumberFormat('fr-FR', {
            minimumFractionDigits: 2,
            maximumFractionDigits: 2
        }).format(val));
    }

    /*function deviseModification() {
        var nombreLigne = parseInt($("#nombreLigne").val());
        for(let iL=0;iL<nombreLigne;iL++){
            $(function(){
                var mapping = {
                    "AR": {
                        "table": "produit_lib_mga",
                    },
                    "USD": {
                        "table": "produit_lib_usd"
                    },
                    "EUR": {
                        "table": "produit_lib_euro"
                    }
                };
                $("#deviseLibelle").html($('#idDevise').val());
                var idDevise = $('#idDevise').val();
                $("#idDevise_"+iL).val(idDevise);
                let autocompleteTriggered = false;
                $("#idProduit_"+iL+"libelle").autocomplete('destroy');
                $("#tauxDeChange_"+iL).val('');
                $("#pu_"+iL).val('');
                $("#idProduit_"+iL+"libelle").autocomplete({
                    source: function(request, response) {
                        $("#idProduit_"+iL).val('');
                        if (autocompleteTriggered) {
                            fetchAutocomplete(request, response, "null", "id", "null", mapping[idDevise].table, "annexe.ProduitLib", "true","puVente;puAchat;taux");
                        }
                    },
                    select: function(event, ui) {
                        $("#idProduit_"+iL+"libelle").val(ui.item.label);
                        $("#idProduit_"+iL).val(ui.item.value);
                        $("#idProduit_"+iL).trigger('change');
                        $(this).autocomplete('disable');
                        var champsDependant = ['pu_'+iL,'puAchat_'+iL,'tauxDeChange_'+iL];
                        for(let i=0;i<champsDependant.length;i++){
                            $('#'+champsDependant[i]).val(ui.item.retour.split(';')[i]);
                        }
                        autocompleteTriggered = false;
                        return false;
                    }
                }).autocomplete('disable');
                $("#idProduit_"+iL+"libelle").off('keydown');
                $("#idProduit_"+iL+"libelle").keydown(function(event) {
                    if (event.key === 'Tab') {
                        event.preventDefault();
                        autocompleteTriggered = true;
                        $(this).autocomplete('enable').autocomplete('search', $(this).val());
                    }
                });
                $("#idProduit_"+iL+"libelle").off('input');
                $("#idProduit_"+iL+"libelle").on('input', function() {
                    $("#idProduit_"+iL).val('');
                    autocompleteTriggered = false;
                    $(this).autocomplete('disable');
                });
                $("#idProduit_"+iL+"searchBtn").off('click');
                $("#idProduit_"+iL+"searchBtn").click(function() {
                    autocompleteTriggered = true;
                    $("#idProduit_"+iL+"libelle").autocomplete('enable').autocomplete('search', $("#idProduit_"+iL+"libelle").val());
                });
            });
        }
    }

    deviseModification();}*/
</script>

<%
} catch (Exception e) {
    e.printStackTrace();
%>
<script language="JavaScript"> alert('<%=e.getMessage()%>');
history.back();</script>

<% }%>
