import java.io.*;
import java.net.*;
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
        String url = request.uri().getHost();
        HttpRequest.Builder builder = request.builder;
        Socket sock = new Socket(url, 80);
        OutputStream output = sock.getOutputStream();
        BufferedWriter writer = new BufferedWriter(new OutputStreamWriter(output));
        writer.write(request.builder.method + " / HTTP/1.1\r\n");
        writer.write("Host: " + url + "\r\n");
        writer.write("Path: " + request.uri().getPath() + "\r\n");

        for (Map.Entry<String, String> entry : builder.headers.entrySet()) {
            writer.write(entry.getKey() + " : " + entry.getValue() + "\r\n");
        }

        writer.write("\r\n");
        writeBody(output, builder.bodyPublisher);
        writer.flush();
        InputStream stream = sock.getInputStream();
        BufferedReader reader = new BufferedReader(new InputStreamReader(stream));
        HttpResponse response = new HttpResponse(reader, request);
        response.handleResponse();

        return response;
    }

    private void writeBody(OutputStream out, HttpRequest.BodyPublisher bodyPublisher) throws IOException {
        byte[] arr = new byte[1024];
        int bytesRead;
        if (bodyPublisher == null)
            return;
        while ((bytesRead = bodyPublisher.body.read(arr)) != -1) {
            out.write(arr, 0, bytesRead);
        }
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
