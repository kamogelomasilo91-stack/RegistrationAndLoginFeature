/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package registrationandloginfeature;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
/**
 *
 * @author kgmas
 */
public class RegistrationAndLoginFeature {

   // Single Scanner
   // streams on system
    private static final Scanner scanner = new Scanner(System.in);

    // Stores every successfully registered user so they can later log in.
    private static final List<Login> registeredUsers = new ArrayList<>();

    public static void main(String[] args) {
        boolean running = true;
        // This is the main program loop that shows a menu.
        while (running) {
            // Menu
            System.out.println("\n===== MENU =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    registerUser();
                    break;
                case "2":
                    loginUser();
                    break;
                case "3":
                    running = false;
                    System.out.println("Goodbye!");
                    break;
                default:
                    System.out.println("Invalid option, please choose 1, 2 or 3.");
            }
        }

        scanner.close();
    }

    // ---------- Registration ----------

    private static void registerUser() {
        System.out.println("\n--- Registration ---");

        String firstName = readValidField(
                "Enter first name: ",
                Login::isValidName
        );

        String lastName = readValidField(
                "Enter last name: ",
                Login::isValidName
        );

        String userName = readValidField(
                "Enter username (max 5 characters, must include an uppercase "
                        + "letter, a lowercase letter, a digit and a special character): ",
                Login::isValidUserName
        );

        if (isUsernameTaken(userName)) {
            System.out.println("That username is already registered. Please log in instead.");
            return;
        }

        String password = readValidField(
                "Enter password (at least 8 characters, must include an uppercase "
                        + "letter, a lowercase letter, a digit and a special character): ",
                Login::isValidPassword
        );

        String cellPhoneNumber = readValidField(
                "Enter cellphone number (format +27XXXXXXXXX): ",
                Login::isValidCellPhone
        );

        Login newUser = new Login(userName, password, cellPhoneNumber, firstName, lastName);
        registeredUsers.add(newUser);

        System.out.println("\nRegistration successful! Username " + userName
                + " has been registered. You can now log in with your details.");
    }

    
    private static String readValidField(String prompt, java.util.function.Predicate<String> validator) {
        String input;
        while (true) {
            System.out.print(prompt);
            input = scanner.nextLine();

            if (validator.test(input)) {
                return input;
            }
            System.out.println("Invalid format, please try again.");
        }
    }

    private static boolean isUsernameTaken(String userName) {
        for (Login user : registeredUsers) {
            if (user.getUserName().equals(userName)) {
                return true;
            }
        }
        return false;
    }

    // ---------- Login ----------

    private static void loginUser() {
        System.out.println("\n--- Login ---");

        if (registeredUsers.isEmpty()) {
            System.out.println("No registered users yet. Please register first.");
            return;
        }

        System.out.print("Enter username: ");
        String userName = scanner.nextLine();

        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        Login matchedUser = findUser(userName, password);

        if (matchedUser != null) { 
            //login details matched an existing user
            System.out.println("Login successful. Welcome " + matchedUser.getFirstName()
                    + " " + matchedUser.getLastName() + "!");
        } else {
            System.out.println("Username or password incorrect. Please try again.");
        }
    }

    private static Login findUser(String userName, String password) {
        for (Login user : registeredUsers) {
            if (user.getUserName().equals(userName) && user.getPassword().equals(password)) {
                return user;
            }
        }
        return null;
    }
}
