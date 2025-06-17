package api.objects;

import com.google.gson.*;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

import java.util.Map;

import static java.lang.System.out;

public class BookingApi {
    private final APIRequestContext req;

    public BookingApi(APIRequestContext req) {
        this.req = req;
    }

    /* ---------- helpers ---------- */
    private static JsonObject sampleBooking() {
        JsonObject dates = new JsonObject();
        dates.addProperty("checkin",  "2023-07-01");
        dates.addProperty("checkout", "2023-07-10");

        JsonObject b = new JsonObject();
        b.addProperty("firstname", "Jim");
        b.addProperty("lastname",  "Brown");
        b.addProperty("totalprice", 111);
        b.addProperty("depositpaid", true);
        b.add("bookingdates", dates);
        b.addProperty("additionalneeds", "Breakfast");
        return b;
    }

    /* ---------- API actions ---------- */

    /** POST /booking — creates a booking and returns its ID. */
    public int create() {
        APIResponse r = req.post("/booking",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(sampleBooking()));

        assert r.status() == 200 : "createBooking -> 200";

        int id = JsonParser.parseString(r.text())
                .getAsJsonObject()
                .get("bookingid").getAsInt();
        out.printf("  [booking] created id=%d%n", id);
        return id;
    }

    /** GET /booking/{id} — retrieves a booking. */
    public JsonObject get(int id) {
        APIResponse r = req.get("/booking/" + id);
        assert r.status() == 200 : "getBooking -> 200";
        return JsonParser.parseString(r.text()).getAsJsonObject();
    }

     /**
     * PUT /booking/{id} — full update.
     * @param newVals key/value pairs to be updated
     */
    public JsonObject update(int id, String token, Map<String, ?> newVals) {
        JsonObject payload = sampleBooking();
        newVals.forEach((k, v) -> payload.addProperty(k, String.valueOf(v)));

        APIResponse r = req.put("/booking/" + id,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Cookie", "token=" + token)
                        .setData(payload));

        assert r.status() == 200 : "updateBooking -> 200";
        return JsonParser.parseString(r.text()).getAsJsonObject();
    }

     /**
     * PATCH /booking/{id} — partial update.
     * @param patch arbitrary JsonObject with fields to be updated
     */
    public JsonObject patch(int id, String token, JsonObject patch) {
        APIResponse r = req.patch("/booking/" + id,
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setHeader("Cookie", "token=" + token)
                        .setData(patch));

        assert r.status() == 200 : "patchBooking -> 200";
        return JsonParser.parseString(r.text()).getAsJsonObject();
    }

    /** DELETE /booking/{id}. */
    public void delete(int id, String token) {
        APIResponse r = req.delete("/booking/" + id,
                RequestOptions.create().setHeader("Cookie", "token=" + token));

        assert r.status() == 201 : "deleteBooking -> 201";
    }
}