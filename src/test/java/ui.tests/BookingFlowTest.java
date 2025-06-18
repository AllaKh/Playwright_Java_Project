package ui.tests;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.LoadState;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.BookingPage;
import ui.core.BasePlaywrightTest;
import utils.TestDataLoader;

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

    private static final String G_NAME = TestDataLoader.get("name");
    private static final String G_MAIL = TestDataLoader.get("email");
    private static final String G_PHONE = TestDataLoader.get("phone");

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeMethod
    public void setUp() {
        booking = new BookingPage(page);
    }

    private void createBooking(String roomId, LocalDate checkIn, LocalDate checkOut) {
        page.navigate("https://automationintesting.online/reservation/" + roomId +
                "?checkin=" + checkIn.format(FMT) +
                "&checkout=" + checkOut.format(FMT));

        booking.completeBooking(
                G_NAME.split(" ")[0],
                G_NAME.split(" ")[1],
                G_MAIL,
                G_PHONE
        );

        booking.waitForConfirmation(15_000);
        booking.returnHome();
        page.waitForLoadState(LoadState.NETWORKIDLE);
    }

    /** Happy‑path booking with randomly chosen future dates */
    @Test(description = "Correct booking with random future date")
    public void successfulBooking() {
        int offset = random.nextInt(15);
        LocalDate checkIn = LocalDate.now().plusDays(offset);
        LocalDate checkOut = checkIn.plusDays(1);
        createBooking("2", checkIn, checkOut);
    }

    /** Past‑date booking should raise a client‑side exception banner */
    @Test(description = "Booking on a past date should show an error")
    public void pastDateBookingShowsError() {
        LocalDate checkIn = LocalDate.now().minusMonths(1);
        LocalDate checkOut = checkIn.plusDays(1);

        page.navigate("https://automationintesting.online/reservation/2" +
                "?checkin=" + checkIn.format(FMT) +
                "&checkout=" + checkOut.format(FMT));

        booking.completeBooking(
                G_NAME.split(" ")[0],
                G_NAME.split(" ")[1],
                G_MAIL,
                G_PHONE
        );

        Locator errorBanner = page.locator("text=Application error: a client-side exception has occurred");
        errorBanner.waitFor(new Locator.WaitForOptions()
                .setState(com.microsoft.playwright.options.WaitForSelectorState.VISIBLE)
                .setTimeout(15_000));

        Assert.assertTrue(errorBanner.isVisible(),
                "Error banner should appear when booking with past dates");
    }

    /** Cancelling the booking‑form collapses it again */
    @Test(description = "Cancelling the booking form hides it")
    public void cancelBookingForm() {
        LocalDate checkIn = LocalDate.now().plusDays(3);
        LocalDate checkOut = checkIn.plusDays(1);

        page.navigate("https://automationintesting.online/reservation/2" +
                "?checkin=" + checkIn.format(FMT) +
                "&checkout=" + checkOut.format(FMT));

        page.locator("button.btn.btn-primary:has-text('Reserve Now')").first().click();
        page.locator("button.btn.btn-secondary:has-text('Cancel')").click();

        boolean collapsed = page.locator("input.room-firstname").isHidden();
        Assert.assertTrue(collapsed, "Booking form should be hidden after Cancel");
    }
}
