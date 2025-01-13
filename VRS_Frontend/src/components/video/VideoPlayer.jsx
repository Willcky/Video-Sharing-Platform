import React, { useEffect, useRef } from 'react';
import PropTypes from 'prop-types';
import { Box } from '@mui/material';
import { styled } from '@mui/material/styles';
import videojs from 'video.js';
import '@videojs/http-streaming';
import 'video.js/dist/video-js.css';

const PlayerWrapper = styled(Box)({
  width: '100%',
  backgroundColor: '#000',
  position: 'relative',
  aspectRatio: '16/9',
  '& .video-js': {
    width: '100%',
    height: '100%',
    position: 'absolute',
    top: 0,
    left: 0,
    fontFamily: 'Roboto, Arial, sans-serif',
  },
  '& .vjs-tech': {
    position: 'absolute',
    top: 0,
    left: 0,
    width: '100%',
    height: '100%',
  },
  '& .vjs-poster': {
    backgroundSize: 'cover',
  },
  // Big play button
  '& .vjs-big-play-button': {
    top: '50%',
    left: '50%',
    transform: 'translate(-50%, -50%)',
    width: '2em',
    height: '2em',
    lineHeight: '2em',
    border: 'none',
    borderRadius: '50%',
    backgroundColor: 'rgba(255, 0, 0, 0.8)',
    '&:hover': {
      backgroundColor: 'rgb(255, 0, 0)',
    },
  },
  // Control bar
  '& .vjs-control-bar': {
    backgroundColor: 'transparent',
    backgroundImage: 'linear-gradient(to top, rgba(0, 0, 0, 0.7) 0%, transparent 100%)',
    height: '60px',
    paddingTop: '10px',
    opacity: 0,
    transform: 'translateY(100%)',
    transition: 'all 0.3s ease-out',
    display: 'flex',
    alignItems: 'center',
    padding: '0 16px',
  },
  // Left controls
  '& .vjs-play-control, & .vjs-volume-panel': {
    display: 'flex',
    alignItems: 'center',
    margin: '0 8px 0 0',
  },
  // Spacer
  '& .vjs-spacer': {
    flex: 1,
  },
  // Right controls
  '& .vjs-time-control, & .vjs-playback-rate, & .vjs-picture-in-picture-control, & .vjs-fullscreen-control': {
    display: 'flex',
    alignItems: 'center',
    margin: '0 0 0 8px',
  },
  // Volume panel
  '& .vjs-volume-panel': {
    '&:hover .vjs-volume-control.vjs-volume-horizontal': {
      width: '80px',
    },
    '& .vjs-volume-control.vjs-volume-horizontal': {
      height: '100%',
      width: '0',
      opacity: '0',
      transition: 'all 0.2s ease',
      display: 'flex',
      alignItems: 'center',
      '&:hover': {
        opacity: '1',
      },
    },
  },
  // Progress bar
  '& .vjs-progress-control': {
    position: 'absolute',
    top: '0',
    right: '0',
    left: '0',
    width: '100%',
    height: '3px',
    transition: 'height 0.2s ease, top 0.2s ease',
    '&:hover': {
      height: '8px',
      top: '-4px',
      '& .vjs-play-progress:after': {
        transform: 'scale(1)',
      },
    },
  },
  // Button styles
  '& .vjs-button': {
    width: '40px',
    height: '40px',
    opacity: 0.8,
    transition: 'opacity 0.2s ease',
    display: 'flex',
    alignItems: 'center',
    justifyContent: 'center',
    '&:hover': {
      opacity: 1,
    },
    '& > .vjs-icon-placeholder:before': {
      lineHeight: '1',
      fontSize: '20px',
      position: 'static',
    },
  },
  // Playback rate button specific styles
  '& .vjs-playback-rate': {
    height: '40px',
    display: 'flex',
    alignItems: 'center',
    '& .vjs-playback-rate-value': {
      lineHeight: '40px',
      height: '40px',
      fontSize: '13px',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
    },
  },
  // Time display
  '& .vjs-time-control': {
    height: '40px',
    display: 'flex',
    alignItems: 'center',
    padding: '0 4px',
    minWidth: 'auto',
    fontSize: '13px',
    lineHeight: '40px',
  },
  '& .vjs-current-time': {
    padding: '0 4px 0 0',
  },
  '& .vjs-duration': {
    padding: '0 0 0 4px',
  },
  '& .vjs-time-divider': {
    padding: '0',
    minWidth: 'auto',
  },
  // Progress bar styles
  '& .vjs-progress-holder': {
    height: '100%',
    background: 'rgba(255, 255, 255, 0.2)',
    margin: '0',
  },
  '& .vjs-play-progress': {
    backgroundColor: '#fff',
    position: 'relative',
    '&:after': {
      content: '""',
      width: '12px',
      height: '12px',
      borderRadius: '50%',
      backgroundColor: '#fff',
      position: 'absolute',
      right: '-6px',
      top: '50%',
      transform: 'scale(0) translateY(-50%)',
      transition: 'transform 0.2s ease',
    },
  },
  // Volume bar
  '& .vjs-volume-bar': {
    margin: 0,
    height: '3px',
    background: 'rgba(255, 255, 255, 0.3)',
  },
  '& .vjs-volume-bar.vjs-slider-horizontal': {
    width: '100%',
    height: '3px',
  },
  '& .vjs-volume-level': {
    backgroundColor: '#fff',
    height: '3px',
  },

  // Control bar visibility
  '& .video-js:hover .vjs-control-bar': {
    opacity: 1,
    transform: 'translateY(0)',
  },
  '& .video-js.vjs-user-active .vjs-control-bar': {
    opacity: 1,
    transform: 'translateY(0)',
  },
  '& .video-js.vjs-user-inactive.vjs-playing .vjs-control-bar': {
    opacity: 0,
    transform: 'translateY(100%)',
    transition: 'all 0.8s ease-out',
  },
  // Hover states
  '& .vjs-control:hover': {
    color: '#fff',
  },
  '& .video-js.vjs-playing .vjs-big-play-button': {
    display: 'none',
  },
});

const getVideoType = (url) => {
  if (!url) return '';
  if (url.includes('.m3u8')) return 'application/x-mpegURL';
  if (url.includes('.mp4')) return 'video/mp4';
  // If no extension, assume it's HLS since we're using CloudFront
  return 'application/x-mpegURL';
};

const VideoPlayer = ({ url, poster, onProgress, onEnded }) => {
  const videoRef = useRef(null);
  const playerRef = useRef(null);

  useEffect(() => {
    if (!videoRef.current || !url) return;

    if (!playerRef.current) {
      const videoElement = videoRef.current;
      console.log('Initializing video player with URL:', url);

      const player = videojs(videoElement, {
        controls: true,
        autoplay: false,
        preload: 'auto',
        fluid: false,
        responsive: true,
        width: '100%',
        height: '100%',
        poster: poster,
        bigPlayButton: true,
        controlBar: {
          children: [
            'playToggle',
            'volumePanel',
            'progressControl',
            'spacer',
            'currentTimeDisplay',
            'timeDivider',
            'durationDisplay',
            'playbackRateMenuButton',
            'pictureInPictureToggle',
            'fullscreenToggle',
          ],
          volumePanel: {
            inline: true,
          },
        },
        playbackRates: [0.5, 1, 1.5, 2],
        html5: {
          vhs: {
            enableLowInitialPlaylist: true,
            smoothQualityChange: true,
            overrideNative: true,
            withCredentials: false,
          },
          nativeAudioTracks: false,
          nativeVideoTracks: false,
        },
        sources: [{
          src: url,
          type: getVideoType(url)
        }]
      }, () => {
        // Player is ready
        console.log('Player is ready with source:', url);

        // Event handlers
        if (onProgress) {
          player.on('timeupdate', () => {
            onProgress({
              played: player.currentTime() / player.duration(),
              playedSeconds: player.currentTime()
            });
          });
        }

        if (onEnded) {
          player.on('ended', onEnded);
        }

        // Error handling
        player.on('error', () => {
          const error = player.error();
          console.error('Video player error:', error && error.message);
          console.error('Video source:', url);
          console.error('Video type:', getVideoType(url));
        });

        // Add additional event listeners for debugging
        player.on('loadstart', () => console.log('Video load started'));
        player.on('loadeddata', () => console.log('Video data loaded'));
        player.on('loadedmetadata', () => console.log('Video metadata loaded'));
        player.on('waiting', () => console.log('Video is waiting for data'));
        player.on('canplay', () => console.log('Video can play'));
        player.on('canplaythrough', () => console.log('Video can play through'));
        player.on('playing', () => console.log('Video is playing'));
        player.on('seeking', () => console.log('Video is seeking'));
        player.on('seeked', () => console.log('Video has seeked'));
        player.on('stalled', () => console.log('Video has stalled'));
      });

      playerRef.current = player;
    } else if (url) {
      // Update source if URL changes and is valid
      console.log('Updating video source:', url);
      const type = getVideoType(url);
      console.log('Setting source with type:', type);
      playerRef.current.src({
        src: url,
        type: type,
      });
      if (poster) {
        playerRef.current.poster(poster);
      }
    }
  }, [url, poster, onProgress, onEnded]);

  // Cleanup
  useEffect(() => {
    const player = playerRef.current;
    return () => {
      if (player) {
        player.dispose();
        playerRef.current = null;
      }
    };
  }, []);

  return (
    <PlayerWrapper>
      <div data-vjs-player>
        <video
          ref={videoRef}
          className="video-js vjs-big-play-centered vjs-theme-city"
          playsInline
          controls
          preload="auto"
          width="100%"
          height="100%"
          poster={poster}
        >
          <p className="vjs-no-js">
            To view this video please enable JavaScript, and consider upgrading to a
            web browser that supports HTML5 video
          </p>
        </video>
      </div>
    </PlayerWrapper>
  );
};

VideoPlayer.propTypes = {
  url: PropTypes.string.isRequired,
  poster: PropTypes.string,
  onProgress: PropTypes.func,
  onEnded: PropTypes.func
};

export default VideoPlayer; 