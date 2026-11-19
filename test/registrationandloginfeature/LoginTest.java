/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import static org.junit.Assert.*;

import org.junit.Test;

public class LoginTest {

    private static final String GOOD_PASSWORD = "Ch&&sec@ke99!";
    private static final String GOOD_CELL = "+27838968976";

    private Login user(String userName, String password, String cell) {
        return new Login(userName, password, cell, "Kyle", "Smith");
    }

    // ---------- assertEquals ----------

    @Test
    public void username_correctlyFormatted() {
        assertEquals("Username successfully captured.", Login.userNameMessage("kyl_1"));
    }

    @Test
    public void username_incorrectlyFormatted() {
        assertEquals("Username is not correctly formatted; please ensure that your username "
                + "contains an underscore and is no more than five characters in length.",
                Login.userNameMessage("kyle!!!!!!!"));
    }

    @Test
    public void password_meetsComplexity() {
        assertEquals("Password successfully captured.", Login.passwordMessage(GOOD_PASSWORD));
    }

    @Test
    public void password_doesNotMeetComplexity() {
        assertEquals("Password is not correctly formatted; please ensure that the password "
                + "contains at least eight characters, a capital letter, a number, and a special character.",
                Login.passwordMessage("password"));
    }

    @Test
    public void cell_correctlyFormatted() {
        assertEquals("Cell number successfully captured.", Login.cellMessage(GOOD_CELL));
    }

    @Test
    public void cell_incorrectlyFormatted() {
        assertEquals("Cell number is incorrectly formatted or does not contain an international code; "
                + "please correct the number and try again.", Login.cellMessage("08966553"));
    }

    @Test
    public void registerUser_allValid() {
        assertEquals("Username successfully captured.\nPassword successfully captured.\n"
                + "Cell number successfully captured.",
                user("kyl_1", GOOD_PASSWORD, GOOD_CELL).registerUser());
    }

    @Test
    public void registerUser_badUsername() {
        assertEquals(Login.USERNAME_BAD, user("kyle!!!!!!!", GOOD_PASSWORD, GOOD_CELL).registerUser());
    }

    @Test
    public void registerUser_badPassword() {
        assertEquals(Login.PASSWORD_BAD, user("kyl_1", "password", GOOD_CELL).registerUser());
    }

    @Test
    public void loginStatus_success() {
        Login u = user("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("Welcome Kyle, Smith it is great to see you again.",
                u.returnLoginStatus(u.loginUser("kyl_1", GOOD_PASSWORD)));
    }

    @Test
    public void loginStatus_failure() {
        Login u = user("kyl_1", GOOD_PASSWORD, GOOD_CELL);
        assertEquals("Username or password incorrect, please try again.",
                u.returnLoginStatus(u.loginUser("kyl_1", "wrong")));
    }

    // ---------- assertTrue / assertFalse ----------

    @Test
    public void loginSuccessful() {
        assertTrue(user("kyl_1", GOOD_PASSWORD, GOOD_CELL).loginUser("kyl_1", GOOD_PASSWORD));
    }

    @Test
    public void loginFailed() {
        assertFalse(user("kyl_1", GOOD_PASSWORD, GOOD_CELL).loginUser("kyl_1", "nope"));
    }

    @Test
    public void usernameCorrectlyFormatted() {
        assertTrue(user("kyl_1", GOOD_PASSWORD, GOOD_CELL).checkUserName());
    }

    @Test
    public void usernameIncorrectlyFormatted() {
        assertFalse(user("kyle!!!!!!!", GOOD_PASSWORD, GOOD_CELL).checkUserName());
    }

    @Test
    public void passwordMeetsComplexity() {
        assertTrue(user("kyl_1", GOOD_PASSWORD, GOOD_CELL).checkPasswordComplexity());
    }

    @Test
    public void passwordFailsComplexity() {
        assertFalse(user("kyl_1", "password", GOOD_CELL).checkPasswordComplexity());
    }

    @Test
    public void cellCorrectlyFormatted() {
        assertTrue(user("kyl_1", GOOD_PASSWORD, GOOD_CELL).checkCellPhoneNumber());
    }

    @Test
    public void cellIncorrectlyFormatted() {
        assertFalse(user("kyl_1", GOOD_PASSWORD, "08966553").checkCellPhoneNumber());
    }
}