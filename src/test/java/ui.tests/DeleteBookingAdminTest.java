package ui.tests;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Response;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.BookingPage;
import pages.HomePage;
import ui.core.BasePlaywrightTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Creates a booking on a random future date, then attempts to reach the
 * non‑existent admin “update” endpoint for that booking.
 * If the endpoint returns **HTTP 404** we simply close the tab; otherwise
 * we try to hit a (likewise hypothetical) “delete” endpoint and assert
 * that it responds with a success code.
 */
public class DeleteBookingAdminTest extends BasePlaywrightTest {

    private HomePage    home;
    private BookingPage booking;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeMethod
    public void initPageObjects() {
        home    = new HomePage(page);
        booking = new BookingPage(page);
    }

    @Test(description = "Create booking → hit /admin/update → expect 404 or delete via /admin/delete")
    public void shouldHandleAdminUpdateAndDeleteEndpoints() {
        /*1. create a booking on a random (1‑10) future day */
        LocalDate today      = LocalDate.now();
        int offset           = random.nextInt(60) + 1;
        String checkIn       = today.plusDays(offset).format(FMT);
        String checkOut      = today.plusDays(offset + 2).format(FMT);

        home.goToRoom("suite", checkIn, checkOut);
        booking.completeBooking("Test", "User", "test@example.com", "12345678901");

        String confirmation = booking.waitForConfirmation(15_000);
        Assert.assertFalse(confirmation.isEmpty(), "Booking confirmation not visible");

        /* 2. open a new tab and hit /admin/update */
        Page adminTab = context.newPage();
        Response updateResp = adminTab.navigate(
                "https://automationintesting.online/admin/update",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        int updateStatus = updateResp != null ? updateResp.status() : 0;
        System.out.println("GET /admin/update returned HTTP " + updateStatus);

        /* 3. handle result */
        if (updateStatus == 404) {
            adminTab.close();   // endpoint missing – nothing more to do
            return;
        }

        // Otherwise try a (hypothetical) delete endpoint
        Response deleteResp = adminTab.navigate(
                "https://automationintesting.online/admin/delete");

        Assert.assertNotNull(deleteResp, "Delete response should not be null");
        Assert.assertTrue(deleteResp.ok(),
                "Expected successful deletion, got HTTP " + deleteResp.status());

        adminTab.close();
    }
}