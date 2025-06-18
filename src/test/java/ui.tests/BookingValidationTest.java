package ui.tests;

import com.microsoft.playwright.Page;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BookingPage;
import ui.core.BasePlaywrightTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Negative‑path validation suite for the room‑booking flow.
 */
public class BookingValidationTest extends BasePlaywrightTest {

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    private BookingPage booking;

    /** Opens the reservation page for tomorrow → tomorrow + 1 day before each test. */
    @BeforeMethod
    public void openReservation() {
        booking = new BookingPage(page);

        LocalDate tomorrow = LocalDate.now().plusDays(1);
        String in  = tomorrow.format(FMT);
        String out = tomorrow.plusDays(1).format(FMT);

        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + in + "&checkout=" + out);
    }

    /* validation cases */

    /* First‑name */

    @Test(
            description = "Firstname blank → 'Firstname should not be blank'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Firstname should not be blank"
    )
    public void firstnameBlank() {
        booking.completeBooking(" ", "Doe", "john@ex.com", "12345678901");
    }

    @Test(
            description = "Firstname < 3 chars → 'size must be between 3 and 30'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "size must be between 3 and 30"
    )
    public void firstnameTooShort() {
        booking.completeBooking("Jo", "Doe", "john@ex.com", "12345678901");
    }

    @Test(
            description = "Firstname > 30 chars → 'size must be between 3 and 30'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "size must be between 3 and 30"
    )
    public void firstnameTooLong() {
        booking.completeBooking("J".repeat(31), "Doe", "john@ex.com", "12345678901");
    }

    /* Last‑name */

    @Test(
            description = "Lastname blank → 'Lastname should not be blank'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Lastname should not be blank"
    )
    public void lastnameBlank() {
        booking.completeBooking("John", " ", "john@ex.com", "12345678901");
    }

    @Test(
            description = "Lastname < 3 chars → 'size must be between 3 and 18'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "size must be between 3 and 18"
    )
    public void lastnameTooShort() {
        booking.completeBooking("John", "Do", "john@ex.com", "12345678901");
    }

    @Test(
            description = "Lastname > 18 chars → 'size must be between 3 and 18'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "size must be between 3 and 18"
    )
    public void lastnameTooLong() {
        booking.completeBooking("John", "D".repeat(19), "john@ex.com", "12345678901");
    }

    /* E‑mail */

    @Test(
            description = "E‑mail blank → 'must not be empty'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "must not be empty"
    )
    public void emailBlank() {
        booking.completeBooking("John", "Doe", " ", "12345678901");
    }

    @Test(
            description = "Malformed e‑mail → 'well‑formed email address'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "well-formed email address"
    )
    public void emailMalformed() {
        booking.completeBooking("John", "Doe", "bad@", "12345678901");
    }

    /* Phone */

    @Test(
            description = "Phone blank → 'must not be empty'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "must not be empty"
    )
    public void phoneBlank() {
        booking.completeBooking("John", "Doe", "john@ex.com", " ");
    }

    @Test(
            description = "Phone < 11 chars → 'size must be between 11 and 21'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "size must be between 11 and 21"
    )
    public void phoneTooShort() {
        booking.completeBooking("John", "Doe", "john@ex.com", "1234567890");
    }

    @Test(
            description = "Phone > 21 chars → 'size must be between 11 and 21'",
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "size must be between 11 and 21"
    )
    public void phoneTooLong() {
        booking.completeBooking("John", "Doe", "john@ex.com", "1".repeat(22));
    }
}
