import { createSlice, createAsyncThunk } from '@reduxjs/toolkit';
import { videoService } from '../services/api';

export const fetchVideos = createAsyncThunk(
  'videos/fetchVideos',
  async (params = {}) => {
    const { category = 'All', page = 1, limit = 20 } = params;
    const response = await videoService.getVideos({
      category,
      page,
      limit,
    });
    return response.data;
  }
);

const videoSlice = createSlice({
    name: 'videos',
    initialState: {
      items: [],
      status: 'idle',
      error: null,
    },
    reducers: {},
    extraReducers: (builder) => {
      builder
        .addCase(fetchVideos.pending, (state) => {
          state.status = 'loading';
        })
        .addCase(fetchVideos.fulfilled, (state, action) => {
          state.status = 'succeeded';
          state.items = action.payload;
        })
        .addCase(fetchVideos.rejected, (state, action) => {
          state.status = 'failed';
          state.error = action.error.message;
        });
    },
  });
  
  export default videoSlice.reducer;