import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;

class HttpClientTest {

    @Test
    public void testHttpClient() throws Exception {
        // Given
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("https://postman-echo.com/post"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .GET()
                .build();
        HttpClient client = HttpClient.newBuilder().build();
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());
        // When
        response.handleResponse();

        // Then
        assertEquals("200", response.getStatusCode());
    }

    @Test
    public void testHttpPost() throws IOException, URISyntaxException {
        // Given
        HttpClient client = HttpClient.newBuilder().build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(new URI("http://www.example.com/form"))
                .headers("Content-Type", "text/plain;charset=UTF-8")
                .build();

        // When
        HttpResponse response = client.send(request, HttpResponse.BodyHandlers.ofString());

        // Then
        assertEquals("200", response.getStatusCode());
    }
}