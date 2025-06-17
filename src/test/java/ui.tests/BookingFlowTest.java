package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * End‑to‑end happy‑path booking flow:
 *  • Choose date range via direct URL
 *  • Run two‑step Reserve‑Now sequence
 *  • Verify confirmation banner and date range
 *  • Click “Return home” and check URL
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

    @Test(description = "Full booking flow with confirmation and return home")
    public void bookingFlow() {
        /* Build check‑in/check‑out from tomorrow → +1 day */
        LocalDate today = LocalDate.now();
        String checkIn  = today.plusDays(1).format(FMT);
        String checkOut = today.plusDays(2).format(FMT);

        /* Direct navigation to Double Room (id=2) */
        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        /* Complete the two‑step booking */
        booking.completeBooking(
                "John", "Doe", "john.doe@example.com", "12345678901");

        /* Wait for confirmation and verify dates */
        String range = booking.waitForConfirmation(15_000);
        String expectedRange = checkIn + " - " + checkOut;
        Assert.assertTrue(range.contains(expectedRange),
                "Confirmation range should contain: " + expectedRange);

        /* Return home and assert URL */
        booking.returnHome();
        page.waitForURL("https://automationintesting.online/");
        Assert.assertTrue(page.url().equals("https://automationintesting.online/"),
                "Should be back on Home page");
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

        // open reservation page
        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        // reveal form (first Reserve Now)
        page.locator("button.btn.btn-primary:has-text('Reserve Now')").first().click();

        // click Cancel (grey btn inside the form)
        page.locator("button.btn.btn-secondary:has-text('Cancel')").click();

        // first‑name field should disappear => form collapsed
        boolean formCollapsed = page.locator("input.room-firstname").isHidden();
        Assert.assertTrue(formCollapsed, "Booking form should be hidden after Cancel");
    }

    /**
     * Attempts to book on unavailable dates and expects a client‑side exception banner.
     */
    @Test(description = "Full booking flow on invalid dates shows client‑side error banner")
    public void bookingFlowInvalidDates() {

        /* Build an obviously unavailable (past) date range */
        LocalDate today   = LocalDate.now();
        String checkIn    = today.minusDays(10).format(FMT);   // 10 days ago
        String checkOut   = today.minusDays(9).format(FMT);    // 9 days ago

        /* Direct navigation to Double Room (id = 2) */
        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        /* Attempt the normal two‑step booking flow */
        booking.completeBooking(
                "Fail", "Case", "fail.case@example.com", "12345678901");

        /* The SPA should crash ⇒ runtime‑error banner */
        Locator errorBanner = page.locator(
                "text=Application error: a client-side exception has occurred");

        errorBanner.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(15_000));

        Assert.assertTrue(errorBanner.isVisible(),
                "Client‑side exception banner should be visible for invalid dates");
    }
}
