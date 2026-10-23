import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class CompanyLoginServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {
        String identifier = request.getParameter("company_identifier");
        String password = request.getParameter("password");
        if (identifier == null || identifier.trim().isEmpty()
                || password == null || password.trim().isEmpty()) {
            response.sendRedirect("company-login.html?login=error");
            return;
        }

        String sql = "SELECT company_id, company_name FROM startups "
                + "WHERE (company_id=? OR email=?) AND password=?";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, identifier.trim());
            ps.setString(2, identifier.trim());
            ps.setString(3, password);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    response.sendRedirect("company-login.html?login=error");
                    return;
                }

                String companyId = rs.getString("company_id");
                String companyName = rs.getString("company_name");
                HttpSession oldSession = request.getSession(false);
                if (oldSession != null) {
                    oldSession.invalidate();
                }
                HttpSession session = request.getSession(true);
                session.setAttribute("companyId", companyId);
                session.setAttribute("companyName", companyName);
                session.setAttribute("userType", "company");
            }
            response.sendRedirect("companyDashboard");
        } catch (Exception e) {
            getServletContext().log("Company login failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Company sign in is temporarily unavailable.");
        }
    }
}
