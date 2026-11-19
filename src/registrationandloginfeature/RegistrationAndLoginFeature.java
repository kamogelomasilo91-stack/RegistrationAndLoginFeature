/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Main.java to edit this template
 */
package registrationandloginfeature;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.function.Function;
import java.util.function.Predicate;

public class RegistrationAndLoginFeature {

    // One Scanner for the whole program (shared with QuickChat)
    private static final Scanner scanner = new Scanner(System.in);
    private static final List<Login> registeredUsers = new ArrayList<>();

    public static void main(String[] args) {
        boolean running = true;
        while (running) {
            System.out.println("\n===== MENU =====");
            System.out.println("1. Register");
            System.out.println("2. Login");
            System.out.println("3. Exit");
            System.out.print("Choose an option: ");
            String choice = scanner.nextLine().trim();

            switch (choice) {
                case "1":
                    register();
                    break;
                case "2":
                    login();
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

    private static void register() {
        System.out.println("\n--- Registration ---");

        System.out.print("Enter first name: ");
        String firstName = scanner.nextLine().trim();
        System.out.print("Enter last name: ");
        String lastName = scanner.nextLine().trim();

        String userName = readValid("Enter username: ", Login::isValidUserName, Login::userNameMessage);
        String password = readValid("Enter password: ", Login::isValidPassword, Login::passwordMessage);
        String cell = readValid("Enter cell phone number (e.g. +27838968976): ",
                Login::isValidCellPhone, Login::cellMessage);

        registeredUsers.add(new Login(userName, password, cell, firstName, lastName));
        System.out.println("\nRegistration successful. You can now log in.");
    }

    /** Keeps asking until the input is valid, printing the brief's message each time. */
    private static String readValid(String prompt, Predicate<String> validator,
                                    Function<String, String> messageFor) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            System.out.println(messageFor.apply(input));
            if (validator.test(input)) {
                return input;
            }
        }
    }

    private static void login() {
        System.out.println("\n--- Login ---");
        System.out.print("Enter username: ");
        String userName = scanner.nextLine().trim();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        for (Login user : registeredUsers) {
            boolean ok = user.loginUser(userName, password);
            if (ok) {
                System.out.println(user.returnLoginStatus(true));
                // Only a successful login reaches QuickChat
                QuickChat.start(scanner, user.getFirstName() + " " + user.getLastName());
                return;
            }
        }
        System.out.println("Username or password incorrect, please try again.");
    }
}