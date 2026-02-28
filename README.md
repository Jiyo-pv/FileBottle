\# 🍾 FileBottle



FileBottle is a Java-based Client-Server File Sharing System that enables secure multi-user file management over a network.  

It supports authentication, file operations, sharing permissions, and activity logging through a modern Swing interface.



---



\## 🚀 Overview



FileBottle allows multiple users to connect to a central server and perform file operations securely.  

The system is built using Java Sockets, Multithreading, File I/O, and JDBC for database integration.



The application supports both:

\- \*\*Server Mode\*\*

\- \*\*Client Mode (Localhost / Custom IP)\*\*



---



\## ✨ Features



\### 🔐 Authentication

\- User Registration \& Login

\- Secure Password Hashing

\- Change Password

\- Login Activity Logging



\### 📂 File Management

\- Upload Files

\- Download Files

\- Rename Files

\- Move to Trash

\- Permanent Delete

\- Restore from Trash



\### 👥 Multi-User Support

\- Dedicated folder per user

\- Simultaneous client connections

\- Connected clients counter (Server View)



\### 🖥 Server Control

\- Run As Server mode

\- Displays local IP address

\- Stop Server button

\- Real-time connected client count



\### 📊 Activity Logging

\- File upload/download/edit logs

\- Login logs

\- Password change logs

\- Share activity tracking



\### 🎨 UI

\- Modern Swing interface

\- Styled buttons and panels

\- Clean dashboard layout



---



\## 🏗 System Architecture



FileBottle follows a \*\*Client-Server Architecture\*\*.



\### Server

\- Listens on Port 5000

\- Handles multiple clients using threads

\- Manages file storage

\- Maintains connected client count



\### Client

\- Connects via IP address

\- Sends commands (UPLOAD, DOWNLOAD, DELETE, RENAME)

\- Authenticates using database



---



\## 📁 Project Structure



```

FileBottle/

│

├── src/                    # Java source files

├── nbproject/              # NetBeans configuration

├── .gitignore

├── README.md

```



\### Runtime Folder (Auto-Generated)



This folder is created automatically when server runs:



```

FileBottleServer/

&nbsp;├── user\_1/

&nbsp;├── user\_2/

&nbsp;├── ...

```



⚠ This folder is ignored in Git (`.gitignore`).



---



\## ⚙️ How To Run



\### Option 1: Run in NetBeans

1\. Open project

2\. Click Run

3\. Select:

&nbsp;  - Use Localhost

&nbsp;  - Custom IP

&nbsp;  - Run As Server



---



\### Option 2: Run Using JAR



1\. Clean \& Build project

2\. Go to `dist/`

3\. Double-click `FileBottle.jar`



OR via terminal:



```

java -jar FileBottle.jar

```



---



\## 🌐 Running Over Network



1️⃣ On Host Machine:

\- Click \*\*Run As Server\*\*

\- Note the displayed IP address



2️⃣ On Client Machines:

\- Choose \*\*Custom IP\*\*

\- Enter server IP

\- Login/Register



---



\## 🔒 Security



\- Passwords are hashed before storing in database.

\- Users have isolated folders on server.

\- File access is controlled per user.

\- Server root is dynamically resolved to JAR location (deployment-safe).



---



\## 🛠 Technologies Used



\- Java SE

\- Java Swing (UI)

\- Java Sockets

\- Multithreading

\- File I/O

\- JDBC

\- Oracle / SQL Database

\- NetBeans IDE



---



\## 📌 Deployment Notes



\- The server storage folder is created automatically next to the JAR.

\- Works when launched via double-click.

\- Can be added to system PATH for command-line launching.

\- Compatible with Windows systems running Java.



---



\## 🎓 Academic Scope



This project demonstrates:



\- Socket Programming

\- Multi-threading

\- Database Connectivity (JDBC)

\- File Handling

\- Authentication Systems

\- Password Hashing

\- Client-Server Architecture

\- GUI Development using Swing



---



\## 👤 Author



\*\*Jiyo P V\*\*



---



\## 📜 License



This project is developed for educational and academic purposes.

