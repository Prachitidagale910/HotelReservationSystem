package reservation_system.mng;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Scanner;

public class HotelReservationSystem {
    public static void main(String[] args) {

        // JDBC URL, username, password
        String url = "jdbc:postgresql://localhost:5432/hotel_db";
        String user = "postgres";
        String password = "root";

     // Try-catch block for connection
        try (Connection con = DriverManager.getConnection(url, user, password)) {
            System.out.println("\n✅ Database connected successfully!\n");

            Scanner sc = new Scanner(System.in);
            int choice;

            do {
                System.out.println("==========================================");
                System.out.println("         🏨 Hotel Reservation System      ");
                System.out.println("==========================================");
                System.out.println(" 1. New Reservation");
                System.out.println(" 2. View All Reservations");
                System.out.println(" 3. Check Room Availability");
                System.out.println(" 4. Update Reservation");
                System.out.println(" 5. Delete Reservation");
                System.out.println(" 6. Exit");
                System.out.println("==========================================");
                System.out.print("Enter your choice (1-6): ");

                choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        newReservation(con, sc);
                        break;
                    case 2:
                        checkReservation(con);
                        break;
                    case 3:
                        checkRoomNo(con, sc);
                        break;
                    case 4:
                        updateReservation(con, sc);
                        break;
                    case 5:
                        deleteReservation(con, sc);
                        break;
                    case 6:
                        System.out.println("Exiting... Thank you!");
                        break;
                    default:
                        System.out.println("Invalid choice! Please try again.");
                }

                System.out.println();

            } while (choice != 6);

            sc.close();

        } catch (SQLException e) {
            System.out.println("❌ Connection failed!");
            e.printStackTrace();
        }


    }

    public static void newReservation(Connection con, Scanner sc) {

        // SQL operations using 'con' will go here
        try {
            System.out.print("Enter guest name: ");
            String guestName = sc.nextLine();

            System.out.print("Enter room number: ");
            int roomNumber = sc.nextInt();
            sc.nextLine(); // consume leftover newline

            System.out.print("Enter contact number: ");
            String contactNumber = sc.nextLine();

            // ✅ Always mention column names except auto-increment ones
            String insert = "INSERT INTO reservations (guest_name, room_number, contact_number) VALUES (?, ?, ?)";
            PreparedStatement p = con.prepareStatement(insert);

            p.setString(1, guestName);
            p.setInt(2, roomNumber);
            p.setString(3, contactNumber);

            int rows = p.executeUpdate(); // ✅ use executeUpdate() for INSERT/UPDATE/DELETE

            if (rows > 0) {
                System.out.println("✅ Reservation added successfully!");
               
            } else {
                System.out.println("⚠️ Failed to add reservation.");
            }

        } catch (Exception e) {
            System.out.println("❌ Invalid input! Please try again.");
            e.printStackTrace();
        }

   
    }
    public static void checkReservation(Connection con) { 
    	
    	try {
    	    Statement st = con.createStatement();
    	    String query = "SELECT * FROM reservations ORDER BY reservation_id";
    	    ResultSet rs = st.executeQuery(query);

    	    // Table header
    	    System.out.println("+-----+----------------------+-----------+-----------------+---------------------+");
    	    System.out.printf("| %-3s | %-20s | %-9s | %-15s | %-19s |%n", "ID", "Guest Name", "Room No", "Contact", "Reservation Date");
    	    System.out.println("+-----+----------------------+-----------+-----------------+---------------------+");

    	    // Table rows
    	    while (rs.next()) {
    	        int id = rs.getInt("reservation_id");
    	        String guestName = rs.getString("guest_name");
    	        int roomNumber = rs.getInt("room_number");
    	        String contact = rs.getString("contact_number");
    	        Timestamp ts = rs.getTimestamp("reservation_date");
    	        LocalDateTime dateTime = ts.toLocalDateTime(); // convert to LocalDateTime
    	        String resDate = dateTime.format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

    	        System.out.printf("| %-3d | %-20s | %-9d | %-15s | %-19s |%n", id, guestName, roomNumber, contact, resDate);
    	    }

    	    // Table footer
    	    System.out.println("+-----+----------------------+-----------+-----------------+---------------------+");

    	} catch (SQLException e) {
    	    e.printStackTrace();
    	}

    	System.out.println("Check Reservation called"); 
    	
    }
    public static void checkRoomNo(Connection con, Scanner sc) {
    	System.out.println("Enter the room no to check : ");
    	int room_no = sc.nextInt();
    	sc.nextLine();
    	
    	try {
			Statement st = con.createStatement();
			String query = "SELECT * FROM reservations WHERE room_number ="+room_no;
			
			ResultSet rs = st.executeQuery(query);
			
			if (rs.next()) {
			    System.out.println("Room is Booked!!!");
			} else {
			    System.out.println("Room is available");
			}
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
    	 
    }
    
    
    public static void checkBookedRoom(Connection con) {
    	
    	
    	try {
			Statement st = con.createStatement();
			String query = "SELECT * FROM reservations";
			
			ResultSet result = st.executeQuery(query);
			while(result.next()) {
			    int roomNumber = result.getInt("room_number");
			    String guestName = result.getString("guest_name");
			    Timestamp ts = result.getTimestamp("reservation_date");
			    String resDate = ts.toLocalDateTime().format(DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm"));

			    System.out.printf("| %-9d | %-20s | %-19s |%n", roomNumber, guestName, resDate);
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}
    	 
    }

    
    
    
    public static void updateReservation(Connection con, Scanner sc) {

    	
        System.out.print("Enter reservation ID to update: ");
        int reservationId = sc.nextInt();
        sc.nextLine(); // consume newline

        // check if reservation ID exists
        String validate = "SELECT * FROM reservations WHERE reservation_id = ?";
        try {
            PreparedStatement checkStmt = con.prepareStatement(validate);
            checkStmt.setInt(1, reservationId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Reservation ID not found.");
                return;
            } else {
                System.out.println("✅ Reservation ID exists.");
            }

            // get current values
            String currentGuestName = rs.getString("guest_name");
            int currentRoomNumber = rs.getInt("room_number");
            String currentContact = rs.getString("contact_number");

            // take new values from user
            System.out.print("Enter new guest name (or leave blank to keep current): ");
            String newGuestName = sc.nextLine();
            if (newGuestName.isEmpty()) newGuestName = currentGuestName;

            System.out.print("Enter new room number (or leave blank to keep current): ");
            String roomInput = sc.nextLine();
            int newRoomNumber = roomInput.isEmpty() ? currentRoomNumber : Integer.parseInt(roomInput);

            System.out.print("Enter new contact number (or leave blank to keep current): ");
            String newContact = sc.nextLine();
            if (newContact.isEmpty()) newContact = currentContact;

            // update data in database
            String query = "UPDATE reservations SET guest_name = ?, room_number = ?, contact_number = ? WHERE reservation_id = ?";
            PreparedStatement pstm = con.prepareStatement(query);
            pstm.setString(1, newGuestName);
            pstm.setInt(2, newRoomNumber);
            pstm.setString(3, newContact);
            pstm.setInt(4, reservationId);

            int rowsUpdated = pstm.executeUpdate();
            if (rowsUpdated > 0) {
                System.out.println("✅ Reservation updated successfully!");
            } else {
                System.out.println("❌ \\Update failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public static void deleteReservation(Connection con, Scanner sc) {

        System.out.print("Enter reservation ID to delete: ");
        int reservationId = sc.nextInt();
        sc.nextLine(); // consume newline

        // Check if reservation ID exists
        String validate = "SELECT * FROM reservations WHERE reservation_id = ?";
        try {
            PreparedStatement checkStmt = con.prepareStatement(validate);
            checkStmt.setInt(1, reservationId);
            ResultSet rs = checkStmt.executeQuery();

            if (!rs.next()) {
                System.out.println("❌ Reservation ID not found!");
                return;
            }

            // Delete the reservation
            String query = "DELETE FROM reservations WHERE reservation_id = ?";
            PreparedStatement delStmt = con.prepareStatement(query);
            delStmt.setInt(1, reservationId);
            int rows = delStmt.executeUpdate();

            if (rows > 0) {
                System.out.println("✅ Reservation deleted successfully.");
            } else {
                System.out.println("❌ Deletion failed.");
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
//HotelReservationSystem
