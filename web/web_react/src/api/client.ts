import axios from 'axios';

const apiClient = axios.create({
  baseURL: '/',
  timeout: 30000,
});

apiClient.interceptors.request.use((config) => {
  const token = localStorage.getItem('tank_token');
  if (token) {
    config.headers.Authorization = `Bearer ${token}`;
  }
  return config;
});

apiClient.interceptors.response.use(
  (response) => response,
  (error) => {
    if (error.response?.status === 401) {
      localStorage.removeItem('tank_token');
      localStorage.removeItem('tank_user');
      window.location.href = '/app/login';
    }
    return Promise.reject(error);
  }
);

export default apiClient;
