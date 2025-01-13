import React, { useEffect, useState, useRef, useCallback } from 'react';
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
import VideoCardCompact from '../components/video/VideoCardCompact';
import VideoCardSkeleton from '../components/video/VideoCardSkeleton';
import CommentSection from '../components/comment/CommentSection';
import axios from '../config/axios.config';
import API_CONFIG from '../config/api.config';

const StyledContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(2, 2),
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
  paddingRight: theme.spacing(2)
}));

const formatDate = (dateString) => {
  if (!dateString) return new Date();
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return new Date();
    }
    return date;
  } catch (error) {
    return new Date();
  }
};

const Watch = () => {
  const { videoId } = useParams();
  const [video, setVideo] = useState(null);
  const [recommendedVideos, setRecommendedVideos] = useState([]);
  const [loading, setLoading] = useState(true);
  const [loadingRecommended, setLoadingRecommended] = useState(true);
  const [error, setError] = useState(false);
  const [errorRecommended, setErrorRecommended] = useState(false);
  const [isLiked, setIsLiked] = useState(false);
  const [isDisliked, setIsDisliked] = useState(false);
  const isLoadingRef = useRef(false);
  const initializedRef = useRef(false);

  const fetchData = useCallback(async () => {
    if (isLoadingRef.current || initializedRef.current) return;
    initializedRef.current = true;

    // Reset states
    setLoading(true);
    setLoadingRecommended(true);
    setError(false);
    setErrorRecommended(false);

    try {
      // First fetch video data
      const response = await videoService.getVideoById(videoId);
      
      const videoData = {
        ...response.data.data,
        videoUrl: response.data.data.playbackUrl,
        views: response.data.data.viewCount || 0,
        likes: response.data.data.likeCount || 0,
        dislikes: response.data.data.dislikeCount || 0,
        uploadDate: response.data.data.createTime,
        tags: response.data.data.tags || [],
        commentCount: response.data.data.commentCount || 0,
        channel: {
          name: response.data.data.username || '',
          avatarUrl: `https://i.pravatar.cc/150?u=${response.data.data.userId}`,
          subscribers: 0,
        },
      };
      console.log("videoData", videoData);
      setVideo(videoData);
      setLoading(false);

      // Fetch user's action history for this video
      try {
        const actionResponse = await axios.get(`${API_CONFIG.ENDPOINTS.ACTIONS.LIST}/${videoId}`);
        const actions = actionResponse.data.data;
        
        // Set initial like/dislike states based on action history
        actions.forEach(action => {
          if (action.actionType === 1) {
            setIsLiked(true);
          } else if (action.actionType === 2) {
            setIsDisliked(true);
          }
        });
      } catch (error) {
        console.error('Error fetching action history:', error);
      }

      // Then fetch recommended videos
      const recommendedResponse = await videoService.getVideos({ limit: 10 });
      
      const transformedVideos = recommendedResponse.data.rows.map(video => ({
        id: video.id,
        title: video.title,
        thumbnailUrl: video.thumbnailUrl,
        username: video.username,
        views: video.viewCount || 0,
        uploadDate: video.createTime,
        duration: video.duration,
      }));
      setRecommendedVideos(transformedVideos.filter(v => v.id !== videoId));
    } catch (error) {
      console.error('Error fetching data:', error);
      if (!video) setError(true);
      setErrorRecommended(true);
    } finally {
      setLoading(false);
      setLoadingRecommended(false);
      isLoadingRef.current = false;
    }
  }, [videoId]);

  useEffect(() => {
    initializedRef.current = false;
    fetchData();

    return () => {
      initializedRef.current = true;
      isLoadingRef.current = false;
    };
  }, [videoId, fetchData]);

  const handleVideoAction = async (actionType) => {
    try {
      await axios.post(API_CONFIG.ENDPOINTS.ACTIONS.DO, {
        videoId,
        actionType
      });

      // Update like/dislike count and states based on action type
      setVideo(prev => {
        let likeDelta = 0;
        let dislikeDelta = 0;

        if (actionType === 1) { // Like
          if (isLiked) {
            // If already liked, remove like (toggle off)
            likeDelta = -1;
            setIsLiked(false);
          } else {
            // Add like
            likeDelta = 1;
            setIsLiked(true);
            // If was disliked, remove dislike
            if (isDisliked) {
              dislikeDelta = -1;
              setIsDisliked(false);
            }
          }
        } else if (actionType === 2) { // Dislike
          if (isDisliked) {
            // If already disliked, remove dislike (toggle off)
            dislikeDelta = -1;
            setIsDisliked(false);
          } else {
            // Add dislike
            dislikeDelta = 1;
            setIsDisliked(true);
            // If was liked, remove like
            if (isLiked) {
              likeDelta = -1;
              setIsLiked(false);
            }
          }
        }

        const newLikes = Math.max(0, prev.likes + likeDelta);
        const newDislikes = Math.max(0, prev.dislikes + dislikeDelta);

        return {
          ...prev,
          likes: newLikes,
          dislikes: newDislikes
        };
      });
    } catch (error) {
      console.error('Error performing video action:', error);
    }
  };

  return (
    <StyledContainer>
      <Grid container spacing={1}>
        <Grid item xs={12} md={9.5}>
          <Box sx={{ 
            maxWidth: '1400px', 
            marginLeft: 'auto', 
            marginRight: { xs: 'auto', md: 0 },
            paddingRight: { xs: 0, md: 1 }
          }}>
            <VideoPlayer 
              url={video?.videoUrl || ''} 
              poster={video?.thumbnailUrl || ''}
            />
            
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
                    {formatViewCount(video.views)} views • {formatDistance(formatDate(video.uploadDate), new Date(), { addSuffix: true })}
                  </ViewsAndDate>
                  
                  <ActionButtons>
                    <Button 
                      startIcon={<ThumbUpIcon color={isLiked ? "error" : "inherit"} />}
                      onClick={() => handleVideoAction(1)}
                      sx={{ color: isLiked ? "error.main" : "inherit" }}
                    >
                      {formatViewCount(video.likes)}
                    </Button>
                    <Button 
                      startIcon={<ThumbDownIcon color={isDisliked ? "error" : "inherit"} />}
                      onClick={() => handleVideoAction(2)}
                      sx={{ color: isDisliked ? "error.main" : "inherit" }}
                    >
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

                <Box sx={{ display: 'flex', flexWrap: 'wrap', gap: 1, mb: 2 }}>
                  {video.tags?.map((tag, index) => (
                    <Box
                      key={index}
                      sx={{
                        backgroundColor: 'rgba(0, 0, 0, 0.08)',
                        borderRadius: '16px',
                        padding: '4px 12px',
                        fontSize: '0.875rem',
                        color: 'text.secondary'
                      }}
                    >
                      {tag}
                    </Box>
                  ))}
                </Box>

                <CommentSection videoId={videoId} initialCommentCount={video.commentCount} />
              </VideoInfoContainer>
            )}
          </Box>
        </Grid>

        <Grid item xs={12} md={2.5}>
          <RecommendedVideosContainer>
            {(loadingRecommended || errorRecommended) ? (
              Array(8).fill(0).map((_, index) => (
                <Box key={index} sx={{ mb: 1 }}>
                  <VideoCardSkeleton />
                </Box>
              ))
            ) : (
              recommendedVideos.map((video) => (
                <Box key={video.id} sx={{ mb: 2 }}>
                  <VideoCardCompact video={video} />
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