package ui.tests;

import com.microsoft.playwright.Page;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;
import pages.ContactUsPage;
import ui.core.BasePlaywrightTest;

/**
 * Contact‑Us form suite – 15 scenarios (1 happy path + 14 negative‑path validations).
 */
public class ContactUsTest extends BasePlaywrightTest {

    private ContactUsPage contact;

    /** Open the Contact‑Us section before each test. */
    @BeforeMethod
    public void openContactForm() {
        page.navigate("https://automationintesting.online/#contact");
        contact = new ContactUsPage(page);
    }

    /* Small visual pause when running headed; noop in CI if headless. */
    private void pause() { page.waitForTimeout(1_000); }

    /* positive path */

    @Test(description = "Submit a valid message and verify the success banner")
    public void positiveSubmission() {
        String name    = "John Doe";
        String subject = "Room availability";

        contact.fillContactForm(
                name,
                "john@example.com",
                "12345678901",
                subject,
                "Hello, I would like to know if a room is available next weekend."
        );
        pause();
        contact.submitForm();

        Assert.assertTrue(contact.bannerVisible(),
                "Success banner (card-body) should be visible");

        String html = contact.bannerInnerHTML();

        Assert.assertTrue(html.contains("Thanks for getting in touch " + name + "!"),
                "Banner should contain the correct greeting with the name");
        Assert.assertTrue(html.contains("<h3 class=\"h4 mb-4\">Thanks for getting in touch " + name + "!</h3>"),
                "Banner should contain correct <h3> header");
        Assert.assertTrue(html.contains("<p style=\"font-weight: bold;\">" + subject + "</p>"),
                "Banner should contain the subject in bold");
    }


    /* negative path (14 cases) */

    /* Name */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Name may not be blank",
            description = "Blank name should throw"
    )
    public void blankName() {
        contact.fillContactForm(" ", "a@b.c", "12345678901",
                "Subject", "This message is long enough.");
    }

    /* Email */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Email may not be blank",
            description = "Blank e‑mail should throw"
    )
    public void blankEmail() {
        contact.fillContactForm("John", " ", "12345678901",
                "Subject", "This message is long enough.");
    }

    /* Phone */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Phone may not be blank",
            description = "Blank phone should throw"
    )
    public void blankPhone() {
        contact.fillContactForm("John", "a@b.c", " ",
                "Subject", "This message is long enough.");
    }

    /* Subject */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Subject may not be blank",
            description = "Blank subject should throw"
    )
    public void blankSubject() {
        contact.fillContactForm("John", "a@b.c", "12345678901",
                " ", "This message is long enough.");
    }

    /* Message */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Message may not be blank",
            description = "Blank message should throw"
    )
    public void blankMessage() {
        contact.fillContactForm("John", "a@b.c", "12345678901",
                "Subject", " ");
    }

    /* Subject length */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Subject must be between 5 and 100 characters",
            description = "Subject too short"
    )
    public void subjectTooShort() {
        contact.fillContactForm("John", "a@b.c", "12345678901",
                "abc", "This message is long enough.");
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Subject must be between 5 and 100 characters",
            description = "Subject too long"
    )
    public void subjectTooLong() {
        contact.fillContactForm("John", "a@b.c", "12345678901",
                "S".repeat(101), "This message is long enough.");
    }

    /* Phone length */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Phone must be between 11 and 21 characters",
            description = "Phone too short"
    )
    public void phoneTooShort() {
        contact.fillContactForm("John", "a@b.c", "1234567",
                "Subject", "This message is long enough.");
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Phone must be between 11 and 21 characters",
            description = "Phone too long"
    )
    public void phoneTooLong() {
        contact.fillContactForm("John", "a@b.c", "1".repeat(22),
                "Subject", "This message is long enough.");
    }

    /* Message length */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Message must be between 20 and 2000 characters",
            description = "Message too short"
    )
    public void messageTooShort() {
        contact.fillContactForm("John", "a@b.c", "12345678901",
                "Subject", "Too short");
    }

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Message must be between 20 and 2000 characters",
            description = "Message too long"
    )
    public void messageTooLong() {
        contact.fillContactForm("John", "a@b.c", "12345678901",
                "Subject", "M".repeat(2001));
    }

    /* E‑mail format */

    @Test(
            expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Email must be a well-formed email address",
            description = "Malformed e‑mail should throw"
    )
    public void invalidEmail() {
        contact.fillContactForm("John", "invalid-email@", "12345678901",
                "Subject", "This message is long enough.");
    }
}
