family_tree
=========================================

Authors :
-------------------
Created by:

 * Marco De Toma - [Github](https://github.com/detomarco)
 * Alessio Di Giacomo - [Github](https://github.com/VFansss)
 * Gianluca Filippone - [Github](https://github.com/Gianlufil)

What is :
-------------------
The objective of family_tree project is to realize a complete working
website about a **collaborative genealogy**.

Everyone can signup on the site inserting their personal registry information.
Then user can submit piece of his/her genealogical tree (the relative) i.e.

* Parents
* Siblings
* Child

Or search if it's relative it's already registered using a common searchbar.

An *unregistered* user can however view registered user inside the system from
the login page.

When an user send a *request* to relative attachment to it's genealogical tree
that invite must be accepted by the other user from his profile.

Every user can *edit our registry information* from a private dashboard.
From here user can edit it's information, add a biography or upload an image.

How it's done :
-------------------
The website it's completely usable from mobile device ( = responsive ) .
Also, it's completely usable **out-the-box**.

The entire system is built around few components:

* Internal logic done with **Java Servlet** and **Java Library**
* **SQL** Database hosted by Apache MySQL
* Template generation done by **Freemarker**
* Heavy presence of **CSS3** for the graphical interface part
* Project done using **Net Beans IDE 8**

Deploy instructions :
-------------------

'**family_tree**' require an active MySQL server running on the machine.
A working dump of the database 'collaborative_genealogy' is provided inside the
project folder.

Then anyone can simply try the website opening the project with *Net Beans IDE* and run it having *Tomcat Module* installed.

:tada:

License :
-------------------
Copyright 2015 retained by authors of the project.
