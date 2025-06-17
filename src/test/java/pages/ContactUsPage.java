// ContactUsPage.java
package pages;

import com.microsoft.playwright.*;
import com.microsoft.playwright.options.WaitForSelectorState;

/**
 * Page object for the “Contact Us” form.
 * Supports both DOM variants for the Message field:
 *  • a plain <textarea id="message">
 *  • a TinyMCE editor inside <iframe id="message_ifr"> with a contenteditable <body>
 * Also enforces all eight validation rules and exposes banner helpers.
 */
public class ContactUsPage extends BasePage {

    /* ─── static selectors ─── */
    private static final String MESSAGE_TEXTAREA   = "#message";            // textarea/div
    private static final String MESSAGE_IFRAME     = "iframe#message_ifr";   // TinyMCE iframe

    /* ─── form fields ─── */
    private final Locator nameInput     = page.locator("#name");
    private final Locator emailInput    = page.locator("#email");
    private final Locator phoneInput    = page.locator("#phone");
    private final Locator subjectInput  = page.locator("#subject");
    private final Locator submitButton  = page.locator("#contact-form button[type='submit']");

    /* ─── feedback ─── */
    private final Locator banner        = page.locator("#contact-success");

    public ContactUsPage(Page page) {
        super(page);
    }

    /* ───────────────────────── validation ─────────────────────────── */

    private void validate(String name, String email, String phone,
                          String subject, String message) {
        if (name == null   || name.isBlank())    throw new IllegalArgumentException("Name may not be blank");
        if (email == null  || email.isBlank())   throw new IllegalArgumentException("Email may not be blank");
        if (phone == null  || phone.isBlank())   throw new IllegalArgumentException("Phone may not be blank");
        if (subject == null|| subject.isBlank()) throw new IllegalArgumentException("Subject may not be blank");
        if (message == null|| message.isBlank()) throw new IllegalArgumentException("Message may not be blank");

        if (subject.length() < 5  || subject.length() > 100)
            throw new IllegalArgumentException("Subject must be between 5 and 100 characters");
        if (phone.length()   < 11 || phone.length()   > 21)
            throw new IllegalArgumentException("Phone must be between 11 and 21 characters");
        if (message.length() < 20 || message.length() > 2000)
            throw new IllegalArgumentException("Message must be between 20 and 2000 characters");
    }

    /* ───────────────────────── helpers ─────────────────────────────── */

    /**
     * Fills the Message box regardless of its rendering type.
     */
    private void fillMessage(String text) {
        // Wait up to 5 seconds for either the textarea or the iframe to appear
        page.waitForSelector(MESSAGE_TEXTAREA + ", " + MESSAGE_IFRAME,
                new Page.WaitForSelectorOptions().setTimeout(5000));

        Locator message = page.locator(MESSAGE_TEXTAREA);
        if (message.count() > 0) {
            String tag = message.evaluate("el => el.tagName.toLowerCase()").toString();
            if ("textarea".equals(tag)) {
                // Simple textarea — fill normally
                message.fill(text);
            } else {
                // Contenteditable div or other element
                message.click();
                page.keyboard().press("Control+A");
                page.keyboard().press("Backspace");
                page.keyboard().type(text);
                message.evaluate("(el,v) => el.innerText = v", text);
            }
            return;
        }

        // Otherwise, the TinyMCE iframe should be present
        page.locator(MESSAGE_IFRAME).waitFor(new Locator.WaitForOptions().setTimeout(5000));
        FrameLocator frame = page.frameLocator(MESSAGE_IFRAME);
        Locator body = frame.locator("body");
        body.waitFor();
        body.click();
        body.fill("");
        body.type(text);
    }

    /* ───────────────────────── public API ─────────────────────────── */

    /**
     * Fills the entire contact form with provided data.
     * Validates all fields before filling.
     */
    public void fillContactForm(String name, String email, String phone,
                                String subject, String message) {
        validate(name, email, phone, subject, message);

        nameInput.fill(name);
        emailInput.fill(email);
        phoneInput.fill(phone);
        subjectInput.fill(subject);

        // Scroll down to message field
        page.evaluate("window.scrollBy(0, document.body.scrollHeight)");
        fillMessage(message);
    }

    /**
     * Clicks the submit button and waits for success banner to appear.
     */
    public void submitForm() {
        submitButton.scrollIntoViewIfNeeded();
        submitButton.click();
        banner.waitFor(new Locator.WaitForOptions()
                .setState(WaitForSelectorState.VISIBLE)
                .setTimeout(8000));
    }

    /**
     * Returns true if success banner is visible.
     */
    public boolean bannerVisible() {
        return banner.isVisible();
    }

    /**
     * Returns trimmed text content of the success banner.
     */
    public String bannerText() {
        return banner.textContent().trim();
    }

    /**
     * Generates expected banner text after successful form submission.
     */
    public static String expectedSuccessMessage(String name, String subject) {
        return "Thanks for getting in touch " + name + "\n" +
                "We'll get back to you about\n\n" +
                subject + "\n\n" +
                "as soon as possible.";
    }
}
