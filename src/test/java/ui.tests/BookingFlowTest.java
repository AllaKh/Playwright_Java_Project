package ui.tests;

import com.microsoft.playwright.*;
import org.testng.annotations.*;
import pages.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class BookingFlowTest {
    Playwright playwright;
    Browser    browser;
    Page       page;

    HomePage   home;
    BookingPage booking;

    final DateTimeFormatter fmt = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @BeforeClass
    public void setUp() {
        playwright = Playwright.create();
        browser    = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));
        page    = browser.newPage();
        home    = new HomePage(page);
        booking = new BookingPage(page);
    }

    @Test
    public void bookingFlowTest() {
        /* Build dates */
        LocalDate today = LocalDate.now();
        String checkIn  = today.plusDays(1).format(fmt);
        String checkOut = today.plusDays(2).format(fmt);

        /* Go straight to “Double Room” reservation URL */
        page.navigate(
                "https://automationintesting.online/reservation/2?checkin="
                        + checkIn + "&checkout=" + checkOut);

        /* Fill form and book */
        booking.fillBookingForm(
                "John", "Doe", "john.doe@example.com", "12345678901");
        booking.submitBooking();

        assert booking.isConfirmationVisible()
                : "Booking confirmation should be visible";
    }

    @AfterClass
    public void tearDown() {
        if (browser != null)    browser.close();
        if (playwright != null) playwright.close();
    }
}
