import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.function.Supplier;

public class HttpRequest {
    public Builder builder;

    private HttpRequest() {
    }

    public HttpURLConnection getConnection() throws IOException {
        URL url = builder.uri.toURL();
        HttpURLConnection con = (HttpURLConnection) url.openConnection();
        con.setRequestMethod(builder.method);
        con.setRequestProperty("host", url.toString());
        for (Map.Entry<String, String> entry : builder.headers.entrySet()) {
            System.out.println(entry.getKey() + " : " + entry.getValue());
            con.setRequestProperty(entry.getKey(), entry.getValue());
        }

        if (!builder.method.equals("GET")) {
            con.setDoOutput(true);
            DataOutputStream out = new DataOutputStream(con.getOutputStream());
            out.write(builder.bodyPublisher.body);
            out.flush();
            out.close();
        }

        return con;
    }

    public static Builder newBuilder() {
        return new Builder();
    }

    public static Builder newBuilder(URI uri) {
        Builder builder = new Builder();
        builder.uri = uri;

        return builder;
    }

    public static class Builder {
        public URI uri;

        private String method;

        private boolean expectContinue;

        Map<String, String> headers = new HashMap<>();

        private BodyPublisher bodyPublisher;

        private volatile Optional<HttpClient.Version> version;

        private Duration duration;

        private Builder() {
        }

        public Builder uri(URI uri) {
            this.uri = uri;
            return this;
        }


        public Builder header(String name, String value) {
            headers.put(name, value);

            return this;
        }

        public Builder headers(String... headers) {
            if (headers.length % 2 != 0)
                throw new IllegalArgumentException("Headers are incorrect");

            for (int i = 0; i < headers.length; i++) {
                this.headers.put(headers[i], headers[++i]);
            }

            return this;
        }

        public Builder GET() {
            this.method = "GET";

            return this;
        }

        public Builder POST(BodyPublisher bodyPublisher) {
            this.method = "POST";
            this.bodyPublisher = bodyPublisher;

            return this;
        }

        public Builder PUT(BodyPublisher bodyPublisher) {
            this.method = "PUT";
            this.bodyPublisher = bodyPublisher;

            return this;
        }

        public Builder DELETE(BodyPublisher bodyPublisher) {
            this.method = "DELETE";
            this.bodyPublisher = bodyPublisher;

            return this;
        }

        public HttpRequest build() {
            HttpRequest request = new HttpRequest();
            request.builder = this;

            return request;
        }
    }

    public static class BodyPublisher {
        private byte[] body;

        private long contentLength() {
            return body.length;
        }
    }

    static class BodyPublishers {
        private BodyPublishers() {
        }

        public static BodyPublisher noBody() {
            BodyPublisher publisher = new BodyPublisher();
            publisher.body = new byte[0];

            return publisher;
        }

        public static BodyPublisher ofByteArray(byte[] arr) {
            BodyPublisher publisher = new BodyPublisher();
            publisher.body = arr;

            return publisher;
        }

        public static BodyPublisher ofString(String data) {
            BodyPublisher publisher = new BodyPublisher();
            publisher.body = data.getBytes();

            return publisher;
        }

        public static BodyPublisher fromFile(Path path) throws IOException {
            BodyPublisher publisher = new BodyPublisher();
            publisher.body = Files.readAllBytes(path);

            return publisher;
        }

        public static BodyPublisher ofInputStream(Supplier<? extends InputStream> inputStream) throws IOException {
            BodyPublisher publisher = new BodyPublisher();
            publisher.body = inputStream.get().readAllBytes();

            return publisher;
        }
    }
}
