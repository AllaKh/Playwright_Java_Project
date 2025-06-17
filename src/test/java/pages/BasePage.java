package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * BasePage – common Playwright helpers that every page object inherits.
 * All public methods are generic utilities; specific page classes
 * contain only business‑level actions.
 */
public class BasePage {

    protected final Page page;

    public BasePage(Page page) {
        this.page = page;
    }

    /* ───────────────────────────── Navigation ─────────────────────────── */

    /** Navigate to a full URL and wait for the network to be idle. */
    public void navigate(String url) {
        page.navigate(url);
        page.waitForLoadState();                // default = LoadState.LOAD
    }

    /** Current browser tab URL. */
    public String currentUrl()     { return page.url(); }

    /** Title shown in the browser tab. */
    public String pageTitle()      { return page.title(); }

    /* ───────────────────────────── Wait helpers ───────────────────────── */

    /** Wait until selector is visible (timeout: Playwright default). */
    public void waitVisible(String selector) {
        page.locator(selector)
                .waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.VISIBLE));
    }

    /** Wait until selector disappears (hidden or detached). */
    public void waitHidden(String selector) {
        page.locator(selector)
                .waitFor(new Locator.WaitForOptions()
                        .setState(WaitForSelectorState.HIDDEN));
    }

    /* ───────────────────────────── Element actions ────────────────────── */

    /** Click after waiting for element to become visible and enabled. */
    public void click(String selector) {
        waitVisible(selector);
        page.locator(selector).click();
    }

    /** Clear the input (if any) and type the given text. */
    public void type(String selector, String text) {
        waitVisible(selector);
        Locator input = page.locator(selector);
        input.fill("");
        input.type(text);
    }

    /** Return textContent() of element once it is visible. */
    public String text(String selector) {
        waitVisible(selector);
        return page.locator(selector).textContent();
    }

    /** True if element is currently visible in the DOM. */
    public boolean isVisible(String selector) {
        return page.locator(selector).isVisible();
    }

    /** Scroll element into view if it is outside the viewport. */
    public void scrollTo(String selector) {
        page.locator(selector).scrollIntoViewIfNeeded();
    }

    /** Convenience: element text contains expected substring? */
    public boolean containsText(String selector, String expected) {
        String actual = text(selector);
        return actual != null && actual.contains(expected);
    }

    /* ───────────────────────────── Generic waits ──────────────────────── */

    /** Wait for a selector to reach a given Playwright {@link WaitForSelectorState}. */
    public void waitFor(String selector, WaitForSelectorState state) {
        page.waitForSelector(selector,
                new Page.WaitForSelectorOptions().setState(state));
    }
}
