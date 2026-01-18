import apiClient from './api.js'

export const ofService = {
  async getAllOf(queryParams = '') {
    try {
      const url = `/of${queryParams ? '?' + queryParams : ''}`
      const response = await apiClient.get(url)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des OF:', error)
      throw error
    }
  },

  async createOf(ofData) {
    try {
      console.log(ofData);
      const response = await apiClient.post('/api/of/create', ofData)
      return response.data
    } catch (error) {
      console.error('Erreur lors de la création de l\'OF:', error)
      throw error
    }
  }
}
