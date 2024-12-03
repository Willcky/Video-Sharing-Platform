import React from 'react';
import PropTypes from 'prop-types';
import { Box } from '@mui/material';
import { styled } from '@mui/material/styles';
import Navbar from './Navbar';
import CategoryList from './CategoryList';

const LayoutWrapper = styled(Box)({
  display: 'flex',
  flexDirection: 'column',
  minHeight: '100vh',
});

const MainContent = styled(Box)({
  flex: 1,
  marginTop: '112px',
});

const Layout = ({ children }) => {
  return (
    <LayoutWrapper>
      <Navbar />
      <CategoryList />
      <MainContent>
        {children}
      </MainContent>
    </LayoutWrapper>
  );
};

Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;