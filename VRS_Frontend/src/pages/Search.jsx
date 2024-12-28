import React, { useState, useEffect } from 'react';
import { useParams } from 'react-router-dom';
import { videoService } from '../services/api';
import VideoThumbnail from '../components/video/VideoThumbnail';

const Search = () => {
  const { searchQuery } = useParams();
  const [videos, setVideos] = useState([]);

  useEffect(() => {
    const fetchSearchResults = async () => {
      const response = await videoService.searchVideos(searchQuery);
      setVideos(response.data);
    };

    fetchSearchResults();
  }, [searchQuery]);

  return (
    <div>
      {videos.map((video) => (
        <VideoThumbnail key={video.id} video={video} />
      ))}
    </div>
  );
};

export default Search; 