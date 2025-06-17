package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page-object wrapper for the “Contact Us” form at
 * https://automationintesting.online/#contact.
 *
 * Key points
 * ──────────
 * • Message field can be either
 *     – <textarea id="description">      (plain or hidden in CSS)
 *     – TinyMCE inside <iframe id="description_ifr">.
 * • The backend validator enables the blue “Submit” button
 *   only after key events are fired in every input,
 *   so we must *type* into the textarea, not just call fill().
 * • Implements eight client-side validation rules via quick
 *   pre-checks in {@link #validate}.
 */
public class ContactUsPage extends BasePage {

    /* ── CSS / XPath selectors ─────────────────────────────────── */
    private static final String MSG_TEXTAREA = "#description";
    private static final String MSG_IFRAME   = "iframe#description_ifr";

    /* Form inputs */
    private final Locator nameInput    = page.locator("#name");
    private final Locator emailInput   = page.locator("#email");
    private final Locator phoneInput   = page.locator("#phone");
    private final Locator subjectInput = page.locator("#subject");

    /* Blue “Submit” button (type="button") */
    private final Locator submitBtn =
            page.locator("button.btn.btn-primary:has-text('Submit')");

    /* Success banner replaced by card-body with specific content */
    private final Locator banner = page.locator("div.card-body.p-4").filter(new Locator.FilterOptions().setHasText("Thanks for getting in touch"));

    public ContactUsPage(Page page) { super(page); }

    /* ───────────────────────── validation ─────────────────────── */
    private void validate(String n, String e, String p, String s, String m) {
        if (n == null || n.trim().isEmpty())   throw new IllegalArgumentException("Name may not be blank");
        if (e == null || e.trim().isEmpty())   throw new IllegalArgumentException("Email may not be blank");

        // Проверка корректности email
        if (!e.trim().matches("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+$")) {
            throw new IllegalArgumentException("Email must be a well-formed email address");
        }

        if (p == null || p.trim().isEmpty())   throw new IllegalArgumentException("Phone may not be blank");
        if (s == null || s.trim().isEmpty())   throw new IllegalArgumentException("Subject may not be blank");
        if (m == null || m.trim().isEmpty())   throw new IllegalArgumentException("Message may not be blank");

        if (s.length() < 5 || s.length() > 100)
            throw new IllegalArgumentException("Subject must be between 5 and 100 characters");
        if (p.length() < 11 || p.length() > 21)
            throw new IllegalArgumentException("Phone must be between 11 and 21 characters");
        if (m.length() < 20 || m.length() > 2000)
            throw new IllegalArgumentException("Message must be between 20 and 2000 characters");
    }

    private void fillMessage(String text) {
        Locator msg = page.locator(MSG_TEXTAREA);
        if (msg.count() > 0) {
            msg.fill("");
            msg.click();
            page.keyboard().type(text);
            return;
        }

        page.waitForSelector(MSG_IFRAME,
                new Page.WaitForSelectorOptions().setTimeout(10_000));
        FrameLocator frame = page.frameLocator(MSG_IFRAME);
        Locator body = frame.locator("body");
        body.waitFor();
        body.click();
        page.keyboard().type(text);
    }

    /**
     * Fill the contact form fields with given values.
     * @param n name
     * @param e email
     * @param p phone
     * @param s subject
     * @param m message
     */
    public void fillContactForm(String n, String e, String p, String s, String m) {
        validate(n, e, p, s, m);

        nameInput.fill(n);
        emailInput.fill(e);
        phoneInput.fill(p);
        subjectInput.fill(s);

        page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
        fillMessage(m);
    }

    /**
     * Click the Submit button and wait for the success banner to appear.
     * This method does not check banner content.
     */
    public void submitForm() {
        submitBtn.scrollIntoViewIfNeeded();
        submitBtn.click();

        Locator successBanner = page.locator("div.card-body.p-4").filter(new Locator.FilterOptions().setHasText("Thanks for getting in touch"));
        successBanner.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(15000));
    }

    /**
     * Check if the success banner is visible.
     * @return true if visible, false otherwise
     */
    public boolean bannerVisible() { return banner.isVisible(); }

    /**
     * Get the inner HTML of the success banner.
     * @return HTML string inside banner
     */
    public String bannerInnerHTML() {
        return banner.innerHTML();
    }

    /**
     * Get the text content of the success banner.
     * @return trimmed banner text content
     */
    public String bannerText() {
        return banner.textContent().trim();
    }
}
