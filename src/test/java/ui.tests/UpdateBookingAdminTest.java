package ui.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.BookingPage;
import pages.HomePage;
import ui.core.BasePlaywrightTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Creates a booking and verifies that accessing /admin/update returns a 404 error.
 */
public class UpdateBookingAdminTest extends BasePlaywrightTest {

    private HomePage home;
    private BookingPage booking;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeMethod
    public void setUpTest() {
        home = new HomePage(page);
        booking = new BookingPage(page);
    }

    @Test(description = "Creates a booking, then verifies /admin/update returns 404 and closes the page")
    public void shouldReturn404OnAdminUpdate() {
        LocalDate today = LocalDate.now();
        int daysToAdd = random.nextInt(10) + 1; // 1 to 10 days ahead
        String checkIn = today.plusDays(daysToAdd).format(FMT);
        String checkOut = today.plusDays(daysToAdd + 2).format(FMT);

        // Create a booking
        home.goToRoom("suite", checkIn, checkOut);
        booking.completeBooking("Test", "User", "test@example.com", "12345678901");

        // Wait for booking confirmation
        String confirmationRange = booking.waitForConfirmation(10_000);
        Assert.assertFalse(confirmationRange.isEmpty(), "Booking confirmation is not visible");

        // Try accessing the admin update page
        Response response = page.navigate(
                "https://automationintesting.online/admin/update",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
        );

        int status = response.status();
        System.out.println("Received HTTP status: " + status);

        // Close the page and assert 404 status
        page.close();
        Assert.assertEquals(status, 404, "Expected 404 on /admin/update, but got: " + status);
    }
}
