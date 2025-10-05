package reservation_system.mng;

import java.sql.*;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;

public class UserDAO {
    private Connection con;

    public UserDAO(Connection con) {
        this.con = con;
    }

    public boolean register(User u) throws SQLException {
        String sql = "INSERT INTO users (username, password_hash, role) VALUES (?, ?, ?)";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, u.getUsername());
        ps.setString(2, u.getPasswordHash());
        ps.setString(3, u.getRole());
        return ps.executeUpdate() > 0;
    }

    public User login(String username, String password) throws SQLException {
        String hashed = hashPassword(password);
        String sql = "SELECT * FROM users WHERE username=? AND password_hash=?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setString(1, username);
        ps.setString(2, hashed);
        ResultSet rs = ps.executeQuery();
        if(rs.next()) {
            return new User(rs.getInt("user_id"), rs.getString("username"), rs.getString("password_hash"), rs.getString("role"));
        }
        return null;
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(password.getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder();
            for(byte b: hash) hexString.append(String.format("%02x", b));
            return hexString.toString();
        } catch(Exception e) { return null; }
    }
}
