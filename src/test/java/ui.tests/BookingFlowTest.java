package ui.tests;

import com.microsoft.playwright.Locator;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.BookingPage;
import ui.core.BasePlaywrightTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * End‑to‑end booking‑flow tests:
 *   • Happy path on a random (future) date
 *   • Cancel‑form flow
 *   • Negative flow on past dates
 */
public class BookingFlowTest extends BasePlaywrightTest {

    private BookingPage booking;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeMethod
    public void setUpBookingPage() {
        booking = new BookingPage(page);
    }

    /** Happy‑path booking with randomly chosen future dates */
    @Test(description = "Booking flow on a random valid future date with confirmation validation")
    public void bookingFlowRandomDate() {
        LocalDate today = LocalDate.now();

        // 2 – 59 days ahead  ⇒ random.nextInt(bound) is exclusive, so (58) + 2 reproduces [2, 60)
        int offset = random.nextInt(60) + 2;
        String checkIn  = today.plusDays(offset).format(FMT);
        String checkOut = today.plusDays(offset + 1).format(FMT);

        // Navigate directly to Double Room (id=2)
        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        // Complete booking
        booking.completeBooking("John", "Doe", "john.doe@example.com", "12345678901");

        // Confirm dates
        String range         = booking.waitForConfirmation(15_000);
        String expectedRange = checkIn + " - " + checkOut;
        Assert.assertTrue(range.contains(expectedRange),
                "Confirmation should contain the expected date range: " + expectedRange);

        // Back to home
        booking.returnHome();
        page.waitForURL("https://automationintesting.online/");
        Assert.assertEquals(page.url(), "https://automationintesting.online/",
                "Should return to the home page after booking confirmation");
    }

    /** Cancelling the booking‑form collapses it again */
    @Test(description = "User opens the booking form and presses Cancel")
    public void cancelBookingForm() {
        LocalDate today  = LocalDate.now();
        String checkIn   = today.plusDays(3).format(FMT);
        String checkOut  = today.plusDays(4).format(FMT);

        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        // Reveal and then cancel
        page.locator("button.btn.btn-primary:has-text('Reserve Now')").first().click();
        page.locator("button.btn.btn-secondary:has-text('Cancel')").click();

        // Form should collapse (firstname field hidden)
        boolean collapsed = page.locator("input.room-firstname").isHidden();
        Assert.assertTrue(collapsed, "Booking form should be hidden after Cancel");
    }

    /** Past‑date booking should raise a client‑side exception banner */
    @Test(description = "Booking flow on invalid (past) dates shows error banner")
    public void bookingFlowInvalidDates() {
        LocalDate today   = LocalDate.now();
        String checkIn    = today.minusDays(10).format(FMT);
        String checkOut   = today.minusDays(9).format(FMT);

        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        booking.completeBooking("Fail", "Case", "fail.case@example.com", "12345678901");

        Locator errorBanner = page.locator(
                "text=Application error: a client-side exception has occurred");

        errorBanner.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(15_000));

        Assert.assertTrue(errorBanner.isVisible(),
                "Error banner should appear when booking with past dates");
    }
}
