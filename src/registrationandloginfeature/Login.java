/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */

package registrationandloginfeature;
import java.util.regex.Pattern;

/**
 *
 * @author kgmas
 */

public class Login {

    private String userName;
    private String password;
    private String cellPhoneNumber;
    private String firstName;
    private String lastName;

    // South African cellphone number format, e.g. +27838968976
    private static final Pattern CELLPHONE_PATTERN =
            Pattern.compile("^\\+27\\d{9}$");

    public Login(String userName, String password, String cellPhoneNumber,
                 String firstName, String lastName) {
        this.userName = userName;
        this.password = password;
        this.cellPhoneNumber = cellPhoneNumber;
        this.firstName = firstName;
        this.lastName = lastName;
    }

    // ---------- Getters ----------

    public String getUserName() {
        return userName;
    }

    public String getPassword() {
        return password;
    }

    public String getCellPhoneNumber() {
        return cellPhoneNumber;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    // ---------- Validation ----------

    /**
     * Username must be no more than 5 characters long and must contain
     * at least one uppercase letter, one lowercase letter, one digit,
     * and one special character.
     */
    public boolean checkUserName() {
        return isValidUserName(userName);
    }

    /**
     * Password must be at least 8 characters long and must contain
     * at least one uppercase letter, one lowercase letter, one digit,
     * and one special character.
     */
    public boolean checkPasswordComplexity() {
        return isValidPassword(password);
    }

    /**
     * Cellphone number must be in the South African format:
     * "+27" followed by exactly 9 digits, e.g. +27838968976.
     */
    public boolean checkCellPhoneNumber() {
        return isValidCellPhone(cellPhoneNumber);
    }

    /**
     * First and last names must not be empty and must contain letters only
     * (spaces and hyphens allowed for double-barrelled names).
     */
    public boolean checkFirstName() {
        return isValidName(firstName);
    }

    public boolean checkLastName() {
        return isValidName(lastName);
    }

    // ---------- Static validators ----------
    // These take a raw candidate String directly, so they can validate
    // user input before a Login object even exists (e.g. during registration).

    public static boolean isValidUserName(String candidate) {
        if (candidate == null || candidate.isEmpty() || candidate.length() > 5) {
            return false;
        }
        return hasUpper(candidate) && hasLower(candidate)
                && hasDigit(candidate) && hasSpecial(candidate);
    }

    public static boolean isValidPassword(String candidate) {
        if (candidate == null || candidate.length() < 8) {
            return false;
        }
        return hasUpper(candidate) && hasLower(candidate)
                && hasDigit(candidate) && hasSpecial(candidate);
    }

    public static boolean isValidCellPhone(String candidate) {
        if (candidate == null) {
            return false;
        }
        return CELLPHONE_PATTERN.matcher(candidate).matches();
    }

    public static boolean isValidName(String candidate) {
        if (candidate == null || candidate.trim().isEmpty()) {
            return false;
        }
        return candidate.matches("[A-Za-z\\-\\s]+");
    }

    // ---------- Helpers ----------

    private static boolean hasUpper(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isUpperCase(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasLower(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isLowerCase(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasDigit(String s) {
        for (char c : s.toCharArray()) {
            if (Character.isDigit(c)) {
                return true;
            }
        }
        return false;
    }

    private static boolean hasSpecial(String s) {
        for (char c : s.toCharArray()) {
            if (!Character.isLetterOrDigit(c) && !Character.isWhitespace(c)) {
                return true;
            }
        }
        return false;
    }
}

