import React, { useEffect } from 'react';
import { useDispatch, useSelector } from 'react-redux';
import { Grid, Box } from '@mui/material';
import { styled } from '@mui/material/styles';
import { fetchVideos } from '../store/videoSlice';
//import VideoThumbnail from '../components/video/VideoThumbnail';
import VideoCard from '../components/video/VideoCard';
import LoadingSpinner from '../components/common/LoadingSpinner';
import VideoCardSkeleton from '../components/video/VideoCardSkeleton';

const StyledContainer = styled(Box)(({ theme }) => ({
    marginTop: theme.spacing(2),
    padding: theme.spacing(2, 6),
    minHeight: 'calc(100vh - 112px)', // Subtract navbar + category list height
    maxHeight: 'calc(100vh - 112px)',
    overflow: 'hidden',
  }));
  
//   const StyledGrid = styled(Grid)(({ theme }) => ({
//     spacing: 3,
//   }));
// const GridItem = styled(Grid)(({ theme }) => ({
//     padding: theme.spacing(3, 3),
//     flex: '0 0 20%', // 5 items per row, each taking exactly 20% width
//     maxWidth: '20%',
//   }));

//   const GridContainer = styled(Grid)({
//     display: 'flex',
//     justifyContent: 'space-between', // Distribute space evenly between items
//     width: '100%',
//     flexWrap: 'wrap',
//   });
  const Home = () => {
    const dispatch = useDispatch();
    const { items: videos, status } = useSelector((state) => state.videos);
  
    useEffect(() => {
      if (status === 'idle') {
        dispatch(fetchVideos({}));
      }
    }, [status, dispatch]);
  
    if (status === 'loading' || status === 'failed') {
      return (
        <StyledContainer>
          <Grid container spacing={2}>
            {[...Array(10)].map((_, index) => (
              <Grid item xs={12} sm={6} md={4} lg={2.4} key={index}>
                <VideoCardSkeleton />
              </Grid>
            ))}
          </Grid>
          <LoadingSpinner />
        </StyledContainer>
      );
    }
  
    return (
      <StyledContainer>
        <Grid container spacing={2}>
          {videos.slice(0, 10).map((video) => (
            <Grid item xs={12} sm={6} md={4} lg={2.4} key={video.id}>
              <VideoCard video={video} />
            </Grid>
          ))}
        </Grid>
      </StyledContainer>
    );
  };
  
  export default Home;