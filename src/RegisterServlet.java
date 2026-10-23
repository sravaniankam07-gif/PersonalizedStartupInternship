import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.LocalDate;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet {
    protected void doPost(HttpServletRequest request,
                          HttpServletResponse response)
                          throws ServletException, IOException {

        String name = value(request, "name");
        String username = value(request, "username");
        String phone = value(request, "t1");
        String email = value(request, "t2");
        String dob = value(request, "t3");
        String city = value(request, "city");
        String gender = value(request, "g");
        String password = request.getParameter("t4");
        String confirmation = request.getParameter("t5");
        if (password == null) {
            password = "";
        }
        if (confirmation == null) {
            confirmation = "";
        }
        String college = value(request, "college");
        String branch = value(request, "branch");
        String qualification = value(request, "qualification");
        String year = value(request, "year");
        String cgpa = value(request, "cgpa");

        if (name.isEmpty() || username.length() < 4 || !phone.matches("[0-9]{10}")
                || !email.matches("^[A-Za-z0-9._%+-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")
                || dob.isEmpty() || city.isEmpty() || gender.isEmpty()
                || password.trim().length() < 6 || !password.equals(confirmation)
                || college.isEmpty() || branch.isEmpty() || qualification.isEmpty()
                || year.isEmpty() || cgpa.isEmpty()) {
            response.sendRedirect("register.html?error=validation");
            return;
        }
        try {
            BigDecimal grade = new BigDecimal(cgpa);
            if (LocalDate.parse(dob).isAfter(LocalDate.now())
                    || grade.compareTo(BigDecimal.ZERO) < 0
                    || grade.compareTo(BigDecimal.TEN) > 0) {
                response.sendRedirect("register.html?error=validation");
                return;
            }
        } catch (RuntimeException e) {
            response.sendRedirect("register.html?error=validation");
            return;
        }

        String sql = "INSERT INTO students "
                + "(name,username,phone,email,dob,city,gender,password,"
                + "college,branch,qualification,year,cgpa) "
                + "VALUES (?,?,?,?,?,?,?,?,?,?,?,?,?)";
        try (Connection con = DBConnection.getConnection();
             PreparedStatement ps = con.prepareStatement(sql)) {
            ps.setString(1, name);
            ps.setString(2, username);
            ps.setString(3, phone);
            ps.setString(4, email);
            ps.setString(5, dob);
            ps.setString(6, city);
            ps.setString(7, gender);
            ps.setString(8, password);
            ps.setString(9, college);
            ps.setString(10, branch);
            ps.setString(11, qualification);
            ps.setString(12, year);
            ps.setString(13, cgpa);
            if (ps.executeUpdate() != 1) {
                response.sendRedirect("register.html?error=failed");
                return;
            }
            response.sendRedirect("login.html?registered=success");
        } catch (SQLException e) {
            if ("23000".equals(e.getSQLState())) {
                response.sendRedirect("register.html?error=exists");
                return;
            }
            getServletContext().log("Student registration failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Student registration is temporarily unavailable.");
        } catch (Exception e) {
            getServletContext().log("Student registration failed", e);
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR,
                    "Student registration is temporarily unavailable.");
        }
    }

    private String value(HttpServletRequest request, String name) {
        String value = request.getParameter(name);
        return value == null ? "" : value.trim();
    }
}
