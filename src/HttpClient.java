import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.util.List;
import java.util.Map;

public class HttpClient {
    Redirect followRedirects;

    private HttpClient() {
    }

    public static HttpClient newHttpClient() {
        return new HttpClient();
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public enum Redirect {
        NEVER, ALWAYS, NORMAL
    }

    public HttpResponse send(HttpRequest request, HttpResponse.BodyHandlers responseBodyHandler) throws IOException {
        HttpURLConnection con = request.getConnection();

        BufferedReader in = new BufferedReader(new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuffer content = new StringBuffer();

        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        Map<String, List<String>> headers = con.getHeaderFields();
        HttpResponse response = new HttpResponse(content.toString());
        response.headers = headers;
        response.handleResponse();

        return response;
    }

    public static class Builder {
        public Redirect redirect;

        public Builder followRedirects(Redirect redirect) {
            this.redirect = redirect;
            return this;
        }

        public HttpClient build() {
            HttpClient client = new HttpClient();
            client.followRedirects = redirect;

            return client;
        }
    }
}
