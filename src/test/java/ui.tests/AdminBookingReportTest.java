package ui.tests;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.LoadState;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.*;
import ui.core.BasePlaywrightTest;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class AdminBookingReportTest extends BasePlaywrightTest {

    private HomePage home;
    private AdminLoginPage adminLogin;
    private BookingPage booking;

    private static final String G_NAME = "Alice Test";
    private static final String G_MAIL = "alice@example.com";
    private static final String G_PHONE = "12345678901";
//    private static final String G_SUBJ = "You have a new booking!";

    private static final DateTimeFormatter URL_FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeMethod
    public void setUp() {
        home = new HomePage(page);
        adminLogin = new AdminLoginPage(page);
        booking = new BookingPage(page);
    }

    private void createBooking(String roomId) {
        int offset = random.nextInt(15);
        LocalDate checkIn = LocalDate.now().plusDays(offset);
        LocalDate checkOut = checkIn.plusDays(1);

        page.navigate("https://automationintesting.online/reservation/" + roomId +
                "?checkin=" + checkIn.format(URL_FMT) +
                "&checkout=" + checkOut.format(URL_FMT));

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

    private void verifyReportRowAndMessage(String expectedReportRow) {
        // Step 1 — Login and go to Admin → Report
        home.clickAdmin();
        adminLogin.login("admin", "password");
        page.waitForURL("**/admin/rooms");

        // Step 2 — Open Report page and check expected row
        page.locator("a[href='/admin/report']").click();
        page.waitForURL("**/admin/report");

        Locator row = page.locator("text=" + expectedReportRow);
        row.first().waitFor(new Locator.WaitForOptions().setTimeout(15_000));
        Assert.assertTrue(row.count() > 0 && row.first().isVisible(),
                "Expected at least one visible row: " + expectedReportRow);

        // Step 3 — Open Admin → Message
        page.locator("a[href='/admin/message']").click();
        page.waitForURL("**/admin/message");

        // Step 4 — Wait for message list to render
        page.waitForTimeout(2000);

        // Step 5 — Find latest message from the guest
        Locator messages = page.locator("div[id^='message']");
        int count = messages.count();
        boolean found = false;

        for (int i = count - 1; i >= 0; i--) {
            Locator nameLocator = messages.nth(i).locator("div.col-sm-2 > p");
            String nameText = nameLocator.textContent().trim();

            if (nameText.equals(G_NAME)) {
                nameLocator.click();
                found = true;
                break;
            }
        }

        Assert.assertTrue(found, "No message row found with guest name: " + G_NAME);

        // Step 6 — Wait for modal to appear
        Locator modal = page.locator("div.ReactModal__Content");
        modal.waitFor(new Locator.WaitForOptions().setTimeout(10_000));

        // Step 7 — Check guest info in modal
        Assert.assertTrue(modal.locator("p:has-text('From: " + G_NAME + "')").isVisible(),
                "Guest name missing in modal");
        Assert.assertTrue(modal.locator("text=" + G_MAIL).isVisible(),
                "Guest email missing in modal");
        Assert.assertTrue(modal.locator("text=" + G_PHONE).isVisible(),
                "Guest phone missing in modal");

        // Step 8 — Close modal
        Locator closeBtn = modal.locator("button:has-text('Close')");
        closeBtn.click();

        // Step 9 — Wait for modal to disappear
        boolean isModalGone = false;
        for (int i = 0; i < 20; i++) {
            if (modal.isHidden()) {
                isModalGone = true;
                break;
            }
            page.waitForTimeout(250);
        }

        Assert.assertTrue(isModalGone, "Modal did not close within expected time");
    }

    @Test(description = "Verifies double room (id 2) is in report and message")
    public void doubleRoomBookingAppears() {
        createBooking("2");
        verifyReportRowAndMessage(G_NAME + " - Room: 102");
    }

    @Test(description = "Verifies single room (id 1) is in report and message")
    public void singleRoomBookingAppears() {
        createBooking("1");
        verifyReportRowAndMessage(G_NAME + " - Room: 101");
    }

    @Test(description = "Verifies suite room (id 3) appears despite room code mismatch")
    public void suiteRoomBookingAppears_Bug() {
        createBooking("3");
        verifyReportRowAndMessage(G_NAME + " - Room: 103"); // Known mismatch: booked as 104
    }

    @Test(description = "Creates suite room booking without verifying admin")
    public void suiteRoomBookingOnlyCreate() {
        createBooking("3");
    }
}