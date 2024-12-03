import { createSlice } from '@reduxjs/toolkit';

const initialState = {
  selectedCategory: 'All',
  categories: [
    'All',
    'Music',
    'Gaming',
    'Sports',
    'Entertainment',
    'News',
    'Education',
    'Technology',
    'Fashion',
    'Comedy',
    'Science',
    'Travel',
    'Food',
    'Art',
  ],
};

const categorySlice = createSlice({
  name: 'category',
  initialState,
  reducers: {
    setCategory: (state, action) => {
      state.selectedCategory = action.payload;
    },
  },
});

export const { setCategory } = categorySlice.actions;
export default categorySlice.reducer; 