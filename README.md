# Blog Platform

A modern, full-featured blog platform built with Spring Boot, featuring user authentication, post management, commenting system, likes, and social following capabilities.

## 🚀 Features

- **User Authentication**: JWT-based secure authentication with registration and login
- **Post Management**: Create, read, update, and delete blog posts
- **Comment System**: Nested commenting on posts with like functionality
- **Social Features**: Follow/unfollow users, view followers and followings
- **Like System**: Like posts and comments with toggle functionality
- **Role-Based Access**: Admin and User roles
- **Hybrid Database**: MongoDB for user/post data, PostgreSQL for relational data (comments, likes, follows)

## 🛠️ Tech Stack

- **Framework**: Spring Boot 3.5.14
- **Language**: Java 17
- **Databases**:
  - MongoDB (Users, Posts)
  - PostgreSQL (Comments, Likes, Follows)
- **Security**: Spring Security with JWT (JJWT 0.12.5)
- **Build Tool**: Maven
- **Libraries**: Lombok, Spring Data JPA, Spring Data MongoDB

## 📋 Prerequisites

- Java 17 or higher
- Maven 3.6+
- MongoDB 4.4+
- PostgreSQL 12+

## 🔧 Setup Instructions

### 1. Clone the Repository

```bash
git clone <repository-url>
cd Blog-Platform
```

### 2. Configure Environment Variables

Create a `.env` file in the project root with the following variables:

```env
MONGODB_URI=mongodb://localhost:27017
DB_URL=jdbc:postgresql://localhost:5432/blogplatform
DB_USERNAME=your_db_username
DB_PASSWORD=your_db_password
PORT=8080
JWT_SECRET=your_secret_key_at_least_256_bits_long
```

### 3. Create PostgreSQL Database

```sql
CREATE DATABASE blogplatform;
```

### 4. Build and Run

```bash
# Using Maven wrapper
./mvnw clean install
./mvnw spring-boot:run

# Or using Maven directly
mvn clean install
mvn spring-boot:run
```

The application will start on `http://localhost:3000`

## 📊 Database Schema

### MongoDB Collections

#### Users Collection
```json
{
  "_id": "string",
  "userName": "string (unique)",
  "email": "string (unique)",
  "password": "string (hashed)",
  "role": "USER | ADMIN",
  "follower": "number",
  "following": "number"
}
```

#### Posts Collection
```json
{
  "_id": "string",
  "userName": "string",
  "imageUrl": "string (optional)",
  "caption": "string (optional)",
  "likeCount": "number",
  "createdAt": "ISODate"
}
```

### PostgreSQL Tables

#### Comments Table
```sql
CREATE TABLE comments (
  id SERIAL PRIMARY KEY,
  targetId VARCHAR NOT NULL,
  targetType VARCHAR NOT NULL, -- 'POST' or 'COMMENT'
  userName VARCHAR NOT NULL,
  content VARCHAR NOT NULL,
  likeCount BIGINT DEFAULT 0
);
```

#### Likes Table
```sql
CREATE TABLE likes (
  id SERIAL PRIMARY KEY,
  targetId VARCHAR NOT NULL,
  targetType VARCHAR NOT NULL, -- 'POST' or 'COMMENT'
  userName VARCHAR NOT NULL,
  UNIQUE(targetId, targetType, userName)
);
```

#### Follows Table
```sql
CREATE TABLE follows (
  id SERIAL PRIMARY KEY,
  followerUserName VARCHAR NOT NULL,
  followingUserName VARCHAR NOT NULL,
  UNIQUE(followerUserName, followingUserName)
);
```

## 🔐 API Documentation

### Authentication

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer <your-jwt-token>
```

---

### Public Endpoints

#### Health Check
```http
GET /public/health-check
```

**Response**: `OK`

#### Register User
```http
POST /public/register
Content-Type: application/json

{
  "userName": "johndoe",
  "email": "john@example.com",
  "password": "securePassword123"
}
```

**Response** (201 Created):
```json
"<jwt-token>"
```

#### Login
```http
POST /public/login
Content-Type: application/json

{
  "userName": "johndoe",
  "password": "securePassword123"
}
```

**Response** (200 OK):
```json
"<jwt-token>"
```

---

### User Endpoints (Protected)

#### Get Current User
```http
GET /user
Authorization: Bearer <token>
```

**Response** (200 OK):
```json
{
  "id": "string",
  "userName": "johndoe",
  "email": "john@example.com",
  "role": "USER",
  "follower": 10,
  "following": 5
}
```

#### Update User
```http
PUT /user/update
Authorization: Bearer <token>
Content-Type: application/json

{
  "email": "newemail@example.com",
  "password": "newPassword123"
}
```

**Response** (200 OK):
```json
{
  "id": "string",
  "userName": "johndoe",
  "email": "newemail@example.com",
  "role": "USER",
  "follower": 10,
  "following": 5
}
```

#### Delete User
```http
DELETE /user/delete
Authorization: Bearer <token>
```

**Response** (204 No Content)

#### Get Followers
```http
GET /user/followers
Authorization: Bearer <token>
```

**Response** (200 OK):
```json
["user1", "user2", "user3"]
```

#### Get Following
```http
GET /user/followings
Authorization: Bearer <token>
```

**Response** (200 OK):
```json
["user1", "user2", "user3"]
```

#### Follow/Unfollow User
```http
POST /user/follow/{followingUserName}
Authorization: Bearer <token>
```

**Response** (201 Created):
```json
"Followed"
```

**Response** (200 OK - if unfollowing):
```json
"Unfollowed"
```

---

### Post Endpoints (Protected)

#### Create Post
```http
POST /post/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "imageUrl": "https://example.com/image.jpg",
  "caption": "This is my first post!"
}
```

**Response** (201 Created):
```json
{
  "id": "string",
  "userName": "johndoe",
  "imageUrl": "https://example.com/image.jpg",
  "caption": "This is my first post!",
  "likeCount": 0,
  "createdAt": "2024-01-01T12:00:00"
}
```

#### Get Post
```http
GET /post/{postId}
Authorization: Bearer <token>
```

**Response** (200 OK):
```json
{
  "id": "string",
  "userName": "johndoe",
  "imageUrl": "https://example.com/image.jpg",
  "caption": "This is my first post!",
  "likeCount": 10,
  "createdAt": "2024-01-01T12:00:00"
}
```

#### Update Post
```http
PUT /post/{postId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "caption": "Updated caption",
  "imageUrl": "https://example.com/new-image.jpg"
}
```

**Response** (200 OK):
```json
{
  "id": "string",
  "userName": "johndoe",
  "imageUrl": "https://example.com/new-image.jpg",
  "caption": "Updated caption",
  "likeCount": 10,
  "createdAt": "2024-01-01T12:00:00"
}
```

#### Delete Post
```http
DELETE /post/{postId}
Authorization: Bearer <token>
```

**Response** (200 OK)

#### Get Post Likes
```http
GET /post/{postId}/likes
Authorization: Bearer <token>
```

**Response** (200 OK):
```json
["user1", "user2", "user3"]
```

#### Like/Unlike Post
```http
POST /post/{postId}/like
Authorization: Bearer <token>
```

**Response** (201 Created):
```json
"Liked the Post"
```

**Response** (200 OK - if unliking):
```json
"DisLiked the Post"
```

---

### Comment Endpoints (Protected)

#### Create Comment
```http
POST /comment/{targetId}/create
Authorization: Bearer <token>
Content-Type: application/json

{
  "targetType": "POST",
  "content": "Great post!"
}
```

**Response** (201 Created):
```json
{
  "id": 1,
  "targetId": "post123",
  "targetType": "POST",
  "userName": "johndoe",
  "content": "Great post!",
  "likeCount": 0
}
```

#### Get All Comments for Target
```http
GET /comment/{targetType}/{targetId}
Authorization: Bearer <token>
```

**Parameters**:
- `targetType`: `POST` or `COMMENT`
- `targetId`: ID of the post or comment

**Response** (200 OK):
```json
[
  {
    "id": 1,
    "targetId": "post123",
    "targetType": "POST",
    "userName": "user1",
    "content": "Great post!",
    "likeCount": 5
  },
  {
    "id": 2,
    "targetId": "post123",
    "targetType": "POST",
    "userName": "user2",
    "content": "I agree!",
    "likeCount": 3
  }
]
```

#### Get Single Comment
```http
GET /comment/{commentId}
Authorization: Bearer <token>
```

**Response** (200 OK):
```json
{
  "id": 1,
  "targetId": "post123",
  "targetType": "POST",
  "userName": "johndoe",
  "content": "Great post!",
  "likeCount": 5
}
```

#### Update Comment
```http
PUT /comment/{commentId}
Authorization: Bearer <token>
Content-Type: application/json

{
  "content": "Updated comment"
}
```

**Response** (200 OK):
```json
{
  "id": 1,
  "targetId": "post123",
  "targetType": "POST",
  "userName": "johndoe",
  "content": "Updated comment",
  "likeCount": 5
}
```

#### Delete Comment
```http
DELETE /comment/{commentId}
Authorization: Bearer <token>
```

**Response** (200 OK)

#### Get Comment Likes
```http
GET /comment/{commentId}/likes
Authorization: Bearer <token>
```

**Response** (200 OK):
```json
["user1", "user2", "user3"]
```

#### Like/Unlike Comment
```http
POST /comment/{commentId}/like
Authorization: Bearer <token>
```

**Response** (201 Created):
```json
"Liked the Comment"
```

**Response** (200 OK - if unliking):
```json
"DisLiked the Comment"
```

---

## 🔒 Security Configuration

- **JWT Token Expiration**: Configurable via application properties
- **Password Encryption**: BCrypt hashing
- **CSRF Protection**: Disabled for stateless API
- **Session Management**: Stateless (JWT-based)

## 📝 Error Responses

All endpoints may return the following error responses:

**400 Bad Request**
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 400,
  "error": "Bad Request",
  "message": "Error message details"
}
```

**401 Unauthorized**
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 401,
  "error": "Unauthorized",
  "message": "Full authentication is required to access this resource"
}
```

**404 Not Found**
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 404,
  "error": "Not Found",
  "message": "Resource not found"
}
```

**500 Internal Server Error**
```json
{
  "timestamp": "2024-01-01T12:00:00",
  "status": 500,
  "error": "Internal Server Error",
  "message": "An unexpected error occurred"
}
```

## 🧪 Testing

Run the test suite:

```bash
./mvnw test
```

## 📦 Project Structure

```
Blog-Platform/
├── src/
│   ├── main/
│   │   ├── java/com/learning/blogPlatform/
│   │   │   ├── config/           # Security configuration
│   │   │   ├── controllers/      # REST controllers
│   │   │   ├── entities/         # Data models
│   │   │   ├── enums/           # Enumerations
│   │   │   ├── filters/         # JWT filter
│   │   │   ├── repositories/    # Data access layer
│   │   │   ├── services/        # Business logic
│   │   │   └── utils/           # Utility classes
│   │   └── resources/
│   │       └── application.yaml # Configuration
│   └── test/
├── .env                         # Environment variables
├── .gitignore
├── pom.xml                      # Maven dependencies
└── README.md
```

## 🤝 Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feature/amazing-feature`)
3. Commit your changes (`git commit -m 'Add amazing feature'`)
4. Push to the branch (`git push origin feature/amazing-feature`)
5. Open a Pull Request

## 📄 License

This project is licensed under the MIT License.

## 👥 Authors

- Taha Balapurwala - Initial work

## 🙏 Acknowledgments

- Spring Boot team for the amazing framework
- MongoDB and PostgreSQL communities
