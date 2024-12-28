import axiosInstance from '../config/axios.config';
import API_CONFIG from '../config/api.config';

export const videoService = {
  getVideos: (options = {}) => {
    const { page = 1, limit = 20, category = null } = options;
    const params = new URLSearchParams({
      page: page.toString(),
      limit: limit.toString(),
    });
    
    if (category && category !== 'All') {
      params.append('category', category);
    }
    
    return axiosInstance.get(`${API_CONFIG.ENDPOINTS.VIDEOS.LIST}?${params.toString()}`);
  },
  
  getVideoById: (videoId) => 
    axiosInstance.get(API_CONFIG.ENDPOINTS.VIDEOS.BY_ID.replace(':id', videoId)),
    
  searchVideos: (query) => 
    axiosInstance.get(`${API_CONFIG.ENDPOINTS.VIDEOS.SEARCH}?q=${query}`),
    
  uploadVideo: (videoData) => 
    axiosInstance.post(API_CONFIG.ENDPOINTS.VIDEOS.UPLOAD, videoData),
    
  likeVideo: (videoId) => 
    axiosInstance.post(API_CONFIG.ENDPOINTS.VIDEOS.LIKE.replace(':id', videoId)),
    
  addComment: (videoId, comment) => 
    axiosInstance.post(API_CONFIG.ENDPOINTS.VIDEOS.COMMENTS.replace(':id', videoId), comment),
};

export const authService = {
  login: (credentials) => 
    axiosInstance.post(API_CONFIG.ENDPOINTS.AUTH.LOGIN, credentials),
    
  register: (userData) => 
    axiosInstance.post(API_CONFIG.ENDPOINTS.AUTH.REGISTER, userData),
    
  getCurrentUser: () => 
    axiosInstance.get(API_CONFIG.ENDPOINTS.AUTH.PROFILE),
}; 