package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.ContactUsPage;

/**
 * Contact Us form tests – 15 cases (1 positive + 14 validation).
 */
public class ContactUsTest {

    private Playwright playwright;
    private Browser    browser;
    private Page       page;
    private ContactUsPage contact;

    @BeforeMethod
    public void setup() {
        playwright = Playwright.create();
        browser    = playwright.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        page       = browser.newPage();
        page.navigate("https://automationintesting.online/#contact");
        contact    = new ContactUsPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void teardown() {
        browser.close();
        playwright.close();
    }

    private void pause() { page.waitForTimeout(1000); }

    /* Positive */
    @Test
    public void positiveSubmission() {
        contact.fillContactForm("John Doe", "john@ex.com", "12345678901", "Room query",
                "I would like to book a room for next Friday, please advise availability.");
        pause();
        contact.submitForm();
        Assert.assertTrue(contact.bannerVisible());
    }

    /* Blank fields */
    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Name may not be blank")
    public void blankName() {
        contact.fillContactForm(" ", "a@b.c", "12345678901", "Subj",
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Email may not be blank")
    public void blankEmail() {
        contact.fillContactForm("A", " ", "12345678901", "Subj",
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Phone may not be blank")
    public void blankPhone() {
        contact.fillContactForm("A", "a@b.c", " ", "Subj",
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Subject may not be blank")
    public void blankSubject() {
        contact.fillContactForm("A", "a@b.c", "12345678901", " ",
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Message may not be blank")
    public void blankMessage() {
        contact.fillContactForm("A", "a@b.c", "12345678901", "Subj", "   ");
    }

    /* Length validations */
    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Subject must be between 5 and 100 characters")
    public void subjectTooShort() {
        contact.fillContactForm("A", "a@b.c", "12345678901", "abc",
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Subject must be between 5 and 100 characters")
    public void subjectTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 101; i++) sb.append('s');
        contact.fillContactForm("A", "a@b.c", "12345678901", sb.toString(),
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Phone must be between 11 and 21 characters")
    public void phoneTooShort() {
        contact.fillContactForm("A", "a@b.c", "1234567", "Subj",
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Phone must be between 11 and 21 characters")
    public void phoneTooLong() {
        contact.fillContactForm("A", "a@b.c", "1234567890123456789012", "Subj",
                "This message has sufficient length to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Message must be between 20 and 2000 characters")
    public void messageTooShort() {
        contact.fillContactForm("A", "a@b.c", "12345678901", "Subj", "short msg");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Message must be between 20 and 2000 characters")
    public void messageTooLong() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < 2101; i++) sb.append('m');
        contact.fillContactForm("A", "a@b.c", "12345678901", "Subj", sb.toString());
    }
}
