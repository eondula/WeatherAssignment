import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.io.IOException;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.google.gson.Gson;

/**
 * Servlet implementation class RegisterServlet
 */
@WebServlet("/RegisterServlet")
public class RegisterServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
       
    /**
     * @see HttpServlet#HttpServlet()
     */
    public RegisterServlet() {
        super();
        // TODO Auto-generated constructor stub
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		response.getWriter().append("Served at: ").append(request.getContextPath());
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 * 
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
//		doGet(request, response);
        PrintWriter pw = response.getWriter();
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");

        User user = new Gson().fromJson(request.getReader(), User.class);

        String username = user.username;
        String password = user.password;
        String email = user.email;

        Gson gson = new Gson();

        if (username == null || username.isBlank() || password == null || password.isBlank() || email == null || email.isBlank()) {
        	System.out.println("RegisterServlet: Missing user info");
        	response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            String error = "User info missing";
            pw.write(gson.toJson(error));
            pw.flush();
        } else {
        	System.out.println("RegisterServlet: Attempting to register user: " + username);
            int userID = registerUser(username, password, email);
            if (userID == -1) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                String error = "Username is taken";
                pw.write(gson.toJson(error));
                pw.flush();
            } else {
            	System.out.println("RegisterServlet: Registration successful for user ID: " + userID);
                response.setStatus(HttpServletResponse.SC_OK);
                pw.write(gson.toJson(userID));
                pw.flush();
            }
        }
    }
	
	private int registerUser(String username, String password, String email) {
		System.out.println("RegisterServlet: Checking if username exists: " + username);
        try (Connection conn = DBConnection.initializeDatabase()) {
            String checkQuery = "SELECT * FROM users WHERE username = ?";
            PreparedStatement checkStmt = conn.prepareStatement(checkQuery);
            checkStmt.setString(1, username);
            ResultSet resultSet = checkStmt.executeQuery();

            if (resultSet.next()) {
                return -1; // Username already exists
            }

            String insertQuery = "INSERT INTO users (username, password, email) VALUES (?, ?, ?)";
            PreparedStatement insertStmt = conn.prepareStatement(insertQuery);
            insertStmt.setString(1, username);
            insertStmt.setString(2, password);
            insertStmt.setString(3, email);
            insertStmt.executeUpdate();
            System.out.println("RegisterServlet: User registered: " + username);

            return 1; // Successful registration
        } catch (SQLException | ClassNotFoundException e) {
            e.printStackTrace();
            return -1;
        }
    }

}
