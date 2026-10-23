import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class LoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        String username = request.getParameter("t1");
        String password = request.getParameter("t2");
        if (username == null || username.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            response.sendRedirect("login.html?login=error");
            return;
        }

        String sql = "SELECT username FROM students WHERE username=? AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, username.trim());
            ps.setString(2, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    response.sendRedirect("login.html?login=error");
                    return;
                }
            }

            HttpSession oldSession = request.getSession(false);
            if (oldSession != null) {
                oldSession.invalidate();
            }
            HttpSession session = request.getSession(true);
            session.setAttribute("username", username.trim());
            session.setAttribute("userType", "student");
            response.sendRedirect("student_details.html");
        } catch (Exception e) {
            getServletContext().log("Student login failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Student sign in is temporarily unavailable.");
        }
    }
}
