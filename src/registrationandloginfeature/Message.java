/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import java.io.*;
import java.util.*;
import java.util.regex.*;

public class Message {

    // Part 2 and Part 3 message collections
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static int totalMessagesSent = 0;
    private static final Random RANDOM = new Random();

    private final String messageID;
    private final int messageNumber;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    public Message(int messageNumber, String recipient, String messageText) {
        this(generateMessageID(), messageNumber, recipient, messageText);
    }

    public Message(String messageID, int messageNumber,
                   String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // Validation methods

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    public String checkRecipientCell() {
        if (recipient != null && recipient.matches("\\+\\d{1,3}\\d{1,10}")) {
            return "Cell phone number successfully captured.";
        }

        return "Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.";
    }

    public String checkMessageLength() {
        int length = messageText.length();

        if (length <= 250) {
            return "Message ready to send.";
        }

        return "Message exceeds 250 characters by " + (length - 250)
                + "; please reduce the size.";
    }

    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");

        String first = clean(words[0]);
        String last = clean(words[words.length - 1]);

        String prefix = messageID.length() >= 2
                ? messageID.substring(0, 2) : messageID;

        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    // Send, disregard or store message

    public String sentMessage() {
        Scanner in = new Scanner(System.in);

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

    public String sentMessage(int choice) {

        switch (choice) {

            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                addTrackingDetails();
                return "Message successfully sent.";

            case 2:
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";

            case 3:
                storeMessage();
                return "Message successfully stored.";

            default:
                return "Invalid option.";
        }
    }

    private void addTrackingDetails() {
        if (!messageIDs.contains(messageID)) {
            messageIDs.add(messageID);
            messageHashes.add(messageHash);
        }
    }

    // Store messages in JSON file

    public void storeMessage() {

        if (!storedMessages.contains(this)) {
            storedMessages.add(this);
            addTrackingDetails();
        }

        String json = "{\n"
                + "\"messageID\":\"" + esc(messageID) + "\",\n"
                + "\"messageNumber\":" + messageNumber + ",\n"
                + "\"messageHash\":\"" + esc(messageHash) + "\",\n"
                + "\"recipient\":\"" + esc(recipient) + "\",\n"
                + "\"message\":\"" + esc(messageText) + "\"\n"
                + "}\n";

        try (FileWriter fw = new FileWriter("messages.json", true)) {
            fw.write(json);
        } catch (IOException e) {
            System.out.println("Could not store message: " + e.getMessage());
        }
    }

    // Load stored messages from JSON file

    public static void loadStoredMessages() {

        File file = new File("messages.json");

        if (!file.exists()) {
            return;
        }

        storedMessages.clear();

        try {
            StringBuilder content = new StringBuilder();

            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;

                while ((line = reader.readLine()) != null) {
                    content.append(line).append("\n");
                }
            }

            Pattern objectPattern = Pattern.compile("\\{(.*?)\\}", Pattern.DOTALL);
            Matcher objectMatcher = objectPattern.matcher(content.toString());

            while (objectMatcher.find()) {

                String object = objectMatcher.group(1);

                String id = getJsonValue(object, "messageID");
                String number = getJsonValue(object, "messageNumber");
                String recipient = getJsonValue(object, "recipient");
                String message = getJsonValue(object, "message");

                if (id != null && number != null
                        && recipient != null && message != null) {

                    Message loaded = new Message(
                            id,
                            Integer.parseInt(number),
                            recipient,
                            message
                    );

                    if (!containsStoredID(id)) {
                        storedMessages.add(loaded);
                        addTrackingForLoaded(loaded);
                    }
                }
            }

        } catch (IOException | NumberFormatException e) {
            System.out.println("Could not load stored messages: " + e.getMessage());
        }
    }

    private static String getJsonValue(String object, String key) {

        Pattern pattern = Pattern.compile(
                "\"" + key + "\"\\s*:\\s*(?:\"((?:\\\\.|[^\"])*)\"|(\\d+))"
        );

        Matcher matcher = pattern.matcher(object);

        if (matcher.find()) {
            if (matcher.group(1) != null) {
                return unescape(matcher.group(1));
            }
            return matcher.group(2);
        }

        return null;
    }

    private static String unescape(String value) {
        return value.replace("\\n", "\n")
                .replace("\\r", "\r")
                .replace("\\t", "\t")
                .replace("\\\"", "\"")
                .replace("\\\\", "\\");
    }

    private static void addTrackingForLoaded(Message message) {
        if (!messageIDs.contains(message.messageID)) {
            messageIDs.add(message.messageID);
            messageHashes.add(message.messageHash);
        }
    }

    private static boolean containsStoredID(String id) {
        for (Message message : storedMessages) {
            if (message.messageID.equals(id)) {
                return true;
            }
        }
        return false;
    }

    // Part 3: Display stored messages

    public static void displayStoredMessages() {

        loadStoredMessages();

        if (storedMessages.isEmpty()) {
            System.out.println("There are no stored messages.");
            return;
        }

        System.out.println("\n--- STORED MESSAGES ---");

        for (Message message : storedMessages) {
            System.out.println("Sender: You");
            System.out.println("Recipient: " + message.recipient);
            System.out.println(message.details());
        }
    }

    // Part 3: Display longest stored message

    public static void displayLongestMessage() {

        loadStoredMessages();

        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages available.");
            return;
        }

        Message longest = storedMessages.get(0);

        for (Message message : storedMessages) {
            if (message.messageText.length() > longest.messageText.length()) {
                longest = message;
            }
        }

        System.out.println("\n--- LONGEST STORED MESSAGE ---");
        System.out.println(longest.details());
        System.out.println("Length: " + longest.messageText.length() + " characters");
    }

    // Part 3: Search using message ID

    public static void searchMessageID(String id) {

        loadStoredMessages();

        for (Message message : storedMessages) {
            if (message.messageID.equals(id)) {
                System.out.println("\nMessage found:");
                System.out.println(message.details());
                return;
            }
        }

        System.out.println("Message ID not found.");
    }

    // Part 3: Search using recipient number

    public static void searchRecipient(String recipient) {

        loadStoredMessages();

        boolean found = false;

        for (Message message : storedMessages) {
            if (message.recipient.equals(recipient)) {
                System.out.println(message.details());
                found = true;
            }
        }

        if (!found) {
            System.out.println("No messages found for this recipient.");
        }
    }

    // Part 3: Delete message using hash

    public static void deleteMessageHash(String hash) {

        loadStoredMessages();

        Message found = null;

        for (Message message : storedMessages) {
            if (message.messageHash.equals(hash)) {
                found = message;
                break;
            }
        }

        if (found == null) {
            System.out.println("Message hash not found.");
            return;
        }

        storedMessages.remove(found);
        rewriteJsonFile();

        System.out.println("Message successfully deleted.");
    }

    private static void rewriteJsonFile() {

        try (FileWriter fw = new FileWriter("messages.json", false)) {

            for (Message message : storedMessages) {

                fw.write("{\n"
                        + "\"messageID\":\"" + esc(message.messageID) + "\",\n"
                        + "\"messageNumber\":" + message.messageNumber + ",\n"
                        + "\"messageHash\":\"" + esc(message.messageHash) + "\",\n"
                        + "\"recipient\":\"" + esc(message.recipient) + "\",\n"
                        + "\"message\":\"" + esc(message.messageText) + "\"\n"
                        + "}\n");
            }

        } catch (IOException e) {
            System.out.println("Could not update JSON file: " + e.getMessage());
        }
    }

    // Part 3: Display report

    public static void displayReport() {

        loadStoredMessages();

        System.out.println("\n========== STORED MESSAGE REPORT ==========");

        if (storedMessages.isEmpty()) {
            System.out.println("No stored messages available.");
            return;
        }

        for (Message message : storedMessages) {
            System.out.println("-----------------------------------");
            System.out.println("Message Hash: " + message.messageHash);
            System.out.println("Recipient: " + message.recipient);
            System.out.println("Message: " + message.messageText);
        }

        System.out.println("-----------------------------------");
        System.out.println("Total stored messages: " + storedMessages.size());
        System.out.println("===================================");
    }

    // Existing Part 2 methods

    public static String printMessages() {
        StringBuilder sb = new StringBuilder();

        for (Message message : sentMessages) {
            sb.append(message.details()).append("\n");
        }

        return sb.toString();
    }

    public static int returnTotalMessages() {
        return totalMessagesSent;
    }

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

    public String details() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText + "\n";
    }

    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    private static String esc(String value) {
        return value.replace("\\", "\\\\")
                .replace("\"", "\\\"")
                .replace("\n", "\\n")
                .replace("\r", "\\r")
                .replace("\t", "\\t");
    }

    public String getMessageID() {
        return messageID;
    }

    public String getMessageHash() {
        return messageHash;
    }

    public String getRecipient() {
        return recipient;
    }

    public String getMessageText() {
        return messageText;
    }

    public static List<Message> getSentMessages() {
        return new ArrayList<>(sentMessages);
    }

    public static List<Message> getDisregardedMessages() {
        return new ArrayList<>(disregardedMessages);
    }

    public static List<Message> getStoredMessages() {
        loadStoredMessages();
        return new ArrayList<>(storedMessages);
    }

    public static List<String> getMessageHashes() {
        return new ArrayList<>(messageHashes);
    }

    public static List<String> getMessageIDs() {
        return new ArrayList<>(messageIDs);
    }

    public static void resetState() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }
}
