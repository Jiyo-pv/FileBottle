FileBottle



FileBottle is a Java-based Client-Server File Sharing System that enables secure multi-user file management over a network. It supports authentication, file upload/download, file operations, activity logging, and server monitoring through a modern Swing interface.



Overview



FileBottle allows multiple users to connect to a central server and securely manage files. The system is built using Java Sockets, Multithreading, File I/O, and JDBC for database connectivity.



The application supports:



Server Mode



Client Mode (Localhost / Custom IP)



Multi-user concurrent access



Features

Authentication



User Registration and Login



Secure Password Hashing



Change Password functionality



Login activity logging



File Management



Upload files



Download files



Rename files



Move files to trash



Restore from trash



Permanent deletion



Multi-User Support



Dedicated folder for each user



Concurrent client handling using threads



Connected clients counter (Server View)



Server Controls



Run As Server mode



Displays local IP address



Stop Server functionality



Real-time connected client tracking



Activity Logging



Logs for uploads, downloads, edits



Login activity tracking



Password change tracking



How To Run

Run from NetBeans



Open project in NetBeans



Click Run



Choose:



Use Localhost



Custom IP



Run As Server



Run Using JAR



Clean \& Build the project



Navigate to the dist folder



Run:



java -jar FileBottle.jar



Technologies Used



Java SE



Java Swing



Java Sockets



Multithreading



File I/O



JDBC



SQL / Oracle Database



NetBeans IDE



Author



Jiyo P V

