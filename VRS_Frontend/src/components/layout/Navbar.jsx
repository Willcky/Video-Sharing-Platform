import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  AppBar,
  Toolbar,
  IconButton,
  InputBase,
  Button,
  Avatar,
  Menu,
  MenuItem,
  Badge,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import MenuIcon from '@mui/icons-material/Menu';
import SearchIcon from '@mui/icons-material/Search';
import VideoCallIcon from '@mui/icons-material/VideoCall';
import AppsIcon from '@mui/icons-material/Apps';
import NotificationsIcon from '@mui/icons-material/Notifications';
import AccountCircleIcon from '@mui/icons-material/AccountCircle';
import { useSelector } from 'react-redux';
import YouTubeLogo from '../../assets/youtube-logo.png';

// Styled components
const StyledAppBar = styled(AppBar)(({ theme }) => ({
    backgroundColor: theme.palette.background.paper,
    color: theme.palette.text.primary,
    boxShadow: 'none',
    //borderBottom: `1px solid ${theme.palette.divider}`,
    height: '64px', // Fixed height for Navbar
    zIndex: 1100, // Higher than CategoryList
  }));

const StyledToolbar = styled(Toolbar)({
  minHeight: '64px',
  display: 'flex',
  justifyContent: 'space-between',
  padding: '0 16px',
});

const LeftSection = styled('div')({
  display: 'flex',
  alignItems: 'center',
});

const Logo = styled('img')(({ theme }) => ({
  height: '50px',
  marginLeft: theme.spacing(2),
  cursor: 'pointer',
}));

const SearchForm = styled('form')(({ theme }) => ({
  flex: '0 1 640px',
  display: 'flex',
  alignItems: 'center',
  border: `1px solid ${theme.palette.divider}`,
  borderRadius: theme.shape.borderRadius,
  backgroundColor: theme.palette.background.paper,
  '&:hover': {
    border: `1px solid #c6c6c6`,
  },
}));

const SearchInput = styled(InputBase)(({ theme }) => ({
  flex: 1,
  padding: theme.spacing(1),
  paddingLeft: theme.spacing(2),
}));

const SearchButton = styled(Button)(({ theme }) => ({
  padding: theme.spacing(1.5, 2),
  borderRadius: '0',
  borderLeft: `1px solid ${theme.palette.divider}`,
  backgroundColor: '#f8f8f8',
  '&:hover': {
    backgroundColor: '#f0f0f0',
  },
}));

const RightSection = styled('div')(({ theme }) => ({
  display: 'flex',
  alignItems: 'center',
  gap: theme.spacing(2),
}));

const StyledIconButton = styled(IconButton)(({ theme }) => ({
  padding: theme.spacing(1),
}));

const StyledAvatar = styled(Avatar)(({ theme }) => ({
  width: theme.spacing(4),
  height: theme.spacing(4),
  cursor: 'pointer',
}));

const Navbar = () => {
  const navigate = useNavigate();
  const [searchQuery, setSearchQuery] = useState('');
  const [anchorEl, setAnchorEl] = useState(null);
  const user = useSelector((state) => state.auth.user);

  const handleSearch = (e) => {
    e.preventDefault();
    if (searchQuery.trim()) {
      navigate(`/search/${searchQuery}`);
    }
  };

  const handleMenuOpen = (event) => {
    setAnchorEl(event.currentTarget);
  };

  const handleMenuClose = () => {
    setAnchorEl(null);
  };

  return (
    <StyledAppBar position="fixed">
      <StyledToolbar>
        <LeftSection>
          <StyledIconButton edge="start">
            <MenuIcon />
          </StyledIconButton>
          <Logo 
            src={YouTubeLogo} 
            alt="YouTube" 
            onClick={() => navigate('/')}
          />
        </LeftSection>

        <SearchForm onSubmit={handleSearch}>
          <SearchInput
            placeholder="Search"
            value={searchQuery}
            onChange={(e) => setSearchQuery(e.target.value)}
          />
          <SearchButton 
            type="submit" 
            startIcon={<SearchIcon />}
          />
        </SearchForm>

        <RightSection>
          <StyledIconButton>
            <VideoCallIcon />
          </StyledIconButton>
          <StyledIconButton>
            <AppsIcon />
          </StyledIconButton>
          <StyledIconButton>
            <Badge badgeContent={3} color="secondary">
              <NotificationsIcon />
            </Badge>
          </StyledIconButton>
          
          {user ? (
            <>
              <StyledAvatar
                src={user.avatarUrl}
                onClick={handleMenuOpen}
              />
              <Menu
                anchorEl={anchorEl}
                keepMounted
                open={Boolean(anchorEl)}
                onClose={handleMenuClose}
              >
                <MenuItem onClick={() => navigate('/profile')}>
                  My Channel
                </MenuItem>
                <MenuItem onClick={() => navigate('/settings')}>
                  Settings
                </MenuItem>
                <MenuItem onClick={() => navigate('/logout')}>
                  Sign Out
                </MenuItem>
              </Menu>
            </>
          ) : (
            <Button
              variant="outlined"
              color="primary"
              startIcon={<AccountCircleIcon />}
              onClick={() => navigate('/login')}
            >
              Sign In
            </Button>
          )}
        </RightSection>
      </StyledToolbar>
    </StyledAppBar>
  );
};

export default Navbar; 