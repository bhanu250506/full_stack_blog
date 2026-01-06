**HiveBlog** is a modern, full-stack social blogging platform designed for developers and creators. It features rich content creation, community groups, and a robust real-time communication system.

Built with a **Spring Boot** backend and a **React (Vite)** frontend, it utilizes **WebSockets (STOMP)** for instant messaging and notifications.

---

## 🚀 Key Features

### ✍️ Content & Social
* **Rich Blogging:** Create, edit, and delete posts with multimedia support (Images/Videos via Cloudinary).
* **Engagement:** Like, comment, and share functionality.
* **Groups/Communities:** Create and join topic-specific groups.
* **User Profiles:** public profiles with author bio and post history.

### 💬 Real-Time Communication
* **Instant Chat:** 1-on-1 private messaging powered by WebSockets.
* **Live Notifications:** Real-time alerts for likes, comments, and messages (Bell Icon).
* **Presence:** Sidebar discovery showing recent chat partners.
* **Persistence:** Chat history is saved and loads instantly upon login.

### 🛡️ Security
* **JWT Authentication:** Stateless security using JSON Web Tokens.
* **Role-Based Access:** Secure endpoints for admins and users.
* **Validation:** Robust backend validation for data integrity.

---

## 🛠️ Tech Stack

### Frontend
* **Framework:** React 18 (Vite)
* **Styling:** Tailwind CSS
* **Icons:** Lucide React
* **State Management:** React Context API
* **Real-time:** `@stomp/stompjs` & `sockjs-client`
* **HTTP Client:** Axios

### Backend
* **Framework:** Spring Boot 3+
* **Database:** MySQL
* **ORM:** Hibernate / Spring Data JPA
* **Security:** Spring Security & JJWT
* **Real-time:** Spring WebSocket (STOMP Broker)
* **Storage:** Cloudinary API (for media)

---

## 🏗️ Architecture

The application follows a standard MVC architecture with a decoupled frontend and backend.



**Database Schema:**
The database handles relations between Users, Posts, Comments, Groups, and ChatMessages.


---

cloudinary.cloud-name=your_cloud_name
<img width="1919" height="943" alt="Screenshot 2026-01-04 103816" src="https://github.com/user-attachments/assets/47154b0d-9d79-4334-8288-40da7f8c7c35" />
<img width="1917" height="642" alt="Screenshot 2026-01-04 104348" src="https://github.com/user-attachments/assets/4934cb42-468f-47d0-a49a-51434c6bcc87" />
<img width="945" height="861" alt="Screenshot 2026-01-04 110455" src="https://github.com/user-attachments/assets/d06f40af-5701-4884-a706-01f01f885d65" />
<img width="1856" height="848" alt="Screenshot 2026-01-04 110525" src="https://github.com/user-attachments/assets/96538675-f1c2-45a1-b1d3-2a186be26cf1" />
<img width="1901" height="867" alt="Screenshot 2026-01-04 120220" src="https://github.com/user-attachments/assets/821a0727-26ac-4560-b757-36bff19cd1b3" />
<img width="1884" height="860" alt="Screenshot 2026-01-04 120232" src="https://github.com/user-attachments/assets/d30dac05-b8e9-4fa4-ac4b-73fd8677376e" />

## ⚙️ Installation & Setup

### Prerequisites
* Node.js (v18+)
* Java JDK 17+
* MySQL Server
* Maven

### 1. Backend Setup (Spring Boot)

1.  Clone the repository and navigate to the backend folder.
2.  Create a MySQL database named `hiveblog_db`.
3.  Configure `src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:mysql://localhost:3306/hiveblog_db
spring.datasource.username=root
spring.datasource.password=your_password

# JWT Secret
application.security.jwt.secret-key=YOUR_VERY_LONG_SECRET_KEY_HERE

# Cloudinary (For Image Uploads)


cloudinary.api-key=your_api_key
cloudinary.api-secret=your_api_secret
