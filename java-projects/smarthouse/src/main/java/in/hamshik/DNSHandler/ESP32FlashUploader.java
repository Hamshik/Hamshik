package in.hamshik.DNSHandler;

import in.hamshik.DNSHandler.WiFiScanner.EspDevice;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.enums.Opcode;
import org.java_websocket.handshake.ServerHandshake;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.ByteBuffer;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class ESP32FlashUploader {
    private static final int CHUNK_SIZE = 8 * 1024;
    private static final int CONNECT_TIMEOUT_SECONDS = 10;
    private static final int UPDATE_TIMEOUT_SECONDS = 120;

    public static String uploadBin(EspDevice device, String binFilePath)
            throws IOException, InterruptedException {
        Path firmwarePath = Path.of(binFilePath);
        if (!Files.isRegularFile(firmwarePath) || !Files.isReadable(firmwarePath)) {
            throw new IOException("Firmware file is missing or unreadable.");
        }

        URI uri;
        try {
            uri = new URI("ws", null, device.host(), device.port(), device.path(), null, null);
        } catch (Exception e) {
            throw new IOException("Invalid ESP32 WebSocket address.", e);
        }

        CompletableFuture<String> updateResult = new CompletableFuture<>();
        WebSocketClient client = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshake) {
            }

            @Override
            public void onMessage(String message) {
                try {
                    var json = com.google.gson.JsonParser.parseString(message).getAsJsonObject();
                    String status = json.has("status") ? json.get("status").getAsString() : "";
                    String detail = json.has("message") ? json.get("message").getAsString() : message;

                    if ("error".equalsIgnoreCase(status)) {
                        updateResult.completeExceptionally(new IOException("ESP32 OTA error: " + detail));
                    } else if ("success".equalsIgnoreCase(status) && "Update Success".equalsIgnoreCase(detail)) {
                        updateResult.complete(detail);
                    }
                } catch (RuntimeException e) {
                    updateResult.completeExceptionally(new IOException("Invalid response from ESP32: " + message, e));
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                if (!updateResult.isDone()) {
                    updateResult.completeExceptionally(new IOException(
                            "ESP32 closed the WebSocket before confirming the update: " + reason));
                }
            }

            @Override
            public void onError(Exception error) {
                updateResult.completeExceptionally(new IOException("ESP32 WebSocket upload failed.", error));
            }
        };

        try {
            if (!client.connectBlocking(CONNECT_TIMEOUT_SECONDS, TimeUnit.SECONDS)) {
                throw new IOException("Could not connect to the ESP32 WebSocket.");
            }

            try (InputStream input = Files.newInputStream(firmwarePath)) {
                byte[] currentChunk = readChunk(input);
                if (currentChunk == null) {
                    throw new IOException("Firmware file is empty.");
                }

                boolean firstChunk = true;
                while (currentChunk != null) {
                    byte[] nextChunk = readChunk(input);
                    Opcode opcode = firstChunk ? Opcode.BINARY : Opcode.CONTINUOUS;
                    client.sendFragmentedFrame(opcode, ByteBuffer.wrap(currentChunk), nextChunk == null);
                    firstChunk = false;
                    currentChunk = nextChunk;
                }
            }

            try {
                return updateResult.get(UPDATE_TIMEOUT_SECONDS, TimeUnit.SECONDS);
            } catch (ExecutionException e) {
                Throwable cause = e.getCause();
                if (cause instanceof IOException ioException) {
                    throw ioException;
                }
                throw new IOException("Firmware update failed.", cause);
            } catch (TimeoutException e) {
                throw new IOException("Timed out waiting for the ESP32 to finish the firmware update.", e);
            }
        } finally {
            client.close();
        }
    }

    private static byte[] readChunk(InputStream input) throws IOException {
        byte[] buffer = new byte[CHUNK_SIZE];
        int bytesRead = input.read(buffer);
        if (bytesRead == -1) {
            return null;
        }
        if (bytesRead == buffer.length) {
            return buffer;
        }
        return java.util.Arrays.copyOf(buffer, bytesRead);
    }
}
