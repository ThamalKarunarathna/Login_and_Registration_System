package com.registration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@WebServlet("/register")
public class RegistrationServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String uname = request.getParameter("name");
        String uemail = request.getParameter("email");
        String upwd = request.getParameter("pass");
        String Reupwd = request.getParameter("re_pass");
        String umobile = request.getParameter("contact");
        RequestDispatcher dis = null;
        Connection con = null;

        // Check if the provided fields are valid
        if (uname == null || uname.equals("")) {
            request.setAttribute("status", "InvalidName");
            dis = request.getRequestDispatcher("registration.jsp");
            dis.forward(request, response);
            return;
        }

        if (uemail == null || uemail.equals("")) {
            request.setAttribute("status", "InvalidEmail");
            dis = request.getRequestDispatcher("registration.jsp");
            dis.forward(request, response);
            return;
        }

        if (upwd == null || upwd.equals("")) {
            request.setAttribute("status", "InvalidPassword");
            dis = request.getRequestDispatcher("registration.jsp");
            dis.forward(request, response);
            return;
        } else if (!upwd.equals(Reupwd)) {
            request.setAttribute("status", "PasswordMismatch");
            dis = request.getRequestDispatcher("registration.jsp");
            dis.forward(request, response);
            return;
        }

        if (umobile == null || umobile.equals("")) {
            request.setAttribute("status", "InvalidMobile");
            dis = request.getRequestDispatcher("registration.jsp");
            dis.forward(request, response);
            return;
        } else if (umobile.length() > 10) {
            request.setAttribute("status", "InvalidMobileLength");
            dis = request.getRequestDispatcher("registration.jsp");
            dis.forward(request, response);
            return;
        }

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            con = DriverManager.getConnection("jdbc:mysql://localhost:3306/company?useSSL=false", "root",
                    "Thamalkaru1$");

            // Check if the user already exists
            PreparedStatement checkUserStmt = con.prepareStatement("SELECT COUNT(*) FROM user WHERE uemail = ?");
            checkUserStmt.setString(1, uemail);
            ResultSet existingUserResult = checkUserStmt.executeQuery();
            existingUserResult.next();
            int existingUserCount = existingUserResult.getInt(1);

            if (existingUserCount > 0) {
                request.setAttribute("status", "UserExists");
                dis = request.getRequestDispatcher("registration.jsp");
                dis.forward(request, response);
                return;
            }

            // Insert the user into the database
            PreparedStatement insertStmt = con.prepareStatement(
                    "INSERT INTO user (uname, upwd, uemail, umobile) VALUES (?, ?, ?, ?)");
            insertStmt.setString(1, uname);
            insertStmt.setString(2, upwd);
            insertStmt.setString(3, uemail);
            insertStmt.setString(4, umobile);

            int rowsAffected = insertStmt.executeUpdate();

            dis = request.getRequestDispatcher("registration.jsp");

            if (rowsAffected > 0) {
                request.setAttribute("status", "Success");
            } else {
                request.setAttribute("status", "Failure");
            }

            dis.forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();

        } finally {
            try {
                if (con != null) {
                    con.close();
                }
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }
}
