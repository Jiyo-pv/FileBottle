# FileBottle



FileBottle is a Java-based Client-Server File Sharing System that enables secure multi-user file management over a network. It supports authentication, file upload/download, file operations, activity logging, and server monitoring through a modern Swing interface.



---



## Overview



FileBottle allows multiple users to connect to a central server and securely manage files. The system is built using Java Sockets, Multithreading, File I/O, and JDBC for database connectivity.



The application supports:



- Server Mode  

- Client Mode (Localhost / Custom IP)  

- Multi-user concurrent access  



---



## Features



### Authentication

- User Registration and Login  

- Secure Password Hashing  

- Change Password functionality  

- Login activity logging  



### File Management

- Upload files  

- Download files  

- Rename files  

- Move files to trash  

- Restore from trash  

- Permanent deletion  



### Multi-User Support

- Dedicated folder for each user  

- Concurrent client handling using threads  

- Connected clients counter (Server View)  



### Server Controls

- Run As Server mode  

- Displays local IP address  

- Stop Server functionality  

- Real-time connected client tracking  



### Activity Logging

- Logs for uploads, downloads, edits  

- Login activity tracking  

- Password change tracking  



---



## System Architecture



FileBottle follows a Client-Server Architecture.



### Server

- Listens on Port 5000  

- Handles multiple clients using multithreading  

- Manages file storage in a dedicated server folder  



### Client

- Connects via IP address  

- Authenticates using database  

- Sends commands (UPLOAD, DOWNLOAD, DELETE, RENAME)  



---



## Project Structure



FileBottle/
  

├── src/                  Java source code  

├── nbproject/            NetBeans configuration  

├── .gitignore  

├── README.md  



Runtime folder (auto-created when server runs):



FileBottleServer/  

&nbsp;├── user\_1/  

&nbsp;├── user\_2/  

&nbsp;├── ...  







---



## How To Run



### Run from NetBeans

1. Open project in NetBeans  

2. Click Run  

3. Choose:

&nbsp;  - Use Localhost  

&nbsp;  - Custom IP  

&nbsp;  - Run As Server  



### Run Using JAR



1. Clean \& Build the project  

2. Navigate to the dist folder  

3. Run:



java -jar FileBottle.jar



Or double-click the JAR file.



---



## Running Over Network



1. On Host Machine:

&nbsp;  - Click Run As Server  

&nbsp;  - Note the displayed IP address  



2. On Client Machines:

&nbsp;  - Choose Custom IP  

&nbsp;  - Enter the server IP  

&nbsp;  - Login or register  



---



## Security



- Passwords are hashed before storing in the database  

- Each user has an isolated folder on the server  

- File access is restricted to authenticated users  



---



## Technologies Used



- Java SE  

- Java Swing  

- Java Sockets  

- Multithreading  

- File I/O  

- JDBC  

- SQL / Oracle Database  

- NetBeans IDE  



---



## Author



Jiyo P V  



---



## License



This project is developed for educational and academic purposes.

