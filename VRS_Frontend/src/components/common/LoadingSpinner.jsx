import React from 'react';
import { CircularProgress, Box } from '@mui/material';
import { styled } from '@mui/material/styles';

// Styled components
const SpinnerWrapper = styled(Box)({
    display: 'flex',
    justifyContent: 'center',
    alignItems: 'flex-start',
    paddingTop: '60px',
    height: 'calc(100vh - 112px)', // Subtract navbar + category list height
    overflow: 'hidden',
  });

const LoadingSpinner = () => {
  return (
    <SpinnerWrapper>
      <CircularProgress />
    </SpinnerWrapper>
  );
};

export default LoadingSpinner; 