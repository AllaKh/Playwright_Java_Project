package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

import pages.HomePage;
import pages.BookingPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Deletes an existing booking and verifies that the booking form becomes visible again.
 */
public class DeleteBookingTest {
    private Playwright playwright;
    private Browser browser;
    private Page page;

    private HomePage home;
    private BookingPage booking;

    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeMethod
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();

        home = new HomePage(page);
        booking = new BookingPage(page);
    }

    @AfterMethod
    public void teardown() {
        browser.close();
        playwright.close();
    }

    @Test(description = "Create and delete a booking; verify that form reappears")
    public void deleteBookingSuccessfully() {
        LocalDate today = LocalDate.now();
        String checkIn = today.plusDays(1).format(formatter);
        String checkOut = today.plusDays(4).format(formatter);

        // Go to the Suite room booking page
        home.goToRoom("suite", checkIn, checkOut);

        // Fill and submit the booking form
        booking.fillBookingForm("Jane", "Doe", "jane@example.com", "123456789");
        booking.submitBooking();

        // Confirm that booking is successful
        Assert.assertTrue(booking.isConfirmationVisible(), "Booking confirmation not visible");

        // Click the Delete button
        booking.deleteBooking();

        // Verify that the booking form is visible again
        Assert.assertTrue(booking.isFormVisible(), "Booking form should be visible after deletion");
    }
}
