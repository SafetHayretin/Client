import java.io.BufferedReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    String statusCode = "200";

    String statusMessage = "OK";

    Map<String, String> headers = new HashMap<>();

    BufferedReader reader;

    HttpRequest request;

    private String body;

    public HttpResponse() {

    }

    public HttpResponse(BufferedReader response) {
        this.reader = response;
    }

    public HttpResponse(BufferedReader response, HttpRequest request) {
        this.reader = response;
        this.request = request;
    }

    public void handleResponse() throws IOException {
        String line = reader.readLine();
        String[] splitted = line.split(" ");
        statusCode = splitted[1];
        statusMessage = splitted[2];

        while ((line = reader.readLine()) != null) {
            if (line.isEmpty())
                break;
            System.out.println(line);
            splitted = line.split(": ");
            headers.put(splitted[0], splitted[1]);
        }
        //ReadBody
        StringBuilder builder = new StringBuilder();
        while ((line = reader.readLine()) != null) {
            if (line.isEmpty())
                break;
            System.out.println(line);
            builder.append(line);
        }
        body = builder.toString();
        reader.close();
    }

    public int statusCode() {
        return Integer.parseInt(statusCode);
    }

    public Map<String, String> headers() {
        return headers;
    }

    public String body() {
        return this.body;
    }

    public HttpRequest request() {
        return request;
    }

    public String getStatusMessage() {
        return statusMessage;
    }

    public void addHeader(String key, String value) {
        headers.put(key, value);
    }

    public String getHeader(String key) {
        return headers.get(key);
    }

    public String[] getHeader(String... key) {
        String[] values = new String[key.length];
        for (int i = 0; i < key.length; i++) {
            values[i] = getHeader(key[i]);
        }

        return values;
    }

    public byte[] getBodyAsBytes() {
        return body.getBytes();
    }

    public static class BodyHandlers {

        private BodyHandlers() {

        }

        public static BodyHandlers ofString() {
            return new BodyHandlers();
        }
    }

    public String getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(String statusCode) {
        this.statusCode = statusCode;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public void setHeaders(Map<String, String> headers) {
        this.headers = headers;
    }

    public BufferedReader getReader() {
        return reader;
    }

    public void setReader(BufferedReader reader) {
        this.reader = reader;
    }

    public HttpRequest getRequest() {
        return request;
    }

    public void setRequest(HttpRequest request) {
        this.request = request;
    }

    public String getBody() {
        return body;
    }

    public void setBody(String body) {
        this.body = body;
    }

    public void setStatusMessage(String statusMessage) {
        this.statusMessage = statusMessage;
    }
}
