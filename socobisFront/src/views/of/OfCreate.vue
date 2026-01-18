<template>
  <div class="container">
    <div class="header">
      <h1>➕ Nouvel Ordre de Fabrication</h1>
      <button @click="goToList" class="btn back-btn">← Retour</button>
    </div>

    <div v-if="loading" class="loading">Chargement...</div>

    <form v-else @submit.prevent="submitForm" class="form">
      <!-- Informations OF -->
      <div class="form-section">
        <h2>Informations OF</h2>

        <div class="form-row">
          <div class="form-group">
            <label>Magasin cible *</label>
            <select v-model="formData.cible" required class="form-input">
              <option value="">Choisir un magasin</option>
              <option v-for="magasin in magasins" :key="magasin.id" :value="magasin.id">
                {{ magasin.val }}
              </option>
            </select>
          </div>

          <div class="form-group">
            <label>Désignation *</label>
            <input v-model="formData.libelle" type="text"  class="form-input" placeholder="Ex: OF pour produit X">
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Date création *</label>
            <input v-model="formData.daty" type="date" required class="form-input">
          </div>

          <div class="form-group">
            <label>Date besoin *</label>
            <input v-model="formData.besoin" type="date" required class="form-input">
          </div>
        </div>

        <div class="form-group">
          <label>Remarque</label>
          <textarea v-model="formData.remarque" class="form-input" rows="2"></textarea>
        </div>

        <div class="form-group">
          <label>Lancé par *</label>
          <input v-model="formData.lancePar" type="text"  class="form-input">
        </div>
      </div>

      <!-- Produits -->
      <div class="form-section">
        <div class="section-header">
          <h2>Produits</h2>
          <button type="button" @click="addProduit" class="btn small-btn">+ Ajouter</button>
        </div>

        <div v-if="formData.produits.length === 0" class="empty">Aucun produit</div>

        <div v-for="(produit, index) in formData.produits" :key="index" class="product-card">
          <div class="product-header">
            <h3>Produit {{ index + 1 }}</h3>
            <button v-if="formData.produits.length > 1" type="button" @click="removeProduit(index)" class="btn delete-btn">×</button>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Produit *</label>
              <select v-model="produit.idIngredients" required class="form-input" @change="onProduitSelect(index)">
                <option value="">Choisir un produit</option>
                <option v-for="prod in produits" :key="prod.id" :value="prod.id">
                  {{ prod.libelle }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Quantité *</label>
              <input v-model="produit.qte" type="number" required min="0.01" step="0.01" class="form-input">
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Unité</label>
              <input v-model="produit.idunite" type="text" class="form-input" readonly>
            </div>

            <div class="form-group">
              <label>Libellé</label>
              <input v-model="produit.libelle" type="text" class="form-input">
            </div>
          </div>

          <div class="form-group">
            <label>Remarque produit</label>
            <textarea v-model="produit.remarque" class="form-input" rows="1"></textarea>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="form-actions">
        <button type="button" @click="resetForm" class="btn secondary-btn">Réinitialiser</button>
        <button type="submit" :disabled="submitting" class="btn primary-btn">
          {{ submitting ? 'Création...' : 'Créer OF' }}
        </button>
      </div>
    </form>

    <!-- Message succès -->
    <div v-if="showSuccess" class="success-modal">
      <div class="success-content">
        <h3>✅ OF créé avec succès</h3>
        <p>ID : {{ createdOfId }}</p>
        <div class="success-actions">
          <button @click="goToList" class="btn primary-btn">OK</button>
          <button @click="createAnother" class="btn secondary-btn">Nouvel OF</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { magasinService } from '@/services/magasin.js'
import { produitsService } from '@/services/produits.js'
import { ofService } from '@/services/of.js'

export default {
  name: 'OfCreate',
  data() {
    return {
      loading: true,
      submitting: false,
      showSuccess: false,
      createdOfId: '',
      magasins: [],
      produits: [],
      formData: {
        cible: '',
        remarque: '',
        libelle: '',
        idBc: '',
        besoin: this.getDefaultDate(7),
        daty: this.getDefaultDate(0),
        lancePar: 'USER001',
        produits: [this.newProduit()]
      }
    }
  },
  created() {
    this.initializeForm()
  },
  methods: {
    getDefaultDate(days) {
      const date = new Date()
      date.setDate(date.getDate() + days)
      return date.toISOString().split('T')[0]
    },

    newProduit() {
      return {
        idIngredients: '',
        remarque: '',
        libelle: '',
        idunite: '',
        qte: 0,
        idBcFille: ''
      }
    },

    async initializeForm() {
      try {
        this.loading = true
        const [magasinsData, produitsData] = await Promise.all([
          magasinService.getAllMagasins(),
          produitsService.getAllProduits()
        ])
        this.magasins = magasinsData
        this.produits = produitsData
      } catch (error) {
        alert('Erreur chargement données')
      } finally {
        this.loading = false
      }
    },

    onProduitSelect(index) {
      const produitId = this.formData.produits[index].idIngredients
      const produit = this.produits.find(p => p.id === produitId)
      if (produit) {
        this.formData.produits[index].idunite = produit.unite || ''
        if (!this.formData.produits[index].libelle) {
          this.formData.produits[index].libelle = produit.libelle || ''
        }
      }
    },

    addProduit() {
      this.formData.produits.push(this.newProduit())
    },

    removeProduit(index) {
      if (this.formData.produits.length > 1) {
        this.formData.produits.splice(index, 1)
      }
    },

    async submitForm() {
      if (!this.validateForm()) return

      this.submitting = true

      try {
        // Fonction pour convertir Date en string YYYY-MM-DD
        const formatDate = (date) => {
          if (!date) return ''
          if (typeof date === 'string') return date.split('T')[0] // Si déjà string
          const d = new Date(date)
          const year = d.getFullYear()
          const month = String(d.getMonth() + 1).padStart(2, '0')
          const day = String(d.getDate()).padStart(2, '0')
          return `${year}-${month}-${day}`
        }

        // Préparer les données pour l'API
        const ofData = {
          cible: this.formData.cible,
          remarque: this.formData.remarque,
          libelle: this.formData.libelle,
          idBc: this.formData.idBc,
          besoin: formatDate(this.formData.besoin), // Convertir en string
          daty: formatDate(this.formData.daty),     // Convertir en string
          lancePar: this.formData.lancePar,
          produits: this.formData.produits.map(p => ({
            ...p,
            qte: parseFloat(p.qte)
          }))
        }

        console.log('Données envoyées:', JSON.stringify(ofData, null, 2))

        // Appeler le service
        const response = await ofService.createOf(ofData)

        // Afficher le succès
        this.createdOfId = response.id || 'N/A'
        this.showSuccess = true

      } catch (error) {
        console.error('Erreur lors de la création:', error)
        alert(`Erreur: ${error.response?.data?.error || error.message}`)
      } finally {
        this.submitting = false
      }
    },

    validateForm() {
      if (!this.formData.cible ) {
        alert('Veuillez remplir le magasin cible')
        return false
      }

      for (const [index, produit] of this.formData.produits.entries()) {
        if (!produit.idIngredients || !produit.qte || produit.qte <= 0) {
          alert(`Produit ${index + 1} invalide`)
          return false
        }
      }

      return true
    },

    resetForm() {
      if (confirm('Réinitialiser ?')) {
        this.formData = {
          cible: '',
          remarque: '',
          libelle: '',
          idBc: '',
          besoin: this.getDefaultDate(7),
          daty: this.getDefaultDate(0),
          lancePar: 'USER001',
          produits: [this.newProduit()]
        }
      }
    },

    goToList() {
      this.$router.push({ name: 'OfList' })
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
  max-width: 1000px;
  margin: 0 auto;
  animation: fadeIn 0.5s ease-out;
}

.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-xl);
  padding: var(--spacing-lg);
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1) 0%, rgba(118, 75, 162, 0.1) 100%);
  border-radius: var(--border-radius-xl);
  backdrop-filter: blur(10px);
}

.header h1 {
  margin: 0;
  font-size: var(--font-size-3xl);
  font-weight: 800;
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}
.form {
  max-width: 900px;
}

.form-section {
  background: var(--white);
  padding: var(--spacing-xl);
  margin-bottom: var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-lg);
  transition: all var(--transition-base);
}

.form-section:hover {
  box-shadow: var(--shadow-xl);
  transform: translateY(-2px);
}

.form-section h2 {
  margin: 0 0 var(--spacing-lg) 0;
  font-size: var(--font-size-xl);
  font-weight: 700;
  color: var(--primary);
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.form-section h2::before {
  content: '';
  width: 4px;
  height: 28px;
  background: linear-gradient(135deg, var(--primary) 0%, var(--secondary) 100%);
  border-radius: 2px;
}
.form-row {
  display: flex;
  gap: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
}

.form-group {
  flex: 1;
}

.form-group label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--font-size-sm);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.form-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  background: var(--gray-50);
  transition: all var(--transition-base);
}

.form-input:focus {
  border-color: var(--primary);
  background: var(--white);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.product-card {
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.03) 0%, rgba(118, 75, 162, 0.03) 100%);
  padding: var(--spacing-lg);
  margin-bottom: var(--spacing-lg);
  border-radius: var(--border-radius-lg);
  border: 2px solid var(--border-color);
  transition: all var(--transition-base);
}

.product-card:hover {
  border-color: var(--primary);
  box-shadow: var(--shadow-md);
  transform: translateY(-2px);
}
.product-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-md);
  padding-bottom: var(--spacing-md);
  border-bottom: 2px solid var(--border-color);
}

.product-header h3 {
  margin: 0;
  font-size: var(--font-size-lg);
  font-weight: 700;
  color: var(--primary);
}

.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: var(--spacing-lg);
}

.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: var(--spacing-md);
  padding: var(--spacing-xl) 0;
}

.btn {
  padding: 12px 28px;
  border: none;
  border-radius: var(--border-radius-md);
  cursor: pointer;
  font-weight: 600;
  font-size: var(--font-size-sm);
  transition: all var(--transition-base);
  box-shadow: var(--shadow-sm);
}

.btn:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: var(--shadow-lg);
}

.btn:active:not(:disabled) {
  transform: translateY(0);
}

.back-btn {
  background: var(--white);
  color: var(--text-secondary);
  border: 2px solid var(--border-color);
}

.back-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.primary-btn {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: var(--white);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.primary-btn:hover:not(:disabled) {
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.primary-btn:disabled {
  background: var(--gray-300);
  cursor: not-allowed;
  transform: none !important;
}

.secondary-btn {
  background: var(--white);
  color: var(--text-primary);
  border: 2px solid var(--border-color);
}

.secondary-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.small-btn {
  padding: 8px 16px;
  font-size: var(--font-size-xs);
  border-radius: var(--border-radius-sm);
}

.delete-btn {
  background: linear-gradient(135deg, var(--error) 0%, var(--error-light) 100%);
  color: var(--white);
  padding: 6px 12px;
  font-size: var(--font-size-lg);
  box-shadow: 0 4px 15px rgba(239, 68, 68, 0.3);
}

.delete-btn:hover {
  box-shadow: 0 6px 20px rgba(239, 68, 68, 0.5);
}

.loading,
.empty {
  text-align: center;
  padding: var(--spacing-2xl);
  color: var(--text-secondary);
  font-size: var(--font-size-lg);
  font-weight: 500;
}

.success-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.6);
  backdrop-filter: blur(8px);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 2000;
  animation: fadeIn 0.3s ease-out;
}

.success-content {
  background: var(--white);
  padding: var(--spacing-2xl);
  border-radius: var(--border-radius-xl);
  text-align: center;
  max-width: 500px;
  width: 90%;
  box-shadow: var(--shadow-xl);
  animation: slideUp 0.4s cubic-bezier(0.34, 1.56, 0.64, 1);
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(40px) scale(0.9);
  }
  to {
    opacity: 1;
    transform: translateY(0) scale(1);
  }
}

.success-content h3 {
  margin: 0 0 var(--spacing-md) 0;
  font-size: var(--font-size-2xl);
  font-weight: 700;
  background: linear-gradient(135deg, var(--success) 0%, var(--success-light) 100%);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.success-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: center;
  margin-top: var(--spacing-xl);
}

@media (max-width: 800px) {
  .form-row {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  .container {
    padding: var(--spacing-md);
  }
  .header {
    flex-direction: column;
    gap: var(--spacing-md);
    text-align: center;
  }
  .form-actions,
  .success-actions {
    flex-direction: column;
  }
}
</style>
