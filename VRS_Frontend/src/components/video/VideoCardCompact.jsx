import React from 'react';
import PropTypes from 'prop-types';
import { Box, Typography, Card, CardMedia } from '@mui/material';
import { formatDistance } from 'date-fns';
import { formatViewCount } from '../../utils/formatters';
import { useNavigate } from 'react-router-dom';

const VideoCardCompact = ({ video }) => {
  const navigate = useNavigate();

  const handleClick = () => {
    navigate(`/watch/${video.id}`);
  };

  return (
    <Card 
      onClick={handleClick}
      sx={{ 
        display: 'flex',
        cursor: 'pointer',
        backgroundColor: 'transparent',
        boxShadow: 'none',
        '&:hover': {
          backgroundColor: 'action.hover'
        }
      }}
    >
      <Box sx={{ position: 'relative', width: '168px', minWidth: '168px', height: '94px' }}>
        <CardMedia
          component="img"
          image={video.thumbnailUrl}
          alt={video.title}
          sx={{ 
            width: '100%',
            height: '100%',
            objectFit: 'cover',
            borderRadius: 1
          }}
        />
        {video.duration && (
          <Box
            sx={{
              position: 'absolute',
              bottom: 4,
              right: 4,
              bgcolor: 'rgba(0, 0, 0, 0.8)',
              color: 'white',
              padding: '1px 4px',
              borderRadius: 0.5,
              fontSize: '0.75rem'
            }}
          >
            {video.duration}
          </Box>
        )}
      </Box>

      <Box sx={{ ml: 1, flex: 1, minWidth: 0 }}>
        <Typography
          variant="subtitle2"
          sx={{
            fontWeight: 'medium',
            overflow: 'hidden',
            textOverflow: 'ellipsis',
            display: '-webkit-box',
            WebkitLineClamp: 2,
            WebkitBoxOrient: 'vertical',
            lineHeight: '1.2em',
            maxHeight: '2.4em'
          }}
        >
          {video.title}
        </Typography>
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ fontSize: '0.8rem' }}
        >
          {video.username}
        </Typography>
        <Typography
          variant="body2"
          color="text.secondary"
          sx={{ fontSize: '0.8rem' }}
        >
          {formatViewCount(video.views)} views • {formatDistance(new Date(video.uploadDate), new Date(), { addSuffix: true })}
        </Typography>
      </Box>
    </Card>
  );
};

VideoCardCompact.propTypes = {
  video: PropTypes.shape({
    id: PropTypes.string.isRequired,
    title: PropTypes.string.isRequired,
    thumbnailUrl: PropTypes.string.isRequired,
    username: PropTypes.string.isRequired,
    views: PropTypes.number.isRequired,
    uploadDate: PropTypes.string.isRequired,
    duration: PropTypes.string
  }).isRequired
};

export default VideoCardCompact; 