package api.objects;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.microsoft.playwright.APIRequestContext;
import com.microsoft.playwright.APIResponse;
import com.microsoft.playwright.options.RequestOptions;

/** Encapsulates /auth endpoint. */
public class AuthApi {
    private final APIRequestContext req;

    public AuthApi(APIRequestContext req) {
        this.req = req;
    }

    /** Sends credentials and returns freshly‑issued token. */
    public String createToken(String user, String pass) {
        // Build JSON payload
        JsonObject payload = new JsonObject();
        payload.addProperty("username", user);
        payload.addProperty("password", pass);

        // POST /auth with JSON body (RequestOptions handles serialization)
        APIResponse resp = req.post("/auth",
                RequestOptions.create()
                        .setHeader("Content-Type", "application/json")
                        .setData(payload));

        assert resp.status() == 200 : "Auth must return 200";

        // Parse response
        JsonObject body = JsonParser.parseString(resp.text()).getAsJsonObject();
        assert body.has("token") : "Response must contain token";

        return body.get("token").getAsString();
    }
}