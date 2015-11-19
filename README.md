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
The goal of family_tree project is to build a complete working
website about a social-oriented **genealogical tree**.

This website, called **Collaborative Genealogy**, represents a social way to
build up your own genealogical tree, contributing at the same time to grow
your relatives' one.

Everyone can signup inserting their personal registry information.

Signed users can add a piece of their genealogical tree, i.e.

* Parents
* Siblings
* Child

...or search if there is a relative which is already a member using a common searchbar
and sending him a request to join to his genealogical tree.

When an user sends a *request* to relative attachment to his genealogical tree,
that invite has to be accepted by the other user.

If a relative can't sign up to the website, it is possible to create a basic non-verified
profile and add it to a genealogical tree.

Once you built your tree, you can navigate through it and reach each of your relatives
that joined to Collaborative Genealogy.

Every user can *edit his registry information* from a private dashboard.
From here user can edit his information, add a biography or upload an avatar.

An *unregistered* user can however search for registered users from the main page.

How it's done :
-------------------
The website is completely available from mobile device ( we made it responsive ) .
Also, it's completely available **out-the-box**.

The entire system is built around few components:

* Internal logic done with **Java Servlet** and **Java Library**
* **SQL** Database hosted by Apache MySQL
* Template generation done by **Freemarker**
* Heavy presence of **CSS3** for the graphical interface part
* **Bootstrap** CSS library

Project developed using **Net Beans IDE 8** and **Apache Tomcat**

Deploy instructions :
-------------------
'**family_tree**' requires an active MySQL server running on the machine.
A working dump of the database 'collaborative_genealogy' is provided into the
project folder.

Then anyone can simply try the website opening the project with *Net Beans IDE*
and run it having *Tomcat Module* installed.

:tada: :tada:

License :
-------------------
Copyright 2015 retained by authors of the project.
