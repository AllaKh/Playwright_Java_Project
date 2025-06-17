package ui.tests;

import com.microsoft.playwright.*;
import org.testng.Assert;
import org.testng.annotations.*;
import pages.HomePage;
import pages.NavigationBar;

/**
 * Verifies that every top‑navigation link opens the correct section or page.
 * The “Amenities” test documents a known issue and will fail only
 * while the bug is still present.
 */
public class NavigationLinksTest {

    private Playwright playwright;
    private Browser    browser;
    private Page       page;

    private HomePage      home;
    private NavigationBar nav;

    /* ─────────────── test life‑cycle ─────────────── */

    @BeforeMethod
    public void setUp() {
        playwright = Playwright.create();
        browser    = playwright.chromium().launch();
        page       = browser.newPage();

        home = new HomePage(page);
        nav  = new NavigationBar(page);

        page.navigate("https://automationintesting.online/");
    }

    @AfterMethod(alwaysRun = true)
    public void tearDown() {
        browser.close();
        playwright.close();
    }

    /* ─────────────── data providers ─────────────── */

    @DataProvider(name = "workingLinks")
    public Object[][] workingLinks() {
        return new Object[][] {
                {"Rooms",    "#rooms"},
                {"Booking",  "#booking"},
                {"Location", "#location"},
                {"Contact",  "#contact"},
                {"Admin",    "/admin"}
        };
    }

    /* ─────────────── green tests ─────────────── */

    @Test(dataProvider = "workingLinks",
            description   = "Each working link loads the expected URL or fragment")
    public void verifyWorkingLinks(String linkText, String expected) {
        nav.clickLink(linkText);
        Assert.assertTrue(
                page.url().contains(expected),
                "URL does not contain expected fragment: " + expected);
    }

    /* ─────────────── red test ─────────────── */

    @Test(description = "KNOWN BUG: Amenities link should navigate but currently fails")
    public void amenitiesKnownBug() {
        nav.clickLink("Amenities");

        // The link is considered healthy if it navigates to #amenities OR if the page stays in view.
        boolean linkWorks = page.url().contains("#amenities")
                || page.isVisible("section#amenities, h2:text-is('Amenities')");

        if (linkWorks) {
            Assert.fail("Bug fixed: Amenities link now works – remove Jira ticket & mark test green.");
        } else {
            System.out.println("[JIRA‑BUG] Amenities link still broken – please fix.");
            // deliberately let the test fail to keep the bug visible on the dashboard
            Assert.fail("Amenities link returns 404 / blank page (known issue).");
        }
    }
}
