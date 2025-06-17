package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page object for the booking page at automationintesting.online
 */
public class BookingPage extends BasePage {

    private final Locator firstNameInput  = page.locator("#firstname");
    private final Locator lastNameInput   = page.locator("#lastname");
    private final Locator emailInput      = page.locator("#email");
    private final Locator phoneInput      = page.locator("#phone");

    /** Blue “Reserve Now” button */
    private final Locator submitButton    = page.locator("button:has-text('Reserve Now')");

    private final Locator confirmationMsg = page.locator("text=Thank you for your booking");
    private final Locator deleteButton    = page.locator("#delete-btn");
    private final Locator bookingForm     = page.locator("form#booking-form");

    public BookingPage(Page page) { super(page); }

    /**
     * If the form is still collapsed, scroll to the bottom and click
     * “Reserve Now” once to reveal it, then fill the four inputs.
     */
    public void fillBookingForm(String first, String last,
                                String email, String phone) {

        // Step 1 – make the form visible (only if hidden yet)
        if (!firstNameInput.isVisible()) {
            page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
            submitButton.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE));
            submitButton.scrollIntoViewIfNeeded();
            submitButton.click(new Locator.ClickOptions().setForce(true));

            // wait until the first input really appears
            firstNameInput.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE));
        }

        // Step 2 – fill all fields
        firstNameInput.fill(first);
        lastNameInput.fill(last);
        emailInput.fill(email);
        phoneInput.fill(phone);
    }

    /** Final scroll + click, then wait for confirmation */
    public void submitBooking() {
        page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
        submitButton.scrollIntoViewIfNeeded();
        submitButton.click(new Locator.ClickOptions().setForce(true));
        confirmationMsg.waitFor(
                new Locator.WaitForOptions().setTimeout(8000)
                        .setState(WaitForSelectorState.VISIBLE));
    }

    public boolean isConfirmationVisible() { return confirmationMsg.isVisible(); }

    /* — delete helpers unchanged — */
    public void deleteBooking() {
        deleteButton.click();
        bookingForm.waitFor(new Locator.WaitForOptions().setTimeout(5000));
    }
    public boolean isFormVisible() { return bookingForm.isVisible(); }
}