package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.BookingPage;
import pages.HomePage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Creates an initial booking, returns home, then creates a second
 * booking with updated first‑name / phone to simulate an “update”.
 */
public class UpdateBookingTest {

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
    @Test(description = "Create a booking, then create another one with updated data")
    public void updateBookingTest() {

        /* Build date range */
        LocalDate today = LocalDate.now();
        String checkIn  = today.plusDays(1).format(FMT);
        String checkOut = today.plusDays(4).format(FMT);

        /* —— First booking —— */
        home.goToRoom("suite", checkIn, checkOut);
        booking.completeBooking(
                "Alice", "Smith", "alice@example.com", "12345678901");

        String range1 = booking.waitForConfirmation(15_000);
        Assert.assertFalse(range1.isEmpty(), "First booking confirmation not visible");

        /* Return home */
        booking.returnHome();
        page.waitForURL("https://automationintesting.online/");

        /* —— Second booking (updated details) —— */
        home.goToRoom("suite", checkIn, checkOut);
        booking.completeBooking(
                "AliceUpdated", "Smith", "alice@example.com", "09876543210");

        String range2 = booking.waitForConfirmation(15_000);
        Assert.assertFalse(range2.isEmpty(), "Updated booking confirmation not visible");
    }
}
