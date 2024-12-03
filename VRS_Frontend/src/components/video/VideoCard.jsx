import React from 'react';
import { Link } from 'react-router-dom';
import PropTypes from 'prop-types';
import { 
    Card, 
    CardMedia, 
    CardContent, 
    Typography, 
    Avatar 
  } from '@mui/material';
  import { styled } from '@mui/material/styles';
import { formatDistance } from 'date-fns';
import { formatViewCount } from '../../utils/formatters';

const StyledCard = styled(Card)(() => ({
    maxWidth: '100%',
    boxShadow: 'none',
    backgroundColor: 'transparent',
    transform: 'scale(1.1)', // Make card bigger
    transformOrigin: 'top center',
  }));
  
  const StyledCardMedia = styled(CardMedia)(() => ({
    height: 0,
    paddingTop: '56.25%', // 16:9
    position: 'relative',
    '&:hover': {
      cursor: 'pointer',
    },
    borderRadius: 8, // Add rounded corners
  }));
  
  const StyledCardContent = styled(CardContent)(({ theme }) => ({
    padding: theme.spacing(1.5, 0), // Increase padding
    '&:last-child': {
      paddingBottom: 0,
    },
  }));
  
  const ChannelInfoContainer = styled('div')(({ theme }) => ({
    display: 'flex',
    alignItems: 'flex-start',
    marginTop: theme.spacing(1),
  }));
  
  const StyledAvatar = styled(Avatar)(({ theme }) => ({
    width: theme.spacing(4),
    height: theme.spacing(4),
    marginRight: theme.spacing(1),
  }));
  
  const VideoInfoContainer = styled('div')({
    flex: 1,
  });
  
  const VideoTitle = styled(Typography)(({ theme }) => ({
    fontWeight: 500,
    lineHeight: 1.2,
    marginBottom: theme.spacing(0.5),
    display: '-webkit-box',
    '-webkit-line-clamp': 2,
    '-webkit-box-orient': 'vertical',
    overflow: 'hidden',
  }));
  
  const ChannelName = styled(Typography)(({ theme }) => ({
    color: theme.palette.text.secondary,
    fontSize: '0.9rem',
  }));
  
  const VideoStats = styled(Typography)(({ theme }) => ({
    color: theme.palette.text.secondary,
    fontSize: '0.9rem',
  }));
  
  const VideoCard = ({ video }) => {
    const {
      id,
      thumbnailUrl,
      title,
      channel,
      views,
      uploadDate,
    } = video;
  
    return (
      <StyledCard>
        <Link to={`/watch/${id}`} style={{ textDecoration: 'none' }}>
          <StyledCardMedia
            image={thumbnailUrl}
            title={title}
          />
        </Link>
        <StyledCardContent>
          <ChannelInfoContainer>
            <StyledAvatar
              src={channel.avatarUrl}
              alt={channel.name}
            />
            <VideoInfoContainer>
              <VideoTitle variant="body1">
                {title}
              </VideoTitle>
              <ChannelName>
                {channel.name}
              </ChannelName>
              <VideoStats>
                {formatViewCount(views)} views • {formatDistance(new Date(uploadDate), new Date(), { addSuffix: true })}
              </VideoStats>
            </VideoInfoContainer>
          </ChannelInfoContainer>
        </StyledCardContent>
      </StyledCard>
    );
  };

  VideoCard.propTypes = {
    video: PropTypes.shape({
      id: PropTypes.string.isRequired,
      thumbnailUrl: PropTypes.string.isRequired,
      title: PropTypes.string.isRequired,
      channel: PropTypes.shape({
        name: PropTypes.string.isRequired,
        avatarUrl: PropTypes.string.isRequired,
      }).isRequired,
      views: PropTypes.number.isRequired,
      uploadDate: PropTypes.string.isRequired,
    }).isRequired,
  };
  
  export default VideoCard; 