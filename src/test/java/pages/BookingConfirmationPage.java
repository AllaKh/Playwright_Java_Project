package pages;

import com.microsoft.playwright.Page;
import com.microsoft.playwright.Locator;
import com.microsoft.playwright.options.WaitForSelectorState;

public class BookingConfirmationPage {
    private final Page page;
    private final Locator confirmationBanner;

    public BookingConfirmationPage(Page page) {
        this.page = page;
        // Предположим, подтверждение - это баннер с классом .confirmation-banner
        this.confirmationBanner = page.locator(".confirmation-banner");
    }

    public boolean waitForBookingConfirmation() {
        try {
            confirmationBanner.waitFor(new Locator.WaitForOptions()
                    .setState(WaitForSelectorState.VISIBLE)
                    .setTimeout(5000));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public String getBookingReference() {
        // Assume the booking reference number is inside the confirmationBanner element, within a child element that has the class .ref
        return confirmationBanner.locator(".ref").textContent();
    }
}
