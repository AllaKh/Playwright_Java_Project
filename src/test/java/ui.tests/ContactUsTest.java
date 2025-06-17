// ContactUsTest.java
package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.ContactUsPage;

/**
 * Test suite for the Contact Us form.
 */
public class ContactUsTest {

    private Playwright playwright;
    private Browser browser;
    private Page page;
    private ContactUsPage contact;

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser = playwright.chromium().launch(
                new BrowserType.LaunchOptions().setHeadless(false));
        page = browser.newPage();
        page.navigate("https://automationintesting.online/#contact");
        contact = new ContactUsPage(page);
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        if (browser != null) browser.close();
        if (playwright != null) playwright.close();
    }

    @Test(description = "Successful submission shows personalised banner")
    public void shouldSubmitContactForm() {
        String name = "Jane Smith";
        String email = "jane@example.com";
        String phone = "12345678901";
        String subject = "Room availability";
        String message = "Hello, I would like to know if a room is available next weekend.";

        contact.fillContactForm(name, email, phone, subject, message);
        contact.submitForm();

        Assert.assertTrue(contact.bannerVisible(), "Banner should be visible");
        String expected = ContactUsPage.expectedSuccessMessage(name, subject);
        Assert.assertEquals(contact.bannerText(), expected);
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Subject must be between 5 and 100 characters")
    public void shouldRejectShortSubject() {
        contact.fillContactForm("A", "a@b.c", "12345678901", "abc", "This message is long enough to pass.");
    }

    @Test(expectedExceptions = IllegalArgumentException.class,
            expectedExceptionsMessageRegExp = "Message may not be blank")
    public void shouldRejectBlankMessage() {
        contact.fillContactForm("A", "a@b.c", "12345678901", "Valid subject", "   ");
    }

    // Additional tests for all validation rules can be added similarly
}
