package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page object for the room‑booking flow at automationintesting.online.
 *
 * Required flow
 * ─────────────
 * 1. Scroll down, click the first blue “Reserve Now” under the price banner.
 * 2. Scroll back to the top of the card and fill first/last name + email.
 * 3. Scroll down again, fill phone, click the second “Reserve Now”.
 * 4. Wait for the “Booking Confirmed” screen.
 */
public class BookingPage extends BasePage {

    /* ── price banners (three rooms have identical markup) ───────── */
    private final Locator priceBanners = page.locator(
            "div.d-flex.align-items-baseline.mb-4 >> text=per night");

    /* ── form fields ─────────────────────────────────────────────── */
    private final Locator firstNameInput = page.locator("input.room-firstname");
    private final Locator lastNameInput  = page.locator("input.room-lastname");
    private final Locator emailInput     = page.locator("input.room-email");
    private final Locator phoneInput     = page.locator("input.room-phone");

    /** Blue “Reserve Now” button (there are two identical buttons) */
    private final Locator reserveNowBtn =
            page.locator("button.btn.btn-primary:has-text('Reserve Now')");

    /* ── confirmation screen ─────────────────────────────────────── */
    private final Locator confirmationTitle =
            page.locator("text=Booking Confirmed");
    private final Locator dateRange =
            page.locator("div.card-body p >> nth=1"); // second <p> contains the range
    private final Locator returnHomeBtn =
            page.locator("a.btn.btn-primary:has-text('Return home')");

    /* delete helpers remain unchanged */
    private final Locator deleteButton = page.locator("#delete-btn");
    private final Locator bookingForm  = page.locator("form#booking-form");

    public BookingPage(Page page) { super(page); }

    /* ───────────────────────── helpers ─────────────────────────── */

    /** Scrolls the viewport to the very bottom of the page. */
    private void scrollToBottom() {
        page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
    }

    /** Scrolls the viewport to the very top of the page. */
    private void scrollToTop() {
        page.evaluate("window.scrollTo(0, 0)");
    }

    /* ───────────────────── booking workflow ────────────────────── */

    /**
     * Performs the complete two‑step booking form interaction:
     *  • Down → click Reserve Now → Up → fill 3 fields
     *  • Down → fill phone → click Reserve Now
     */
    public void completeBooking(String first, String last,
                                String email, String phone) {

        /* 1️⃣  Ensure price banners are visible (page is ready). */
        priceBanners.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(10_000));

        /* 2️⃣  Scroll down and click the first Reserve Now (reveals the form). */
        scrollToBottom();
        reserveNowBtn.first().click(new Locator.ClickOptions().setForce(true));

        /* 3️⃣  Scroll up, wait for name/email fields, fill them. */
        scrollToTop();
        firstNameInput.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        firstNameInput.fill(first);
        lastNameInput.fill(last);
        emailInput.fill(email);

        /* 4️⃣  Scroll back down, fill phone, click the second Reserve Now. */
        scrollToBottom();
        phoneInput.fill(phone);
        reserveNowBtn.last().scrollIntoViewIfNeeded();
        reserveNowBtn.last().click(new Locator.ClickOptions().setForce(true));
    }

    /**
     * Waits for confirmation banner to appear and returns the date range text.
     */
    public String waitForConfirmation(int timeoutMs) {
        confirmationTitle.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(timeoutMs));
        return dateRange.textContent().trim();
    }

    /** Clicks “Return home” on the confirmation page. */
    public void returnHome() {
        returnHomeBtn.click();
    }

    /* existing helpers for delete, visibility, etc. */
    public void deleteBooking() {
        deleteButton.click();
        bookingForm.waitFor(new Locator.WaitForOptions().setTimeout(5000));
    }
    public boolean isFormVisible() { return bookingForm.isVisible(); }
}
