package pages;

import com.microsoft.playwright.Locator;
import com.microsoft.playwright.Page;

/**
 * Page object for Admin login page at /admin
 */
public class AdminLoginPage extends BasePage {

    private final Locator usernameInput;
    private final Locator passwordInput;
    private final Locator loginButton;

    public AdminLoginPage(Page page) {
        super(page);
        usernameInput = page.locator("#username");
        passwordInput = page.locator("#password");
        loginButton = page.locator("button[type='submit']");
    }

    public boolean isAt() {
        // Проверка по URL и наличию поля username
        return page.url().contains("/admin") && usernameInput.isVisible();
    }

    public void login(String username, String password) {
        usernameInput.fill(username);
        passwordInput.fill(password);
        loginButton.click();
    }
}
