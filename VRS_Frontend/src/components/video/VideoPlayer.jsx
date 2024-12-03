import React from 'react';
import ReactPlayer from 'react-player';
import { Box } from '@mui/material';
import { styled } from '@mui/material/styles';

// Styled components
const PlayerWrapper = styled(Box)({
  position: 'relative',
  paddingTop: '56.25%', // 16:9 Aspect Ratio
});

const StyledReactPlayer = styled(ReactPlayer)({
  position: 'absolute',
  top: 0,
  left: 0,
});

const VideoPlayer = ({ url, onProgress, onEnded }) => {
  return (
    <PlayerWrapper>
      <StyledReactPlayer
        url={url}
        width="100%"
        height="100%"
        controls
        playing
        onProgress={onProgress}
        onEnded={onEnded}
      />
    </PlayerWrapper>
  );
};

export default VideoPlayer; 