package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page object for the “Contact Us” form.
 * Supports both DOM variants for the Message field:
 *   • <textarea id="message">
 *   • TinyMCE inside <iframe id="message_ifr"> with a contenteditable <body>.
 * Implements eight validation rules and exposes banner helpers.
 */
public class ContactUsPage extends BasePage {

    /* Selectors */
    private static final String MESSAGE_TEXTAREA = "#message";
    private static final String MESSAGE_IFRAME   = "iframe#message_ifr";

    /* Form fields */
    private final Locator nameInput    = page.locator("#name");
    private final Locator emailInput   = page.locator("#email");
    private final Locator phoneInput   = page.locator("#phone");
    private final Locator subjectInput = page.locator("#subject");
    private final Locator submitBtn    = page.locator("#contact-form button[type='submit']");

    /* Success banner */
    private final Locator banner = page.locator("#contact-success");

    public ContactUsPage(Page page) { super(page); }

    /* Validation according to requirements */
    private void validate(String n, String e, String p, String s, String m) {
        if (n == null || n.isBlank())   throw new IllegalArgumentException("Name may not be blank");
        if (e == null || e.isBlank())   throw new IllegalArgumentException("Email may not be blank");
        if (p == null || p.isBlank())   throw new IllegalArgumentException("Phone may not be blank");
        if (s == null || s.isBlank())   throw new IllegalArgumentException("Subject may not be blank");
        if (m == null || m.isBlank())   throw new IllegalArgumentException("Message may not be blank");
        if (s.length() < 5 || s.length() > 100)
            throw new IllegalArgumentException("Subject must be between 5 and 100 characters");
        if (p.length() < 11 || p.length() > 21)
            throw new IllegalArgumentException("Phone must be between 11 and 21 characters");
        if (m.length() < 20 || m.length() > 2000)
            throw new IllegalArgumentException("Message must be between 20 and 2000 characters");
    }

    /* Helper to fill the Message field */
    private void fillMessage(String text) {
        Locator message = page.locator(MESSAGE_TEXTAREA);
        if (message.count() > 0) {
            if ("textarea".equals(message.evaluate("el=>el.tagName.toLowerCase()"))) {
                message.fill(text);
            } else {
                message.click();
                page.keyboard().press("Control+A");
                page.keyboard().press("Backspace");
                page.keyboard().type(text);
            }
            return;
        }
        page.waitForSelector(MESSAGE_IFRAME, new Page.WaitForSelectorOptions().setTimeout(8000));
        FrameLocator frame = page.frameLocator(MESSAGE_IFRAME);
        Locator body = frame.locator("body");
        body.waitFor();
        body.click();
        body.fill("");
        body.type(text);
    }

    /* Public API */
    public void fillContactForm(String n, String e, String p, String s, String m) {
        validate(n, e, p, s, m);
        nameInput.fill(n);
        emailInput.fill(e);
        phoneInput.fill(p);
        subjectInput.fill(s);
        page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
        fillMessage(m);
    }

    public void submitForm() {
        submitBtn.scrollIntoViewIfNeeded();
        submitBtn.click();
        banner.waitFor(new Locator.WaitForOptions().setState(WaitForSelectorState.VISIBLE).setTimeout(8000));
    }

    public boolean bannerVisible() { return banner.isVisible(); }
    public String  bannerText()    { return banner.textContent().trim(); }
}