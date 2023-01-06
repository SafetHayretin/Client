import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpRequestTest {
    static HttpRequest.Builder builder;

    @BeforeAll
    static void createBuilder() {
        builder = HttpRequest.newBuilder();
    }

    @Test
    public void testUri() throws Exception {
        URI expectedUri = new URI("http://www.example.com");
        HttpRequest request = HttpRequest.newBuilder(expectedUri).build();
        assertEquals(expectedUri, request.uri());
    }

    @Test
    public void testGetConnection() throws Exception {
        URI uri = new URI("http://www.example.com");
        HttpRequest request = HttpRequest.newBuilder().uri(uri).POST(HttpRequest.BodyPublishers.ofString("Sample request body")).build();
        HttpURLConnection con = request.getConnection();

        assertEquals("POST", con.getRequestMethod());
        assertEquals("http://www.example.com", con.getRequestProperty("host"));
    }

    @Test
    public void testNewBuilder() throws URISyntaxException {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        assertEquals(null, builder.uri);

        URI expectedUri = new URI("http://www.example.com");
        builder = HttpRequest.newBuilder(expectedUri);

        assertEquals(expectedUri, builder.uri);
    }

    @Test
    public void testBuilderUri() throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        URI expectedUri = new URI("http://www.example.com");
        builder.uri(expectedUri);

        assertEquals(expectedUri, builder.uri);
    }

    @Test
    public void testBuilderHeader() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.header("Content-Type", "application/json");

        assertEquals("application/json", builder.headers.get("Content-Type"));
    }

    @Test
    public void testBuilderHeaders() throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.headers("Content-Type", "application/json", "Authorization", "Basic abc123");

        assertEquals("application/json", builder.headers.get("Content-Type"));
        assertEquals("Basic abc123", builder.headers.get("Authorization"));
    }

    @Test
    public void testGET() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.GET();

        assertEquals("GET", builder.method);
        assertEquals(null, builder.bodyPublisher);
    }

    @Test
    public void testPOST() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        HttpRequest.BodyPublisher bodyPublisher = HttpRequest.BodyPublishers.ofString("{\"username\": \"password\"}");
        builder.POST(bodyPublisher);

        assertEquals("POST", builder.method);
        assertEquals(bodyPublisher, builder.bodyPublisher);
    }

    @Test
    public void testBuild() throws Exception {
        URI expectedUri = new URI("http://www.example.com");
        HttpRequest.Builder builder = HttpRequest.newBuilder(expectedUri);
        HttpRequest request = builder.build();

        assertEquals(expectedUri, request.builder.uri);
    }

    @Test
    public void testBodyPublisherNoBody() throws Exception {
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.noBody();
        assertEquals(0, publisher.contentLength());
    }

    @Test
    public void testBodyPublisherOfByteArray() throws Exception {
        byte[] arr = "Hello, world!".getBytes();
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofByteArray(arr);
        assertEquals(arr.length, publisher.contentLength());
    }

    @Test
    public void testBodyPublisherOfString() throws Exception {
        String data = "Hello, world!";
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.ofString(data);
        assertEquals(data.getBytes().length, publisher.contentLength());
    }

    @Test
    public void testBodyPublisherFromFile() throws Exception {
        HttpRequest.BodyPublisher publisher = HttpRequest.BodyPublishers.fromFile(Path.of("pom.xml"));
        assertEquals(Files.size(Path.of("pom.xml")), publisher.contentLength());
    }

    @Test
    public void testGETNoBodyPublisher() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.GET();
        assertEquals("GET", builder.method);
        assertEquals(null, builder.bodyPublisher);
    }

    @Test
    public void testPOSTNoBodyPublisher() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.POST(null);
        assertEquals("POST", builder.method);
        assertEquals(null, builder.bodyPublisher);
    }

    @Test
    public void testPUTNoBodyPublisher() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.PUT(null);
        assertEquals("PUT", builder.method);
        assertEquals(null, builder.bodyPublisher);
    }

    @Test
    public void testDELETENoBodyPublisher() {
        HttpRequest.Builder builder = HttpRequest.newBuilder();
        builder.DELETE(null);

        assertEquals("DELETE", builder.method);
        assertEquals(null, builder.bodyPublisher);
    }

    @Test
    public void testGetConnectionWithHeaders() throws Exception {
        URI uri = new URI("http://www.example.com");
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri).GET();
        builder.headers("Content-Type", "application/json", "Authorization", "Basic abc123");
        HttpRequest request = builder.build();
        HttpURLConnection con = request.getConnection();
        assertEquals("GET", con.getRequestMethod());
        assertEquals("http://www.example.com", con.getRequestProperty("host"));
        assertEquals("application/json", con.getRequestProperty("Content-Type"));
        assertEquals("Basic abc123", con.getRequestProperty("Authorization"));
    }
}