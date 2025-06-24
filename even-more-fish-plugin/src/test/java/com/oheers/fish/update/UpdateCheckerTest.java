package com.oheers.fish.update;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.logging.Logger;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class UpdateCheckerTest {

    private HttpClient mockHttpClient;
    private Logger mockLogger;
    private UpdateChecker checker;

    @BeforeEach
    void setUp() {
        mockHttpClient = mock(HttpClient.class);
        mockLogger = mock(Logger.class);
        checker = new UpdateChecker("1.0.0", mockHttpClient, mockLogger);
    }

    @Test
    void testGetVersionFallbackOnHttpFailure() throws Exception {
        when(mockHttpClient.send(any(), any())).thenThrow(new IOException("Simulated failure"));

        String version = checker.getVersion();

        assertEquals("1.0.0", version);
        verify(mockLogger).warning(contains("Simulated failure"));
        verify(mockLogger).info(contains("modrinth.com"));
    }

    @Test
    void testGetVersionParsesLatestCorrectly() throws Exception {
        HttpResponse<String> mockResponse = mock(HttpResponse.class);
        String json = """
                [
                  {
                    "version_number": "1.2.3"
                  }
                ]
                """;

        when(mockResponse.statusCode()).thenReturn(200);
        when(mockResponse.body()).thenReturn(json);

        when(mockHttpClient.send(
                any(HttpRequest.class),
                any(HttpResponse.BodyHandler.class))
        ).thenReturn(mockResponse);


        String version = checker.getVersion();
        assertEquals("1.2.3", version);
    }

    @Test
    void testCheckUpdateFalseWhenSameVersion() {
        UpdateChecker spyChecker = spy(checker);
        doReturn("1.0.0").when(spyChecker).getVersion();

        CompletableFuture<Boolean> result = spyChecker.checkUpdate();
        assertFalse(result.join());
    }

    @Test
    void testCheckUpdateTrueWhenNewerVersion() {
        UpdateChecker spyChecker = spy(checker);
        doReturn("1.2.0").when(spyChecker).getVersion();

        CompletableFuture<Boolean> result = spyChecker.checkUpdate();
        assertTrue(result.join());
    }
}
