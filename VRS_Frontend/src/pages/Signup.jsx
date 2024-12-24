import React, { useState } from 'react';
import PropTypes from 'prop-types';
import { useNavigate, Link } from 'react-router-dom';
import {
  Box,
  Paper,
  Typography,
  TextField,
  Button,
  Divider,
  Alert,
  List,
  ListItem,
  ListItemIcon,
  ListItemText,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import { authService } from '../services/api';
import { useDispatch } from 'react-redux';
import { setUser } from '../store/authSlice';
import CheckCircleOutlineIcon from '@mui/icons-material/CheckCircleOutline';
import ErrorOutlineIcon from '@mui/icons-material/ErrorOutline';

const StyledContainer = styled(Box)(({ theme }) => ({
  display: 'flex',
  justifyContent: 'center',
  alignItems: 'center',
  minHeight: 'calc(100vh - 64px)',
  backgroundColor: theme.palette.background.default,
}));

const SignupPaper = styled(Paper)(({ theme }) => ({
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

const RequirementsList = styled(List)(({ theme }) => ({
  padding: 0,
  marginTop: theme.spacing(1),
}));

const RequirementItem = styled(ListItem)(({ theme }) => ({
  padding: theme.spacing(0, 1),
  color: theme.palette.text.secondary,
}));

const validatePassword = (password) => {
  if (!password) return {
    isValid: false,
    requirements: {
      length: false,
      specialChar: false,
      number: false,
      lowercase: false,
      uppercase: false,
    }
  };
  
  const minLength = 8;
  const hasSpecialChar = /[!@#$%^&*(),.?":{}|<>]/.test(password);
  const hasNumber = /\d/.test(password);
  const hasLowerCase = /[a-z]/.test(password);
  const hasUpperCase = /[A-Z]/.test(password);

  const requirements = {
    length: password.length >= minLength,
    specialChar: hasSpecialChar,
    number: hasNumber,
    lowercase: hasLowerCase,
    uppercase: hasUpperCase,
  };

  const isValid = Object.values(requirements).every(Boolean);

  return {
    isValid,
    requirements,
  };
};

const PasswordRequirements = ({ password, show }) => {
  if (!show) return null;
  
  const validation = validatePassword(password);

  const requirements = [
    { key: 'length', label: 'At least 8 characters' },
    { key: 'specialChar', label: 'One special character' },
    { key: 'number', label: 'One number' },
    { key: 'lowercase', label: 'One lowercase letter' },
    { key: 'uppercase', label: 'One uppercase letter' },
  ];

  return (
    <RequirementsList>
      {requirements.map(({ key, label }) => (
        <RequirementItem key={key}>
          <ListItemIcon sx={{ minWidth: 36 }}>
            {validation.requirements[key] ? (
              <CheckCircleOutlineIcon color="success" fontSize="small" />
            ) : (
              <ErrorOutlineIcon color="error" fontSize="small" />
            )}
          </ListItemIcon>
          <ListItemText 
            primary={label}
            primaryTypographyProps={{
              variant: 'caption',
              color: validation.requirements[key] ? 'success.main' : 'error',
            }}
          />
        </RequirementItem>
      ))}
    </RequirementsList>
  );
};

PasswordRequirements.propTypes = {
  password: PropTypes.string.isRequired,
  show: PropTypes.bool.isRequired,
};

const validateEmail = (email) => {
  if (!email) return true; // Don't show error when empty
  const emailRegex = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  return emailRegex.test(email);
};

const Signup = () => {
  const navigate = useNavigate();
  const dispatch = useDispatch();
  const [formData, setFormData] = useState({
    username: '',
    email: '',
    password: '',
    confirmPassword: '',
  });
  const [error, setError] = useState('');
  const [loading, setLoading] = useState(false);
  const [confirmPasswordError, setConfirmPasswordError] = useState('');
  const [showPasswordRequirements, setShowPasswordRequirements] = useState(false);
  const [emailError, setEmailError] = useState('');

  const handleChange = (e) => {
    const { name, value } = e.target;
    setFormData(prev => ({
      ...prev,
      [name]: value
    }));

    // Email validation
    if (name === 'email') {
      if (value && !validateEmail(value)) {
        setEmailError('Please enter a valid email address');
      } else {
        setEmailError('');
      }
    }

    if (name === 'password') {
      if (!showPasswordRequirements && value) {
        setShowPasswordRequirements(true);
      }
      if (formData.confirmPassword) {
        setConfirmPasswordError(
          value !== formData.confirmPassword ? 'Passwords do not match' : ''
        );
      }
    }

    if (name === 'confirmPassword') {
      setConfirmPasswordError(
        value !== formData.password ? 'Passwords do not match' : ''
      );
    }

    setError('');
  };

  const isFormValid = () => {
    return (
      formData.username.trim() !== '' &&
      formData.email.trim() !== '' &&
      validateEmail(formData.email) &&
      validatePassword(formData.password).isValid &&
      formData.password === formData.confirmPassword
    );
  };

  const validateForm = () => {
    if (!formData.username.trim() || !formData.email.trim()) {
      setError('All fields are required');
      return false;
    }

    if (!validateEmail(formData.email)) {
      setEmailError('Please enter a valid email address');
      return false;
    }

    const { isValid } = validatePassword(formData.password);
    if (!isValid) {
      return false;
    }

    if (formData.password !== formData.confirmPassword) {
      setConfirmPasswordError('Passwords do not match');
      return false;
    }

    return true;
  };

  const handleSubmit = async (e) => {
    e.preventDefault();
    if (!validateForm()) return;

    setLoading(true);
    setError('');

    try {
      const response = await authService.register({
        username: formData.username,
        email: formData.email,
        password: formData.password,
      });
      
      const { token, user } = response.data;
      
      // Store token in localStorage
      localStorage.setItem('token', token);
      
      // Update auth state
      dispatch(setUser(user));
      
      // Redirect to home page
      navigate('/');
    } catch (error) {
      setError(error.response?.data?.message || 'Failed to create account. Please try again.');
    } finally {
      setLoading(false);
    }
  };

  return (
    <StyledContainer>
      <SignupPaper elevation={3}>
        <Typography variant="h5" align="center" gutterBottom>
          Create Account
        </Typography>
        
        {error && (
          <Alert severity="error" onClose={() => setError('')}>
            {error}
          </Alert>
        )}

        <StyledForm onSubmit={handleSubmit}>
          <TextField
            label="Username"
            name="username"
            variant="outlined"
            fullWidth
            required
            value={formData.username}
            onChange={handleChange}
            error={error && !formData.username.trim()}
          />
          
          <TextField
            label="Email"
            name="email"
            type="email"
            variant="outlined"
            fullWidth
            required
            value={formData.email}
            onChange={handleChange}
            error={!!emailError}
            helperText={emailError}
          />
          
          <Box>
            <TextField
              label="Password"
              name="password"
              type="password"
              variant="outlined"
              fullWidth
              required
              value={formData.password}
              onChange={handleChange}
            />
            <PasswordRequirements 
              password={formData.password} 
              show={showPasswordRequirements}
            />
          </Box>
          
          <TextField
            label="Confirm Password"
            name="confirmPassword"
            type="password"
            variant="outlined"
            fullWidth
            required
            value={formData.confirmPassword}
            onChange={handleChange}
            error={!!confirmPasswordError}
            helperText={confirmPasswordError}
          />

          <Button
            type="submit"
            variant="contained"
            color="primary"
            fullWidth
            size="large"
            disabled={loading || !isFormValid()}
          >
            {loading ? 'Creating Account...' : 'Sign up'}
          </Button>
        </StyledForm>

        <StyledDivider />

        <Box sx={{ textAlign: 'center' }}>
          <Typography variant="body2" color="textSecondary">
            Already have an account?{' '}
            <Link to="/login" style={{ color: 'inherit', fontWeight: 'bold' }}>
              Sign in
            </Link>
          </Typography>
        </Box>
      </SignupPaper>
    </StyledContainer>
  );
};

export default Signup; 