import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class RecommendationServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
                         throws ServletException, IOException {

        HttpSession session = request.getSession();
        String username = (String) session.getAttribute("username");

        if (username == null || !"student".equals(session.getAttribute("userType"))) {
            response.sendRedirect("login.html");
            return;
        }

        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();

        try {
            Connection con = DBConnection.getConnection();

            String profileSql = "SELECT skills, preferred_location, preferred_duration, "
                    + "preferred_domain, experience FROM students WHERE username=?";
            PreparedStatement profileStatement = con.prepareStatement(profileSql);
            profileStatement.setString(1, username);
            ResultSet profile = profileStatement.executeQuery();
            boolean profileComplete = profile.next()
                    && hasValue(profile.getString("skills"))
                    && hasValue(profile.getString("preferred_location"))
                    && hasValue(profile.getString("preferred_duration"))
                    && hasValue(profile.getString("preferred_domain"))
                    && hasValue(profile.getString("experience"));
            profile.close();
            profileStatement.close();
            if (!profileComplete) {
                con.close();
                response.sendRedirect("student_details.html?profile=incomplete");
                return;
            }

            String sql = "SELECT i.*, s.company_name FROM internships i " +
                "JOIN startups s ON i.company_id=s.company_id " +
                "JOIN students st ON st.username=? " +
                "WHERE st.preferred_domain LIKE CONCAT('%',i.domain,'%') " +
                "OR st.preferred_location LIKE CONCAT('%',i.location,'%')";

            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);

            ResultSet rs = ps.executeQuery();

            out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
            out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
            out.println("<title>Recommendations</title>");
            out.println("<link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<header class='site-header'><div class='header-inner'>");
            out.println("<h1>Personalized Startup Internship Recommendation System</h1>");
            out.println("<p>Explore opportunities matched to your preferences.</p>");
            out.println("</div></header>");
            out.println("<nav class='site-nav' aria-label='Main navigation'>");
            out.println("<a href='index.html'>Home</a>");
            out.println("</nav><main class='page-content'>");
            out.println("<h2>Recommended Internships</h2>");
            out.println("<table class='result-table' border='1' cellpadding='8' align='center'>");
            out.println("<tr><th>Company</th><th>Internship</th>" +
                        "<th>Domain</th><th>Location</th>" +
                        "<th>Duration</th><th>Stipend</th></tr>");

            boolean found = false;
            while (rs.next()) {
                found = true;
                out.println("<tr>");
                out.println("<td>" + rs.getString("company_name") + "</td>");
                out.println("<td>" + rs.getString("title") + "</td>");
                out.println("<td>" + rs.getString("domain") + "</td>");
                out.println("<td>" + rs.getString("location") + "</td>");
                out.println("<td>" + rs.getString("duration") + "</td>");
                out.println("<td>" + rs.getString("stipend") + "</td>");
                out.println("</tr>");
            }

            out.println("</table>");
            if (!found) {
                out.println("<p>No matching internships were found. Update your student details and try again.</p>");
            }
            out.println("<p><a href='student_details.html'>Update Student Profile</a> | ");
            out.println("<a href='logout'>Sign Out</a></p></main></body></html>");

            rs.close();
            ps.close();
            con.close();

        } catch (Exception e) {
            getServletContext().log("Student recommendations could not be loaded", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("<!DOCTYPE html><html><head><title>Recommendations</title>");
            out.println("<link rel='stylesheet' href='css/style.css'></head><body>");
            out.println("<h2>Recommendations are temporarily unavailable</h2>");
            out.println("<p>Please try again, or update your student profile.</p>");
            out.println("<a href='index.html'>Home</a></body></html>");
        }
    }

    private boolean hasValue(String value) {
        return value != null && !value.trim().isEmpty();
    }
}
