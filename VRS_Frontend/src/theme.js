import { createTheme } from '@mui/material/styles';

const theme = createTheme({
  palette: {
    primary: {
      main: '#FF0000', // YouTube red
      contrastText: '#ffffff',
    },
    secondary: {
      main: '#282828', // Dark gray for secondary elements
    },
    background: {
      default: '#ffffff',
      paper: '#ffffff',
    },
  },
  typography: {
    fontFamily: 'Roboto, Arial, sans-serif',
    h6: {
      fontWeight: 500,
    },
    body1: {
      fontSize: '0.9rem',
    },
  },
  components: {  // Replace 'overrides' with 'components'
    MuiButton: {
      styleOverrides: {  // Use 'styleOverrides' instead of 'root'
        root: {
          textTransform: 'none',
        },
      },
    },
  },
});

export default theme; 