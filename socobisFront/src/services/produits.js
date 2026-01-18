import apiClient from './api.js'

export const produitsService = {
  async getAllProduits() {
    try {
      const response = await apiClient.get('/produit')
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des produits:', error)
      throw error
    }
  }
}
