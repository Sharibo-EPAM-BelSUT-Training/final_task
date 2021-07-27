# final_task
This is a final task in **[EPAM BelSUT Training](https://github.com/Sharibo-EPAM-BelSUT-Training).**

This is a todo list website written in **javaEE** according to a old standard without annotations. As a DBMS, a **MySQL 8** was used, the connection with which was implemented via **JDBC**. The frontend is written in **html5**, pure **css** and **javascript** using the **jquery library**. Jquery helped me a lot in implementing ajax-requests to the server. *I have not used JSP, sorry.* Information between the client-side and the server-side is transmitted in **JSON** format. To work with the format on the server side, I used the **json-simple library**. Uploading files from the client to the server is implemented using the **Apache Commons FileUpload package**.

The project haves logging on the server side using the **log4j library**, on the client side using **javascript-logging**.

It was a very difficult experience for me. I absolutely did not know javaEE, javascript, html, css, sql at that time. *All I had was a cursory understanding of how websites work.* A month was allocated for this work, and this work was also independent, that is I really did not have any help from the instructor. During this month, I had to very quickly deal with the listed technologies. At first, nothing worked for me, because I had no understanding of how to write in order for it to work. Some of the code is written roughly. I caught a lot of weird bugs. I found most of them using debuggers. And most of the errors were in the transfer of data between the server and the client.
*\*The minute of complaints is over.\**

## How it works
The website consists of 3 html-pages: [registration, login and the todo list itself](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/tree/master/src/main/webapp). Accordingly, these include [javascript files](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/tree/master/src/main/webapp/js) and [css](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/tree/master/src/main/webapp/css) with the same names.

The backend is categorized into:
- [database connector](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/tree/master/src/main/java/gmail/alexejkrawez/db)
- [model](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/tree/master/src/main/java/gmail/alexejkrawez/app/model): classes-descriptions of users and their notes;
- [entites](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/tree/master/src/main/java/gmail/alexejkrawez/app/entities): classes with calls to the database based on classes-descriptions;
- [servlets](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/tree/master/src/main/java/gmail/alexejkrawez/app/servlets):
  - to check the registration data and to register a new user in the database (applies to registration.html);
  - to check the entered login, create a new session with user and get his notes (applies to login.html);
  - for a todo list in a separate folder (applies to todo.html);

When accessing the database, an instance of the connection to the database is issued. The connection is implemented via a **lazy singleton.** If there is no connection, this connection will be created: the driver will be registered, the connection will be created, a query will be made about the existence of the database. If the database exists, then a request is made to use this database. Otherwise, sql queries are executed to completely create the database.

The base contains two tables: users and notes, linked to each other:

**users:**
- user_id
- email
- login
- password *(no encryption)*

**notes:**
- user_id *(FOREIGN KEY)*
- id
- date *(date of creation)*
- target_date *(needed to translate tomorrow's records into today's ones, when the **date** becomes > **target_dates**)*
- note
- file_path
- status

*P.S. note-column contain notes of all users at once, no table is created for each user (I understand that this is not good).*

#### registration.html
Length restrictions are set for email, login and password. Tracking compliance with these restrictions occurs on the **registration.html** page using ajax-requests of the javascript, as well as when the form is submitted again in the [Registration.java](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/blob/master/src/main/java/gmail/alexejkrawez/app/servlets/Registration.java) servlet.

#### login.html

After successful registration, it will be set in javascript the transition to **login.html**. Upon successful entry into the [Login.java](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/blob/master/src/main/java/gmail/alexejkrawez/app/servlets/Login.java) servlet, a session will be created for each user, which contains the user's id, a list of his notes. That is all records corresponding to the user's ID are retrieved from the database. If there are no such records, *an empty list is added to the session.*

#### todo.html

Further in the javascript there is a transition to **todo.html**.
Immediately upon displaying the todo.html page, the javascript requests the state of the user's session. The session lifetime is set in the [web.xml](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/blob/master/src/main/webapp/WEB-INF/web.xml) file — 12 hours. Next, the javascript requests a list of user notes and, substituting the values that came from the database into the containers, writes them into arrays (yesterday, today, tomorrow, deleted) and displays them on the screen at certain positions according to their status. The status column in the exercise book table has the following meanings:
1. today.
2. tomorrow.
3. yesterday.
4. in the trash can.
5. executed *(exists only in the database, never queried from the database again).*

Arrays in javascript are needed to memorize notes when switching between yesterday-today-tomorrow columns and viewing the trash can. This eliminates the need to make a request to the server every time you switch. In general, arrays in javascript are a sorted version of a list of all user's notes in a session on the backend side.

When creating a note on a certain day, deleting a note, emptying the trash, restoring a note from the trash bin, marking it as executed, the javascript sends ajax-requests to certain servlets. Servlets make a request to the database and return the response to the javascript on the client side.

When a note is created by javascript, a ```formdata``` object is formed for the possibility of simultaneous sending of a text and a file, if it has been added. On the server in [CreateNote.java](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/blob/master/src/main/java/gmail/alexejkrawez/app/servlets/todo/CreateNote.java) servlet the file, after parsing JSON, is written to the usersFiles directory in the application root, to the subdirectory with the user_id number. That is, the files of different users are separated from each other. A random number in the range from 1000 to 9999 is added to the file name. This is done to avoid overwriting files with the same name, but different content. When rendering on the screen, javascript removes the numbers at the beginning of the name using a regular expression, but when downloading from the server, the file will be given *"as is"* with a number at the beginning of the name.

The "Log Out" button is associated with the [CloseSession.java](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/blob/master/src/main/java/gmail/alexejkrawez/app/servlets/todo/CloseSession.java) servlet. The servlet executes ```session.invalidate()``` and reports the operation status to the javascript. If the answer is successful, the javascript redirects to the **login.html** page.

## Usage
This is a standard JavaEE application that can be run on any server that supports the JavaEE standard (e.g. Tomcat).
The application establishes a connection to the database on the **standard port 3306** according to the data:
- user: deus
- password: exmachina

#### Step by step:
1. It is required to ensure the presence of the user **deus** in the MySQL 8.
2. You should specify the server [**.war**-archive](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/releases) for its deployment.
3. With standard settings, the application will be available at: ```server-name/final_task/```

You can change the settings for connecting to the database in the [DBConnector.java](https://github.com/Sharibo-EPAM-BelSUT-Training/final_task/blob/master/src/main/java/gmail/alexejkrawez/db/DBConnector.java) servlet, fields:
- URL
- USERNAME
- PASSWORD
- DATABASE_NAME
