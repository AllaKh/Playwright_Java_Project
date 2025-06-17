package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page–object wrapper for the room‑booking flow at
 * https://automationintesting.online.
 *
 * Validation logic now **mirrors ContactUsPage**: all inputs are
 * checked locally and the method throws {@link IllegalArgumentException}
 * with *identical wording* to the banner messages the server would
 * otherwise return.
 */
public class BookingPage extends BasePage {

    /* ── price banners ── */
    private final Locator priceBanners = page.locator(
            "div.d-flex.align-items-baseline.mb-4 >> text=per night");

    /* ── form fields ── */
    private final Locator firstNameInput = page.locator("input.room-firstname");
    private final Locator lastNameInput  = page.locator("input.room-lastname");
    private final Locator emailInput     = page.locator("input.room-email");
    private final Locator phoneInput     = page.locator("input.room-phone");

    /* two blue “Reserve Now” buttons */
    private final Locator reserveNowBtn =
            page.locator("button.btn.btn-primary:has-text('Reserve Now')");

    /* ── confirmation elements ── */
    private final Locator confirmationTitle = page.locator("text=Booking Confirmed");
    private final Locator dateRange         = page.locator("div.card-body p >> nth=1");
    private final Locator returnHomeBtn     = page.locator("a.btn.btn-primary:has-text('Return home')");

    /* delete helpers (unchanged) */
    private final Locator deleteButton = page.locator("#delete-btn");
    private final Locator bookingForm  = page.locator("form#booking-form");

    public BookingPage(Page page) { super(page); }

    /* ─────────────────────────── validation ─────────────────────────── */

    private void validate(String fn, String ln, String email, String phone) {
        /* blanks */
        if (fn == null || fn.trim().isEmpty())
            throw new IllegalArgumentException("Firstname should not be blank");
        if (ln == null || ln.trim().isEmpty())
            throw new IllegalArgumentException("Lastname should not be blank");
        if (email == null || email.trim().isEmpty())
            throw new IllegalArgumentException("must not be empty");
        if (phone == null || phone.trim().isEmpty())
            throw new IllegalArgumentException("must not be empty");

        /* lengths */
        if (fn.trim().length() < 3 || fn.trim().length() > 30)
            throw new IllegalArgumentException("size must be between 3 and 30");
        if (ln.trim().length() < 3 || ln.trim().length() > 18)
            throw new IllegalArgumentException("size must be between 3 and 18");
        if (phone.trim().length() < 11 || phone.trim().length() > 21)
            throw new IllegalArgumentException("size must be between 11 and 21");

        /* e‑mail format */
        if (!email.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$"))
            throw new IllegalArgumentException("well-formed email address");
    }

    /* ─────────────────────────── helpers ─────────────────────────── */

    private void scrollToBottom() { page.evaluate("window.scrollBy(0, document.body.scrollHeight)"); }
    private void scrollToTop()    { page.evaluate("window.scrollTo(0, 0)"); }

    /* ─────────────────────── main workflow ─────────────────────── */

    /**
     * Performs the two‑step booking interaction **after** passing the
     * same client‑side validation rules used in ContactUsPage.
     */
    public void completeBooking(String first, String last,
                                String email, String phone) {

        /* local validation first */
        validate(first, last, email, phone);

        /* ensure banners visible, reveal the form */
        priceBanners.first().waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(10_000));

        scrollToBottom();
        reserveNowBtn.first().click(new Locator.ClickOptions().setForce(true));

        /* top – fill three fields */
        scrollToTop();
        firstNameInput.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE));
        firstNameInput.fill(first);
        lastNameInput.fill(last);
        emailInput.fill(email);

        /* bottom – phone + submit */
        scrollToBottom();
        phoneInput.fill(phone);
        reserveNowBtn.last().scrollIntoViewIfNeeded();
        reserveNowBtn.last().click(new Locator.ClickOptions().setForce(true));
    }

    /* ───────────────── confirmation helpers ───────────────── */

    public String waitForConfirmation(int timeoutMs) {
        confirmationTitle.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE).setTimeout(timeoutMs));
        return dateRange.textContent().trim();
    }
    public void returnHome() { returnHomeBtn.click(); }

    /* ───────────────── delete helpers ───────────────── */

    public void deleteBooking() {
        deleteButton.click();
        bookingForm.waitFor(new Locator.WaitForOptions().setTimeout(5_000));
    }
    public boolean isFormVisible() { return bookingForm.isVisible(); }
}
