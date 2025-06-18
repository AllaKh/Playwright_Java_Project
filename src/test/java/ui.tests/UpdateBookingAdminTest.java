package ui.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitUntilState;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.BookingPage;
import pages.HomePage;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Random;

/**
 * Makes a random booking, then checks /admin/update returns 404.
 */
public class UpdateBookingAdminTest {

    private Playwright playwright;
    private Browser browser;
    private Page page;

    private HomePage home;
    private BookingPage booking;

    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(new BrowserType.LaunchOptions()
                .setHeadless(false));
        page = browser.newPage();
        home = new HomePage(page);
        booking = new BookingPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test(description = "Creates a booking, then verifies /admin/update returns 404 and closes the window")
    public void shouldUpdateBookingViaAdminAndRedirectTo404() {
        // Generate a random future date
        LocalDate today = LocalDate.now();
        int daysToAdd = new Random().nextInt(10) + 1; // 1 to 10 days in the future
        String checkIn = today.plusDays(daysToAdd).format(FMT);
        String checkOut = today.plusDays(daysToAdd + 2).format(FMT);

        // Create booking
        home.goToRoom("suite", checkIn, checkOut);
        booking.completeBooking("Test", "User", "test@example.com", "12345678901");

        String confirmationRange = booking.waitForConfirmation(10_000);
        Assert.assertFalse(confirmationRange.isEmpty(), "Booking confirmation not visible");

        // Try accessing a non-existent admin update page
        Response response = page.navigate(
                "https://automationintesting.online/admin/update",
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED));

        int status = response.status();
        System.out.println("Received HTTP status: " + status);

        // Close window and assert 404
        page.close();
        Assert.assertEquals(status, 404, "Expected 404 on /admin/update, but got: " + status);
    }
}
