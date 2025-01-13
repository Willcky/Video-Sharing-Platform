import axios from 'axios';
import API_CONFIG from './api.config';

const axiosInstance = axios.create({
  baseURL: API_CONFIG.BASE_URL,
  headers: API_CONFIG.HEADERS,
  timeout: 10000
});

// Add request interceptor
axiosInstance.interceptors.request.use(
  (config) => {
    const token = localStorage.getItem('token');
    if (token) {
      config.headers.Authorization = `Bearer ${token}`;
    }
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// Add response interceptor
axiosInstance.interceptors.response.use(
  (response) => {
    // Check if response body contains error code 401
    if (response.data && response.data.code === 401) {
      localStorage.removeItem('token');
      window.location.href = '/login';
      return Promise.reject(new Error(response.data.msg || 'Unauthorized'));
    }
    return response;
  },
  (error) => {
    if (error.response?.status === 401 || (error.response?.data && error.response.data.code === 401)) {
      // Handle unauthorized access from either HTTP status or response body
      localStorage.removeItem('token');
      window.location.href = '/login';
    }
    return Promise.reject(error);
  }
);

export default axiosInstance; 