import axios from 'axios';

// Note: Toutes les variables d'environnement personnalisées
// DOIVENT commencer par VUE_APP_ dans Vue CLI
const baseURL = import.meta.env.VUE_APP_API_URL || 'http://localhost:8080/socobis';

console.log('API Base URL:', baseURL); // Pour debug

const apiClient = axios.create({
  baseURL: baseURL,
  timeout: 10000,
  withCredentials: true,
  headers: {
    'Content-Type': 'application/json',
    'Accept': 'application/json'
  }
});

// Intercepteurs optionnels
apiClient.interceptors.request.use(
  config => {
    // Ajoutez un token si disponible
    const token = localStorage.getItem('token') || sessionStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  error => Promise.reject(error)
);

apiClient.interceptors.response.use(
  response => response,
  error => {
    // Gestion d'erreurs globale
    if (error.response) {
      switch (error.response.status) {
        case 401:
          // Rediriger vers login
          if (window.location.pathname !== '/login') {
            window.location.href = '/login';
          }
          break;
        case 403:
          console.error('Accès refusé');
          break;
        case 404:
          console.error('Ressource non trouvée');
          break;
        case 500:
          console.error('Erreur serveur');
          break;
      }
    }
    return Promise.reject(error);
  }
);

export default apiClient;
