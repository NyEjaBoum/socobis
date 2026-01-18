import apiClient from './api.js'

export const magasinService = {
  async getAllMagasins() {
    try {
      const response = await apiClient.get('/magasin')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des magasins:', error)
      throw error
    }
  }
}
