package reservation_system.mng;

import java.security.MessageDigest;
import java.sql.*;
import java.util.Scanner;

public class AuthService {

    public static void register(Connection con, Scanner sc) {
        try {
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            String hash = hashPassword(password);

            String sql = "INSERT INTO users (username, password_hash) VALUES (?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hash);

            ps.executeUpdate();
            System.out.println("✅ User registered successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Registration failed. Username may already exist.");
        }
    }

    public static User login(Connection con, Scanner sc) {
        try {
            System.out.print("Enter username: ");
            String username = sc.nextLine();
            System.out.print("Enter password: ");
            String password = sc.nextLine();

            String hash = hashPassword(password);

            String sql = "SELECT user_id, username, password_hash, role FROM users WHERE username = ? AND password_hash = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, username);
            ps.setString(2, hash);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                System.out.println("✅ Login successful! Welcome " + username);
                // Use 4-parameter constructor
                return new User(
                        rs.getInt("user_id"),
                        rs.getString("username"),
                        rs.getString("password_hash"),
                        rs.getString("role")
                );
            } else {
                System.out.println("❌ Invalid username or password.");
                return null;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    // Hash password using SHA-256
    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes("UTF-8"));
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
