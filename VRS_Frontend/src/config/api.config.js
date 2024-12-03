const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:5000',
  ENDPOINTS: {
    VIDEOS: {
      LIST: '/videos',
      BY_ID: '/videos/:id',
      SEARCH: '/videos/search',
      BY_CATEGORY: '/videos/category/:category',
      LIKE: '/videos/:id/like',
      COMMENTS: '/videos/:id/comments',
      UPLOAD: '/videos/upload'
    },
    AUTH: {
      LOGIN: '/auth/login',
      REGISTER: '/auth/register',
      PROFILE: '/auth/me'
    }
  },
  HEADERS: {
    'Content-Type': 'application/json'
  }
};

export default API_CONFIG; 