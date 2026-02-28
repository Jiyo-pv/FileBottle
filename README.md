\# 🍾 FileBottle – Java Client-Server File Sharing System



FileBottle is a multi-user client-server file sharing system built using Java (Swing + Sockets).  

It allows users to securely upload, download, rename, delete, and manage files over a network.



---



\## 🚀 Features



\- 🔐 User Registration \& Login

\- 🔑 Password Hashing (Secure Storage)

\- 📂 Upload / Download Files

\- ✏ Rename Files

\- 🗑 Delete Files

\- 🌐 Custom IP Server Connection

\- 🖥 Run As Server Mode

\- 👥 Multi-client Support

\- 📊 Connected Clients Counter

\- 🧾 Activity Logging (Login, Password Change)

\- 🎨 Modern Swing UI Design



---



\## 🏗 Architecture



FileBottle follows a Client-Server Architecture:



\- \*\*Server\*\*

&nbsp; - Handles file storage

&nbsp; - Manages multiple clients using threads

&nbsp; - Stores files in `FileBottleServer/` directory



\- \*\*Client\*\*

&nbsp; - Connects via IP Address

&nbsp; - Authenticates users

&nbsp; - Sends file commands (UPLOAD, DOWNLOAD, DELETE, RENAME)



---



\## 📁 Project Structure



```

FileBottle/

│

├── src/                  # Java source files

├── nbproject/            # NetBeans project config

├── .gitignore

├── README.md

```



Runtime folder (auto-created, not tracked in Git):



```

FileBottleServer/

&nbsp;├── user\_1/

&nbsp;├── user\_2/

```



---



\## ⚙️ How To Run



\### Option 1 – Run from NetBeans

1\. Open project in NetBeans

2\. Click Run

3\. Choose:

&nbsp;  - Use Localhost

&nbsp;  - Custom IP

&nbsp;  - Run As Server



---



\### Option 2 – Run Using JAR



1\. Build Project (`Clean \& Build`)

2\. Go to `dist/`

3\. Double-click `FileBottle.jar`



OR via command:



```

java -jar FileBottle.jar

```



---



\## 🌐 Using Over Network



1\. On one machine:

&nbsp;  - Click \*\*Run As Server\*\*

&nbsp;  - Note the displayed IP address



2\. On other machines:

&nbsp;  - Choose \*\*Custom IP\*\*

&nbsp;  - Enter server IP



---



\## 🔐 Security



\- Passwords are hashed before storing in database.

\- Each user has a separate folder on the server.

\- Files are isolated per user.



---



\## 🛠 Technologies Used



\- Java SE

\- Java Swing (UI)

\- Java Sockets

\- Multithreading

\- File I/O

\- JDBC (Database Integration)

\- NetBeans IDE



---



\## 📦 Deployment Notes



\- `FileBottleServer/` is auto-generated.

\- Runtime folders are ignored via `.gitignore`.

\- Works when launched via JAR from any directory.



---



\## 🎓 Academic Purpose



This project was developed as a client-server system to demonstrate:



\- Socket programming

\- Multithreading

\- File handling

\- Authentication systems

\- Secure password storage

\- UI design using Swing



---



\## 👤 Author



\*\*Jiyo P V\*\*



---



\## 📜 License



This project is for educational purposes.

