package com.registration;

import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;


@WebServlet("/login")
public class LoginServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;


	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		
		String uemail = request.getParameter("username");
		String pwd = request.getParameter("password");
		
		HttpSession session = request.getSession();
		RequestDispatcher dis = null;
		
		if(uemail == null || uemail.equals("")) {
			
			request.setAttribute("status","InvalidEmail");
			dis = request.getRequestDispatcher("login.jsp");
			dis.forward(request, response);
		}
		
		if(pwd == null || pwd.equals("")) {
			
			request.setAttribute("status","InvalidPassword");
			dis = request.getRequestDispatcher("login.jsp");
			dis.forward(request, response);
		}
		
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/company?useSSL=false","root","Thamalkaru1$");
			PreparedStatement pst = con.prepareStatement("SELECT * FROM user WHERE uemail = ? AND upwd = ?");
			pst.setString(1, uemail);
			pst.setString(2, pwd);
			
			ResultSet rs = pst.executeQuery();
			
			if(rs.next()) {
				session.setAttribute("name", rs.getString("uname"));
				dis = request.getRequestDispatcher("index.jsp");
			}else {
				
				request.setAttribute("status", "Failier");
				dis = request.getRequestDispatcher("login.jsp");
			}
			
			dis.forward(request, response);
			
		} catch (Exception e) {
		
			e.printStackTrace();
		}
		
	}

}
