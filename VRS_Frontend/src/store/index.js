import { configureStore } from '@reduxjs/toolkit';
import videoReducer from './videoSlice';
import authReducer from './authSlice';
import categoryReducer from './categorySlice';

export const store = configureStore({
  reducer: {
    videos: videoReducer,
    auth: authReducer,
    category: categoryReducer,
  },
  middleware: (getDefaultMiddleware) =>
    getDefaultMiddleware({
      serializableCheck: false,
    }),
  devTools: process.env.NODE_ENV !== 'production',
}); 