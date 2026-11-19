/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import static org.junit.Assert.*;

import java.io.File;
import java.util.List;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class MessageTest {

    private static final String TEST_FILE = "test_messages.json";
    private static final String LATE_TEXT = "Where are you? You are late! I have asked you to be on time.";

    private Message msg1;   // Part 2 test data: message 1 (Send)
    private Message msg2;   // Part 2 test data: message 2 (Discard)

    @Before
    public void setUp() {
        Message.setStorageFile(TEST_FILE);
        new File(TEST_FILE).delete();
        Message.resetState();
        msg1 = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        msg2 = new Message("0087654321", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    @After
    public void tearDown() {
        new File(TEST_FILE).delete();
    }

    // ======================= Part 2 =======================

    @Test
    public void messageLength_success() {
        assertEquals("Message ready to send.", msg1.checkMessageLength());
    }

    @Test
    public void messageLength_failure() {
        Message m = new Message("0012345678", 0, "+27718693002", "a".repeat(260));
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                m.checkMessageLength());
    }

    @Test
    public void messageLength_exactly250() {
        Message m = new Message("0012345678", 0, "+27718693002", "a".repeat(250));
        assertEquals("Message ready to send.", m.checkMessageLength());
    }

    @Test
    public void recipient_success() {
        assertEquals("Cell phone number successfully captured.", msg1.checkRecipientCell());
    }

    @Test
    public void recipient_failure() {
        assertEquals("Cell phone number is incorrectly formatted or does not contain an "
                + "international code. Please correct the number and try again.",
                msg2.checkRecipientCell());
    }

    @Test
    public void messageHash_testCase1() {
        assertEquals("00:0:HITONIGHT", msg1.createMessageHash());
    }

    @Test
    public void messageHash_remainingMessagesInLoop() {
        String[] ids = {"0012345678", "0087654321"};
        String[] texts = {"Hi Mike, can you join us for dinner tonight?",
                          "Hi Keegan, did you receive the payment?"};
        String[] expected = {"00:0:HITONIGHT", "00:1:HIPAYMENT"};
        for (int i = 0; i < texts.length; i++) {
            Message m = new Message(ids[i], i, "+27718693002", texts[i]);
            assertEquals(expected[i], m.createMessageHash());
        }
    }

    @Test
    public void messageID_created() {
        Message m = new Message("+27718693002", "Hello there");
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
    }

    @Test
    public void messageID_tooLong() {
        assertFalse(new Message("00123456789", 0, "+27718693002", "Hello there").checkMessageID());
    }

    @Test
    public void messageSent_send() {
        assertEquals("Message successfully sent.", msg1.sentMessage(1));
    }

    @Test
    public void messageSent_disregard() {
        assertEquals("Press 0 to delete the message.", msg2.sentMessage(2));
    }

    @Test
    public void messageSent_store() {
        assertEquals("Message successfully stored.", msg1.sentMessage(3));
    }

    @Test
    public void totalMessages_afterSendAndDiscard() {
        msg1.sentMessage(1);
        msg2.sentMessage(2);
        assertEquals(1, Message.returnTotalMessages());
    }

    // ======================= Part 3 =======================

    private Message m1, m2, m3, m4, m5;

    /** Loads the Part 3 test data and applies each message's flag. */
    private void loadPart3Data() {
        Message.resetState();
        m1 = new Message("0000000001", 0, "+27834557896", "Did you get the cake?");
        m2 = new Message("0000000002", 1, "+27838884567", LATE_TEXT);
        m3 = new Message("0000000003", 2, "+27834484567", "Yohoooo, I am at your gate.");
        m4 = new Message("0000000004", 3, "0838884567", "It is dinner time!");
        m5 = new Message("0000000005", 4, "+27838884567", "Ok, I am leaving without you.");

        m1.sentMessage(1);   // Sent
        m2.sentMessage(3);   // Stored
        m3.sentMessage(2);   // Disregard
        m4.sentMessage(1);   // Sent
        m5.sentMessage(3);   // Stored
    }

    @Test
    public void sentMessagesArray_populated() {
        loadPart3Data();
        List<Message> sent = Message.getSentMessages();
        assertEquals("Did you get the cake?", sent.get(0).getMessageText());
        assertEquals("It is dinner time!", sent.get(1).getMessageText());
    }

    @Test
    public void disregardedAndStoredArrays_populated() {
        loadPart3Data();
        assertEquals(1, Message.getDisregardedMessages().size());
        assertEquals(2, Message.getStoredMessages().size());
        assertEquals(4, Message.getMessageIDs().size());   // sent + stored (not disregarded)
        assertEquals(4, Message.getMessageHashes().size());
    }

    @Test
    public void longestStoredMessage() {
        loadPart3Data();
        assertEquals(LATE_TEXT, Message.longestStoredMessage());
    }

    @Test
    public void searchByMessageID() {
        loadPart3Data();
        assertEquals("Recipient: 0838884567, Message: It is dinner time!",
                Message.searchByMessageID(m4.getMessageID()));
    }

    @Test
    public void searchByRecipient() {
        loadPart3Data();
        assertEquals("\"" + LATE_TEXT + "\" \"Ok, I am leaving without you.\"",
                Message.searchByRecipient("+27838884567"));
    }

    @Test
    public void deleteByHash() {
        loadPart3Data();
        assertEquals("Message: \"" + LATE_TEXT + "\" successfully deleted.",
                Message.deleteByHash(m2.getMessageHash()));
        assertEquals(1, Message.getStoredMessages().size());
    }

    @Test
    public void deleteByHash_notFound() {
        loadPart3Data();
        assertEquals("Message hash not found.", Message.deleteByHash("99:9:NOPE"));
    }

    @Test
    public void displayReport_containsHashRecipientAndMessage() {
        loadPart3Data();
        String report = Message.displayReport();
        assertTrue(report.contains(m1.getMessageHash()));
        assertTrue(report.contains("+27834557896"));
        assertTrue(report.contains("Did you get the cake?"));
        assertTrue(report.contains(m2.getMessageHash()));
        assertTrue(report.contains(LATE_TEXT));
    }

    @Test
    public void storedMessages_surviveJsonRoundTrip() {
        loadPart3Data();
        Message.resetState();                 // wipe memory; the file stays on disk
        Message.loadStoredMessages();
        List<Message> stored = Message.getStoredMessages();
        assertEquals(2, stored.size());
        assertEquals(LATE_TEXT, stored.get(0).getMessageText());
    }
}