<%@ page contentType="text/html; charset=UTF-8" pageEncoding="UTF-8" %>
<%@ page import="vente.Vente" %>
<%@ page import="vente.VenteDetailsLib" %>
<%@ page import="bean.CGenUtil" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="utilitaire.Utilitaire" %>
<jsp:include page="../elements/js.jsp" />
<%
    try {
        String lien = (String) session.getAttribute("lien");
        String idVente = request.getParameter("id");
        if (idVente == null || idVente.trim().isEmpty()) {
            throw new Exception("Identifiant de facture manquant");
        }
        Vente v = new Vente();
        v.setId(idVente);
        VenteDetailsLib crt = new VenteDetailsLib();
        crt.setNomTable("VENTE_DETAILS_CPL");
        crt.setIdVente(idVente);
        VenteDetailsLib[] details = (VenteDetailsLib[]) CGenUtil.rechercher(crt, null, null, null, " ");
%>
<div class="content-wrapper">
  <section class="content-header">
    <h1>Echange de produit - Facture <%= idVente %></h1>
  </section>
  <section class="content">
    <div class="box box-primary">
      <div class="box-body">
        <p>
          Règle: le produit de remplacement doit avoir un prix unitaire de vente supérieur ou égal au produit échangé.
        </p>
        <form method="post" action="<%= lien %>?but=vente/vente-echange-valider.jsp" id="formEchange">
          <input type="hidden" name="idVente" value="<%= idVente %>" />
          <input type="hidden" name="idDevise" id="idDeviseExchange" value="AR" />
          <div class="form-group">
            <label for="idVenteDetail">Ligne à échanger</label>
            <select class="form-control" required name="idVenteDetail" id="idVenteDetail">
              <option value="">-- Choisir une ligne --</option>
              <%
                String idVenteDetailSel = request.getParameter("idVenteDetail");
                if (details != null) {
                    for (VenteDetailsLib d : details) {
                        String label = String.format("%s - %s | PU=%.2f | Qte=%.2f", d.getId(), Utilitaire.champNull(d.getLibelle()), d.getPu(), d.getQte());
              %>
              <option value="<%= d.getId() %>" data-pu="<%= d.getPu() %>" data-devise="<%= Utilitaire.champNull(d.getIdDevise()) %>" <%= (idVenteDetailSel!=null && idVenteDetailSel.equals(d.getId()))?"selected":"" %>><%= label %></option>
              <%
                    }
                }
              %>
            </select>
          </div>
          <div class="form-group">
            <label for="idProduitNouveauLib">Produit de remplacement</label>
            <div class="input-group">
              <input type="hidden" required name="idProduitNouveau" id="idProduitNouveau" />
              <input type="text" class="form-control" id="idProduitNouveauLib" placeholder="Recherchez un produit (TAB pour lister)" autocomplete="off" />
              <span class="input-group-btn">
                <button class="btn btn-default" type="button" id="idProduitNouveauSearchBtn"><i class="fa fa-search"></i></button>
              </span>
            </div>
            <small>Astuce: appuyez sur TAB ou cliquez Rechercher pour afficher la liste.</small>
          </div>
          <div class="form-group">
            <label for="qte">Quantité</label>
            <input type="number" step="0.01" min="0.01" class="form-control" name="qte" id="qte" placeholder="Laisser vide pour échanger la quantité totale" />
          </div>
          <button type="submit" class="btn btn-primary">Valider l'échange</button>
          <a href="<%= lien %>?but=vente/vente-fiche.jsp&id=<%= idVente %>" class="btn btn-default" style="margin-left:10px">Annuler</a>
        </form>
        <script>
          (function(){
            // Utilise la même table produit que les autres écrans pour la recherche.
            var TABLE_PRODUIT = "PRODUIT_LIB_MGA";

            function getSelectedDetailPU(){
              var opt = document.querySelector('#idVenteDetail option:checked');
              if(!opt) return NaN;
              var pu = parseFloat(opt.getAttribute('data-pu'));
              return isNaN(pu) ? NaN : pu;
            }

            function getSelectedDetailDevise(){
              var opt = document.querySelector('#idVenteDetail option:checked');
              var dev = (opt && opt.getAttribute('data-devise')) || 'AR';
              if(!dev || dev.trim()==='') dev = 'AR';
              return dev.toUpperCase();
            }

            function initAutocomplete(){
              var devise = getSelectedDetailDevise();
              document.getElementById('idDeviseExchange').value = devise;
              // jQuery UI autocomplete réutilisant fetchAutocomplete défini dans elements/js.jsp
              var $lib = $("#idProduitNouveauLib");
              var $id = $("#idProduitNouveau");
              var $btn = $("#idProduitNouveauSearchBtn");

              var autocompleteTriggered = false;
              try { $lib.autocomplete('destroy'); } catch(e) {}
              $lib.val('');
              $id.val('');

              $lib.autocomplete({
                source: function(request, response){
                  $id.val('');
                  if(!autocompleteTriggered){
                    return;
                  }
                  var puOriginal = getSelectedDetailPU();

                  // On enveloppe la réponse de fetchAutocomplete pour filtrer les produits
                  // qui ne respectent pas la règle puVente >= puOriginal.
                  fetchAutocomplete(
                    request,
                    function(items){
                      if(isNaN(puOriginal)){
                        // Si pas de PU original (cas anormal), on laisse tout passer
                        return response(items);
                      }
                      var filtres = $.map(items, function(it){
                        var retour = (it.retour ? it.retour.split(';') : []);
                        var puVente = parseFloat(retour[0] || '0');
                        if(!isNaN(puVente) && puVente >= puOriginal){
                          return it; // garder dans la liste
                        }
                        return null;  // exclure
                      });
                      response(filtres);
                    },
                    "null",
                    "id",
                    "null",
                    TABLE_PRODUIT,
                    "annexe.ProduitLib",
                    "true",
                    "puVente;puAchat;taux"
                  );
                },
                select: function(event, ui){
                  // À ce stade, la liste est déjà filtrée (puVente >= puOriginal).
                  // Comme dans vente-modif.jsp, on utilise directement ui.item.value comme id produit.
                  $lib.val(ui.item.label);
                  $id.val(ui.item.value);
                  $(this).autocomplete('disable');
                  autocompleteTriggered = false;
                  return false;
                }
              }).autocomplete('disable');

              $lib.off('keydown').on('keydown', function(event){
                if(event.key === 'Tab'){
                  event.preventDefault();
                  autocompleteTriggered = true;
                  $(this).autocomplete('enable').autocomplete('search', $(this).val());
                }
              });
              // Déclenchement automatique après saisie (>=2 caractères) pour confort utilisateur
              var autoTimer = null;
              $lib.off('input').on('input', function(){
                $id.val('');
                autocompleteTriggered = false;
                $(this).autocomplete('disable');
                clearTimeout(autoTimer);
                var self = this;
                if(this.value.length >= 2){
                  autoTimer = setTimeout(function(){
                    autocompleteTriggered = true;
                    $(self).autocomplete('enable').autocomplete('search', self.value);
                  }, 250);
                }
              });
              $btn.off('click').on('click', function(){
                autocompleteTriggered = true;
                $lib.autocomplete('enable').autocomplete('search', $lib.val());
              });

              // Sécurité supplémentaire : empêche la soumission si aucun produit sélectionné
              $('#formEchange').off('submit.echange').on('submit.echange', function(){
                if(!$id.val()){
                  alert('Veuillez choisir un produit de remplacement dans la liste.');
                  return false;
                }
                return true;
              });
            }

            // Met à jour devise et réinitialise l 'autocomplete quand la ligne change
            $('#idVenteDetail').on('change', function(){
              initAutocomplete();
            });

            // Initialisation au chargement (et si une ligne est déjà présélectionnée)
            initAutocomplete();

            // Double filet de sécurité côté client à la soumission
            $('#formEchange').on('submit', function(){
              var puOriginal = getSelectedDetailPU();
              // si l'ID produit est vide, on bloque car champ requis côté HTML peut ne pas suffire pour le hidden
              if(!$('#idProduitNouveau').val()){
                alert('Veuillez choisir un produit de remplacement.');
                return false;
              }
              return true; // règle de prix déjà contrôlée au select; reste contrôlée côté serveur
            });
          })();
        </script>
      </div>
    </div>
  </section>
</div>
<%
    } catch (Exception e) {
        e.printStackTrace();
%>
<script>alert("<%= new String(e.getMessage().getBytes(), "UTF-8") %>"); history.back();</script>
<%
    }
%>
