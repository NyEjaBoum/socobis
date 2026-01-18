// fabrication.js (mise à jour)
import apiClient from './api.js'

export const fabricationService = {
  async getAllFabrications(queryParams = '') {
    try {
      const url = `/api/fabrication/list${queryParams ? '?' + queryParams : ''}`
      const response = await apiClient.get(url)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des fabrications:', error)
      throw error
    }
  },

  async createFabrication(fabricationData) {
    try {
      const response = await apiClient.post('/api/fabrication/create', fabricationData)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de la fabrication:', error)
      throw error
    }
  },

  async validateFabrication(fabricationId) {
    try {
      const params = new URLSearchParams()
      params.append('id', fabricationId)
      params.append('nomTable',"fabrication")
      params.append('className',"fabrication.Fabrication")

      const response = await apiClient.post('/api/object/validate', params.toString(), {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la validation de la fabrication:', error)
      throw error
    }
  },

  async startFabrication(fabricationId) {
    try {
      const params = new URLSearchParams()
      params.append('id', fabricationId)
      params.append('className', 'fabrication.Fabrication')
      params.append('action', 'entamer')

      const response = await apiClient.post('/api/object/action', params.toString(), {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors du démarrage de la fabrication:', error)
      throw error
    }
  },

  async finishFabrication(fabricationId) {
    try {
      const params = new URLSearchParams()
      params.append('id', fabricationId)
      params.append('className', 'fabrication.Fabrication')
      params.append('action', 'terminer')

      const response = await apiClient.post('/api/object/action', params.toString(), {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors de la finalisation de la fabrication:', error)
      throw error
    }
  },

  async blockFabrication(fabricationId) {
    try {
      const params = new URLSearchParams()
      params.append('id', fabricationId)
      params.append('className', 'fabrication.Fabrication')
      params.append('action', 'bloquer')

      const response = await apiClient.post('/api/object/action', params.toString(), {
        headers: {
          'Content-Type': 'application/x-www-form-urlencoded'
        }
      })
      return response.data
    } catch (error) {
      console.error('Erreur lors du blocage de la fabrication:', error)
      throw error
    }
  }
}
