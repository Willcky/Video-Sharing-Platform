import React, { useState } from 'react';
import { useNavigate, Link } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Divider,
  Alert,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { authService } from '../services/api';
import { useDispatch } from 'react-redux';
import { setUser } from '../store/authSlice';

const StyledContainer = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  minHeight: 'calc(100vh - 64px)',
  backgroundColor: theme.palette.background.default,
}));

const LoginPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(4),
  width: '100%',
  maxWidth: '400px',
  display: 'flex',
  flexDirection: 'column',
  gap: theme.spacing(3),
}));

const StyledForm = styled('form')({
  display: 'flex',
  flexDirection: 'column',
  gap: '16px',
});

const StyledDivider = styled(Divider)(({ theme }) => ({
  margin: theme.spacing(2, 0),
}));

const Login = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!username || !password) return;

    setLoading(true);
    setError('');

    try {
      const response = await authService.login({ username, password });
      console.log('Login response:', response.data);

      if (response.data.code === 500) {
        setError(response.data.msg || 'Login failed. Please check your credentials.');
        return;
      }

      const { access_token, username: userName } = response.data.data;
      
      if (!access_token) {
        setError('Invalid response from server');
        return;
      }
      
      // Store token and username in localStorage
      localStorage.setItem('token', access_token);
      localStorage.setItem('username', userName);
      
      // Update auth state
      dispatch(setUser({ username: userName }));
      
      // Redirect to home page
      navigate('/');
    } catch (error) {
      setError(error.response?.data?.msg || 'Failed to login. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <StyledContainer>
      <LoginPaper elevation={3}>
        <Typography variant="h5" align="center" gutterBottom>
          Sign in
        </Typography>
        
        {error && (
          <Alert severity="error" onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        <StyledForm onSubmit={handleSubmit}>
          <TextField
            label="Username"
            variant="outlined"
            fullWidth
            required
            value={username}
            onChange={(e) => setUsername(e.target.value)}
            autoComplete="username"
          />
          
          <TextField
            label="Password"
            type="password"
            variant="outlined"
            fullWidth
            required
            value={password}
            onChange={(e) => setPassword(e.target.value)}
            autoComplete="current-password"
          />

          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            size="large"
            disabled={loading}
          >
            {loading ? 'Signing in...' : 'Sign in'}
          </Button>
        </StyledForm>

        <StyledDivider />

        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="body2" color="textSecondary">
            Don&apos;t have an account?{' '}
            <Link to="/signup" style={{ color: 'inherit', fontWeight: 'bold' }}>
              Sign up
            </Link>
          </Typography>
        </Box>
      </LoginPaper>
    </StyledContainer>
  );
};

export default Login; 