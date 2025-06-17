package pages;

import com.microsoft.playwright.Page;

/**
 * Page object representing the Home page of https://automationintesting.online/
 */
public class HomePage extends BasePage {

    // URL главной страницы
    private static final String HOME_URL = "https://automationintesting.online/";

    public HomePage(Page page) {
        super(page);
    }

    /**
     * Opens the home page
     */
    public void open() {
        page.navigate(HOME_URL);
    }

    /**
     * Navigate to a specific room booking page
     * @param roomType - "single", "double", or "suite"
     * @param checkIn - check-in date in yyyy-MM-dd format (e.g. 2025-06-16)
     * @param checkOut - check-out date in yyyy-MM-dd format (e.g. 2025-06-17)
     */
    public void goToRoom(String roomType, String checkIn, String checkOut) {
        String roomId;
        switch (roomType.toLowerCase()) {
            case "single":
                roomId = "1";
                break;
            case "double":
                roomId = "2";
                break;
            case "suite":
                roomId = "3";
                break;
            default:
                throw new IllegalArgumentException("Invalid room type: " + roomType);
        }

        String url = String.format("https://automationintesting.online/reservation/%s?checkin=%s&checkout=%s", roomId, checkIn, checkOut);
        page.navigate(url);
    }

    /**
     * Navigate to admin page directly
     */
    public void clickAdmin() {
        page.navigate("https://automationintesting.online/admin");
    }

    /**
     * Navigate to rooms section (anchor)
     */
    public void goToRooms() {
        page.navigate(HOME_URL + "#rooms");
    }

    /**
     * Navigate to booking section (anchor)
     */
    public void goToBooking() {
        page.navigate(HOME_URL + "#booking");
    }

    /**
     * Navigate to location section (anchor)
     */
    public void goToLocation() {
        page.navigate(HOME_URL + "#location");
    }

    /**
     * Navigate to contact section (anchor)
     */
    public void goToContact() {
        page.navigate(HOME_URL + "#contact");
    }
}
