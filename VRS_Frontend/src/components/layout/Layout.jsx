import React from 'react';
import PropTypes from 'prop-types';
import { Box } from '@mui/material';
import { styled } from '@mui/material/styles';
import { useLocation } from 'react-router-dom';
import Navbar from './Navbar';
import CategoryList from './CategoryList';

const LayoutWrapper = styled(Box)({
  display: 'flex',
  flexDirection: 'column',
  minHeight: '100vh',
});

const MainContent = styled(Box)(({ hasCategories }) => ({
  flex: 1,
  marginTop: hasCategories ? '112px' : hasCategories === false ? '64px' : 0,
}));

const Layout = ({ children }) => {
  const location = useLocation();
  const isAuthPage = location.pathname === '/login' || location.pathname === '/signup';
  const shouldHideCategories = location.pathname.startsWith('/watch/') || 
                             location.pathname.startsWith('/upload') || 
                             isAuthPage;

  if (isAuthPage) {
    return <LayoutWrapper>{children}</LayoutWrapper>;
  }

  return (
    <LayoutWrapper>
      <Navbar />
      {!shouldHideCategories && <CategoryList />}
      <MainContent hasCategories={!shouldHideCategories}>
        {children}
      </MainContent>
    </LayoutWrapper>
  );
};

Layout.propTypes = {
  children: PropTypes.node.isRequired,
};

export default Layout;