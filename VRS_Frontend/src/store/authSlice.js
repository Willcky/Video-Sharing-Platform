import { createSlice } from '@reduxjs/toolkit';

// Check for existing session
const token = localStorage.getItem('token');
const username = localStorage.getItem('username');

const initialState = {
  user: username ? { username } : null,
  isAuthenticated: !!token,
};

const authSlice = createSlice({
  name: 'auth',
  initialState,
  reducers: {
    setUser: (state, action) => {
      state.user = action.payload;
      state.isAuthenticated = !!action.payload;
    },
    logout: (state) => {
      state.user = null;
      state.isAuthenticated = false;
      localStorage.removeItem('token');
      localStorage.removeItem('username');
    },
  },
});

export const { setUser, logout } = authSlice.actions;
export default authSlice.reducer; 