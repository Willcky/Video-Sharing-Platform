import React from 'react';
import { Chip, Box } from '@mui/material';
import { styled } from '@mui/material/styles';
import { useDispatch, useSelector } from 'react-redux';
import { setCategory } from '../../store/categorySlice';

// Styled components
const StyledBox = styled(Box)(({ theme }) => ({
    padding: theme.spacing(1),
    overflowX: 'auto',
    whiteSpace: 'nowrap',
    backgroundColor: theme.palette.background.paper,
    //borderBottom: `1px solid ${theme.palette.divider}`,
    position: 'fixed',
    top: '64px', // Navbar height
    left: 0,
    right: 0,
    zIndex: 1000,
    height: '48px', // Fixed height for CategoryList
    display: 'flex',
    alignItems: 'center',
    '&::-webkit-scrollbar': {
      display: 'none',
    },
    msOverflowStyle: 'none',
    scrollbarWidth: 'none',
  }));

  const StyledChip = styled(Chip, {
    shouldForwardProp: (prop) => prop !== 'active'
  })(({ theme, active }) => ({
    margin: theme.spacing(0, 0.5),
    '&:first-of-type': {
      marginLeft: theme.spacing(2),
    },
    '&:last-of-type': {
      marginRight: theme.spacing(2),
    },
    ...(active && {
      backgroundColor: theme.palette.common.black,
      color: theme.palette.common.white,
      '&:hover': {
        backgroundColor: theme.palette.grey[800],
      },
    }),
  }));

const CategoryList = () => {
  const dispatch = useDispatch();
  const { selectedCategory, categories } = useSelector((state) => state.category);

  const handleCategoryClick = (category) => {
    dispatch(setCategory(category));
  };

  
  return (
    <StyledBox>
      {categories && categories.map((category) => (
        <StyledChip
          key={category}
          label={category}
          clickable
          onClick={() => handleCategoryClick(category)}
          active={selectedCategory === category}
          size="small"
        />
      ))}
    </StyledBox>
  );
};

export default CategoryList; 