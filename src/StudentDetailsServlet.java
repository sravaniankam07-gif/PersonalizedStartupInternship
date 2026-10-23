import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StudentDetailsServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"student".equals(session.getAttribute("userType"))
                || session.getAttribute("username") == null) {
            response.sendRedirect("login.html");
            return;
        }

        String[] skills = request.getParameterValues("skills");
        String[] locations = request.getParameterValues("location");
        String[] domains = request.getParameterValues("domain");
        String duration = value(request, "duration");
        String experience = value(request, "experience");
        String stipend = value(request, "stipend");

        if (skills == null || skills.length == 0 || locations == null || locations.length == 0
                || domains == null || domains.length == 0
                || !("2 months".equals(duration) || "3 months".equals(duration)
                     || "6 months".equals(duration))
                || !experience.matches("[0-9]+") || !stipend.matches("[0-9]+")) {
            response.sendRedirect("student_details.html?profile=invalid");
            return;
        }

        if (experience.length() > 2 || stipend.length() > 6) {
            response.sendRedirect("student_details.html?profile=invalid");
            return;
        }
        int years = Integer.parseInt(experience);
        int expectedStipend = Integer.parseInt(stipend);
        if (years > 20 || expectedStipend < 10000 || expectedStipend > 100000) {
            response.sendRedirect("student_details.html?profile=invalid");
            return;
        }

        String sql = "UPDATE students SET skills=?, experience=?, "
                + "preferred_location=?, preferred_duration=?, "
                + "expected_stipend=?, preferred_domain=? WHERE username=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, String.join(", ", skills));
            ps.setString(2, experience);
            ps.setString(3, String.join(", ", locations));
            ps.setString(4, duration);
            ps.setString(5, stipend);
            ps.setString(6, String.join(", ", domains));
            ps.setString(7, (String) session.getAttribute("username"));
            ps.executeUpdate();
            response.sendRedirect("recommendations");
        } catch (Exception e) {
            getServletContext().log("Student profile could not be saved", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Your profile could not be saved.");
        }
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
