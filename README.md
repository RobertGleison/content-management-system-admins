# Netflix-like CMS (Content Management System)

A content management system for administrators to manage streaming content. Built with Android (Java).


## Key Features

### Content Management
- Upload movies and video content with metadata
- Add custom thumbnails for content preview
- Delete existing content
- Browse and filter content library
- Search functionality by title and genre

### User Management
- Admin-only access through secure authentication
- View and manage user accounts
- Monitor user activity

### Security
- Firebase Authentication integration
- Token-based API security
- Secure media storage handling

### Technical Features
- Responsive media handling
- Image compression and optimization
- Efficient content loading with pagination
- Real-time content updates

## Technologies Used

- Android (Java) 
- Firebase for handle authentication
- Retrofit for API communication
- Glide for image loading
- OkHttp for network operations

## Security Notes

- Access restricted to authenticated administrators only, managed by Firebase
- Automatic token refresh mechanism
- Secure media upload handling
- Protected API endpoints using bearer tokens

## Requirements

- Android Studio
- Android SDK version 21 or higher
- Firebase project configuration
- Backend API access [CMS-Backend](https://github.com/RobertGleison/content-management-system-server)
