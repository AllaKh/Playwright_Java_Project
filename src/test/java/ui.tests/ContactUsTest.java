package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.ContactUsPage;

/**
 * Contact Us form tests – 15 cases (1 positive + 14 validation).
 */
public class ContactUsTest {

    private Playwright    playwright;
    private Browser       browser;
    private Page          page;
    private ContactUsPage contact;

    @BeforeMethod
    public void setup() {
        playwright = Playwright.create();
        browser = playwright.chromium()
                .launch(new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://automationintesting.online/#contact");
        contact = new ContactUsPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        browser.close();
        playwright.close();
    }

    private void pause() {
        page.waitForTimeout(1_000);
    }

    @Test
    public void positiveSubmission() {
        String name = "John Doe";
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

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Name may not be blank")
    public void blankName() {
        contact.fillContactForm(" ", "a@b.c", "12345678901", "Subject", "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Email may not be blank")
    public void blankEmail() {
        contact.fillContactForm("John", " ", "12345678901", "Subject", "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Phone may not be blank")
    public void blankPhone() {
        contact.fillContactForm("John", "a@b.c", " ", "Subject", "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Subject may not be blank")
    public void blankSubject() {
        contact.fillContactForm("John", "a@b.c", "12345678901", " ", "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Message may not be blank")
    public void blankMessage() {
        contact.fillContactForm("John", "a@b.c", "12345678901", "Subject", " ");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Subject must be between 5 and 100 characters")
    public void subjectTooShort() {
        contact.fillContactForm("John", "a@b.c", "12345678901", "abc", "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Subject must be between 5 and 100 characters")
    public void subjectTooLong() {
        String longSubject = "S".repeat(101);
        contact.fillContactForm("John", "a@b.c", "12345678901", longSubject, "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Phone must be between 11 and 21 characters")
    public void phoneTooShort() {
        contact.fillContactForm("John", "a@b.c", "1234567", "Subject", "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Phone must be between 11 and 21 characters")
    public void phoneTooLong() {
        String longPhone = "1".repeat(22);
        contact.fillContactForm("John", "a@b.c", longPhone, "Subject", "This message is long enough.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Message must be between 20 and 2000 characters")
    public void messageTooShort() {
        contact.fillContactForm("John", "a@b.c", "12345678901", "Subject", "Too short");
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Message must be between 20 and 2000 characters")
    public void messageTooLong() {
        String longMessage = "M".repeat(2001);
        contact.fillContactForm("John", "a@b.c", "12345678901", "Subject", longMessage);
    }

    @Test(expectedExceptions = IllegalArgumentException.class, expectedExceptionsMessageRegExp = "Email must be a well-formed email address")
    public void invalidEmail() {
        contact.fillContactForm("John", "invalid-email@", "12345678901", "Subject", "This message is long enough.");
    }

}
