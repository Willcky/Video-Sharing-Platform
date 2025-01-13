import React, { useState, useEffect, useCallback, useRef } from 'react';
import { Grid, Box } from '@mui/material';
import { styled } from '@mui/material/styles';
import VideoCard from '../components/video/VideoCard';
import { videoService } from '../services/api';
import VideoCardSkeleton from '../components/video/VideoCardSkeleton';

const StyledContainer = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(2),
  padding: theme.spacing(2, 6),
  minHeight: 'calc(100vh - 112px)',
  maxHeight: 'calc(100vh - 112px)',
  overflow: 'auto',
}));

const Home = () => {
  const [videos, setVideos] = useState([]);
  const [loading, setLoading] = useState(false);
  const [hasMore, setHasMore] = useState(true);
  const [pageNum, setPageNum] = useState(1);
  const loadingRef = useRef(false);
  const pageSize = 10;

  const fetchVideos = useCallback(async () => {
    if (loadingRef.current || !hasMore) return;

    loadingRef.current = true;
    setLoading(true);
    
    try {
      const response = await videoService.getVideos({
        pageNum,
        pageSize,
      });

      const { total, rows } = response.data;
      
      // Transform the data to match VideoCard props
      const transformedVideos = rows.map(video => ({
        id: video.id,
        title: video.title,
        thumbnailUrl: video.thumbnailUrl,
        username: video.username,
        views: video.viewCount,
        uploadDate: video.createTime,
        duration: video.duration,
      }));

      setVideos(prev => {
        const newVideos = [...prev, ...transformedVideos];
        setHasMore(newVideos.length < total);
        return newVideos;
      });
    } catch (error) {
      console.error('Error fetching videos:', error);
    } finally {
      setLoading(false);
      loadingRef.current = false;
    }
  }, [pageNum, hasMore]);

  useEffect(() => {
    fetchVideos();
  }, [pageNum, fetchVideos]);

  const handleScroll = useCallback((e) => {
    const { scrollTop, clientHeight, scrollHeight } = e.target;
    if (scrollHeight - scrollTop <= clientHeight * 1.5) {
      if (!loadingRef.current && hasMore) {
        setPageNum(prev => prev + 1);
      }
    }
  }, [hasMore]);

  return (
    <StyledContainer onScroll={handleScroll}>
      <Grid container spacing={3}>
        {videos.map((video) => (
          <Grid item xs={12} sm={6} md={3} key={video.id}>
            <VideoCard video={video} />
          </Grid>
        ))}
        {loading && Array(4).fill(0).map((_, index) => (
          <Grid item xs={12} sm={6} md={3} key={`skeleton-${index}`}>
            <VideoCardSkeleton />
          </Grid>
        ))}
      </Grid>
    </StyledContainer>
  );
};

export default Home;