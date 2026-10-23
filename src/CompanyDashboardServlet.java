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

public class CompanyDashboardServlet extends HttpServlet {
    protected void doGet(HttpServletRequest request,
                         HttpServletResponse response)
                         throws ServletException, IOException {
        HttpSession session = request.getSession(false);
        if (session == null || !"company".equals(session.getAttribute("userType"))
                || session.getAttribute("companyId") == null) {
            response.sendRedirect("company-login.html");
            return;
        }

        String companyId = (String) session.getAttribute("companyId");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter out = response.getWriter();
        out.println("<!DOCTYPE html><html><head><meta charset='UTF-8'>");
        out.println("<meta name='viewport' content='width=device-width, initial-scale=1'>");
        out.println("<title>Company Dashboard | Internship Recommendations</title>");
        out.println("<link rel='stylesheet' href='css/style.css'></head><body>");
        out.println("<header class='site-header'><div class='header-inner'>");
        out.println("<h1>Company Dashboard</h1><p>Manage your startup and internship opportunities.</p>");
        out.println("</div></header><nav class='site-nav' aria-label='Company navigation'>");
        out.println("<a href='companyDashboard' aria-current='page'>Dashboard</a>");
        out.println("<a href='index.html'>Home</a><a href='logout'>Sign Out</a></nav>");
        out.println("<main class='page-content'><h2>Welcome, "
                + escape((String) session.getAttribute("companyName")) + "</h2>");

        String status = request.getParameter("status");
        if ("posted".equals(status)) {
            out.println("<div class='status-message' role='status'>Internship posted successfully.</div>");
        } else if ("details".equals(status)) {
            out.println("<div class='status-message' role='status'>Company details updated successfully.</div>");
        }

        out.println("<div class='dashboard-grid'>");
        out.println("<section class='dashboard-card'><h3>Startup details</h3>");
        out.println("<p>Review and update your company name and contact email.</p>");
        out.println("<a class='button' href='startup_register.html'>Update Startup Details</a></section>");
        out.println("<section class='dashboard-card'><h3>Internship opportunities</h3>");
        out.println("<p>Create a new internship posting for students to discover.</p>");
        out.println("<a class='button' href='post_internship.html'>Post Internship</a></section></div>");
        out.println("<section><h2>Your Posted Internships</h2>");
        out.println("<table class='result-table'><thead><tr><th>Title</th><th>Domain</th>");
        out.println("<th>Location</th><th>Duration</th><th>Stipend</th></tr></thead><tbody>");

        String sql = "SELECT title, domain, location, duration, stipend "
                + "FROM internships WHERE company_id=? ORDER BY id DESC";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, companyId);
            try (ResultSet rs = ps.executeQuery()) {
                boolean hasInternships = false;
                while (rs.next()) {
                    hasInternships = true;
                    out.println("<tr><td>" + escape(rs.getString("title")) + "</td><td>"
                            + escape(rs.getString("domain")) + "</td><td>"
                            + escape(rs.getString("location")) + "</td><td>"
                            + escape(rs.getString("duration")) + "</td><td>"
                            + escape(rs.getString("stipend")) + "</td></tr>");
                }
                if (!hasInternships) {
                    out.println("<tr><td colspan='5'>You have not posted any internships yet.</td></tr>");
                }
            }
            out.println("</tbody></table></section></main></body></html>");
        } catch (Exception e) {
            getServletContext().log("Could not load company dashboard internships", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            out.println("</tbody></table><p class='status-message status-error' role='alert'>"
                    + "Your dashboard is available, but internship listings could not be loaded.</p>");
            out.println("</section></main></body></html>");
        }
    }

    private String escape(String value) {
        if (value == null) {
            return "";
        }
        return value.replace("&", "&amp;")
                .replace("<", "&lt;")
                .replace(">", "&gt;")
                .replace("\"", "&quot;")
                .replace("'", "&#39;");
    }
}
