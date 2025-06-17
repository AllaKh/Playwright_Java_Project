package pages;

import com.microsoft.playwright.*;

/**
 * Page Object that represents the “Update Booking” screen.
 * It allows you to find a booking by reference, edit fields,
 * and submit the update.
 */
public class UpdateBookingPage extends BasePage {

    public UpdateBookingPage(Page page) {
        super(page);
    }

    /* ─────────────────────────
     * Locators
     * ───────────────────────── */

    public Locator refInput()       { return page.locator("#booking_reference"); }
    public Locator searchBtn()      { return page.locator("button:has-text('Search')"); }

    // editable fields that appear after Search
    public Locator phoneInput()     { return page.locator("#phone"); }
    public Locator fullNameInput()  { return page.locator("#full_name"); }
    public Locator updateBtn()      { return page.locator("button:has-text('Update')"); }

    /* ─────────────────────────
     * Actions
     * ───────────────────────── */

    /** Enter the booking reference and click <Search>. */
    public void searchBooking(String reference) {
        refInput().fill(reference);
        searchBtn().click();
        // wait until editable form is displayed
        phoneInput().waitFor();
    }

    /** Change the phone number (must contain ≥7 digits). */
    public void updatePhone(String newPhone) {
        if (newPhone.replaceAll("\\D", "").length() < 7) {
            throw new IllegalArgumentException("Phone number must have at least 7 digits");
        }
        phoneInput().fill(newPhone);
    }

    /** Optionally change customer full name. */
    public void updateFullName(String fullName) {
        fullNameInput().fill(fullName);
    }

    /** Submit the updated booking form. */
    public void submitUpdate() {
        updateBtn().click();
    }

    /* ─────────────────────────
     * Assertions / helpers
     * ───────────────────────── */

    /** Returns true if a success toast/alert appears after update. */
    public boolean isUpdateSuccessVisible() {
        return page.locator("text=Booking updated successfully").isVisible();
    }

    /** Quick access to current phone value (useful for assertions). */
    public String currentPhone() {
        return phoneInput().inputValue();
    }
}
