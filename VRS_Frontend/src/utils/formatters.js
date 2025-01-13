export const formatViewCount = (count) => {
  if (count === undefined || count === null) return '0';
  
  const num = Number(count);
  if (isNaN(num)) return '0';

  if (num >= 1000000) {
    return (num / 1000000).toFixed(1) + 'M';
  }
  if (num >= 1000) {
    return (num / 1000).toFixed(1) + 'K';
  }
  return num.toString();
}; 