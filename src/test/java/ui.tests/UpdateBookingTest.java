package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;

import pages.HomePage;
import pages.BookingPage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class UpdateBookingTest {
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

    @Test(description = "Create and update booking")
    public void updateBookingTest() {
        LocalDate today = LocalDate.now();
        String checkIn = today.plusDays(1).format(formatter);
        String checkOut = today.plusDays(4).format(formatter);

        // Go to suite room reservation with properly formatted dates
        home.goToRoom("suite", checkIn, checkOut);

        // Fill booking form and submit
        booking.fillBookingForm("Alice", "Smith", "alice@example.com", "12345678901");
        booking.submitBooking();
        Assert.assertTrue(booking.isConfirmationVisible(), "Booking confirmation should be visible");

        // Now update booking: change first name and phone
        booking.fillBookingForm("AliceUpdated", "Smith", "alice@example.com", "09876543210");
        booking.submitBooking();

        // Confirmation message should still be visible (updated)
        Assert.assertTrue(booking.isConfirmationVisible(), "Booking confirmation should be visible after update");
    }
}
