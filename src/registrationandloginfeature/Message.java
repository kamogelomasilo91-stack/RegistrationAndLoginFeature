/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Scanner;

public final class Message {

    // ---- Part 3 arrays (lists) ----
    private static final List<Message> sentMessages = new ArrayList<>();
    private static final List<Message> disregardedMessages = new ArrayList<>();
    private static final List<Message> storedMessages = new ArrayList<>();
    private static final List<String> messageHashes = new ArrayList<>();
    private static final List<String> messageIDs = new ArrayList<>();

    private static int totalMessagesSent = 0;
    private static final Random RANDOM = new Random();
    private static String storageFile = "messages.json";
    private static String currentSender = "You";

    // REFERENCE: the JSON format (an array of objects with quoted keys/strings and the
    // escape rules used in quote() and JsonReader below) follows the JSON specification.
    // [add the exact page(s) you used for your research, e.g. json.org, and the date you accessed it]

    /** Plain data holder that is written to / read from the JSON file. */
    private static class StoredRecord {
        String messageID;
        int messageNumber;
        String messageHash;
        String sender;
        String recipient;
        String message;
    }

    // ---- per-message fields ----
    private final String messageID;
    private final int messageNumber;
    private final String sender;
    private final String recipient;
    private final String messageText;
    private final String messageHash;

    /** Normal constructor: random ID, message number = messages sent so far (starts at 0). */
    public Message(String recipient, String messageText) {
        this(generateMessageID(), totalMessagesSent, recipient, messageText);
    }

    /** Fixed ID and number (used by the unit tests). */
    public Message(String messageID, int messageNumber, String recipient, String messageText) {
        this(messageID, messageNumber, currentSender, recipient, messageText);
    }

    private Message(String messageID, int messageNumber, String sender,
                    String recipient, String messageText) {
        this.messageID = messageID;
        this.messageNumber = messageNumber;
        this.sender = sender;
        this.recipient = recipient;
        this.messageText = messageText;
        this.messageHash = createMessageHash();
    }

    // ------------------------------------------------------------------
    // Part 2 methods
    // ------------------------------------------------------------------

    public boolean checkMessageID() {
        return messageID != null && messageID.length() <= 10;
    }

    /** Reuses the Part 1 cell phone checker. */
    public String checkRecipientCell() {
        if (Login.isValidCellPhone(recipient)) {
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

    /** First two digits of the ID : message number : FIRSTWORD + LASTWORD (all caps). */
    public String createMessageHash() {
        String[] words = messageText.trim().split("\\s+");
        String first = clean(words[0]);
        String last = clean(words[words.length - 1]);
        String prefix = messageID.length() >= 2 ? messageID.substring(0, 2) : messageID;
        return (prefix + ":" + messageNumber + ":" + first + last).toUpperCase();
    }

    /** Interactive version: uses the shared Scanner. */
    public String sentMessage(Scanner in) {
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

    /** Testable version: 1 = send, 2 = disregard, 3 = store. */
    public String sentMessage(int choice) {
        switch (choice) {
            case 1:
                sentMessages.add(this);
                totalMessagesSent++;
                track(this);
                return "Message successfully sent.";
            case 2:
                disregardedMessages.add(this);
                return "Press 0 to delete the message.";
            case 3:
                storedMessages.add(this);
                track(this);
                saveStoredMessages();
                return "Message successfully stored.";
            default:
                return "Invalid option.";
        }
    }

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

    // ------------------------------------------------------------------
    // JSON storage
    // ------------------------------------------------------------------

    /** Writes ALL stored messages to the JSON file as one valid JSON array. */
    public static void saveStoredMessages() {
        StringBuilder sb = new StringBuilder("[\n");
        for (int i = 0; i < storedMessages.size(); i++) {
            Message m = storedMessages.get(i);
            sb.append("  {\n")
              .append("    \"messageID\": ").append(quote(m.messageID)).append(",\n")
              .append("    \"messageNumber\": ").append(m.messageNumber).append(",\n")
              .append("    \"messageHash\": ").append(quote(m.messageHash)).append(",\n")
              .append("    \"sender\": ").append(quote(m.sender)).append(",\n")
              .append("    \"recipient\": ").append(quote(m.recipient)).append(",\n")
              .append("    \"message\": ").append(quote(m.messageText)).append("\n")
              .append("  }").append(i < storedMessages.size() - 1 ? "," : "").append("\n");
        }
        sb.append("]\n");
        try {
            Files.write(new File(storageFile).toPath(), sb.toString().getBytes(StandardCharsets.UTF_8));
        } catch (IOException e) {
            System.out.println("Could not store messages: " + e.getMessage());
        }
    }

    /** Reads the JSON file into the stored messages array. */
    public static void loadStoredMessages() {
        File file = new File(storageFile);
        if (!file.exists()) {
            return;
        }
        try {
            String json = new String(Files.readAllBytes(file.toPath()), StandardCharsets.UTF_8);
            List<StoredRecord> records = new JsonReader(json).readRecords();
            storedMessages.clear();
            for (StoredRecord r : records) {
                Message m = new Message(r.messageID, r.messageNumber, r.sender, r.recipient, r.message);
                storedMessages.add(m);
                track(m);
            }
        } catch (IOException | IllegalArgumentException e) {
            System.out.println("Could not load stored messages: " + e.getMessage());
        }
    }

    /** Wraps text in quotes and escapes characters that are not allowed raw in JSON. */
    private static String quote(String text) {
        StringBuilder sb = new StringBuilder("\"");
        for (char c : text.toCharArray()) {
            switch (c) {
                case '"':  sb.append("\\\""); break;
                case '\\': sb.append("\\\\"); break;
                case '\n': sb.append("\\n"); break;
                case '\r': sb.append("\\r"); break;
                case '\t': sb.append("\\t"); break;
                default:
                    if (c < 0x20) {
                        sb.append(String.format("\\u%04x", (int) c));
                    } else {
                        sb.append(c);
                    }
            }
        }
        return sb.append("\"").toString();
    }

    /** Minimal reader for an array of flat objects (strings and whole numbers). */
    private static class JsonReader {
        private final String s;
        private int i = 0;

        JsonReader(String s) {
            this.s = s;
        }

        List<StoredRecord> readRecords() {
            List<StoredRecord> list = new ArrayList<>();
            skipSpaces();
            expect('[');
            skipSpaces();
            if (peek() == ']') {
                return list;
            }
            while (true) {
                list.add(readObject());
                skipSpaces();
                char c = next();
                if (c == ']') {
                    return list;
                }
                if (c != ',') {
                    throw new IllegalArgumentException("Expected ',' or ']' at position " + (i - 1));
                }
            }
        }

        private StoredRecord readObject() {
            skipSpaces();
            expect('{');
            StoredRecord r = new StoredRecord();
            skipSpaces();
            if (peek() == '}') {
                i++;
                return r;
            }
            while (true) {
                skipSpaces();
                String key = readString();
                skipSpaces();
                expect(':');
                skipSpaces();
                String value = peek() == '"' ? readString() : readNumber();
                switch (key) {
                    case "messageID":     r.messageID = value; break;
                    case "messageNumber": r.messageNumber = Integer.parseInt(value); break;
                    case "messageHash":   r.messageHash = value; break;
                    case "sender":        r.sender = value; break;
                    case "recipient":     r.recipient = value; break;
                    case "message":       r.message = value; break;
                    default: break;   // ignore unknown keys
                }
                skipSpaces();
                char c = next();
                if (c == '}') {
                    return r;
                }
                if (c != ',') {
                    throw new IllegalArgumentException("Expected ',' or '}' at position " + (i - 1));
                }
            }
        }

        private String readString() {
            expect('"');
            StringBuilder sb = new StringBuilder();
            while (true) {
                char c = next();
                if (c == '"') {
                    return sb.toString();
                }
                if (c != '\\') {
                    sb.append(c);
                    continue;
                }
                char e = next();
                switch (e) {
                    case 'n': sb.append('\n'); break;
                    case 'r': sb.append('\r'); break;
                    case 't': sb.append('\t'); break;
                    case 'b': sb.append('\b'); break;
                    case 'f': sb.append('\f'); break;
                    case 'u':
                        if (i + 4 > s.length()) {
                            throw new IllegalArgumentException("Bad unicode escape");
                        }
                        sb.append((char) Integer.parseInt(s.substring(i, i + 4), 16));
                        i += 4;
                        break;
                    default: sb.append(e);   // \" \\ \/
                }
            }
        }

        private String readNumber() {
            int start = i;
            while (i < s.length() && (Character.isDigit(s.charAt(i)) || s.charAt(i) == '-')) {
                i++;
            }
            if (start == i) {
                throw new IllegalArgumentException("Expected a value at position " + i);
            }
            return s.substring(start, i);
        }

        private void skipSpaces() {
            while (i < s.length() && Character.isWhitespace(s.charAt(i))) {
                i++;
            }
        }

        private char peek() {
            if (i >= s.length()) {
                throw new IllegalArgumentException("Unexpected end of JSON");
            }
            return s.charAt(i);
        }

        private char next() {
            char c = peek();
            i++;
            return c;
        }

        private void expect(char c) {
            if (next() != c) {
                throw new IllegalArgumentException("Expected '" + c + "' at position " + (i - 1));
            }
        }
    }

    // ------------------------------------------------------------------
    // Part 3 features (all return Strings so they can be unit tested)
    // ------------------------------------------------------------------

    public static String displaySenderAndRecipient() {
        if (storedMessages.isEmpty()) {
            return "No stored messages.";
        }
        StringBuilder sb = new StringBuilder();
        for (Message m : storedMessages) {
            sb.append("Sender: ").append(m.sender)
              .append(", Recipient: ").append(m.recipient).append("\n");
        }
        return sb.toString();
    }

    public static String longestStoredMessage() {
        Message longest = null;
        for (Message m : storedMessages) {
            if (longest == null || m.messageText.length() > longest.messageText.length()) {
                longest = m;
            }
        }
        return longest == null ? "No stored messages." : longest.messageText;
    }

    /** Searches sent and stored messages. */
    public static String searchByMessageID(String id) {
        for (Message m : allMessages()) {
            if (m.messageID.equals(id)) {
                return "Recipient: " + m.recipient + ", Message: " + m.messageText;
            }
        }
        return "Message ID not found.";
    }

    /** Searches sent and stored messages for one recipient. */
    public static String searchByRecipient(String number) {
        StringBuilder sb = new StringBuilder();
        for (Message m : allMessages()) {
            if (m.recipient.equals(number)) {
                if (sb.length() > 0) {
                    sb.append(" ");
                }
                sb.append("\"").append(m.messageText).append("\"");
            }
        }
        return sb.length() == 0 ? "No messages found for this recipient." : sb.toString();
    }

    public static String deleteByHash(String hash) {
        Message found = null;
        for (Message m : storedMessages) {
            if (m.messageHash.equalsIgnoreCase(hash.trim())) {
                found = m;
                break;
            }
        }
        if (found == null) {
            return "Message hash not found.";
        }
        storedMessages.remove(found);
        messageHashes.remove(found.messageHash);
        messageIDs.remove(found.messageID);
        saveStoredMessages();
        return "Message: \"" + found.messageText + "\" successfully deleted.";
    }

    /** Full details of every sent and stored message. */
    public static String displayReport() {
        StringBuilder sb = new StringBuilder("===== MESSAGE REPORT =====\n");
        sb.append("--- Sent messages ---\n");
        appendReport(sb, sentMessages);
        sb.append("--- Stored messages ---\n");
        appendReport(sb, storedMessages);
        return sb.toString();
    }

    private static void appendReport(StringBuilder sb, List<Message> list) {
        if (list.isEmpty()) {
            sb.append("(none)\n");
        }
        for (Message m : list) {
            sb.append("Message ID: ").append(m.messageID).append("\n")
              .append("Message Hash: ").append(m.messageHash).append("\n")
              .append("Recipient: ").append(m.recipient).append("\n")
              .append("Message: ").append(m.messageText).append("\n\n");
        }
    }

    // ------------------------------------------------------------------
    // Helpers
    // ------------------------------------------------------------------

    private static List<Message> allMessages() {
        List<Message> all = new ArrayList<>(sentMessages);
        all.addAll(storedMessages);
        return all;
    }

    private static void track(Message m) {
        if (!messageIDs.contains(m.messageID)) {
            messageIDs.add(m.messageID);
        }
        if (!messageHashes.contains(m.messageHash)) {
            messageHashes.add(m.messageHash);
        }
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

    /** Message ID, Message Hash, Recipient, Message (the order the brief asks for). */
    public String details() {
        return "Message ID: " + messageID + "\n"
                + "Message Hash: " + messageHash + "\n"
                + "Recipient: " + recipient + "\n"
                + "Message: " + messageText + "\n";
    }

    private static String clean(String word) {
        return word.replaceAll("[^A-Za-z0-9]", "");
    }

    // ---- getters / test helpers ----

    public String getMessageID() { return messageID; }
    public String getMessageHash() { return messageHash; }
    public String getSender() { return sender; }
    public String getRecipient() { return recipient; }
    public String getMessageText() { return messageText; }

    public static List<Message> getSentMessages() { return new ArrayList<>(sentMessages); }
    public static List<Message> getDisregardedMessages() { return new ArrayList<>(disregardedMessages); }
    public static List<Message> getStoredMessages() { return new ArrayList<>(storedMessages); }
    public static List<String> getMessageHashes() { return new ArrayList<>(messageHashes); }
    public static List<String> getMessageIDs() { return new ArrayList<>(messageIDs); }

    public static void setSender(String sender) { currentSender = sender; }
    public static void setStorageFile(String path) { storageFile = path; }

    public static void resetState() {
        sentMessages.clear();
        disregardedMessages.clear();
        storedMessages.clear();
        messageHashes.clear();
        messageIDs.clear();
        totalMessagesSent = 0;
    }
}