// mvtStock.js
import apiClient from './api.js'

export const mvtStockService = {
  async getTypeMvtStocks() {
    try {
      const response = await apiClient.get('/api/type-mvt-stock')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des types de mouvement:', error)
      throw error
    }
  },

  async getPoints() {
    try {
      const response = await apiClient.get('/api/point')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des points:', error)
      throw error
    }
  },

  async getIngredientsAuto() {
    try {
      const response = await apiClient.get('/api/ingredients-auto')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des ingrédients:', error)
      throw error
    }
  },

  async getFabricationMvtStock(fabricationId, typeMvtStock, idMagasin) {
    try {
      let url = `/api/fabrication/mvt-stock?idFab=${fabricationId}`
      if (typeMvtStock) url += `&typeMvtStock=${typeMvtStock}`
      if (idMagasin) url += `&idMagasin=${idMagasin}`

      const response = await apiClient.get(url)
      console.log(response)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération du mouvement de stock:', error)
      throw error
    }
  },

  async createMvtStock(mvtStockData) {
    try {
      const response = await apiClient.post('/api/mvt-stock/create', mvtStockData)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création du mouvement de stock:', error)
      throw error
    }
  },

  async validateMvtStock(mvtStockId) {
    try {
      const params = new URLSearchParams()
      params.append('id', mvtStockId)
      params.append('className', 'stock.MvtStock')

      const response = await apiClient.post('/api/object/validate', params.toString(), {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la validation du mouvement de stock:', error)
      throw error
    }
  }
}
