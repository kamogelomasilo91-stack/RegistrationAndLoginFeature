/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package registrationandloginfeature;

import java.util.Scanner;


//This class manages the Quick Chat application//
public class QuickChat {

    public static void main(String[] args) {
        Scanner in = new Scanner(System.in);

        // Login is handled in Part 1 before this method is called.

        // 2. Welcome message
        System.out.println("Welcome to QuickChat.");

        // 5. User decides how many messages they will enter
        int maxMessages = readInt(in, "How many messages do you wish to enter? ");
        int created = 0;

        // 4. Run until the user chooses Quit
        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            int option = readInt(in, "Choose an option: ");

            switch (option) {
                case 1:
                    if (created >= maxMessages) {
                        System.out.println("You have entered all " + maxMessages + " messages.");
                        break;
                    }
                    created += sendOne(in, created) ? 1 : 0;
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    break;
                default:
                    System.out.println("Please choose 1, 2 or 3.");
            }
        }

        // Totals once everything has been sent
        System.out.println("\n" + Message.printMessages());
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
    }

    /** Captures and processes one message. Returns true if it counted towards the limit. */
    private static boolean sendOne(Scanner in, int messageNumber) {
        String recipient;
        Message probe;
        while (true) {
            System.out.print("Recipient cell number (with international code, e.g. +27718693002): ");
            recipient = in.nextLine().trim();
            probe = new Message(messageNumber, recipient, "x");
            String result = probe.checkRecipientCell();
            System.out.println(result);
            if (result.startsWith("Cell phone number successfully")) break;
        }

        String text;
        while (true) {
            System.out.print("Enter your message: ");
            text = in.nextLine();
            if (text.isBlank()) {
                System.out.println("Message cannot be empty.");
            } else if (text.length() > 250) {
                System.out.println("Please enter a message of less than 250 characters.");
            } else {
                System.out.println("Message sent");
                break;
            }
        }

        Message msg = new Message(messageNumber, recipient, text);
        System.out.println(msg.messageIDGenerated());

        String outcome = msg.sentMessage();
        System.out.println(outcome);

        if (outcome.startsWith("Message successfully sent")) {
            System.out.println(msg.details());
            return true;
        }
        if (outcome.startsWith("Press 0")) {
            System.out.print("> ");
            in.nextLine();                 // user presses 0 to delete
            System.out.println("Message deleted.");
            return false;
        }
        return outcome.startsWith("Message successfully stored");
    }

    private static int readInt(Scanner in, String prompt) {
        while (true) {
            System.out.print(prompt);
            try {
                return Integer.parseInt(in.nextLine().trim());
            } catch (NumberFormatException e) {
                System.out.println("Please enter a number.");
            }
        }
    }

}
