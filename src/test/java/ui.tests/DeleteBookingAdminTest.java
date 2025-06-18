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
 * Creates a booking on a random future date, then tries to delete it via admin page.
 * If admin page returns 404, closes the window and finishes test.
 */
public class DeleteBookingAdminTest {

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

    @Test(description = "Creates a booking, then tries to delete it via admin and closes window on 404")
    public void shouldDeleteBookingViaAdmin() {
        LocalDate today = LocalDate.now();
        Random random = new Random();

        int daysToAdd = random.nextInt(10) + 1;  // random day 1..10 days from now
        String checkIn = today.plusDays(daysToAdd).format(FMT);
        String checkOut = today.plusDays(daysToAdd + 2).format(FMT);

        // Create booking
        home.goToRoom("suite", checkIn, checkOut);
        booking.completeBooking("Test", "User", "test@example.com", "12345678901");

        String bookingReference = booking.waitForConfirmation(15_000);
        Assert.assertFalse(bookingReference.isEmpty(), "Booking confirmation not visible");

        // Open new admin page tab
        Page adminPage = browser.newPage();

        // Navigate to admin update page for this booking reference
        Response response = adminPage.navigate(
                "https://automationintesting.online/admin/update?reference=" + bookingReference,
                new Page.NavigateOptions().setWaitUntil(WaitUntilState.DOMCONTENTLOADED)
        );

        int status = response != null ? response.status() : 0;
        System.out.println("Admin update page HTTP status: " + status);

        // If 404, close and stop test
        if (status == 404) {
            adminPage.close();
            return;
        }

        // Otherwise, proceed to delete booking via admin URL
        Response deleteResponse = adminPage.navigate(
                "https://automationintesting.online/admin/delete?reference=" + bookingReference
        );

        Assert.assertNotNull(deleteResponse, "Delete response should not be null");
        Assert.assertTrue(deleteResponse.ok(), "Failed to delete booking via admin");

        adminPage.close();
    }
}
