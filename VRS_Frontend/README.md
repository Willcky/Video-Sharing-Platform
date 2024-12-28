# wiliwili

A modern Online Video Platform built with React.js, featuring video playback, search functionality, video Recommendation.

## 🚀 Features

- Video playback with React Player
- Search functionality
- User authentication
- Responsive design
- Video recommendations
- Like/dislike functionality
- Comments system
- User profiles

## 🛠️ Technologies

- React.js 18
- Redux Toolkit
- Material-UI
- React Router v6
- Axios
- React Player
- ESLint & Prettier
- Husky

## 📦 Installation

1. Clone the repository:

```bash
git clone https://github.com/yourusername/youtube-clone.git
cd youtube-clone
```

2. Install dependencies:

```bash
npm install
```

3. Create a `.env` file in the root directory:

```bash
REACT_APP_API_BASE_URL=your_api_url_here
```

4. Start the development server:

```bash
npm start
```

## 🏗️ Project Structure

```
youtube-clone/
├── src/
│   ├── components/     # Reusable components
│   ├── services/       # API services
│   ├── store/         # Redux store configuration
│   ├── utils/         # Utility functions
│   ├── hooks/         # Custom React hooks
│   ├── context/       # React context
│   ├── assets/        # Static assets
│   └── pages/         # Page components
```

## 🔧 Configuration

### ESLint
The project uses ESLint for code linting. Configuration can be found in `.eslintrc.js`.

### Prettier
Code formatting is handled by Prettier. Configuration can be found in `.prettierrc`.

### Husky
Pre-commit hooks are configured using Husky to ensure code quality.

## 🚥 API Integration

The application uses RESTful APIs for backend integration. Main endpoints:

- `/videos` - Get video listings
- `/videos/:id` - Get video details
- `/videos/search` - Search videos
- `/auth/login` - User authentication
- `/auth/register` - User registration

## 🔒 Environment Variables

Required environment variables:

```
REACT_APP_API_BASE_URL=your_api_url
```

## 📝 Scripts

- `npm start` - Start development server
- `npm build` - Build for production
- `npm test` - Run tests
- `npm run lint` - Run ESLint
- `npm run format` - Format code with Prettier

## 🚀 Deployment

1. Build the project:

```bash
npm run build
```

2. The build folder will contain production-ready files.

3. Deploy to your preferred hosting service (Netlify, Vercel, etc.).

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch
3. Commit changes
4. Push to the branch
5. Open a pull request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- K.C - Initial work

## 🙏 Acknowledgments

- YouTube for inspiration
- React.js community
- Material-UI team