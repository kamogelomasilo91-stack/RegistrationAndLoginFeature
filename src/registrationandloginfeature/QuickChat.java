/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import java.util.Scanner;

/** QuickChat is only started after a successful login. */
public class QuickChat {

    public static void start(Scanner in, String senderName) {
        Message.setSender(senderName);
        Message.loadStoredMessages();

        System.out.println("Welcome to QuickChat.");

        int maxMessages = readPositiveInt(in, "How many messages do you wish to enter? ");
        int entered = 0;

        boolean running = true;
        while (running) {
            System.out.println("\n1) Send Messages");
            System.out.println("2) Show recently sent messages");
            System.out.println("3) Quit");
            System.out.println("4) Stored Messages");
            int option = readInt(in, "Choose an option: ");

            switch (option) {
                case 1:
                    if (entered >= maxMessages) {
                        System.out.println("You have already entered all " + maxMessages + " messages.");
                    } else if (sendOne(in)) {
                        entered++;
                    }
                    break;
                case 2:
                    System.out.println("Coming Soon.");
                    break;
                case 3:
                    running = false;
                    break;
                case 4:
                    storedMessagesMenu(in);
                    break;
                default:
                    System.out.println("Please choose 1, 2, 3 or 4.");
            }
        }

        System.out.println("\n" + Message.printMessages());
        System.out.println("Total messages sent: " + Message.returnTotalMessages());
    }

    /** Captures one message. Returns true if it was sent or stored (counts towards the limit). */
    private static boolean sendOne(Scanner in) {
        String recipient;
        while (true) {
            System.out.print("Recipient cell number (with international code, e.g. +27718693002): ");
            recipient = in.nextLine().trim();
            if (Login.isValidCellPhone(recipient)) {
                System.out.println("Cell phone number successfully captured.");
                break;
            }
            System.out.println("Cell phone number is incorrectly formatted or does not contain an "
                    + "international code. Please correct the number and try again.");
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
                break;
            }
        }

        Message msg = new Message(recipient, text);
        System.out.println(msg.checkMessageLength());
        System.out.println(msg.messageIDGenerated());

        String outcome = msg.sentMessage(in);
        System.out.println(outcome);

        if (outcome.startsWith("Message successfully sent")) {
            System.out.println(msg.details());
            return true;
        }
        if (outcome.startsWith("Press 0")) {
            System.out.print("> ");
            if (in.nextLine().trim().equals("0")) {
                System.out.println("Message deleted.");
            } else {
                System.out.println("Message disregarded.");
            }
            return false;
        }
        return outcome.startsWith("Message successfully stored");
    }

    private static void storedMessagesMenu(Scanner in) {
        boolean back = false;
        while (!back) {
            System.out.println("\n--- Stored Messages ---");
            System.out.println("1) Display sender and recipient of all stored messages");
            System.out.println("2) Display the longest stored message");
            System.out.println("3) Search for a message ID");
            System.out.println("4) Search for all messages to a recipient");
            System.out.println("5) Delete a message using its hash");
            System.out.println("6) Display report");
            System.out.println("0) Back");
            int option = readInt(in, "Choose an option: ");

            switch (option) {
                case 1:
                    System.out.println(Message.displaySenderAndRecipient());
                    break;
                case 2:
                    System.out.println(Message.longestStoredMessage());
                    break;
                case 3:
                    System.out.print("Enter message ID: ");
                    System.out.println(Message.searchByMessageID(in.nextLine().trim()));
                    break;
                case 4:
                    System.out.print("Enter recipient number: ");
                    System.out.println(Message.searchByRecipient(in.nextLine().trim()));
                    break;
                case 5:
                    System.out.print("Enter message hash: ");
                    System.out.println(Message.deleteByHash(in.nextLine()));
                    break;
                case 6:
                    System.out.println(Message.displayReport());
                    break;
                case 0:
                    back = true;
                    break;
                default:
                    System.out.println("Please choose 0 to 6.");
            }
        }
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

    private static int readPositiveInt(Scanner in, String prompt) {
        while (true) {
            int n = readInt(in, prompt);
            if (n >= 1) {
                return n;
            }
            System.out.println("Please enter at least 1.");
        }
    }
}