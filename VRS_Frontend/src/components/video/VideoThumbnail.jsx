import React from 'react';
import PropTypes from 'prop-types';
import { Link } from 'react-router-dom';
import { Box, Typography } from '@mui/material';
import { styled } from '@mui/material/styles';

const ThumbnailWrapper = styled(Box)(({ theme }) => ({
  position: 'relative',
  width: '100%',
  '&:hover': {
    cursor: 'pointer',
    '& .duration': {
      backgroundColor: theme.palette.common.black,
    },
  },
}));

const ThumbnailImage = styled('img')({
  width: '100%',
  height: 'auto',
  display: 'block',
  borderRadius: 8,
});

const Duration = styled(Box)(({ theme }) => ({
  position: 'absolute',
  bottom: 8,
  right: 8,
  padding: '3px 4px',
  backgroundColor: 'rgba(0, 0, 0, 0.8)',
  color: theme.palette.common.white,
  borderRadius: 4,
  fontSize: '12px',
  fontWeight: 500,
}));

const Title = styled(Typography)(({ theme }) => ({
  fontWeight: 500,
  fontSize: '1rem',
  marginTop: theme.spacing(1),
  display: '-webkit-box',
  WebkitLineClamp: 2,
  WebkitBoxOrient: 'vertical',
  overflow: 'hidden',
  textOverflow: 'ellipsis',
  lineHeight: 1.2,
  height: '2.4em',
}));

const formatDuration = (seconds) => {
  const hours = Math.floor(seconds / 3600);
  const minutes = Math.floor((seconds % 3600) / 60);
  const remainingSeconds = seconds % 60;

  if (hours > 0) {
    return `${hours}:${minutes.toString().padStart(2, '0')}:${remainingSeconds.toString().padStart(2, '0')}`;
  }
  return `${minutes}:${remainingSeconds.toString().padStart(2, '0')}`;
};

const VideoThumbnail = ({ video }) => {
  return (
    <Link to={`/watch/${video.id}`} style={{ textDecoration: 'none', color: 'inherit' }}>
      <ThumbnailWrapper>
        <ThumbnailImage
          src={video.thumbnailUrl}
          alt={video.title}
          loading="lazy"
        />
        <Duration className="duration">
          {formatDuration(video.duration)}
        </Duration>
      </ThumbnailWrapper>
      <Title variant="body1">
        {video.title}
      </Title>
    </Link>
  );
};
VideoThumbnail.propTypes = {
    video: PropTypes.shape({
      id: PropTypes.string.isRequired,
      thumbnailUrl: PropTypes.string.isRequired,
      title: PropTypes.string.isRequired,
      duration: PropTypes.number.isRequired,
    }).isRequired,
  };
export default VideoThumbnail; 