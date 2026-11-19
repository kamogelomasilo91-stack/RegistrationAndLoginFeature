/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import java.util.Scanner;

public class QuickChat {

    public static void main(String[] args) {

        Scanner in = new Scanner(System.in);

        // Load messages previously stored in the JSON file.
        Message.loadStoredMessages();

        System.out.println("Welcome to QuickChat.");

        int maxMessages = readInt(in,
                "How many messages do you wish to enter? ");

        int created = 0;
        boolean running = true;

        while (running) {

            System.out.println("\n========== QUICKCHAT MENU ==========");
            System.out.println("1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");

            int option = readInt(in, "Choose an option: ");

            switch (option) {

                case 1:

                    if (created >= maxMessages) {
                        System.out.println("You have entered all "
                                + maxMessages + " messages.");
                        break;
                    }

                    if (sendOne(in, created)) {
                        created++;
                    }

                    break;

                case 2:

                    System.out.println("\n--- RECENTLY SENT MESSAGES ---");
                    System.out.println(Message.printMessages());
                    break;

                case 3:

                    running = false;
                    System.out.println("Thank you for using QuickChat.");
                    break;

                case 4:

                    storedMessagesMenu(in);
                    break;

                default:

                    System.out.println("Please choose 1, 2, 3 or 4.");
            }
        }

        System.out.println("\nTotal messages sent: "
                + Message.returnTotalMessages());

        in.close();
    }

    // Send one message

    private static boolean sendOne(Scanner in, int messageNumber) {

        String recipient;
        Message probe;

        while (true) {

            System.out.print("Recipient cell number "
                    + "(with international code, e.g. +27718693002): ");

            recipient = in.nextLine().trim();

            probe = new Message(messageNumber, recipient, "x");

            String result = probe.checkRecipientCell();

            System.out.println(result);

            if (result.startsWith("Cell phone number successfully")) {
                break;
            }
        }

        String text;

        while (true) {

            System.out.print("Enter your message: ");
            text = in.nextLine();

            if (text.isBlank()) {

                System.out.println("Message cannot be empty.");

            } else if (text.length() > 250) {

                System.out.println(
                        "Please enter a message of less than 250 characters.");

            } else {

                System.out.println("Message ready to send.");
                break;
            }
        }

        Message msg = new Message(messageNumber, recipient, text);

        System.out.println(msg.messageIDGenerated());

        System.out.println(msg.sentMessage());

        return true;
    }

    // Part 3: Stored Messages menu

    private static void storedMessagesMenu(Scanner in) {

        boolean back = false;

        while (!back) {

            System.out.println("\n========== STORED MESSAGES ==========");
            System.out.println("1) Display stored messages");
            System.out.println("2) Display longest stored message");
            System.out.println("3) Search message using Message ID");
            System.out.println("4) Search messages using recipient");
            System.out.println("5) Delete message using Message Hash");
            System.out.println("6) Display complete message report");
            System.out.println("7) Back to main menu");

            int choice = readInt(in, "Choose an option: ");

            switch (choice) {

                case 1:

                    Message.displayStoredMessages();
                    break;

                case 2:

                    Message.displayLongestMessage();
                    break;

                case 3:

                    System.out.print("Enter Message ID: ");
                    String id = in.nextLine().trim();

                    Message.searchMessageID(id);
                    break;

                case 4:

                    System.out.print("Enter recipient cellphone number: ");
                    String recipient = in.nextLine().trim();

                    Message.searchRecipient(recipient);
                    break;

                case 5:

                    System.out.print("Enter Message Hash: ");
                    String hash = in.nextLine().trim();

                    Message.deleteMessageHash(hash);
                    break;

                case 6:

                    Message.displayReport();
                    break;

                case 7:

                    back = true;
                    break;

                default:

                    System.out.println("Please choose a number from 1 to 7.");
            }
        }
    }

    // Read integer safely

    private static int readInt(Scanner in, String prompt) {

        while (true) {

            System.out.print(prompt);

            try {

                return Integer.parseInt(in.nextLine().trim());

            } catch (NumberFormatException e) {

                System.out.println("Please enter a valid number.");
            }
        }
    }
}
