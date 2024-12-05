import React from 'react';
import { BrowserRouter as Router, Routes, Route } from 'react-router-dom';
import { ThemeProvider } from '@mui/material';
import { Provider } from 'react-redux';
import { store } from './store';
import theme from './theme';
import Layout from './components/layout/Layout';
import Home from './pages/Home';
import Watch from './pages/Watch';
import Search from './pages/Search';
import Upload from './pages/Upload';

const App = () => {
  return (
    <Provider store={store}>
      <ThemeProvider theme={theme}>
        <Router>
          <Layout>
            <Routes>
              <Route path="/" element={<Home />} />
              <Route path="/watch/:videoId" element={<Watch />} />
              <Route path="/search/:searchQuery" element={<Search />} />
              <Route path="/upload" element={<Upload />} />
            </Routes>
          </Layout>
        </Router>
      </ThemeProvider>
    </Provider>
  );
};

export default App;