import java.util.List;
import java.util.Map;

public class HttpResponse {
    String responseCode;

    Map<String, List<String>> headers;

    String response;

    public HttpResponse(String response) {
        this.response = response;
    }

    public void handleResponse() {
        responseCode = headers.get(null).toString();
    }

    public int statusCode() {
        String[] split = responseCode.split(" ");

        return Integer.parseInt(split[1]);
    }

    public static class BodyHandlers {

        private BodyHandlers() {

        }

        public static BodyHandlers ofString() {
            return new BodyHandlers();
        }
    }
}
