package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

/**
 * End-to-end booking flow tests:
 *  • Booking on valid/invalid dates
 *  • Booking form cancelation
 *  • Confirmation and navigation checks
 */
public class BookingFlowTest {

    private Playwright playwright;
    private Browser    browser;
    private Page       page;

    private BookingPage booking;

    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeClass
    public void setupClass() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeMethod
    public void setup() {
        page = browser.newPage();
        booking = new BookingPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        page.close();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        browser.close();
        playwright.close();
    }

    /**
     * Booking with a random valid future date and validating confirmation
     */
    @Test(description = "Booking flow on a random valid future date with confirmation validation")
    public void bookingFlowRandomDate() {
        LocalDate today = LocalDate.now();
        int offset = ThreadLocalRandom.current().nextInt(2, 30);
        String checkIn = today.plusDays(offset).format(FMT);
        String checkOut = today.plusDays(offset + 1).format(FMT);

        // Navigate directly to Double Room (id=2)
        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        // Fill out and submit the booking form
        booking.completeBooking("John", "Doe", "john.doe@example.com", "12345678901");

        // Wait for confirmation message and validate the date range
        String range = booking.waitForConfirmation(15_000);
        String expectedRange = checkIn + " - " + checkOut;
        Assert.assertTrue(range.contains(expectedRange),
                "Confirmation should contain the expected date range: " + expectedRange);

        // Return to home and validate URL
        booking.returnHome();
        page.waitForURL("https://automationintesting.online/");
        Assert.assertEquals(page.url(), "https://automationintesting.online/",
                "Should return to the home page after booking confirmation");
    }

    /**
     * Cancels the booking form after it is revealed and confirms that
     * the name input disappears (form collapses back).
     */
    @Test(description = "User opens the booking form and presses Cancel")
    public void cancelBookingForm() {

        LocalDate today  = LocalDate.now();
        String checkIn   = today.plusDays(3).format(FMT);
        String checkOut  = today.plusDays(4).format(FMT);

        // Open reservation page
        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        // Reveal the form
        page.locator("button.btn.btn-primary:has-text('Reserve Now')").first().click();

        // Click Cancel inside the form
        page.locator("button.btn.btn-secondary:has-text('Cancel')").click();

        // Assert form is hidden
        boolean formCollapsed = page.locator("input.room-firstname").isHidden();
        Assert.assertTrue(formCollapsed, "Booking form should be hidden after Cancel");
    }

    /**
     * Attempts to book on unavailable (past) dates and expects a client-side exception banner.
     */
    @Test(description = "Booking flow on invalid (past) dates shows error banner")
    public void bookingFlowInvalidDates() {
        LocalDate today = LocalDate.now();
        String checkIn  = today.minusDays(10).format(FMT); // 10 days ago
        String checkOut = today.minusDays(9).format(FMT);  // 9 days ago

        // Navigate to reservation page
        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        // Attempt booking with past dates
        booking.completeBooking("Fail", "Case", "fail.case@example.com", "12345678901");

        // Verify error banner is shown
        Locator errorBanner = page.locator(
                "text=Application error: a client-side exception has occurred");

        errorBanner.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(15_000));

        Assert.assertTrue(errorBanner.isVisible(),
                "Error banner should appear when booking with past dates");
    }
}
