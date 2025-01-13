import React, { useState, useEffect } from 'react';
import PropTypes from 'prop-types';
import axios from '../../config/axios.config';
import API_CONFIG from '../../config/api.config';
import { formatDistance } from 'date-fns';
import { Button, TextField, Box, Typography, Avatar, IconButton } from '@mui/material';
import ThumbUpIcon from '@mui/icons-material/ThumbUp';
import ThumbDownIcon from '@mui/icons-material/ThumbDown';
import ExpandMoreIcon from '@mui/icons-material/ExpandMore';

const formatDate = (dateString) => {
  if (!dateString) return new Date();
  try {
    const date = new Date(dateString);
    if (isNaN(date.getTime())) {
      return new Date();
    }
    return date;
  } catch (error) {
    return new Date();
  }
};

const CommentSection = ({ videoId, initialCommentCount }) => {
  const [comments, setComments] = useState([]);
  const [totalComments, setTotalComments] = useState(initialCommentCount || 0);
  const [newComment, setNewComment] = useState('');
  const [replyText, setReplyText] = useState('');
  const [commentImage, setCommentImage] = useState(null);
  const [replyToComment, setReplyToComment] = useState(null);
  const [subComments, setSubComments] = useState({});
  const [expandedComments, setExpandedComments] = useState(new Set());
  const [likedComments, setLikedComments] = useState(new Set());
  const [dislikedComments, setDislikedComments] = useState(new Set());

  // Fetch first level comments
  const fetchComments = async () => {
    try {
      const url = API_CONFIG.ENDPOINTS.COMMENTS.LIST.replace(':videoId', videoId);
      const response = await axios.get(url, {
        params: { pageNum: 1 }
      });
      setComments(response.data.rows);
      // Don't override the initial comment count from video data
      if (initialCommentCount === undefined) {
        setTotalComments(response.data.total);
      }

      // Fetch action history for comments
      try {
        const actionResponse = await axios.get(`${API_CONFIG.ENDPOINTS.ACTIONS.LIST}/${videoId}`);
        const actions = actionResponse.data.data;
        
        // Set initial like/dislike states based on action history
        const newLikedComments = new Set();
        const newDislikedComments = new Set();
        
        actions.forEach(action => {
          if (action.commentId) { // Only process comment actions
            if (action.actionType === 3) { // LIKE_COMMENT
              newLikedComments.add(action.commentId);
            } else if (action.actionType === 4) { // DISLIKE_COMMENT
              newDislikedComments.add(action.commentId);
            }
          }
        });

        setLikedComments(newLikedComments);
        setDislikedComments(newDislikedComments);
      } catch (error) {
        console.error('Error fetching action history:', error);
      }
    } catch (error) {
      console.error('Error fetching comments:', error);
    }
  };

  // Fetch sub-comments for a specific comment
  const fetchSubComments = async (commentId, pageNum = 1) => {
    try {
      const url = API_CONFIG.ENDPOINTS.COMMENTS.REPLIES.replace(':commentId', commentId);
      const response = await axios.get(url, {
        params: { pageNum }
      });
      setSubComments(prev => ({
        ...prev,
        [commentId]: response.data.rows
      }));
    } catch (error) {
      console.error('Error fetching sub-comments:', error);
    }
  };

  useEffect(() => {
    fetchComments();
  }, [videoId]);

  const handleSubmitComment = async () => {
    const commentContent = replyToComment ? replyText : newComment;
    if (!commentContent.trim()) return;

    const formData = new FormData();
    formData.append('videoId', videoId);
    formData.append('content', commentContent);
    
    if (replyToComment) {
      formData.append('replyUserId', replyToComment.userId);
      const parentCommentId = replyToComment.parentCommentId || replyToComment.commentId;
      formData.append('pCommentId', parentCommentId);
    }
    
    if (commentImage) {
      formData.append('image', commentImage);
    }

    try {
      const response = await axios.post(API_CONFIG.ENDPOINTS.COMMENTS.CREATE, formData);
      const newCommentData = response.data.data;

      if (replyToComment) {
        const parentCommentId = replyToComment.parentCommentId || replyToComment.commentId;
        const formattedReply = {
          commentId: newCommentData.commentId,
          content: newCommentData.content,
          userId: newCommentData.userId,
          userName: newCommentData.userName,
          avatar: newCommentData.avatar,
          imgPath: newCommentData.imgPath,
          postTime: newCommentData.postTime,
          likeCount: 0,
          hateCount: 0,
          replyUserId: replyToComment.userId,
          replyUserName: replyToComment.userName
        };

        setSubComments(prev => ({
          ...prev,
          [parentCommentId]: [
            formattedReply,
            ...(prev[parentCommentId] || [])
          ]
        }));
        setComments(prev => prev.map(comment => 
          comment.commentId === parentCommentId
            ? { ...comment, commentCount: (comment.commentCount || 0) + 1 }
            : comment
        ));
        setReplyText('');
      } else {
        const formattedComment = {
          commentId: newCommentData.commentId,
          content: newCommentData.content,
          userId: newCommentData.userId,
          userName: newCommentData.userName,
          avatar: newCommentData.avatar,
          imgPath: newCommentData.imgPath,
          postTime: newCommentData.postTime,
          likeCount: 0,
          hateCount: 0,
          commentCount: 0
        };
        setComments(prev => [formattedComment, ...prev]);
        // Only increment total comments for new top-level comments
        setTotalComments(prev => prev + 1);
        setNewComment('');
      }

      setCommentImage(null);
      setReplyToComment(null);
    } catch (error) {
      console.error('Error posting comment:', error);
    }
  };

  const handleImageUpload = (event) => {
    setCommentImage(event.target.files[0]);
  };

  const toggleSubComments = async (commentId) => {
    if (expandedComments.has(commentId)) {
      // Fold sub-comments
      setExpandedComments(prev => {
        const next = new Set(prev);
        next.delete(commentId);
        return next;
      });
    } else {
      // Unfold and fetch if not already loaded
      setExpandedComments(prev => new Set([...prev, commentId]));
      if (!subComments[commentId]) {
        await fetchSubComments(commentId);
      }
    }
  };

  const handleCommentAction = async (commentId, actionType) => {
    try {
      await axios.post(API_CONFIG.ENDPOINTS.ACTIONS.DO, {
        videoId,
        commentId,
        actionType
      });

      // Update like/dislike count optimistically
      const updateCount = (comments, targetCommentId, isLike) => {
        return comments.map(comment => {
          if (comment.commentId === targetCommentId) {
            return {
              ...comment,
              likeCount: comment.likeCount + (isLike ? 1 : 0),
              hateCount: comment.hateCount + (isLike ? 0 : 1)
            };
          }
          return comment;
        });
      };

      if (actionType === 3) { // LIKE_COMMENT
        if (likedComments.has(commentId)) {
          // Unlike
          setLikedComments(prev => {
            const next = new Set(prev);
            next.delete(commentId);
            return next;
          });
          setComments(prev => prev.map(comment => 
            comment.commentId === commentId 
              ? { ...comment, likeCount: Math.max(0, comment.likeCount - 1) }
              : comment
          ));
        } else {
          // Like
          setLikedComments(prev => new Set([...prev, commentId]));
          if (dislikedComments.has(commentId)) {
            setDislikedComments(prev => {
              const next = new Set(prev);
              next.delete(commentId);
              return next;
            });
            setComments(prev => prev.map(comment => 
              comment.commentId === commentId 
                ? { 
                    ...comment, 
                    likeCount: comment.likeCount + 1,
                    hateCount: Math.max(0, comment.hateCount - 1)
                  }
                : comment
            ));
          } else {
            setComments(prev => updateCount(prev, commentId, true));
          }
        }
      } else if (actionType === 4) { // DISLIKE_COMMENT
        if (dislikedComments.has(commentId)) {
          // Undislike
          setDislikedComments(prev => {
            const next = new Set(prev);
            next.delete(commentId);
            return next;
          });
          setComments(prev => prev.map(comment => 
            comment.commentId === commentId 
              ? { ...comment, hateCount: Math.max(0, comment.hateCount - 1) }
              : comment
          ));
        } else {
          // Dislike
          setDislikedComments(prev => new Set([...prev, commentId]));
          if (likedComments.has(commentId)) {
            setLikedComments(prev => {
              const next = new Set(prev);
              next.delete(commentId);
              return next;
            });
            setComments(prev => prev.map(comment => 
              comment.commentId === commentId 
                ? { 
                    ...comment, 
                    hateCount: comment.hateCount + 1,
                    likeCount: Math.max(0, comment.likeCount - 1)
                  }
                : comment
            ));
          } else {
            setComments(prev => updateCount(prev, commentId, false));
          }
        }
      }
    } catch (error) {
      console.error('Error performing comment action:', error);
    }
  };

  return (
    <Box sx={{ mt: 4 }}>
      <Typography variant="h6" sx={{ mb: 2 }}>
        {totalComments} Comments
      </Typography>
      {/* Main comment input section */}
      <Box sx={{ mb: 3 }}>
        <TextField
          fullWidth
          multiline
          rows={3}
          value={newComment}
          onChange={(e) => setNewComment(e.target.value)}
          placeholder="Write a comment..."
          variant="outlined"
        />
        <Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between' }}>
          <input
            type="file"
            accept="image/*"
            onChange={handleImageUpload}
            style={{ display: 'none' }}
            id="comment-image-upload"
          />
          <label htmlFor="comment-image-upload">
            <Button component="span" variant="outlined">
              Upload Image
            </Button>
          </label>
          <Button variant="contained" onClick={handleSubmitComment}>
            Post Comment
          </Button>
        </Box>
      </Box>

      {/* Comments list */}
      <Box>
        {comments.map((comment) => (
          <Box key={comment.commentId} sx={{ mb: 3, pl: 2 }}>
            <Box sx={{ display: 'flex', alignItems: 'flex-start', mb: 1 }}>
              <Avatar src={comment.avatar} alt={comment.userName} />
              <Box sx={{ ml: 2, flex: 1 }}>
                <Typography variant="subtitle1" fontWeight="bold">
                  {comment.userName}
                </Typography>
                <Typography variant="body1">{comment.content}</Typography>
                {comment.imgPath && (
                  <Box sx={{ mt: 1 }}>
                    <img src={comment.imgPath} alt="Comment attachment" style={{ maxWidth: '200px' }} />
                  </Box>
                )}
                <Box sx={{ mt: 1, display: 'flex', alignItems: 'center', gap: 2 }}>
                  <Typography variant="body2" color="text.secondary">
                    {formatDistance(formatDate(comment.postTime), new Date(), { addSuffix: true })}
                  </Typography>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <IconButton 
                      size="small"
                      onClick={() => handleCommentAction(comment.commentId, 3)}
                      sx={{ color: likedComments.has(comment.commentId) ? "error.main" : "inherit" }}
                    >
                      <ThumbUpIcon fontSize="small" />
                    </IconButton>
                    <Typography variant="body2">{comment.likeCount}</Typography>
                  </Box>
                  <Box sx={{ display: 'flex', alignItems: 'center' }}>
                    <IconButton 
                      size="small"
                      onClick={() => handleCommentAction(comment.commentId, 4)}
                      sx={{ color: dislikedComments.has(comment.commentId) ? "error.main" : "inherit" }}
                    >
                      <ThumbDownIcon fontSize="small" />
                    </IconButton>
                    <Typography variant="body2">{comment.hateCount}</Typography>
                  </Box>
                  <Button
                    size="small"
                    onClick={() => setReplyToComment(comment)}
                  >
                    Reply
                  </Button>
                  {comment.commentCount > 0 && (
                    <Button
                      size="small"
                      endIcon={<ExpandMoreIcon sx={{ 
                        transform: expandedComments.has(comment.commentId) ? 'rotate(180deg)' : 'none',
                        transition: 'transform 0.2s'
                      }} />}
                      onClick={() => toggleSubComments(comment.commentId)}
                    >
                      {comment.commentCount} replies
                    </Button>
                  )}
                </Box>

                {/* Reply input */}
                {replyToComment?.commentId === comment.commentId && (
                  <Box sx={{ mt: 2 }}>
                    <TextField
                      fullWidth
                      size="small"
                      value={replyText}
                      onChange={(e) => setReplyText(e.target.value)}
                      placeholder={`Reply to ${comment.userName}...`}
                    />
                    <Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                      <Box>
                        <input
                          type="file"
                          accept="image/*"
                          onChange={handleImageUpload}
                          style={{ display: 'none' }}
                          id={`reply-image-upload-${comment.commentId}`}
                        />
                        <label htmlFor={`reply-image-upload-${comment.commentId}`}>
                          <Button component="span" size="small" variant="outlined">
                            Upload Image
                          </Button>
                        </label>
                        {commentImage && (
                          <Typography variant="caption" sx={{ ml: 1 }}>
                            Image selected
                          </Typography>
                        )}
                      </Box>
                      <Box sx={{ display: 'flex', gap: 1 }}>
                        <Button size="small" onClick={() => {
                          setReplyToComment(null);
                          setCommentImage(null);
                        }}>
                          Cancel
                        </Button>
                        <Button size="small" variant="contained" onClick={handleSubmitComment}>
                          Reply
                        </Button>
                      </Box>
                    </Box>
                  </Box>
                )}

                {/* Sub-comments */}
                {expandedComments.has(comment.commentId) && subComments[comment.commentId]?.map((subComment) => (
                  <Box key={subComment.commentId} sx={{ mt: 2, pl: 2 }}>
                    <Box sx={{ display: 'flex', alignItems: 'flex-start', width: '100%' }}>
                      <Avatar src={subComment.avatar} alt={subComment.userName} sx={{ width: 32, height: 32 }} />
                      <Box sx={{ ml: 1, flex: 1, width: '100%' }}>
                        <Typography variant="subtitle2">
                          {subComment.userName}
                          {subComment.replyUserId !== comment.userId && (
                            <span style={{ color: 'gray' }}> replying to {subComment.replyUserName}</span>
                          )}
                        </Typography>
                        <Typography variant="body2">{subComment.content}</Typography>
                        {subComment.imgPath && (
                          <Box sx={{ mt: 1 }}>
                            <img src={subComment.imgPath} alt="Reply attachment" style={{ maxWidth: '150px' }} />
                          </Box>
                        )}
                        <Box sx={{ mt: 1, display: 'flex', alignItems: 'center', gap: 2 }}>
                          <Typography variant="body2" color="text.secondary">
                            {formatDistance(formatDate(subComment.postTime), new Date(), { addSuffix: true })}
                          </Typography>
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <IconButton 
                              size="small"
                              onClick={() => handleCommentAction(subComment.commentId, 3)}
                              sx={{ color: likedComments.has(subComment.commentId) ? "error.main" : "inherit" }}
                            >
                              <ThumbUpIcon fontSize="small" />
                            </IconButton>
                            <Typography variant="body2">{subComment.likeCount}</Typography>
                          </Box>
                          <Box sx={{ display: 'flex', alignItems: 'center' }}>
                            <IconButton 
                              size="small"
                              onClick={() => handleCommentAction(subComment.commentId, 4)}
                              sx={{ color: dislikedComments.has(subComment.commentId) ? "error.main" : "inherit" }}
                            >
                              <ThumbDownIcon fontSize="small" />
                            </IconButton>
                            <Typography variant="body2">{subComment.hateCount}</Typography>
                          </Box>
                          <Button
                            size="small"
                            onClick={() => setReplyToComment({
                              ...subComment,
                              parentCommentId: comment.commentId
                            })}
                          >
                            Reply
                          </Button>
                        </Box>

                        {/* Reply input for sub-comment */}
                        {replyToComment?.commentId === subComment.commentId && (
                          <Box sx={{ mt: 2, width: '100%', pr: 2 }}>
                            <TextField
                              fullWidth
                              size="small"
                              value={replyText}
                              onChange={(e) => setReplyText(e.target.value)}
                              placeholder={`Reply to ${subComment.userName}...`}
                            />
                            <Box sx={{ mt: 1, display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
                              <Box>
                                <input
                                  type="file"
                                  accept="image/*"
                                  onChange={handleImageUpload}
                                  style={{ display: 'none' }}
                                  id={`reply-image-upload-${subComment.commentId}`}
                                />
                                <label htmlFor={`reply-image-upload-${subComment.commentId}`}>
                                  <Button component="span" size="small" variant="outlined">
                                    Upload Image
                                  </Button>
                                </label>
                                {commentImage && (
                                  <Typography variant="caption" sx={{ ml: 1 }}>
                                    Image selected
                                  </Typography>
                                )}
                              </Box>
                              <Box sx={{ display: 'flex', gap: 1 }}>
                                <Button size="small" onClick={() => {
                                  setReplyToComment(null);
                                  setCommentImage(null);
                                }}>
                                  Cancel
                                </Button>
                                <Button size="small" variant="contained" onClick={handleSubmitComment}>
                                  Reply
                                </Button>
                              </Box>
                            </Box>
                          </Box>
                        )}
                      </Box>
                    </Box>
                  </Box>
                ))}
              </Box>
            </Box>
          </Box>
        ))}
      </Box>
    </Box>
  );
};

CommentSection.propTypes = {
  videoId: PropTypes.string.isRequired,
  initialCommentCount: PropTypes.number,
};

export default CommentSection; 