import java.io.IOException;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class StartupRegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {
        String companyName = value(request, "company_name");
        String email = value(request, "email");
        HttpSession session = request.getSession(false);
        boolean authenticatedCompany = session != null
                && "company".equals(session.getAttribute("userType"))
                && session.getAttribute("companyId") != null;

        if (companyName.isEmpty() || !email.matches(
                "^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")) {
            response.sendRedirect(authenticatedCompany
                    ? "startup_register.html?error=validation"
                    : "company-register.html?error=validation");
            return;
        }

        String sql;
        if (authenticatedCompany) {
            sql = "UPDATE startups SET company_name=?, email=? WHERE company_id=?";
        } else {
            String companyId = value(request, "company_id");
            String password = request.getParameter("password");
            String confirmation = request.getParameter("confirm_password");
            if (password == null) {
                password = "";
            }
            if (confirmation == null) {
                confirmation = "";
            }
            if (companyId.isEmpty() || password.trim().length() < 6
                    || !password.equals(confirmation)) {
                response.sendRedirect("company-register.html?error=validation");
                return;
            }
            sql = "INSERT INTO startups (company_name,company_id,email,password) VALUES (?,?,?,?)";
        }

        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            if (authenticatedCompany) {
                ps.setString(1, companyName);
                ps.setString(2, email);
                ps.setString(3, (String) session.getAttribute("companyId"));
            } else {
                ps.setString(1, companyName);
                ps.setString(2, value(request, "company_id"));
                ps.setString(3, email);
                ps.setString(4, request.getParameter("password"));
            }

            int result = ps.executeUpdate();
            if (!authenticatedCompany && result != 1) {
                response.sendRedirect("company-register.html?error=failed");
                return;
            }
            if (authenticatedCompany) {
                session.setAttribute("companyName", companyName);
                response.sendRedirect("companyDashboard?status=details");
            } else {
                response.sendRedirect("company-login.html?registered=success");
            }
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState()) && !authenticatedCompany) {
                response.sendRedirect("company-register.html?error=exists");
                return;
            }
            getServletContext().log("Company details could not be saved", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Company details could not be saved.");
        } catch (Exception e) {
            getServletContext().log("Company details could not be saved", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Company details could not be saved.");
        }
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
