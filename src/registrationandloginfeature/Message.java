/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package registrationandloginfeature;



import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Message {
    // ---- shared state (all messages sent while the program runs) ----
    private static final List<Message> sentMessages = new ArrayList<>();
    private static int totalMessagesSent = 0;
    private static final Random RANDOM = new Random();

    // ---- per-message fields ----
    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    /** Normal constructor: the message ID is randomly generated. */
    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    /** Constructor that accepts a fixed ID (used by the unit tests). */
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // ------------------------------------------------------------------
    // Required methods
    // ------------------------------------------------------------------

    /** Message ID must be no more than ten characters. */
    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /**
     * Recipient must start with an international code (+) and contain no
     * more than ten digits after the code (codes are 1-3 digits).
     * e.g. +27718693002 is valid; 08575975889 is not.
     */
    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+\\d{1,3}\\d{1,10}")) {
            return "Cell phone number successfully captured.";
        }
        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    /** Message must not exceed 250 characters. */
    public String checkMessageLength() {
        int length = messageText.length();
        if (length <= 250) {
            return "Message ready to send.";
        }
        return "Message exceeds 250 characters by " + (length - 250)
                + "; please reduce the size.";
    }

    /** First two digits of ID : message number : FIRSTWORD + LASTWORD, in caps. */
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String first = clean(words[0]);
        String last = clean(words[words.length - 1]);
        String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Interactive version: asks the user what to do with the message. */
    public String sentMessage() {
        java.util.Scanner in = new java.util.Scanner(System.in);
        System.out.println("Choose an option:");
        System.out.println("1) Send Message");
        System.out.println("2) Disregard Message");
        System.out.println("3) Store Message to send later");
        int choice;
        try {
            choice = Integer.parseInt(in.nextLine().trim());
        } catch (NumberFormatException e) {
            choice = -1;
        }
        return sentMessage(choice);
    }

    /** Testable version: the choice is passed in. 1 = send, 2 = disregard, 3 = store. */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                return "Message successfully sent.";
            case 2:
                return "Press 0 to delete the message.";
            case 3:
                storeMessage();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

    /** Returns every message sent while the program is running. */
    public static String printMessages() {
        StringBuilder sb = new StringBuilder();
        for (Message m : sentMessages) {
            sb.append(m.details()).append("\n");
        }
        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

    /** Own method: appends this message to messages.json (hand-built JSON, no library needed). */
    public void storeMessage() {
        String json = "{\n"
                + "  \"messageID\": \"" + esc(messageID) + "\",\n"
                + "  \"messageNumber\": " + messageNumber + ",\n"
                + "  \"messageHash\": \"" + esc(messageHash) + "\",\n"
                + "  \"recipient\": \"" + esc(recipient) + "\",\n"
                + "  \"message\": \"" + esc(messageText) + "\"\n"
                + "}\n";
        try (FileWriter fw = new FileWriter("messages.json", true)) {
            fw.write(json);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    /** Random ten-digit number (leading zeros allowed, as in the 00:0:... example). */
    public static String generateMessageID() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 10; i++) {
            sb.append(RANDOM.nextInt(10));
        }
        return sb.toString();
    }

    public String messageIDGenerated() {
        return "Message ID generated: " + messageID;
    }

    /** Full details in the required order: ID, Hash, Recipient, Message. */
    public String details() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText + "\n";
    }

    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    private static String esc(String s) {
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }

    /** Clears static state (useful between unit tests). */
    public static void resetState() {
        sentMessages.clear();
        totalMessagesSent = 0;
    }
}
