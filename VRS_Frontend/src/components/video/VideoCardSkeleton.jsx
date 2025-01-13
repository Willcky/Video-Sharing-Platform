import React from 'react';
import { Card, CardContent, Skeleton } from '@mui/material';
import { styled } from '@mui/material/styles';

const StyledCard = styled(Card)(() => ({
  maxWidth: '100%',
  boxShadow: 'none',
  backgroundColor: 'transparent',
  transform: 'scale(0.95)',
  transformOrigin: 'center top',
}));

const ThumbnailSkeleton = styled(Skeleton)(() => ({
  paddingTop: '56.25%', // 16:9 aspect ratio
  borderRadius: 8,
  transform: 'none',
}));

const StyledCardContent = styled(CardContent)(({ theme }) => ({
  padding: theme.spacing(1, 0),
  '&:last-child': {
    paddingBottom: 0,
  },
}));

const VideoInfoContainer = styled('div')({
  flex: 1,
  minWidth: 0,
});

const VideoCardSkeleton = () => {
  return (
    <StyledCard>
      <ThumbnailSkeleton variant="rectangular" animation="wave" />
      <StyledCardContent>
        <VideoInfoContainer>
          <Skeleton 
            variant="text" 
            width="90%" 
            height={20} 
            animation="wave"
            sx={{ mb: 0.5 }}
          />
          <Skeleton 
            variant="text" 
            width="60%" 
            height={20} 
            animation="wave"
            sx={{ mb: 0.5 }}
          />
          <Skeleton 
            variant="text" 
            width="40%" 
            height={20} 
            animation="wave"
          />
        </VideoInfoContainer>
      </StyledCardContent>
    </StyledCard>
  );
};

export default VideoCardSkeleton; 