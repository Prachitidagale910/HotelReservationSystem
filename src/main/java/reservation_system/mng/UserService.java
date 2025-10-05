package reservation_system.mng;

import java.sql.*;

public class UserService {

    public static void viewAllUsers(Connection con) throws SQLException {
        Statement st = con.createStatement();
        ResultSet rs = st.executeQuery("SELECT user_id, username, role FROM users");
        System.out.println("ID\tUsername\tRole");
        while(rs.next()) {
            System.out.println(rs.getInt("user_id") + "\t" + rs.getString("username") + "\t" + rs.getString("role"));
        }
    }

    public static void deleteUser(Connection con, int userId) throws SQLException {
        String sql = "DELETE FROM users WHERE user_id = ?";
        PreparedStatement ps = con.prepareStatement(sql);
        ps.setInt(1, userId);
        int rows = ps.executeUpdate();
        if(rows > 0) System.out.println("✅ User deleted successfully!");
        else System.out.println("❌ User not found!");
    }
}
