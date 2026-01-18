// machine.js (pour les machines)
import apiClient from './api.js'

export const machineService = {
  async getAllMachines() {
    try {
      const response = await apiClient.get('/api/machine/list') // À créer si nécessaire
      return response.data
    } catch (error) {
      console.error('Erreur lors de la récupération des machines:', error)
      throw error
    }
  }
}
