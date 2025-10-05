package reservation_system.mng;

import java.sql.*;
import java.util.Scanner;

public class HotelReservationSystem {

    private static final String URL = "jdbc:postgresql://localhost:5432/hotel_db";
    private static final String USER = "postgres";
    private static final String PASSWORD = "root";

    public static void main(String[] args) {
        try (Connection con = DriverManager.getConnection(URL, USER, PASSWORD);
             Scanner sc = new Scanner(System.in)) {

            System.out.println("\n✅ Database connected successfully!\n");

            while (true) {
                System.out.println("==========================================");
                System.out.println("       🏨 Hotel Reservation System       ");
                System.out.println("==========================================");
                System.out.println("1. Login");
                System.out.println("2. Register");
                System.out.println("3. Exit");
                System.out.print("Enter choice: ");

                int choice = sc.nextInt();
                sc.nextLine(); // consume newline

                switch (choice) {
                    case 1:
                        User loggedInUser = AuthService.login(con, sc);
                        if (loggedInUser != null) {
                            if (loggedInUser.getRole().equalsIgnoreCase("admin")) {
                                adminMenu(con, sc, loggedInUser);
                            } else {
                                userMenu(con, sc, loggedInUser);
                            }
                        }
                        break;
                    case 2:
                        AuthService.register(con, sc);
                        break;
                    case 3:
                        System.out.println("👋 Exiting... Thank you!");
                        return; // exit program
                    default:
                        System.out.println("❌ Invalid choice. Try again!");
                        break;
                }
            }

        } catch (SQLException e) {
            System.out.println("❌ Database connection failed!");
            e.printStackTrace();
        }
    }

    // ========================== USER MENU ==========================
    private static void userMenu(Connection con, Scanner sc, User user) {
        int choice;
        do {
            System.out.println("\n==========================================");
            System.out.println("        👤 User Dashboard - " + user.getUsername());
            System.out.println("==========================================");
            System.out.println("1. New Reservation");
            System.out.println("2. View My Reservations");
            System.out.println("3. Update My Reservation");
            System.out.println("4. Delete My Reservation");
            System.out.println("5. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    ReservationService.newReservation(con, sc, user.getUserId());
                    break;
                case 2:
                    ReservationService.viewUserReservations(con, user.getUserId());
                    break;
                case 3:
                    ReservationService.updateUserReservation(con, sc, user.getUserId());
                    break;
                case 4:
                    ReservationService.deleteUserReservation(con, sc, user.getUserId());
                    break;
                case 5:
                    System.out.println("👋 Logged out successfully!");
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
                    break;
            }
        } while (choice != 5);
    }

    // ========================== ADMIN MENU ==========================
    private static void adminMenu(Connection con, Scanner sc, User admin) {
        int choice;
        do {
            System.out.println("\n==========================================");
            System.out.println("       👨‍💻 Admin Dashboard - " + admin.getUsername());
            System.out.println("==========================================");
            System.out.println("1. View All Reservations");
            System.out.println("2. Check Room Availability");
            System.out.println("3. Update Any Reservation");
            System.out.println("4. Delete Any Reservation");
            System.out.println("5. View All Users");
            System.out.println("6. Delete User");
            System.out.println("7. Logout");
            System.out.print("Enter choice: ");
            choice = sc.nextInt();
            sc.nextLine();

            switch (choice) {
                case 1:
                    ReservationService.viewAllReservations(con);
                    break;
                case 2:
                    ReservationService.checkRoomAvailability(con, sc);
                    break;
                case 3:
                    ReservationService.updateAnyReservation(con, sc);
                    break;
                case 4:
                    ReservationService.deleteAnyReservation(con, sc);
                    break;
                case 5:
				try {
					UserService.viewAllUsers(con);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                    break;
                case 6:
                    System.out.print("Enter User ID to delete: ");
                    int deleteId = sc.nextInt();
                    sc.nextLine();
				try {
					UserService.deleteUser(con, deleteId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                    break;
                case 7:
                    System.out.println("👋 Logged out successfully!");
                    break;
                default:
                    System.out.println("❌ Invalid choice.");
                    break;
            }
        } while (choice != 7);
    }
}
