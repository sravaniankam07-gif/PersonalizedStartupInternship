import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class PostInternshipServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"company".equals(session.getAttribute("userType"))
                || session.getAttribute("companyId") == null) {
            response.sendRedirect("company-login.html");
            return;
        }

        String title = value(request, "title");
        String domain = value(request, "domain");
        String location = value(request, "location");
        String duration = value(request, "duration");
        String stipend = value(request, "stipend");
        String skills = value(request, "skills");
        String eligibility = value(request, "eligibility");
        String link = value(request, "application_link");
        if (title.isEmpty() || domain.isEmpty() || location.isEmpty()
                || duration.isEmpty() || skills.isEmpty() || eligibility.isEmpty()
                || !stipend.matches("[0-9]+")
                || (!link.isEmpty() && !link.matches("^https?://.+"))) {
            response.sendRedirect("post_internship.html?error=validation");
            return;
        }

        String sql = "INSERT INTO internships "
                + "(company_id,title,domain,location,duration,stipend,skills,eligibility,application_link) "
                + "VALUES (?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, (String) session.getAttribute("companyId"));
            ps.setString(2, title);
            ps.setString(3, domain);
            ps.setString(4, location);
            ps.setString(5, duration);
            ps.setInt(6, Integer.parseInt(stipend));
            ps.setString(7, skills);
            ps.setString(8, eligibility);
            ps.setString(9, link.isEmpty() ? null : link);
            if (ps.executeUpdate() != 1) {
                response.sendRedirect("post_internship.html?error=failed");
                return;
            }
            response.sendRedirect("companyDashboard?status=posted");
        } catch (Exception e) {
            getServletContext().log("Internship could not be posted", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "The internship could not be posted.");
        }
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
