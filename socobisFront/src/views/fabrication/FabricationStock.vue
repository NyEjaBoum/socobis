<!-- FabricationStock.vue -->
<template>
  <div class="container">
    <div class="header">
      <h1>📦 Gestion des stocks - Fabrication {{ fabricationId }}</h1>
      <button @click="goBack" class="btn back-btn">← Retour</button>
    </div>

    <div v-if="loading" class="loading">Chargement des données...</div>

    <form v-else @submit.prevent="submitForm" class="form">
      <!-- Informations du mouvement -->
      <div class="form-section">
        <h2>Informations du mouvement</h2>

        <div class="form-row">
          <div class="form-group">
            <label>Type de mouvement *</label>
            <select v-model="formData.idTypeMvStock" required class="form-input" @change="onTypeMvtChange">
              <option value="">Choisir un type</option>
              <option v-for="type in typeMvtStocks" :key="type.id" :value="type.id">
                {{ type.val }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Magasin *</label>
            <select v-model="formData.idMagasin" required class="form-input" @change="onMagasinChange">
              <option value="">Choisir un magasin</option>
              <option v-for="magasin in magasins" :key="magasin.id" :value="magasin.id">
                {{ magasin.val }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Point</label>
            <select v-model="formData.idPoint" class="form-input">
              <option value="">Choisir un point</option>
              <option v-for="point in points" :key="point.id" :value="point.id">
                {{ point.val }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Date *</label>
            <input v-model="formData.daty" type="date" required class="form-input">
          </div>
        </div>

        <div class="form-group">
          <label>Désignation *</label>
          <input v-model="formData.designation" type="text" required class="form-input"
                 :placeholder="'Mouvement de stock - Fabrication ' + fabricationId">
        </div>

        <div class="form-group">
          <label>Fabrication précédente</label>
          <input v-model="formData.fabPrecedent" type="text" class="form-input" placeholder="ID fabrication précédente">
        </div>
      </div>

      <!-- Détails des produits -->
      <div class="form-section">
        <div class="section-header">
          <h2>Détails des produits</h2>
          <button type="button" @click="loadFromFabrication" class="btn small-btn">
            🔄 Charger depuis fabrication
          </button>
        </div>

        <div v-if="formData.produits.length === 0" class="empty">
          Aucun produit. Cliquez sur "Charger depuis fabrication" pour pré-remplir.
        </div>

        <table v-else class="table">
          <thead>
            <tr>
              <th>Produit</th>
              <th>Désignation</th>
              <th v-if="showEntree">Entrée</th>
              <th v-if="showSortie">Sortie</th>
              <th>Prix unitaire</th>
              <th>Mouvement source</th>
            </tr>
          </thead>
          <tbody>
            <tr v-for="(produit, index) in formData.produits" :key="index">
              <td>
                <select v-model="produit.idProduit" required class="form-input small" @change="onProduitSelect(index)">
                  <option value="">Choisir un produit</option>
                  <option v-for="ing in ingredientsAuto" :key="ing.id" :value="ing.id">
                    {{ ing.libelle }}
                  </option>
                </select>
              </td>
              <td>
                <input v-model="produit.designation" type="text" class="form-input small">
              </td>
              <td v-if="showEntree">
                <input v-model="produit.entree" type="number" min="0" step="0.01" class="form-input small">
              </td>
              <td v-if="showSortie">
                <input v-model="produit.sortie" type="number" min="0" step="0.01" class="form-input small">
              </td>
              <td>
                <input v-model="produit.pu" type="number" min="0" step="0.01" class="form-input small">
              </td>
              <td>
                <input v-model="produit.mvtSrc" type="text" class="form-input small" placeholder="ID mouvement source">
              </td>
            </tr>
          </tbody>
        </table>
      </div>

      <!-- Actions -->
      <div class="form-actions">
        <button type="button" @click="resetForm" class="btn secondary-btn">Réinitialiser</button>
        <button type="submit" :disabled="submitting" class="btn primary-btn">
          {{ submitting ? 'Création...' : 'Créer mouvement de stock' }}
        </button>
      </div>
    </form>

    <!-- Modal de succès -->
    <div v-if="showSuccess" class="success-modal">
      <div class="success-content">
        <h3>✅ Mouvement de stock créé avec succès</h3>
        <p>ID : {{ createdMvtId }}</p>
        <div class="success-actions">
          <button @click="goBack" class="btn primary-btn">Retour à la liste</button>
          <button @click="createAnother" class="btn secondary-btn">Nouveau mouvement</button>
          <button v-if="createdMvtId" @click="validateMvtStock" class="btn validate-btn">Valider mouvement</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { magasinService } from '@/services/magasin.js'
import { mvtStockService } from '@/services/mvtStock.js'
import { fabricationService } from '@/services/fabrication.js'

export default {
  name: 'FabricationStock',
  data() {
    return {
      loading: true,
      submitting: false,
      showSuccess: false,
      createdMvtId: '',
      fabricationId: '',
      mouvementType: '',

      typeMvtStocks: [],
      points: [],
      magasins: [],
      ingredientsAuto: [],

      formData: {
        designation: '',
        idMagasin: '',
        idPoint: '',
        idTypeMvStock: '',
        idobjet: '',
        fabPrecedent: '',
        daty: this.getDefaultDate(0),
        produits: []
      }
    }
  },
  computed: {
    showEntree() {
      return this.formData.idTypeMvStock === 'TYPEMVT001' || this.mouvementType === 'ENTREE'
    },
    showSortie() {
      return this.formData.idTypeMvStock === 'TYPEMVT002' || this.mouvementType === 'SORTIE'
    }
  },
  created() {
    this.fabricationId = this.$route.params.id
    this.mouvementType = this.$route.query.typeMvt || ''
    this.initializeForm()
  },
  methods: {
    getDefaultDate(days) {
      const date = new Date()
      date.setDate(date.getDate() + days)
      return date.toISOString().split('T')[0]
    },

    async initializeForm() {
      try {
        this.loading = true

        // Charger toutes les données de référence en parallèle
        const [magasinsData, typeMvtData, pointsData, ingredientsData] = await Promise.all([
          magasinService.getAllMagasins(),
          mvtStockService.getTypeMvtStocks(),
          mvtStockService.getPoints(),
          mvtStockService.getIngredientsAuto()
        ])

        this.magasins = magasinsData
        this.typeMvtStocks = typeMvtData
        this.points = pointsData
        this.ingredientsAuto = ingredientsData

        // Définir le type de mouvement selon le paramètre
        if (this.mouvementType === 'ENTREE') {
          this.formData.idTypeMvStock = 'TYPEMVT001' // Type entrée
          this.formData.designation = `Entrée de stock - Fabrication ${this.fabricationId}`
        } else if (this.mouvementType === 'SORTIE') {
          this.formData.idTypeMvStock = 'TYPEMVT002' // Type sortie
          this.formData.designation = `Sortie de stock - Fabrication ${this.fabricationId}`
        }

        this.formData.idobjet = this.fabricationId

      } catch (error) {
        console.error('Erreur initialisation:', error)
        alert('Erreur lors du chargement des données')
      } finally {
        this.loading = false
      }
    },

    async loadFromFabrication() {
      try {
        if (!this.formData.idTypeMvStock) {
          alert('Veuillez d\'abord sélectionner un type de mouvement')
          return
        }

        if (!this.formData.idMagasin) {
          alert('Veuillez d\'abord sélectionner un magasin')
          return
        }

        this.loading = true

        // Récupérer les données de mouvement de stock depuis la fabrication
        const mvtStockData = await mvtStockService.getFabricationMvtStock(
          this.fabricationId,
          this.formData.idTypeMvStock,
          this.formData.idMagasin
        )

        if (mvtStockData && mvtStockData.fille) {
          this.formData.produits = mvtStockData.fille.map(fille => ({
            idProduit: fille.idProduit,
            designation: fille.designation || '',
            entree: fille.entree || 0,
            sortie: fille.sortie || 0,
            pu: fille.pu || 0,
            mvtSrc: fille.mvtSrc || ''
          }))

          // Mettre à jour la désignation si vide
          if (!this.formData.designation && mvtStockData.designation) {
            this.formData.designation = mvtStockData.designation
          }

          alert(`Chargé ${this.formData.produits.length} produits depuis la fabrication`)
        } else {
          alert('Aucune donnée de mouvement de stock disponible pour cette fabrication')
        }

      } catch (error) {
        console.error('Erreur chargement:', error)
        alert(`Erreur: ${error.response?.data?.error || error.message}`)
      } finally {
        this.loading = false
      }
    },

    onTypeMvtChange() {
      // Mettre à jour la désignation selon le type
      const type = this.typeMvtStocks.find(t => t.id === this.formData.idTypeMvStock)
      if (type) {
        if (type.id === 'TYPEMVT001') {
          this.formData.designation = `Entrée de stock - Fabrication ${this.fabricationId}`
        } else if (type.id === 'TYPEMVT002') {
          this.formData.designation = `Sortie de stock - Fabrication ${this.fabricationId}`
        }
      }
    },

    onMagasinChange() {
      // Recharger les produits si le magasin change
      if (this.formData.produits.length > 0) {
        if (confirm('Changer de magasin va vider la liste des produits. Continuer?')) {
          this.formData.produits = []
        } else {
          // Revenir à l'ancienne valeur
          // Note: Vous devrez stocker l'ancienne valeur
        }
      }
    },

    onProduitSelect(index) {
      const produitId = this.formData.produits[index].idProduit
      const produit = this.ingredientsAuto.find(p => p.id === produitId)
      if (produit) {
        if (!this.formData.produits[index].designation) {
          this.formData.produits[index].designation = produit.libelle || ''
        }
        if (!this.formData.produits[index].pu) {
          this.formData.produits[index].pu = produit.pu || 0
        }
      }
    },

    async submitForm() {
      if (!this.validateForm()) return

      this.submitting = true

      try {
        const formatDate = (date) => {
          if (!date) return ''
          if (typeof date === 'string') return date.split('T')[0]
          const d = new Date(date)
          const year = d.getFullYear()
          const month = String(d.getMonth() + 1).padStart(2, '0')
          const day = String(d.getDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }

        // Préparer les données
        const mvtData = {
          designation: this.formData.designation,
          idMagasin: this.formData.idMagasin,
          idPoint: this.formData.idPoint,
          idTypeMvStock: this.formData.idTypeMvStock,
          idobjet: this.formData.idobjet,
          fabPrecedent: this.formData.fabPrecedent,
          daty: formatDate(this.formData.daty),
          produits: this.formData.produits.map(p => ({
            ...p,
            entree: p.entree ? parseFloat(p.entree) : 0,
            sortie: p.sortie ? parseFloat(p.sortie) : 0,
            pu: p.pu ? parseFloat(p.pu) : 0
          }))
        }

        console.log('Données envoyées:', JSON.stringify(mvtData, null, 2))

        const response = await mvtStockService.createMvtStock(mvtData)

        this.createdMvtId = response.id || 'N/A'
        this.showSuccess = true

      } catch (error) {
        console.error('Erreur lors de la création:', error)
        alert(`Erreur: ${error.response?.data?.error || error.message}`)
      } finally {
        this.submitting = false
      }
    },

    validateForm() {
      if (!this.formData.designation || !this.formData.idMagasin ||
          !this.formData.idTypeMvStock || !this.formData.daty) {
        alert('Veuillez remplir tous les champs obligatoires')
        return false
      }

      if (this.formData.produits.length === 0) {
        alert('Veuillez ajouter au moins un produit')
        return false
      }

      for (const [index, produit] of this.formData.produits.entries()) {
        if (!produit.idProduit) {
          alert(`Produit ${index + 1}: ID produit manquant`)
          return false
        }

        if (this.showEntree && (!produit.entree || produit.entree <= 0)) {
          alert(`Produit ${index + 1}: Quantité d'entrée invalide`)
          return false
        }

        if (this.showSortie && (!produit.sortie || produit.sortie <= 0)) {
          alert(`Produit ${index + 1}: Quantité de sortie invalide`)
          return false
        }
      }

      return true
    },

    async validateMvtStock() {
      if (!this.createdMvtId) return

      try {
        await mvtStockService.validateMvtStock(this.createdMvtId)
        alert('Mouvement de stock validé avec succès!')
        this.goBack()
      } catch (error) {
        alert(`Erreur lors de la validation: ${error.response?.data?.error || error.message}`)
      }
    },

    resetForm() {
      if (confirm('Réinitialiser le formulaire ?')) {
        this.formData = {
          designation: this.mouvementType === 'ENTREE'
            ? `Entrée de stock - Fabrication ${this.fabricationId}`
            : this.mouvementType === 'SORTIE'
            ? `Sortie de stock - Fabrication ${this.fabricationId}`
            : '',
          idMagasin: '',
          idPoint: '',
          idTypeMvStock: this.mouvementType === 'ENTREE' ? 'TYPEMVT001'
                       : this.mouvementType === 'SORTIE' ? 'TYPEMVT002' : '',
          idobjet: this.fabricationId,
          fabPrecedent: '',
          daty: this.getDefaultDate(0),
          produits: []
        }
      }
    },

    goBack() {
      this.$router.go(-1)
    },

    createAnother() {
      this.showSuccess = false
      this.resetForm()
    }
  }
}
</script>

<style scoped>
.container {
  padding: 32px 16px;
  max-width: 1100px;
  margin: 0 auto;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
  padding-bottom: 0;
  border-bottom: none;
}
.header h1 {
  margin: 0;
  color: #222;
  font-size: 1.7rem;
  font-weight: 700;
}
.btn {
  padding: 8px 18px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  background: #f3f7fa;
  color: #222;
  transition: background 0.2s;
}
.btn:hover:not(:disabled) {
  background: #eaf6ff;
}
.back-btn {
  background: #eee;
  color: #888;
}
.back-btn:hover {
  background: #ccc;
}
.primary-btn {
  background: #e67e22;
  color: #fff;
}
.primary-btn:hover:not(:disabled) {
  background: #d35400;
}
.primary-btn:disabled {
  background: #bdc3c7;
  cursor: not-allowed;
}
.secondary-btn {
  background: #f8fafc;
  color: #222;
  border: 1px solid #e0e0e0;
}
.secondary-btn:hover {
  background: #e0e0e0;
}
.validate-btn {
  background: #2ecc71;
  color: #fff;
}
.validate-btn:hover {
  background: #27ae60;
}
.small-btn {
  padding: 5px 10px;
  font-size: 13px;
}
.form {
  background: #fff;
  border-radius: 10px;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
  overflow: hidden;
}
.form-section {
  padding: 18px 16px;
  border-bottom: 1px solid #f0f0f0;
}
.form-section:last-child {
  border-bottom: none;
}
.form-section h2 {
  margin: 0 0 18px 0;
  color: #e67e22;
  font-size: 1.1rem;
  display: flex;
  justify-content: space-between;
  align-items: center;
}
.form-row {
  display: flex;
  gap: 18px;
  margin-bottom: 12px;
}
.form-group {
  flex: 1;
}
.form-group label {
  display: block;
  margin-bottom: 4px;
  font-weight: 500;
  color: #555;
  font-size: 14px;
}
.form-input {
  width: 100%;
  padding: 8px 10px;
  border: 1px solid #e0e0e0;
  border-radius: 6px;
  font-size: 15px;
  background: #f8fafc;
  transition: border-color 0.2s;
}
.form-input:focus {
  outline: none;
  border-color: #e67e22;
  box-shadow: 0 0 0 2px rgba(230, 126, 34, 0.1);
}
.form-input.small {
  padding: 6px;
  font-size: 13px;
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.table {
  width: 100%;
  border-collapse: collapse;
  margin-top: 15px;
  background: #fff;
  border-radius: 8px;
  overflow: hidden;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
}
.table th {
  background: #f3f7fa;
  color: #222;
  padding: 12px 10px;
  text-align: left;
  font-weight: 600;
  font-size: 15px;
  border-bottom: 2px solid #e0e0e0;
}
.table td {
  padding: 10px 8px;
  border-bottom: 1px solid #f0f0f0;
  vertical-align: middle;
}
.table tr:hover {
  background: #f6fbff;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 15px;
  padding: 18px 0;
  background: #f8fafc;
  border-top: 1px solid #e0e0e0;
}
.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #888;
  font-size: 16px;
}
.empty {
  background: #f8fafc;
  border-radius: 4px;
  border: 1px dashed #e0e0e0;
}
.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0,0,0,0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}
.success-content {
  background: #fff;
  padding: 30px;
  border-radius: 8px;
  text-align: center;
  max-width: 500px;
  width: 90%;
  box-shadow: 0 10px 30px rgba(0,0,0,0.08);
}
.success-content h3 {
  margin: 0 0 10px 0;
  color: #27ae60;
  font-size: 20px;
}
.success-content p {
  margin: 0 0 18px 0;
  color: #666;
  font-size: 15px;
  font-weight: 500;
}
.success-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  flex-wrap: wrap;
}
@media (max-width: 800px) {
  .form-row {
    flex-direction: column;
    gap: 10px;
  }
  .container {
    padding: 12px 2px;
  }
  .table th, .table td {
    padding: 8px 4px;
  }
  .success-actions {
    flex-direction: column;
  }
  .success-actions .btn {
    width: 100%;
    margin-bottom: 10px;
  }
}
.stock-out-btn {
  background: #e74c3c;
  color: #fff;
}
.stock-out-btn:hover {
  background: #c0392b;
}
.stock-in-btn {
  background: #2ecc71;
  color: #fff;
}
.stock-in-btn:hover {
  background: #27ae60;
}
.view-btn {
  background: #3498db;
  color: #fff;
}
.block-btn {
  background: #f39c12;
  color: #fff;
}
.finish-btn {
  background: #9b59b6;
  color: #fff;
}
</style>
