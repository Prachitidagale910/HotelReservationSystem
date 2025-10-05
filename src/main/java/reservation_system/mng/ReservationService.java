package reservation_system.mng;


import java.sql.*;
import java.util.Scanner;

public class ReservationService {

    // ---------- USER ----------
    public static void newReservation(Connection con, Scanner sc, int userId) {
        try {
            System.out.print("Enter guest name: ");
            String guestName = sc.nextLine();
            System.out.print("Enter room number: ");
            int roomNumber = sc.nextInt();
            sc.nextLine();
            System.out.print("Enter contact number: ");
            String contact = sc.nextLine();

            String sql = "INSERT INTO reservations (guest_name, room_number, contact_number, user_id) VALUES (?, ?, ?, ?)";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, guestName);
            ps.setInt(2, roomNumber);
            ps.setString(3, contact);
            ps.setInt(4, userId);
            ps.executeUpdate();

            System.out.println("✅ Reservation created successfully!");

        } catch (SQLException e) {
            System.out.println("❌ Error creating reservation.");
            e.printStackTrace();
        }
    }

    public static void viewUserReservations(Connection con, int userId) {
        try {
            String sql = "SELECT * FROM reservations WHERE user_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, userId);
            ResultSet rs = ps.executeQuery();

            System.out.println("Your Reservations:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("reservation_id") +
                        ", Guest: " + rs.getString("guest_name") +
                        ", Room: " + rs.getInt("room_number") +
                        ", Contact: " + rs.getString("contact_number") +
                        ", Date: " + rs.getTimestamp("reservation_date"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateUserReservation(Connection con, Scanner sc, int userId) {
        try {
            System.out.print("Enter Reservation ID to update: ");
            int id = sc.nextInt();
            sc.nextLine();

            String checkSql = "SELECT * FROM reservations WHERE reservation_id = ? AND user_id = ?";
            PreparedStatement check = con.prepareStatement(checkSql);
            check.setInt(1, id);
            check.setInt(2, userId);
            ResultSet rs = check.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Reservation not found or not yours.");
                return;
            }

            System.out.print("Enter new guest name: ");
            String guest = sc.nextLine();
            System.out.print("Enter new contact number: ");
            String contact = sc.nextLine();

            String sql = "UPDATE reservations SET guest_name = ?, contact_number = ? WHERE reservation_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, guest);
            ps.setString(2, contact);
            ps.setInt(3, id);
            ps.executeUpdate();

            System.out.println("✅ Reservation updated!");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteUserReservation(Connection con, Scanner sc, int userId) {
        try {
            System.out.print("Enter Reservation ID to delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            String sql = "DELETE FROM reservations WHERE reservation_id = ? AND user_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            ps.setInt(2, userId);

            int rows = ps.executeUpdate();
            if (rows > 0) {
                System.out.println("✅ Reservation deleted!");
            } else {
                System.out.println("❌ Not found or not yours.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // ---------- ADMIN ----------
    public static void viewAllReservations(Connection con) {
        try {
            String sql = "SELECT * FROM reservations";
            Statement st = con.createStatement();
            ResultSet rs = st.executeQuery(sql);

            System.out.println("All Reservations:");
            while (rs.next()) {
                System.out.println("ID: " + rs.getInt("reservation_id") +
                        ", Guest: " + rs.getString("guest_name") +
                        ", Room: " + rs.getInt("room_number") +
                        ", Contact: " + rs.getString("contact_number") +
                        ", Date: " + rs.getTimestamp("reservation_date"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void checkRoomAvailability(Connection con, Scanner sc) {
        try {
            System.out.print("Enter room number to check: ");
            int room = sc.nextInt();
            sc.nextLine();

            String sql = "SELECT COUNT(*) FROM reservations WHERE room_number = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, room);
            ResultSet rs = ps.executeQuery();
            rs.next();

            if (rs.getInt(1) > 0) {
                System.out.println("❌ Room " + room + " is already booked.");
            } else {
                System.out.println("✅ Room " + room + " is available.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void updateAnyReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter Reservation ID to update: ");
            int id = sc.nextInt();
            sc.nextLine();

            System.out.print("Enter new guest name: ");
            String guest = sc.nextLine();
            System.out.print("Enter new contact number: ");
            String contact = sc.nextLine();

            String sql = "UPDATE reservations SET guest_name = ?, contact_number = ? WHERE reservation_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setString(1, guest);
            ps.setString(2, contact);
            ps.setInt(3, id);
            int rows = ps.executeUpdate();

            if (rows > 0) System.out.println("✅ Reservation updated!");
            else System.out.println("❌ Reservation not found.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteAnyReservation(Connection con, Scanner sc) {
        try {
            System.out.print("Enter Reservation ID to delete: ");
            int id = sc.nextInt();
            sc.nextLine();

            String sql = "DELETE FROM reservations WHERE reservation_id = ?";
            PreparedStatement ps = con.prepareStatement(sql);
            ps.setInt(1, id);
            int rows = ps.executeUpdate();

            if (rows > 0) System.out.println("✅ Reservation deleted!");
            else System.out.println("❌ Reservation not found.");

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
