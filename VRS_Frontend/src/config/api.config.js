const API_CONFIG = {
  BASE_URL: process.env.REACT_APP_API_BASE_URL || 'http://localhost:8080',
  ENDPOINTS: {
    VIDEOS: {
      LIST: '/videos/video/list',
      BY_ID: '/videos/video/:id',
      SEARCH: '/videos/video/search',
      BY_CATEGORY: '/videos/video/category/:category',
      LIKE: '/videos/video/:id/like',
      COMMENTS: '/videos/video/:id/comments',
      UPLOAD: '/videos/video/upload'
    },
    AUTH: {
      LOGIN: '/vrsauth/login',
      REGISTER: '/vrsauth/register',
      PROFILE: '/vrsauth/me'
    },
    COMMENTS: {
      LIST: '/interact/comment/list/:videoId',
      REPLIES: '/interact/comment/replies/:commentId',
      CREATE: '/interact/comment'
    },
    ACTIONS: {
      DO: '/interact/action/do',
      LIST: '/interact/action/list'
    }
  },
  HEADERS: {
    'Content-Type': 'application/json'
  }
};

export default API_CONFIG; 