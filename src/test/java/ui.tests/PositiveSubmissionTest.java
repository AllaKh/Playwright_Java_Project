// PositiveSubmissionTest.java
package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.ContactUsPage;

public class PositiveSubmissionTest {
    private Playwright pw; private Browser br; private Page pg; private ContactUsPage cu;

    @BeforeMethod public void setUp() {
        pw = Playwright.create();
        br = pw.chromium().launch(new BrowserType.LaunchOptions().setHeadless(false));
        pg = br.newPage();
        pg.navigate("https://automationintesting.online/#contact");
        cu = new ContactUsPage(pg);
    }
    @AfterMethod public void tearDown() { br.close(); pw.close(); }

    @Test
    public void positiveSubmission() {
        cu.fillContactForm("John Doe", "john@ex.com", "12345678901",
                "Room availability",
                "Hello, I'd like to book a room next weekend. Please advise.");
        pg.waitForTimeout(1000);
        cu.submitForm();
        Assert.assertTrue(cu.bannerVisible());
    }
}
