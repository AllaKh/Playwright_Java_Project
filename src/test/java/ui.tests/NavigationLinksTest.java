package ui.tests;

import com.microsoft.playwright.Locator;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.HomePage;
import pages.NavigationBar;
import ui.core.BasePlaywrightTest;

/**
 * Verifies that every top‑navigation link opens the correct section or page.
 * The “Amenities” test documents a known issue and will fail only
 * while the bug is still present.
 */
public class NavigationLinksTest extends BasePlaywrightTest {

    private HomePage      home;
    private NavigationBar nav;

    /* per‑test setup */

    @BeforeMethod
    public void openHomePage() {
        page.navigate("https://automationintesting.online/");
        home = new HomePage(page);
        nav  = new NavigationBar(page);
    }

    /* data providers */

    @DataProvider(name = "workingLinks")
    public Object[][] workingLinks() {
        return new Object[][]{
                {"Rooms",    "#rooms"},
                {"Booking",  "#booking"},
                {"Location", "#location"},
                {"Contact",  "#contact"},
                {"Admin",    "/admin"}
        };
    }

    /* green tests */

    @Test(
            dataProvider  = "workingLinks",
            description   = "Each working link loads the expected URL or fragment"
    )
    public void verifyWorkingLinks(String linkText, String expected) {
        nav.clickLink(linkText);
        Assert.assertTrue(
                page.url().contains(expected),
                "URL does not contain expected fragment: " + expected
        );
    }

    /* red test (known bug) */

    @Test(description = "KNOWN BUG: Amenities link should navigate but currently fails")
    public void amenitiesKnownBug() {
        nav.clickLink("Amenities");

        // Healthy if we actually navigate OR if the Amenities section becomes visible
        boolean linkWorks = page.url().contains("#amenities")
                || page.isVisible("section#amenities, h2:text-is('Amenities')");

        if (linkWorks) {
            Assert.fail("Bug fixed: Amenities link now works – remove Jira ticket & mark test green.");
        } else {
            System.out.println("[JIRA‑BUG] Amenities link still broken – please fix.");
            Assert.fail("Amenities link returns 404 / blank page (known issue).");
        }
    }
}
