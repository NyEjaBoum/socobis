<!-- FabricationCreate.vue -->
<template>
  <div class="container">
    <div class="header">
      <h1>🏭 Nouvelle Fabrication</h1>
      <button @click="goToList" class="btn back-btn">← Retour</button>
    </div>

    <div v-if="loading" class="loading">Chargement...</div>

    <form v-else @submit.prevent="submitForm" class="form">
      <!-- Informations Fabrication -->
      <div class="form-section">
        <h2>Informations Fabrication</h2>

        <div class="form-row">
          <div class="form-group">
            <label>OF associé</label>
            <input v-model="formData.idOf" type="text" class="form-input" placeholder="ID OF">
          </div>

          <div class="form-group">
            <label>OF Fille associée</label>
            <input v-model="formData.idOffille" type="text" class="form-input" placeholder="ID OF Fille">
          </div>
        </div>

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
            <label>Lancé par *</label>
            <select v-model="formData.lancePar" required class="form-input">
              <option value="">Choisir un magasin</option>
              <option v-for="magasin in magasins" :key="magasin.id" :value="magasin.id">
                {{ magasin.val }}
              </option>
            </select>
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Désignation *</label>
            <input v-model="formData.libelle" type="text" required class="form-input" placeholder="Ex: Fabrication produit X">
          </div>

          <div class="form-group">
            <label>Équipe</label>
            <input v-model="formData.equipe" type="text" class="form-input" placeholder="Équipe de fabrication">
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Date création *</label>
            <input v-model="formData.daty" type="date" required class="form-input">
          </div>

          <div class="form-group">
            <label>Date besoin</label>
            <input v-model="formData.besoin" type="date" class="form-input">
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Fabrication précédente</label>
            <input v-model="formData.fabricationPrec" type="text" class="form-input" placeholder="ID fabrication précédente">
          </div>

          <div class="form-group">
            <label>Fabrication suivante</label>
            <input v-model="formData.fabricationSuiv" type="text" class="form-input" placeholder="ID fabrication suivante">
          </div>
        </div>

        <div class="form-row">
          <div class="form-group">
            <label>Nombre de pétrins</label>
            <input v-model="formData.nbPetris" type="number" min="0" step="0.01" class="form-input">
          </div>

          <div class="form-group">
            <label>Bon de commande</label>
            <input v-model="formData.idBc" type="text" class="form-input" placeholder="ID BC">
          </div>
        </div>

        <div class="form-group">
          <label>Remarque</label>
          <textarea v-model="formData.remarque" class="form-input" rows="2"></textarea>
        </div>
      </div>

      <!-- Composants de fabrication -->
      <div class="form-section">
        <div class="section-header">
          <h2>Composants de fabrication</h2>
          <button type="button" @click="addComposant" class="btn small-btn">+ Ajouter</button>
        </div>

        <div v-if="formData.produits.length === 0" class="empty">Aucun composant</div>

        <div v-for="(composant, index) in formData.produits" :key="index" class="composant-card">
          <div class="composant-header">
            <h3>Composant {{ index + 1 }}</h3>
            <button v-if="formData.produits.length > 1" type="button" @click="removeComposant(index)" class="btn delete-btn">×</button>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Composant *</label>
              <select v-model="composant.idIngredients" required class="form-input" @change="onComposantSelect(index)">
                <option value="">Choisir un composant</option>
                <option v-for="prod in produits" :key="prod.id" :value="prod.id">
                  {{ prod.libelle }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Quantité *</label>
              <input v-model="composant.qte" type="number" required min="0.01" step="0.01" class="form-input">
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Machine</label>
              <select v-model="composant.idMachine" class="form-input">
                <option value="">Choisir une machine</option>
                <option v-for="machine in machines" :key="machine.id" :value="machine.id">
                  {{ machine.libelle || machine.val }}
                </option>
              </select>
            </div>

            <div class="form-group">
              <label>Unité</label>
              <input v-model="composant.idunite" type="text" class="form-input" readonly>
            </div>
          </div>

          <div class="form-row">
            <div class="form-group">
              <label>Niveau</label>
              <input v-model="composant.niveau" type="number" min="0" class="form-input">
            </div>

            <div class="form-group">
              <label>PU</label>
              <input v-model="composant.pu" type="number" min="0" step="0.01" class="form-input">
            </div>
          </div>

          <div class="form-group">
            <label>Opérateur</label>
            <input v-model="composant.operateur" type="text" class="form-input">
          </div>

          <div class="form-group">
            <label>Remarque composant</label>
            <textarea v-model="composant.remarque" class="form-input" rows="1"></textarea>
          </div>
        </div>
      </div>

      <!-- Actions -->
      <div class="form-actions">
        <button type="button" @click="resetForm" class="btn secondary-btn">Réinitialiser</button>
        <button type="submit" :disabled="submitting" class="btn primary-btn">
          {{ submitting ? 'Création...' : 'Créer Fabrication' }}
        </button>
      </div>
    </form>

    <!-- Message succès -->
    <div v-if="showSuccess" class="success-modal">
      <div class="success-content">
        <h3>✅ Fabrication créée avec succès</h3>
        <p>ID : {{ createdFabId }}</p>
        <div class="success-actions">
          <button @click="goToList" class="btn primary-btn">OK</button>
          <button @click="createAnother" class="btn secondary-btn">Nouvelle Fabrication</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { magasinService } from '@/services/magasin.js'
import { produitsService } from '@/services/produits.js'
import { fabricationService } from '@/services/fabrication.js'
import { machineService } from '@/services/machine.js'

export default {
  name: 'FabricationCreate',
  data() {
    return {
      loading: true,
      submitting: false,
      showSuccess: false,
      createdFabId: '',
      magasins: [],
      produits: [],
      machines: [],
      formData: {
        idOf: '',
        idOffille: '',
        lancePar: '',
        cible: '',
        remarque: '',
        libelle: '',
        idBc: '',
        besoin: this.getDefaultDate(7),
        daty: this.getDefaultDate(0),
        fabricationPrec: '',
        fabricationSuiv: '',
        equipe: '',
        nbPetris: null,
        produits: [this.newComposant()]
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

    newComposant() {
      return {
        idIngredients: '',
        remarque: '',
        libelle: '',
        idunite: '',
        qte: 0,
        idBcFille: '',
        idMachine: '',
        operateur: '',
        niveau: null,
        pu: null
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

        // Charger les machines si le service existe
        try {
          const machinesData = await machineService.getAllMachines()
          this.machines = machinesData
        } catch (e) {
          console.log('Service machine non disponible')
          this.machines = []
        }
      } catch (error) {
        alert('Erreur chargement données')
      } finally {
        this.loading = false
      }
    },

    onComposantSelect(index) {
      const composantId = this.formData.produits[index].idIngredients
      const composant = this.produits.find(p => p.id === composantId)
      if (composant) {
        this.formData.produits[index].idunite = composant.unite || ''
        if (!this.formData.produits[index].libelle) {
          this.formData.produits[index].libelle = composant.libelle || ''
        }
      }
    },

    addComposant() {
      this.formData.produits.push(this.newComposant())
    },

    removeComposant(index) {
      if (this.formData.produits.length > 1) {
        this.formData.produits.splice(index, 1)
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
        const fabData = {
          idOf: this.formData.idOf,
          idOffille: this.formData.idOffille,
          lancePar: this.formData.lancePar,
          cible: this.formData.cible,
          remarque: this.formData.remarque,
          libelle: this.formData.libelle,
          idBc: this.formData.idBc,
          besoin: formatDate(this.formData.besoin),
          daty: formatDate(this.formData.daty),
          fabricationPrec: this.formData.fabricationPrec,
          fabricationSuiv: this.formData.fabricationSuiv,
          equipe: this.formData.equipe,
          nbPetris: this.formData.nbPetris,
          produits: this.formData.produits.map(p => ({
            ...p,
            qte: parseFloat(p.qte),
            niveau: p.niveau ? parseInt(p.niveau) : null,
            pu: p.pu ? parseFloat(p.pu) : null
          }))
        }

        console.log('Données envoyées:', JSON.stringify(fabData, null, 2))

        const response = await fabricationService.createFabrication(fabData)

        this.createdFabId = response.id || 'N/A'
        this.showSuccess = true

      } catch (error) {
        console.error('Erreur lors de la création:', error)
        alert(`Erreur: ${error.response?.data?.error || error.message}`)
      } finally {
        this.submitting = false
      }
    },

    validateForm() {
      if (!this.formData.cible || !this.formData.lancePar || !this.formData.libelle) {
        alert('Veuillez remplir les champs obligatoires (magasin cible, lancé par, désignation)')
        return false
      }

      for (const [index, composant] of this.formData.produits.entries()) {
        if (!composant.idIngredients || !composant.qte || composant.qte <= 0) {
          alert(`Composant ${index + 1} invalide`)
          return false
        }
      }

      return true
    },

    resetForm() {
      if (confirm('Réinitialiser ?')) {
        this.formData = {
          idOf: '',
          idOffille: '',
          lancePar: '',
          cible: '',
          remarque: '',
          libelle: '',
          idBc: '',
          besoin: this.getDefaultDate(7),
          daty: this.getDefaultDate(0),
          fabricationPrec: '',
          fabricationSuiv: '',
          equipe: '',
          nbPetris: null,
          produits: [this.newComposant()]
        }
      }
    },

    goToList() {
      this.$router.push({ name: 'FabricationList' })
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
  max-width: 900px;
  margin: 0 auto;
}
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 28px;
}
.header h1 {
  margin: 0;
  color: #222;
  font-size: 1.7rem;
  font-weight: 700;
}
.form {
  max-width: 700px;
}
.form-section {
  background: #fff;
  padding: 18px 16px;
  margin-bottom: 20px;
  border-radius: 10px;
  border: 1px solid #f0f0f0;
  box-shadow: 0 1px 4px rgba(0,0,0,0.03);
}
.form-section h2 {
  margin: 0 0 18px 0;
  color: #e67e22;
  font-size: 1.1rem;
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
}
.section-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 10px;
}
.composant-card {
  background: #f8fafc;
  padding: 12px 10px;
  margin-bottom: 12px;
  border-radius: 8px;
  border: 1px solid #f0f0f0;
}
.composant-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 8px;
}
.composant-header h3 {
  margin: 0;
  font-size: 15px;
}
.form-actions {
  display: flex;
  justify-content: flex-end;
  gap: 10px;
  padding: 18px 0;
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
}
.secondary-btn:hover {
  background: #e0e0e0;
}
.small-btn {
  padding: 5px 10px;
  font-size: 13px;
}
.delete-btn {
  background: #e74c3c;
  color: #fff;
  padding: 2px 8px;
  font-size: 18px;
}
.delete-btn:hover {
  background: #c0392b;
}
.loading, .empty {
  text-align: center;
  padding: 40px;
  color: #888;
  font-size: 16px;
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
}
.success-content h3 {
  margin: 0 0 10px 0;
  color: #27ae60;
}
.success-actions {
  display: flex;
  gap: 10px;
  justify-content: center;
  margin-top: 20px;
}
@media (max-width: 800px) {
  .form-row {
    flex-direction: column;
    gap: 10px;
  }
  .container {
    padding: 12px 2px;
  }
}
</style>
