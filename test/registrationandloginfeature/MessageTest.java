/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

public class MessageTest {

    private Message msg1;   // Test data: message 1 (Send)
    private Message msg2;   // Test data: message 2 (Discard)

    @Before
    public void setUp() {
        Message.resetState();
        msg1 = new Message("0012345678", 0, "+27718693002",
                "Hi Mike, can you join us for dinner tonight?");
        msg2 = new Message("0087654321", 1, "08575975889",
                "Hi Keegan, did you receive the payment?");
    }

    @Test
    public void messageLength_success() {
        assertEquals("Message ready to send.", msg1.checkMessageLength());
    }

    @Test
    public void messageLength_failure() {
        String longText = "a".repeat(260);
        Message m = new Message("0012345678", 0, "+27718693002", longText);
        assertEquals("Message exceeds 250 characters by 10; please reduce the size.",
                m.checkMessageLength());
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
        String[] ids   = {"0012345678", "0087654321"};
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
        Message m = new Message(0, "+27718693002", "Hello there");
        assertTrue(m.checkMessageID());
        assertEquals(10, m.getMessageID().length());
        assertEquals("Message ID generated: " + m.getMessageID(), m.messageIDGenerated());
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
        msg1.sentMessage(1);   // sent
        msg2.sentMessage(2);   // discarded, not counted
        assertEquals(1, Message.returnTotalMessages());
    }
}
