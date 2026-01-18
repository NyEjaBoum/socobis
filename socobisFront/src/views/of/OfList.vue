<template>
  <div class="container">
    <div class="header">
      <h1>📋 Liste des Ordres de Fabrication</h1>
      <button @click="goToCreate" class="btn add-btn">➕ Nouvel OF</button>
    </div>

    <!-- Filtres -->
    <div class="filters">
      <div class="filter-row">
        <div class="filter-group">
          <label>Date min</label>
          <input v-model="filters.dateMin" type="date" class="filter-input">
        </div>

        <div class="filter-group">
          <label>Date max</label>
          <input v-model="filters.dateMax" type="date" class="filter-input">
        </div>

        <div class="filter-group">
          <label>Statut</label>
          <select v-model="filters.etat" class="filter-input">
            <option value="">Tous</option>
            <option value="1">Créé</option>
            <option value="11">Validé</option>
            <option value="21">Entamé</option>
            <option value="41">Terminé</option>
          </select>
        </div>

        <div class="filter-group">
          <label>Recherche</label>
          <input v-model="filters.search" type="text" placeholder="ID ou libellé..." class="filter-input">
        </div>
      </div>

      <div class="filter-actions">
        <button @click="applyFilters" class="btn filter-btn">🔍 Appliquer</button>
        <button @click="resetFilters" class="btn reset-btn">🗑️ Réinitialiser</button>
      </div>
    </div>

    <!-- Contrôles de pagination en haut -->
    <div v-if="pagination.total > 0" class="pagination-top">
      <div class="pagination-info">
        Affichage {{ ((pagination.page - 1) * pagination.npp) + 1 }} à
        {{ Math.min(pagination.page * pagination.npp, pagination.total) }}
        sur {{ pagination.total }} OF
      </div>

      <div class="pagination-controls">
        <select v-model="pagination.npp" @change="changePageSize" class="page-size">
          <option value="10">10 par page</option>
          <option value="25">25 par page</option>
          <option value="50">50 par page</option>
          <option value="100">100 par page</option>
        </select>

        <button @click="prevPage" :disabled="pagination.page <= 1" class="btn page-btn">
          ◀
        </button>

        <span class="page-current">Page {{ pagination.page }} / {{ totalPages }}</span>

        <button @click="nextPage" :disabled="pagination.page >= totalPages" class="btn page-btn">
          ▶
        </button>
      </div>
    </div>

    <!-- Tableau -->
    <div v-if="loading" class="loading">Chargement...</div>
    <div v-else-if="error" class="error">{{ error }}</div>
    <div v-else-if="ofs.length === 0" class="empty">Aucun OF trouvé</div>

    <table v-else class="table">
      <thead>
        <tr>
          <th>ID</th>
          <th>Désignation</th>
          <th>Date</th>
          <th>Magasin</th>
          <th>Statut</th>
          <th>Actions</th>
        </tr>
      </thead>
      <tbody>
        <tr v-for="ofItem in ofs" :key="ofItem.id">
          <td>{{ ofItem.id }}</td>
          <td>{{ ofItem.libelle || '-' }}</td>
          <td>{{ formatDate(ofItem.daty) }}</td>
          <td>{{ ofItem.cible || '-' }}</td>
          <td><span class="status" :class="getStatusClass(ofItem.etat)">{{ getStatusText(ofItem.etat) }}</span></td>
          <td>
            <button @click="viewDetails(ofItem.id)" class="btn small-btn">👁️</button>
            <button @click="validateOf(ofItem)" class="btn small-btn" v-if="ofItem.etat === 1">✓</button>
          </td>
        </tr>
      </tbody>
    </table>

    <!-- Pagination en bas -->
    <div v-if="pagination.total > 0" class="pagination-bottom">
      <div class="pagination-numbers">
        <button
          v-for="pageNum in visiblePages"
          :key="pageNum"
          @click="goToPage(pageNum)"
          :class="['btn', 'page-num', { active: pageNum === pagination.page }]"
        >
          {{ pageNum }}
        </button>
      </div>
    </div>
  </div>
</template>

<script>
import { ofService } from '@/services/of.js'

export default {
  name: 'OfList',
  data() {
    // Date d'aujourd'hui par défaut pour dateMin
    const today = new Date().toISOString().split('T')[0]

    return {
      ofs: [],
      loading: false,
      error: null,
      filters: {
        dateMin: today,
        dateMax: '',
        search: '',
        etat: ''
      },
      pagination: {
        page: 1,
        npp: 10,
        total: 0
      }
    }
  },
  computed: {
    totalPages() {
      return Math.ceil(this.pagination.total / this.pagination.npp)
    },
    visiblePages() {
      const pages = []
      const maxVisible = 5
      let start = Math.max(1, this.pagination.page - Math.floor(maxVisible / 2))
      let end = Math.min(this.totalPages, start + maxVisible - 1)

      // Ajuster si on est proche de la fin
      if (end - start + 1 < maxVisible) {
        start = Math.max(1, end - maxVisible + 1)
      }

      for (let i = start; i <= end; i++) {
        pages.push(i)
      }
      return pages
    }
  },
  created() {
    this.fetchOfs()
  },
  methods: {
    async fetchOfs() {
      this.loading = true
      this.error = null

      try {
        // Construire les paramètres de requête
        const params = new URLSearchParams()

        // Ajouter les filtres
        if (this.filters.dateMin) params.append('dateMin', this.filters.dateMin)
        if (this.filters.dateMax) params.append('dateMax', this.filters.dateMax)
        if (this.filters.search) params.append('search', this.filters.search)
        if (this.filters.etat) params.append('etat', this.filters.etat)

        // Ajouter la pagination
        params.append('page', this.pagination.page)
        params.append('npp', this.pagination.npp)

        // Appeler le service avec les paramètres
        const response = await ofService.getAllOf(params.toString())

        // Mettre à jour les données
        this.ofs = response.data || []
        this.pagination.total = response.total || 0
        this.pagination.page = response.page || 1
        this.pagination.npp = response.npp || 10

      } catch (error) {
        this.error = 'Erreur de chargement'
        console.error(error)
      } finally {
        this.loading = false
      }
    },

    applyFilters() {
      this.pagination.page = 1 // Retour à la première page
      this.fetchOfs()
    },

    resetFilters() {
      const today = new Date().toISOString().split('T')[0]
      this.filters = {
        dateMin: today,
        dateMax: '',
        search: '',
        etat: ''
      }
      this.pagination.page = 1
      this.fetchOfs()
    },

    changePageSize() {
      this.pagination.page = 1
      this.fetchOfs()
    },

    prevPage() {
      if (this.pagination.page > 1) {
        this.pagination.page--
        this.fetchOfs()
      }
    },

    nextPage() {
      if (this.pagination.page < this.totalPages) {
        this.pagination.page++
        this.fetchOfs()
      }
    },

    goToPage(pageNum) {
      this.pagination.page = pageNum
      this.fetchOfs()
    },

    formatDate(date) {
      return date ? new Date(date).toLocaleDateString('fr-FR') : '-'
    },

    getStatusText(etat) {
      const statusMap = {
        1: 'Créé',
        11: 'Validé',
        21: 'Entamé',
        31: 'Bloqué',
        41: 'Terminé'
      }
      return statusMap[etat] || 'Créé'
    },

    getStatusClass(etat) {
      const classMap = {
        1: 'status-created',
        11: 'status-validated',
        21: 'status-started',
        31: 'status-blocked',
        41: 'status-finished'
      }
      return classMap[etat] || 'status-created'
    },

    goToCreate() {
      this.$router.push({ name: 'OfCreate' })
    },

    viewDetails(id) {
      // À implémenter selon vos besoins
      console.log('Voir détails OF:', id)
    },

    validateOf(ofItem) {
      if (confirm(`Valider l'OF ${ofItem.id} ?`)) {
        // À implémenter : appel API pour valider l'OF
        console.log('Valider OF:', ofItem.id)
      }
    }
  }
}
</script>

<style scoped>
.container {
  max-width: 1400px;
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
  letter-spacing: -0.5px;
}
.filters {
  background: var(--white);
  padding: var(--spacing-xl);
  border-radius: var(--border-radius-xl);
  margin-bottom: var(--spacing-xl);
  box-shadow: var(--shadow-lg);
  border: 2px solid transparent;
  background-clip: padding-box;
  position: relative;
}

.filters::before {
  content: '';
  position: absolute;
  inset: 0;
  border-radius: var(--border-radius-xl);
  padding: 2px;
  background: linear-gradient(135deg, var(--primary), var(--secondary));
  -webkit-mask: linear-gradient(#fff 0 0) content-box, linear-gradient(#fff 0 0);
  -webkit-mask-composite: xor;
  mask-composite: exclude;
  opacity: 0.5;
}
.filter-row {
  display: flex;
  gap: var(--spacing-lg);
  flex-wrap: wrap;
  margin-bottom: var(--spacing-md);
}

.filter-group {
  flex: 1;
  min-width: 200px;
}

.filter-group label {
  display: block;
  margin-bottom: var(--spacing-sm);
  font-weight: 600;
  color: var(--text-primary);
  font-size: var(--font-size-sm);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.filter-input {
  width: 100%;
  padding: 12px 16px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-md);
  font-size: var(--font-size-sm);
  background: var(--gray-50);
  transition: all var(--transition-base);
}

.filter-input:focus {
  border-color: var(--primary);
  background: var(--white);
  box-shadow: 0 0 0 4px rgba(102, 126, 234, 0.1);
  transform: translateY(-2px);
}

.filter-actions {
  display: flex;
  gap: var(--spacing-md);
  justify-content: flex-end;
  margin-top: var(--spacing-lg);
}
.btn {
  padding: 12px 24px;
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

.btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.add-btn {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: var(--white);
  font-weight: 700;
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.add-btn:hover {
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}
.filter-btn {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: var(--white);
  box-shadow: 0 4px 15px rgba(102, 126, 234, 0.4);
}

.filter-btn:hover {
  box-shadow: 0 6px 20px rgba(102, 126, 234, 0.6);
}

.reset-btn {
  background: var(--white);
  color: var(--text-secondary);
  border: 2px solid var(--border-color);
}

.reset-btn:hover {
  border-color: var(--primary);
  color: var(--primary);
}

.table-container {
  background: var(--white);
  border-radius: var(--border-radius-xl);
  box-shadow: var(--shadow-lg);
  overflow: hidden;
  margin-bottom: var(--spacing-xl);
}

.table {
  width: 100%;
  border-collapse: collapse;
}

.table th {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: var(--white);
  padding: 16px 20px;
  text-align: left;
  font-weight: 700;
  font-size: var(--font-size-sm);
  text-transform: uppercase;
  letter-spacing: 0.5px;
}

.table td {
  padding: 16px 20px;
  border-bottom: 1px solid var(--gray-100);
  font-size: var(--font-size-sm);
  color: var(--text-secondary);
}

.table tr {
  transition: all var(--transition-fast);
}

.table tr:last-child td {
  border-bottom: none;
}

.table tr:hover {
  background: linear-gradient(90deg, rgba(102, 126, 234, 0.05) 0%, transparent 100%);
  transform: scale(1.01);
}

.status {
  display: inline-flex;
  align-items: center;
  gap: 6px;
  padding: 6px 14px;
  border-radius: 20px;
  font-size: var(--font-size-xs);
  font-weight: 700;
  text-transform: uppercase;
  letter-spacing: 0.5px;
  box-shadow: var(--shadow-sm);
  transition: all var(--transition-base);
}

.status:hover {
  transform: scale(1.05);
  box-shadow: var(--shadow-md);
}

.status::before {
  content: '';
  width: 6px;
  height: 6px;
  border-radius: 50%;
  animation: pulse 2s ease-in-out infinite;
}

@keyframes pulse {
  0%, 100% { opacity: 1; }
  50% { opacity: 0.5; }
}

.status-created {
  background: linear-gradient(135deg, #fef3c7 0%, #fde68a 100%);
  color: #92400e;
}

.status-created::before {
  background: #f59e0b;
}

.status-validated {
  background: linear-gradient(135deg, #dbeafe 0%, #bfdbfe 100%);
  color: #1e40af;
}

.status-validated::before {
  background: #3b82f6;
}

.status-started {
  background: linear-gradient(135deg, #d1fae5 0%, #a7f3d0 100%);
  color: #065f46;
}

.status-started::before {
  background: #10b981;
}

.status-blocked {
  background: linear-gradient(135deg, #fee2e2 0%, #fecaca 100%);
  color: #991b1b;
}

.status-blocked::before {
  background: #ef4444;
}

.status-finished {
  background: linear-gradient(135deg, #e5e7eb 0%, #d1d5db 100%);
  color: #374151;
}

.status-finished::before {
  background: #6b7280;
}

.small-btn {
  padding: 8px 16px;
  font-size: var(--font-size-xs);
  border-radius: var(--border-radius-sm);
}

.loading,
.error,
.empty {
  padding: var(--spacing-2xl);
  text-align: center;
  font-size: var(--font-size-lg);
  font-weight: 500;
}

.loading {
  color: var(--text-secondary);
  animation: fadeIn 0.5s ease-out;
}

.error {
  color: var(--error);
  background: var(--error-light);
  background: linear-gradient(135deg, rgba(239, 68, 68, 0.1) 0%, rgba(248, 113, 113, 0.1) 100%);
  border-radius: var(--border-radius-lg);
  padding: var(--spacing-xl);
  font-weight: 600;
}

.empty {
  color: var(--text-tertiary);
}

.pagination-top,
.pagination-bottom {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: var(--spacing-lg);
  background: var(--white);
  border-radius: var(--border-radius-lg);
  box-shadow: var(--shadow-md);
  margin-bottom: var(--spacing-lg);
}

.pagination-bottom {
  justify-content: center;
}

.pagination-info {
  color: var(--text-secondary);
  font-size: var(--font-size-sm);
  font-weight: 500;
}

.pagination-controls {
  display: flex;
  align-items: center;
  gap: var(--spacing-md);
}

.page-size {
  padding: 8px 12px;
  border: 2px solid var(--border-color);
  border-radius: var(--border-radius-sm);
  font-size: var(--font-size-sm);
  font-weight: 500;
  background: var(--gray-50);
  cursor: pointer;
  transition: all var(--transition-base);
}

.page-size:focus {
  border-color: var(--primary);
  outline: none;
}

.page-btn {
  padding: 8px 16px;
  background: var(--gray-100);
  font-weight: 600;
}

.page-btn:hover:not(:disabled) {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: var(--white);
}

.page-current {
  margin: 0 var(--spacing-md);
  font-weight: 700;
  color: var(--text-primary);
}

.pagination-numbers {
  display: flex;
  gap: var(--spacing-sm);
}

.page-num {
  padding: 10px 16px;
  background: var(--gray-100);
  border-radius: var(--border-radius-sm);
  font-weight: 600;
  transition: all var(--transition-base);
}

.page-num:hover {
  background: var(--gray-200);
}

.page-num.active {
  background: linear-gradient(135deg, var(--primary) 0%, var(--primary-light) 100%);
  color: var(--white);
  box-shadow: 0 4px 12px rgba(102, 126, 234, 0.4);
  transform: scale(1.05);
}

@media (max-width: 800px) {
  .filter-row,
  .form-row {
    flex-direction: column;
    gap: var(--spacing-md);
  }
  .container {
    padding: var(--spacing-md);
  }
  .table th,
  .table td {
    padding: 12px 8px;
    font-size: var(--font-size-xs);
  }
  .header {
    flex-direction: column;
    gap: var(--spacing-md);
    text-align: center;
  }
  .pagination-top,
  .pagination-bottom {
    flex-direction: column;
    gap: var(--spacing-md);
  }
}
</style>
