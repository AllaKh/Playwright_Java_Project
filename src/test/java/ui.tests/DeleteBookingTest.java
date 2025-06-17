package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.BookingPage;
import pages.HomePage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Creates a booking, deletes it, and verifies that the form re‑appears.
 */
public class DeleteBookingTest {

    private Playwright playwright;
    private Browser    browser;
    private Page       page;

    private HomePage   home;
    private BookingPage booking;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    /* ── lifecycle ──────────────────────────────────────────────── */
    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();

        home    = new HomePage(page);
        booking = new BookingPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    /* ── test ───────────────────────────────────────────────────── */
    @Test(description = "Create a booking, delete it, verify that the form becomes visible again")
    public void deleteBookingSuccessfully() {

        /* Build date range */
        LocalDate today = LocalDate.now();
        String checkIn  = today.plusDays(1).format(FMT);
        String checkOut = today.plusDays(4).format(FMT);

        /* Create booking */
        home.goToRoom("suite", checkIn, checkOut);
        booking.completeBooking(
                "Jane", "Doe", "jane@example.com", "12345678901");

        String range = booking.waitForConfirmation(15_000);
        Assert.assertFalse(range.isEmpty(), "Booking confirmation not visible");

        /* Delete and verify form returns */
        booking.deleteBooking();
        Assert.assertTrue(booking.isFormVisible(),
                "Booking form should be visible after deletion");
    }
}
