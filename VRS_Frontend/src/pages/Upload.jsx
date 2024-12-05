import React, { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import {
  Box,
  Typography,
  Button,
  TextField,
  Paper,
  IconButton,
  FormControl,
  InputLabel,
  Select,
  MenuItem,
  Chip,
} from '@mui/material';
import { styled } from '@mui/material/styles';
import CloudUploadIcon from '@mui/icons-material/CloudUpload';
import CloseIcon from '@mui/icons-material/Close';
import { videoService } from '../services/api';

const StyledContainer = styled(Box)(({ theme }) => ({
  padding: theme.spacing(3),
  maxWidth: '800px',
  margin: '0 auto',
  marginTop: theme.spacing(2),
}));

const UploadPaper = styled(Paper)(({ theme }) => ({
  padding: theme.spacing(3),
  display: 'flex',
  flexDirection: 'column',
  gap: theme.spacing(3),
}));

const UploadArea = styled(Box)(({ theme }) => ({
  border: `2px dashed ${theme.palette.divider}`,
  borderRadius: theme.shape.borderRadius,
  padding: theme.spacing(6),
  textAlign: 'center',
  cursor: 'pointer',
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
  },
}));

const ThumbnailPreview = styled(Box)(({ theme }) => ({
  position: 'relative',
  width: '100%',
  maxWidth: '320px',
  margin: '0 auto',
  marginTop: theme.spacing(2),
}));

const PreviewImage = styled('img')({
  width: '100%',
  height: 'auto',
  borderRadius: 8,
});

const CloseButton = styled(IconButton)(({ theme }) => ({
  position: 'absolute',
  top: -theme.spacing(1),
  right: -theme.spacing(1),
  backgroundColor: theme.palette.background.paper,
  '&:hover': {
    backgroundColor: theme.palette.action.hover,
  },
}));

const TagsContainer = styled(Box)(({ theme }) => ({
  display: 'flex',
  flexWrap: 'wrap',
  gap: theme.spacing(1),
  marginTop: theme.spacing(1),
}));

const Upload = () => {
  const navigate = useNavigate();
  const [videoFile, setVideoFile] = useState(null);
  const [thumbnailFile, setThumbnailFile] = useState(null);
  const [thumbnailPreview, setThumbnailPreview] = useState('');
  const [title, setTitle] = useState('');
  const [description, setDescription] = useState('');
  const [category, setCategory] = useState('');
  const [uploading, setUploading] = useState(false);
  const [tags, setTags] = useState([]);
  const [currentTag, setCurrentTag] = useState('');

  const categories = [
    'Music', 'Gaming', 'Sports', 'Entertainment', 'News',
    'Education', 'Technology', 'Fashion', 'Comedy', 'Science',
    'Travel', 'Food', 'Art'
  ];

  const handleVideoSelect = (event) => {
    const file = event.target.files[0];
    if (file && file.type.startsWith('video/')) {
      setVideoFile(file);
    }
  };

  const handleThumbnailSelect = (event) => {
    const file = event.target.files[0];
    if (file && file.type.startsWith('image/')) {
      setThumbnailFile(file);
      const reader = new FileReader();
      reader.onloadend = () => {
        setThumbnailPreview(reader.result);
      };
      reader.readAsDataURL(file);
    }
  };

  const handleTagKeyDown = (event) => {
    if (event.key === 'Enter' && currentTag.trim()) {
      event.preventDefault();
      if (tags.length >= 10) {
        return;
      }
      const newTag = currentTag.trim().toLowerCase();
      if (!tags.includes(newTag)) {
        setTags([...tags, newTag]);
      }
      setCurrentTag('');
    } else if (event.key === 'Backspace' && !currentTag && tags.length > 0) {
      setTags(tags.slice(0, -1));
    }
  };

  const handleDeleteTag = (tagToDelete) => {
    setTags(tags.filter(tag => tag !== tagToDelete));
  };

  const handleSubmit = async (event) => {
    event.preventDefault();
    if (!videoFile || !title || !category) return;

    setUploading(true);
    try {
      const formData = new FormData();
      formData.append('video', videoFile);
      formData.append('thumbnail', thumbnailFile);
      formData.append('title', title);
      formData.append('description', description);
      formData.append('category', category);
      formData.append('tags', JSON.stringify(tags));

      await videoService.uploadVideo(formData);
      navigate('/');
    } catch (error) {
      console.error('Error uploading video:', error);
    } finally {
      setUploading(false);
    }
  };

  return (
    <StyledContainer>
      <Typography variant="h5" gutterBottom>
        Upload Video
      </Typography>

      <UploadPaper component="form" onSubmit={handleSubmit}>
        <input
          type="file"
          accept="video/*"
          id="video-input"
          style={{ display: 'none' }}
          onChange={handleVideoSelect}
        />
        <label htmlFor="video-input">
          <UploadArea>
            <CloudUploadIcon sx={{ fontSize: 48, mb: 2, color: 'text.secondary' }} />
            <Typography variant="h6" gutterBottom>
              {videoFile ? videoFile.name : 'Select video to upload'}
            </Typography>
            <Typography variant="body2" color="text.secondary">
              or drag and drop video file
            </Typography>
          </UploadArea>
        </label>

        <TextField
          label="Title"
          variant="outlined"
          fullWidth
          required
          value={title}
          onChange={(e) => setTitle(e.target.value)}
        />

        <TextField
          label="Description"
          variant="outlined"
          fullWidth
          multiline
          rows={4}
          value={description}
          onChange={(e) => setDescription(e.target.value)}
        />

        <FormControl fullWidth required>
          <InputLabel>Category</InputLabel>
          <Select
            value={category}
            label="Category"
            onChange={(e) => setCategory(e.target.value)}
          >
            {categories.map((cat) => (
              <MenuItem key={cat} value={cat}>
                {cat}
              </MenuItem>
            ))}
          </Select>
        </FormControl>

        <Box>
          <TextField
            label="Tags"
            variant="outlined"
            fullWidth
            value={currentTag}
            onChange={(e) => setCurrentTag(e.target.value)}
            onKeyDown={handleTagKeyDown}
            helperText={`Press Enter to add a tag. ${10 - tags.length} tags remaining.`}
            disabled={tags.length >= 10}
          />
          <TagsContainer>
            {tags.map((tag) => (
              <Chip
                key={tag}
                label={tag}
                onDelete={() => handleDeleteTag(tag)}
                color="primary"
                variant="outlined"
              />
            ))}
          </TagsContainer>
        </Box>

        <Box>
          <input
            type="file"
            accept="image/*"
            id="thumbnail-input"
            style={{ display: 'none' }}
            onChange={handleThumbnailSelect}
          />
          <label htmlFor="thumbnail-input">
            <Button
              variant="outlined"
              component="span"
              startIcon={<CloudUploadIcon />}
            >
              Upload Thumbnail
            </Button>
          </label>

          {thumbnailPreview && (
            <ThumbnailPreview>
              <PreviewImage src={thumbnailPreview} alt="Thumbnail preview" />
              <CloseButton
                size="small"
                onClick={() => {
                  setThumbnailFile(null);
                  setThumbnailPreview('');
                }}
              >
                <CloseIcon />
              </CloseButton>
            </ThumbnailPreview>
          )}
        </Box>

        <Box sx={{ display: 'flex', gap: 2, justifyContent: 'flex-end' }}>
          <Button onClick={() => navigate('/')}>
            Cancel
          </Button>
          <Button
            type="submit"
            variant="contained"
            disabled={!videoFile || !title || !category || uploading}
          >
            {uploading ? 'Uploading...' : 'Upload'}
          </Button>
        </Box>
      </UploadPaper>
    </StyledContainer>
  );
};

export default Upload; 