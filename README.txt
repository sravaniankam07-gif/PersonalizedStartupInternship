Personalized Startup Internship Recommendation System

This version follows the simple HTML/CSS/JavaScript/Servlet style used in the supplied lab PDF.

Included lab topics from the PDF:
1. External CSS (css/style.css) and simple internal CSS in the frame pages.
2. Separate student/company login and registration workflows using HTML forms and JavaScript/server validation.
3. Student registration validations: phone, email, future DOB, CGPA range and password match.
4. Frames demonstration using frames.html, nav.html, f1.html and f2.html.
5. Student skills, preferred location, duration, stipend and startup domain form.
6. Java Servlet deployment structure using WEB-INF/web.xml.
7. JDBC/MySQL connectivity through DBConnection.java.

Run with XAMPP MySQL on port 3306 and Apache Tomcat 9 on port 8080.

NAVIGATION FLOW
---------------
From Home, students can sign in or create an account and then complete their
student profile before viewing recommendations. Companies can sign in or
create a separate company account, then manage startup details and post/view
internships from the company dashboard. The standalone Frames demonstration
remains available for the lab exercise but is intentionally omitted from
portal navigation.

Authentication is database-backed for this project demonstration, using the
existing MySQL tables and password columns. Passwords are stored as plain
text in the supplied schema; this implementation is not suitable for
production use without password hashing and additional security controls.

Important: tomcat-users.xml is a Tomcat configuration file and is NOT part of this project. Do not put Java servlet source code inside it.
