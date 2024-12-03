import React from 'react';
import { Card, CardContent, Box, Skeleton } from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledCard = styled(Card)(() => ({
    maxWidth: '100%',
    boxShadow: 'none',
    backgroundColor: 'transparent',
    transform: 'scale(1)', // Make card bigger
    transformOrigin: 'top center',
  }));

const ThumbnailSkeleton = styled(Skeleton)(() => ({
    paddingTop: '56.25%', // 16:9 aspect ratio
    borderRadius: 8, // Match VideoCard border radius
    transform: 'none',
}));

const ChannelInfoContainer = styled(Box)(({ theme }) => ({
    display: 'flex',
    alignItems: 'flex-start',
    marginTop: theme.spacing(1.5), // Increase margin
  }));

const VideoCardSkeleton = () => {
  return (
    <StyledCard>
      <ThumbnailSkeleton variant="rectangular" animation="wave" />
      <CardContent sx={{ p: 0, '&:last-child': { pb: 0 }, mt: 2 }}>
        <ChannelInfoContainer>
          <Skeleton variant="circular" width={40} height={40} sx={{ mr: 1 }} />
          <Box sx={{ flex: 1 }}>
            <Skeleton variant="text" width="90%" height={20} sx={{ mb: 0.5 }} />
            <Skeleton variant="text" width="60%" height={20} />
            <Skeleton variant="text" width="40%" height={20} />
          </Box>
        </ChannelInfoContainer>
      </CardContent>
    </StyledCard>
  );
};

export default VideoCardSkeleton; 