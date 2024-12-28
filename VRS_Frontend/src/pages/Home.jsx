import React from 'react';
import { Grid, Box } from '@mui/material';
import { styled } from '@mui/material/styles';
import VideoCard from '../components/video/VideoCard';

const StyledContainer = styled(Box)(({ theme }) => ({
  marginTop: theme.spacing(2),
  padding: theme.spacing(2, 6),
  minHeight: 'calc(100vh - 112px)',
  maxHeight: 'calc(100vh - 112px)',
  overflow: 'auto',
}));

const mockVideos = [
  {
    id: '1',
    title: 'Introduction to React Hooks',
    thumbnailUrl: 'https://i.ytimg.com/vi/dpw9EHDh2bM/hqdefault.jpg',
    views: 150000,
    uploadDate: '2023-11-15',
    channel: {
      name: 'React Tutorials',
      avatarUrl: 'https://i.pravatar.cc/150?img=1',
    }
  },
  {
    id: '2',
    title: 'Building Modern UIs with Material-UI',
    thumbnailUrl: 'https://i.ytimg.com/vi/vyJU9g4cg5Y/hqdefault.jpg',
    views: 98000,
    uploadDate: '2023-11-14',
    channel: {
      name: 'UI Design Channel',
      avatarUrl: 'https://i.pravatar.cc/150?img=2',
    }
  },
  {
    id: '3',
    title: 'JavaScript ES6+ Features Explained',
    thumbnailUrl: 'https://i.ytimg.com/vi/NCwa_xi0Uuc/hqdefault.jpg',
    views: 200000,
    uploadDate: '2023-11-13',
    channel: {
      name: 'JavaScript Pro',
      avatarUrl: 'https://i.pravatar.cc/150?img=3',
    }
  },
  {
    id: '4',
    title: 'Redux Toolkit Complete Guide',
    thumbnailUrl: 'https://i.ytimg.com/vi/9zySeP5vH9c/hqdefault.jpg',
    views: 75000,
    uploadDate: '2023-11-12',
    channel: {
      name: 'State Management Tips',
      avatarUrl: 'https://i.pravatar.cc/150?img=4',
    }
  },
  {
    id: '5',
    title: 'CSS Grid Layout Mastery',
    thumbnailUrl: 'https://i.ytimg.com/vi/EFafSYg-PkI/hqdefault.jpg',
    views: 120000,
    uploadDate: '2023-11-11',
    channel: {
      name: 'CSS Masters',
      avatarUrl: 'https://i.pravatar.cc/150?img=5',
    }
  },
  {
    id: '6',
    title: 'Node.js Backend Development',
    thumbnailUrl: 'https://i.ytimg.com/vi/TlB_eWDSMt4/hqdefault.jpg',
    views: 180000,
    uploadDate: '2023-11-10',
    channel: {
      name: 'Backend Dev',
      avatarUrl: 'https://i.pravatar.cc/150?img=6',
    }
  },
  {
    id: '7',
    title: 'TypeScript for Beginners',
    thumbnailUrl: 'https://i.ytimg.com/vi/BwuLxPH8IDs/hqdefault.jpg',
    views: 90000,
    uploadDate: '2023-11-09',
    channel: {
      name: 'TypeScript Guru',
      avatarUrl: 'https://i.pravatar.cc/150?img=7',
    }
  },
  {
    id: '8',
    title: 'Docker Containerization',
    thumbnailUrl: 'https://i.ytimg.com/vi/fqMOX6JJhGo/hqdefault.jpg',
    views: 135000,
    uploadDate: '2023-11-08',
    channel: {
      name: 'DevOps Channel',
      avatarUrl: 'https://i.pravatar.cc/150?img=8',
    }
  },
  {
    id: '9',
    title: 'GraphQL API Design',
    thumbnailUrl: 'https://i.ytimg.com/vi/ed8SzALpx1Q/hqdefault.jpg',
    views: 85000,
    uploadDate: '2023-11-07',
    channel: {
      name: 'API Design Pro',
      avatarUrl: 'https://i.pravatar.cc/150?img=9',
    }
  },
  {
    id: '10',
    title: 'Web Security Best Practices',
    thumbnailUrl: 'https://i.ytimg.com/vi/F5kZw9_GDps/hqdefault.jpg',
    views: 165000,
    uploadDate: '2023-11-06',
    channel: {
      name: 'Security Expert',
      avatarUrl: 'https://i.pravatar.cc/150?img=10',
    }
  }
];

const Home = () => {
  return (
    <StyledContainer>
      <Grid container spacing={3}>
        {mockVideos.map((video) => (
          <Grid item xs={12} sm={6} md={3} key={video.id}>
            <VideoCard video={video} />
          </Grid>
        ))}
      </Grid>
    </StyledContainer>
  );
};

export default Home;