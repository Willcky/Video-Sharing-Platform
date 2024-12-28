import React, { useEffect, useState } from 'react';
import { useParams } from 'react-router-dom';
import { Box, Grid, Typography, Avatar, Button, Skeleton } from '@mui/material';
import { styled } from '@mui/material/styles';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';
import ShareIcon from '@mui/icons-material/Share';
import { formatDistance } from 'date-fns';
import { formatViewCount } from '../utils/formatters';
import { videoService } from '../services/api';
import VideoPlayer from '../components/video/VideoPlayer';
import VideoCard from '../components/video/VideoCard';
import VideoCardSkeleton from '../components/video/VideoCardSkeleton';

const StyledContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(2, 6),
  minHeight: 'calc(100vh - 64px)',
  backgroundColor: theme.palette.background.default,
}));

const VideoInfoContainer = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(2),
}));

const VideoTitle = styled(Typography)(({ theme }) => ({
  fontWeight: 'bold',
  fontSize: '1.2rem',
  marginBottom: theme.spacing(1),
}));

const StatsContainer = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'space-between',
  alignItems: 'center',
  marginBottom: theme.spacing(2),
}));

const ViewsAndDate = styled(Typography)(({ theme }) => ({
  color: theme.palette.text.secondary,
}));

const ActionButtons = styled(Box)(({ theme }) => ({
  display: 'flex',
  gap: theme.spacing(2),
}));

const ChannelContainer = styled(Box)(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  marginBottom: theme.spacing(2),
  padding: theme.spacing(2, 0),
  borderBottom: `1px solid ${theme.palette.divider}`,
  borderTop: `1px solid ${theme.palette.divider}`,
}));

const ChannelInfo = styled(Box)(({ theme }) => ({
  marginLeft: theme.spacing(2),
  flex: 1,
}));

const ChannelName = styled(Typography)({
  fontWeight: 'bold',
});

const SubscriberCount = styled(Typography)(({ theme }) => ({
  color: theme.palette.text.secondary,
  fontSize: '0.9rem',
}));

const Description = styled(Typography)(({ theme }) => ({
  whiteSpace: 'pre-wrap',
  marginBottom: theme.spacing(2),
}));

const RecommendedVideosContainer = styled(Box)(({ theme }) => ({
  paddingLeft: theme.spacing(2),
  '& .MuiCard-root': {
    transform: 'scale(0.9)',
    transformOrigin: 'top left'
  }
}));

const Watch = () => {
  const { videoId } = useParams();
  const [video, setVideo] = useState(null);
  const [recommendedVideos, setRecommendedVideos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingRecommended, setLoadingRecommended] = useState(true);
  const [error, setError] = useState(false);
  const [errorRecommended, setErrorRecommended] = useState(false);

  useEffect(() => {
    const fetchVideoData = async () => {
      try {
        const response = await videoService.getVideoById(videoId);
        setVideo(response.data);
        setError(false);
      } catch (error) {
        console.error('Error fetching video:', error);
        setError(true);
      } finally {
        setLoading(false);
      }
    };

    const fetchRecommendedVideos = async () => {
      try {
        const recommendedResponse = await videoService.getVideos({ limit: 10 });
        setRecommendedVideos(recommendedResponse.data.filter(v => v.id !== videoId));
        setErrorRecommended(false);
      } catch (error) {
        console.error('Error fetching recommended videos:', error);
        setErrorRecommended(true);
      } finally {
        setLoadingRecommended(false);
      }
    };

    setLoading(true);
    setLoadingRecommended(true);
    fetchVideoData();
    fetchRecommendedVideos();
  }, [videoId]);

  return (
    <StyledContainer>
      <Grid container spacing={3}>
        <Grid item xs={12} md={9}>
          <VideoPlayer url={video?.videoUrl || ''} />
          
          {(loading || error) ? (
            <VideoInfoContainer>
              <Box sx={{ mb: 2 }}>
                <Skeleton variant="text" width="70%" height={32} />
                <Skeleton variant="text" width="30%" height={24} />
              </Box>
              
              <Box sx={{ display: 'flex', alignItems: 'center', mb: 2 }}>
                <Skeleton variant="circular" width={48} height={48} sx={{ mr: 2 }} />
                <Box sx={{ flex: 1 }}>
                  <Skeleton variant="text" width="40%" />
                  <Skeleton variant="text" width="20%" />
                </Box>
                <Skeleton variant="rectangular" width={100} height={36} />
              </Box>
              
              <Skeleton variant="text" width="100%" height={100} />
            </VideoInfoContainer>
          ) : video && (
            <VideoInfoContainer>
              <VideoTitle variant="h1">{video.title}</VideoTitle>
              
              <StatsContainer>
                <ViewsAndDate>
                  {formatViewCount(video.views)} views • {formatDistance(new Date(video.uploadDate), new Date(), { addSuffix: true })}
                </ViewsAndDate>
                
                <ActionButtons>
                  <Button startIcon={<ThumbUpIcon />}>
                    {formatViewCount(video.likes)}
                  </Button>
                  <Button startIcon={<ThumbDownIcon />}>
                    {formatViewCount(video.dislikes)}
                  </Button>
                  <Button startIcon={<ShareIcon />}>
                    Share
                  </Button>
                </ActionButtons>
              </StatsContainer>

              <ChannelContainer>
                <Avatar 
                  src={video.channel.avatarUrl}
                  alt={video.channel.name}
                  sx={{ width: 48, height: 48 }}
                />
                <ChannelInfo>
                  <ChannelName variant="subtitle1">
                    {video.channel.name}
                  </ChannelName>
                  <SubscriberCount>
                    {formatViewCount(video.channel.subscribers)} subscribers
                  </SubscriberCount>
                </ChannelInfo>
                <Button variant="contained" color="primary">
                  Subscribe
                </Button>
              </ChannelContainer>

              <Description variant="body1">
                {video.description}
              </Description>
            </VideoInfoContainer>
          )}
        </Grid>

        <Grid item xs={12} md={3}>
          <RecommendedVideosContainer>
            {(loadingRecommended || errorRecommended) ? (
              Array(8).fill(0).map((_, index) => (
                <Box key={index} sx={{ mb: 1 }}>
                  <VideoCardSkeleton />
                </Box>
              ))
            ) : (
              recommendedVideos.map((video) => (
                <Box key={video.id} sx={{ mb: 1 }}>
                  <VideoCard video={video} />
                </Box>
              ))
            )}
          </RecommendedVideosContainer>
        </Grid>
      </Grid>
    </StyledContainer>
  );
};

export default Watch; 