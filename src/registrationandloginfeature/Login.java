/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;

import java.util.regex.Pattern;

public class Login {

    // Cell phone rule: international code (+ and 1-3 digits) followed by a number
    // of no more than ten digits.
    // REFERENCE: [add the website/author you used for this regex, plus the date you
    // accessed it. The brief requires a proper attribution here.]
    private static final Pattern CELL_PATTERN = Pattern.compile("^\\+\\d{1,3}\\d{1,10}$");

    // Messages (from the brief)
    public static final String USERNAME_OK = "Username successfully captured.";
    public static final String USERNAME_BAD = "Username is not correctly formatted; please ensure that "
            + "your username contains an underscore and is no more than five characters in length.";
    public static final String PASSWORD_OK = "Password successfully captured.";
    public static final String PASSWORD_BAD = "Password is not correctly formatted; please ensure that "
            + "the password contains at least eight characters, a capital letter, a number, and a special character.";
    public static final String CELL_OK = "Cell number successfully captured.";
    public static final String CELL_BAD = "Cell number is incorrectly formatted or does not contain an "
            + "international code; please correct the number and try again.";

    private final String userName;
    private final String password;
    private final String cellPhoneNumber;
    private final String firstName;
    private final String lastName;

    public Login(String userName, String password, String cellPhoneNumber,
                 String firstName, String lastName) {
        this.userName = userName;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ---------- Static validators (usable before a Login object exists) ----------

    public static boolean isValidUserName(String s) {
        return s != null && s.contains("_") && s.length() <= 5;
    }

    public static boolean isValidPassword(String s) {
        if (s == null || s.length() < 8) {
            return false;
        }
        boolean upper = s.chars().anyMatch(Character::isUpperCase);
        boolean digit = s.chars().anyMatch(Character::isDigit);
        boolean special = s.chars().anyMatch(c -> !Character.isLetterOrDigit(c) && !Character.isWhitespace(c));
        return upper && digit && special;
    }

    public static boolean isValidCellPhone(String s) {
        return s != null && CELL_PATTERN.matcher(s).matches();
    }

    public static String userNameMessage(String s) {
        return isValidUserName(s) ? USERNAME_OK : USERNAME_BAD;
    }

    public static String passwordMessage(String s) {
        return isValidPassword(s) ? PASSWORD_OK : PASSWORD_BAD;
    }

    public static String cellMessage(String s) {
        return isValidCellPhone(s) ? CELL_OK : CELL_BAD;
    }

    // ---------- Methods required by the brief ----------

    public boolean checkUserName() {
        return isValidUserName(userName);
    }

    public boolean checkPasswordComplexity() {
        return isValidPassword(password);
    }

    public boolean checkCellPhoneNumber() {
        return isValidCellPhone(cellPhoneNumber);
    }

    /** Returns the first problem found, or all three success messages. */
    public String registerUser() {
        if (!checkUserName()) {
            return USERNAME_BAD;
        }
        if (!checkPasswordComplexity()) {
            return PASSWORD_BAD;
        }
        if (!checkCellPhoneNumber()) {
            return CELL_BAD;
        }
        return USERNAME_OK + "\n" + PASSWORD_OK + "\n" + CELL_OK;
    }

    /** True if the entered details match the registered details. */
    public boolean loginUser(String enteredUserName, String enteredPassword) {
        return userName.equals(enteredUserName) && password.equals(enteredPassword);
    }

    public String returnLoginStatus(boolean loggedIn) {
        if (loggedIn) {
            return "Welcome " + firstName + ", " + lastName + " it is great to see you again.";
        }
        return "Username or password incorrect, please try again.";
    }

    // ---------- Getters ----------

    public String getUserName() { return userName; }
    public String getPassword() { return password; }
    public String getCellPhoneNumber() { return cellPhoneNumber; }
    public String getFirstName() { return firstName; }
    public String getLastName() { return lastName; }
}