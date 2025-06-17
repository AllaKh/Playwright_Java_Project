package ui.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.concurrent.ThreadLocalRandom;

public class AdminBookingReportTest {

    private Playwright playwright;
    private Browser browser;
    private BrowserContext context;
    private Page page;

    private HomePage home;
    private AdminLoginPage adminLogin;
    private BookingPage booking;

    private static final DateTimeFormatter URL_DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeClass
    public void setUpClass() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
    }

    @BeforeMethod
    public void setUpTest() {
        context = browser.newContext();
        page = context.newPage();

        home = new HomePage(page);
        adminLogin = new AdminLoginPage(page);
        booking = new BookingPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDownTest() {
        if (page != null && !page.isClosed()) page.close();
        if (context != null) context.close();
    }

    @AfterClass(alwaysRun = true)
    public void tearDownClass() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    // Helper method to generate random date from today to 30 days ahead
    private LocalDate getRandomBookingDate() {
        long startEpochDay = LocalDate.now().toEpochDay();
        long endEpochDay = LocalDate.now().plusDays(30).toEpochDay();
        long randomDay = ThreadLocalRandom.current().nextLong(startEpochDay, endEpochDay + 1);
        return LocalDate.ofEpochDay(randomDay);
    }

    @Test(description = "Create a booking on a random future date and verify it appears anywhere on admin report page")
    public void bookingShouldAppearInAdminReport() {
        LocalDate bookingDate = getRandomBookingDate();
        String checkIn = bookingDate.format(URL_DATE_FORMAT);
        String checkOut = bookingDate.plusDays(1).format(URL_DATE_FORMAT);

        page.navigate("https://automationintesting.online/reservation/2"
                + "?checkin=" + checkIn + "&checkout=" + checkOut);

        String firstName = "Alice";
        String lastName = "Test";
        String fullName = firstName + " " + lastName;
        String email = "alice@example.com";
        String phone = "12345678901";

        booking.completeBooking(firstName, lastName, email, phone);
        booking.waitForConfirmation(15_000);
        booking.returnHome();
        page.waitForLoadState(LoadState.NETWORKIDLE);

        home.clickAdmin();
        adminLogin.login("admin", "password");
        page.waitForURL("**/admin/rooms", new Page.WaitForURLOptions().setTimeout(15_000));

        page.locator("a[href='/admin/report']").click();
        page.waitForURL("**/admin/report", new Page.WaitForURLOptions().setTimeout(15_000));

        String expectedText = fullName + " - Room: 102";

        Locator bookingLocators = page.locator("text=" + expectedText);
        bookingLocators.first().waitFor(new Locator.WaitForOptions().setTimeout(15_000));

        int count = bookingLocators.count();
        boolean visibleFound = false;

        for (int i = 0; i < count; i++) {
            if (bookingLocators.nth(i).isVisible()) {
                visibleFound = true;
                break;
            }
        }

        Assert.assertTrue(visibleFound, "Expected at least one visible element with text '" + expectedText + "'.");
    }
}
