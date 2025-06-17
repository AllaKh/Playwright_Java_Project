// src/main/java/api/tests/BookingApiTest.java
package api.tests;

import api.core.BaseApiTest;
import api.objects.*;
import com.google.gson.JsonObject;

import java.util.Map;

public class BookingApiTest extends BaseApiTest {
    private final BookingApi booking = new BookingApi(REQ);
    private final String token = new AuthApi(REQ).createToken("admin", "password123");

    public void run() {
        System.out.println("Running BookingApiTest …");

        /* 1. Create */
        int id = booking.create();
        assert id > 0;

        /* 2. Read */
        JsonObject got = booking.get(id);
        assert "Jim".equals(got.get("firstname").getAsString());

        /* 3. Update (PUT) */
        JsonObject upd = booking.update(id, token, Map.of("firstname", "James"));
        assert "James".equals(upd.get("firstname").getAsString());

        /* 4. Patch */
        JsonObject patch = new JsonObject();
        patch.addProperty("additionalneeds", "Lunch");
        JsonObject upd2 = booking.patch(id, token, patch);
        assert "Lunch".equals(upd2.get("additionalneeds").getAsString());

        /* 5. Delete */
        booking.delete(id, token);
        assert REQ.get("/booking/" + id).status() == 404 : "Booking must be gone";
    }
}