import React from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import { 
  Card, 
  CardMedia, 
  CardContent, 
  Typography,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { formatDistance } from 'date-fns';
import { formatViewCount } from '../../utils/formatters';

const StyledCard = styled(Card)(() => ({
  maxWidth: '100%',
  boxShadow: 'none',
  backgroundColor: 'transparent',
  transform: 'scale(0.95)',
  transformOrigin: 'center top',
}));

const StyledCardMedia = styled(CardMedia)(() => ({
  height: 0,
  paddingTop: '56.25%', // 16:9
  position: 'relative',
  '&:hover': {
    cursor: 'pointer',
  },
  borderRadius: 8,
}));

const StyledCardContent = styled(CardContent)(({ theme }) => ({
  padding: theme.spacing(1, 0),
  '&:last-child': {
    paddingBottom: 0,
  },
}));

const VideoInfoContainer = styled('div')({
  flex: 1,
  minWidth: 0, // This helps with text truncation
});

const VideoTitle = styled(Typography)(({ theme }) => ({
  fontWeight: 500,
  lineHeight: 1.2,
  marginBottom: theme.spacing(0.5),
  display: '-webkit-box',
  '-webkit-line-clamp': 2,
  '-webkit-box-orient': 'vertical',
  overflow: 'hidden',
  fontSize: '0.95rem',
}));

const Username = styled(Typography)(({ theme }) => ({
  color: theme.palette.text.secondary,
  fontSize: '0.85rem',
  whiteSpace: 'nowrap',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
}));

const VideoStats = styled(Typography)(({ theme }) => ({
  color: theme.palette.text.secondary,
  fontSize: '0.85rem',
  whiteSpace: 'nowrap',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
}));

const DurationBadge = styled('div')(({ theme }) => ({
  position: 'absolute',
  bottom: theme.spacing(1),
  right: theme.spacing(1),
  backgroundColor: 'rgba(0, 0, 0, 0.8)',
  color: '#fff',
  padding: theme.spacing(0.2, 0.5),
  borderRadius: 4,
  fontSize: '0.8rem',
}));

const formatDuration = (seconds) => {
  const minutes = Math.floor(seconds / 60);
  const remainingSeconds = seconds % 60;
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
};

const VideoCard = ({ video }) => {
  const {
    id,
    thumbnailUrl,
    title,
    username,
    views,
    uploadDate,
    duration,
  } = video;

  return (
    <StyledCard>
      <Link to={`/watch/${id}`} style={{ textDecoration: 'none', position: 'relative' }}>
        <StyledCardMedia
          image={thumbnailUrl}
          title={title}
        />
        {duration && (
          <DurationBadge>
            {formatDuration(duration)}
          </DurationBadge>
        )}
      </Link>
      <StyledCardContent>
        <VideoInfoContainer>
          <VideoTitle variant="body1">
            {title}
          </VideoTitle>
          <Username>
            {username}
          </Username>
          <VideoStats>
            {formatViewCount(views)} views • {formatDistance(new Date(uploadDate), new Date(), { addSuffix: true })}
          </VideoStats>
        </VideoInfoContainer>
      </StyledCardContent>
    </StyledCard>
  );
};

VideoCard.propTypes = {
  video: PropTypes.shape({
    id: PropTypes.string.isRequired,
    thumbnailUrl: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    username: PropTypes.string.isRequired,
    views: PropTypes.number.isRequired,
    uploadDate: PropTypes.string.isRequired,
    duration: PropTypes.number,
  }).isRequired,
};

export default VideoCard; 