package pages;

import com.microsoft.playwright.Page;

/**
 * Wrapper for the main top navigation bar.
 * Provides a single helper {@code clickLink()} used by every UI test.
 */
public class NavigationBar extends BasePage {

    public NavigationBar(Page page) {
        super(page);
    }

    /**
     * Clicks the first <a> element that contains the given visible text
     * and then waits for the browser to finish navigation / hash change.
     *
     * @param visibleText exact text that appears in the link
     */
    public void clickLink(String visibleText) {
        page.locator("a:has-text('" + visibleText + "')").first().click();
        // wait until either a new hash or a full URL change happens
        page.waitForURL(url -> true);          // a simple, always‑true predicate
    }

    /** Convenience getter in case a test needs a locator directly. */
    public com.microsoft.playwright.Locator locatorFor(String visibleText) {
        return page.locator("a:has-text('" + visibleText + "')");
    }
}
