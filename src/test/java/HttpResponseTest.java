import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.nio.Buffer;

import static org.junit.jupiter.api.Assertions.*;

class HttpResponseTest {

    static HttpResponse response;

    @BeforeAll
    static void setResponse() {
        response = new HttpResponse();
    }

    @AfterEach
    void resetResponse() {
        response = new HttpResponse();
    }

    @Test
    @DisplayName("Test the default values of the HttpResponse object")
    public void testDefaultValues() {
        assertEquals(response.getStatusCode(), "200");
        assertEquals(response.getStatusMessage(), "OK");
        assertNull(response.getBody());
        assertTrue(response.getHeaders().isEmpty());
    }

    @Test
    @DisplayName("Test setting and getting the status code and message")
    public void testSettingValues() {
        response.setStatusCode(String.valueOf(404));
        response.setStatusMessage("Not Found");
        assertEquals(response.getStatusCode(), "404");
        assertEquals(response.getStatusMessage(), "Not Found");
    }

    @Test
    @DisplayName("Test setting and getting the body")
    public void testBody() {
        response.setBody("<html>Not Found</html>");
        assertEquals(response.getBody(), "<html>Not Found</html>");
    }


    @Test
    @DisplayName("Test adding and getting headers")
    public void testHeaders() {
        response.addHeader("Content-Type", "text/html");
        assertEquals(response.getHeaders().size(), 1);
        assertEquals(response.getHeaders().get("Content-Type"), "text/html");
    }

    @Test
    public void testAddHeader() {
        // Test adding a single header
        response.addHeader("Content-Type", "text/html");
        assertEquals(response.getHeaders().size(), 1);
        assertEquals(response.getHeaders().get("Content-Type"), "text/html");

        // Test adding multiple headers
        response.addHeader("Content-Length", "1024");
        response.addHeader("Connection", "close");
        assertEquals(response.getHeaders().size(), 3);
        assertEquals(response.getHeaders().get("Content-Length"), "1024");
        assertEquals(response.getHeaders().get("Connection"), "close");

        // Test replacing a header
        response.addHeader("Content-Type", "application/json");
        assertEquals(response.getHeaders().size(), 3);
        assertEquals(response.getHeaders().get("Content-Type"), "application/json");
    }

    @Test
    public void testGetHeader() {
        HttpResponse response = new HttpResponse();

        // Test getting a single header
        response.addHeader("Content-Type", "text/html");
        assertEquals(response.getHeader("Content-Type"), "text/html");

        // Test getting multiple headers
        response.addHeader("Content-Length", "1024");
        response.addHeader("Connection", "close");
        assertArrayEquals(response.getHeader("Content-Type", "Content-Length", "Connection"),
                new String[]{"text/html", "1024", "close"});

        // Test getting a non-existent header
        assertNull(response.getHeader("X-Custom-Header"));
    }

    @Test
    public void testGetBodyAsBytes() {
        HttpResponse response = new HttpResponse();

        // Test getting the body as a byte array
        response.setBody("<html>Hello World</html>");
        assertArrayEquals(response.getBodyAsBytes(), "<html>Hello World</html>".getBytes());

        // Test getting the body as a byte array when the body is null
        response.setBody(null);
        assertNull(response.getBody());
    }

    @Test
    public void testHandleRequest() throws IOException {
        String response = """
                HTTP/1.1 200 OK
                Content-Type: text/html; charset=utf-8
                Connection: keep-alive
                
                HelloWorld
                """;

        Reader reader = new StringReader(response);
        BufferedReader bufferedReader = new BufferedReader(reader);
        HttpResponse response1 = new HttpResponse(bufferedReader);

        response1.handleResponse();

        assertEquals("text/html; charset=utf-8", response1.getHeader("Content-Type"));
        assertEquals("keep-alive", response1.getHeader("Connection"));
        assertEquals("HelloWorld", response1.body());
    }
}