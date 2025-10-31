package net.runelite.client.plugins.a1tests.helpers;

import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.Perspective;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.handshake.ServerHandshake;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.inject.Inject;
import javax.inject.Singleton;
import java.net.URI;

@Singleton
public class WebSocketHelper {

    private static final Logger log = LoggerFactory.getLogger(WebSocketHelper.class);
    private WebSocketClient webSocketClient;
    private final Client client;

    @Inject
    public WebSocketHelper(Client client) {
        this.client = client;
    }

    /**
     * Establishes a WebSocket connection to the provided URL.
     * Handles onOpen, onMessage, onClose, and onError callbacks.
     */
    public void connectToWebSocket(String url) throws Exception {
        URI uri = new URI(url);
        webSocketClient = new WebSocketClient(uri) {
            @Override
            public void onOpen(ServerHandshake handshakeData) {
                log.info("Connected to WebSocket server.");
            }

            @Override
            public void onMessage(String message) {
                log.info("Received message: {}", message);

                if (message.contains("movement_done")) {
                    log.info("Movement done message received.");
                    // You can trigger in-game logic here instead of sending a signal
                } else {
                    log.warn("Unknown message type received.");
                }
            }

            @Override
            public void onClose(int code, String reason, boolean remote) {
                log.info("Disconnected from WebSocket server. Reason: {}", reason);
            }

            @Override
            public void onError(Exception ex) {
                log.error("WebSocket error", ex);
            }
        };
        webSocketClient.connectBlocking(); // Waits for connection to establish before continuing
    }

    /**
     * Sends a "move_to" signal to the WebSocket based on the screen coordinates
     * of a TileObject (e.g. sack, vein, etc.).
     */
    public void sendMoveToSignal(TileObject tileObject) {
        if (tileObject == null) {
            log.warn("TileObject is null.");
            return;
        }

        WorldPoint worldPoint = tileObject.getWorldLocation();
        LocalPoint localPoint = tileObject.getLocalLocation();

        if (localPoint == null || worldPoint == null) {
            log.warn("TileObject coordinates could not be determined.");
            return;
        }

        sendMoveToSignalInternal(localPoint, worldPoint.getPlane());
    }

    /**
     * Sends a "move_to" signal based on a given WorldPoint.
     * Converts the WorldPoint to LocalPoint and screen coordinates.
     */
    public void sendMoveToSignal(WorldPoint worldPoint) {
        if (worldPoint == null) {
            log.warn("WorldPoint is null.");
            return;
        }

        LocalPoint localPoint = LocalPoint.fromWorld(client, worldPoint);
        if (localPoint == null) {
            log.warn("Could not convert WorldPoint to LocalPoint.");
            return;
        }

        sendMoveToSignalInternal(localPoint, worldPoint.getPlane());
    }

    /**
     * Internal method to format and send the "move_to" message with
     * the computed screen coordinates.
     */
    private void sendMoveToSignalInternal(LocalPoint localPoint, int plane) {
        Point screenPoint = Perspective.localToCanvas(client, localPoint, plane);

        if (screenPoint == null) {
            log.warn("Unable to get screen coordinates.");
            return;
        }

        // Adjust screen coordinates to simulate an accurate click area
        int screenX = screenPoint.getX() + 8;
        int screenY = screenPoint.getY() + 31;

        String message = String.format("{\"type\": \"move_to\", \"screenX\": %d, \"screenY\": %d}", screenX, screenY);

        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
            log.info("Sent move_to message: ScreenX = {}, ScreenY = {}", screenX, screenY);
        } else {
            log.error("WebSocket client is not connected.");
        }
    }

    /**
     * Sends a "move_to" message directly using given screen coordinates.
     */
    public void sendMoveToSignal(int screenX, int screenY) {
        String message = String.format("{\"type\": \"move_to\", \"screenX\": %d, \"screenY\": %d}", screenX, screenY);

        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.send(message);
            log.info("Sent move_to message: ScreenX = {}, ScreenY = {}", screenX, screenY);
        } else {
            log.error("WebSocket client is not connected.");
        }
    }

    /**
     * Closes the WebSocket connection if it's open.
     */
    public void closeConnection() {
        if (webSocketClient != null && webSocketClient.isOpen()) {
            webSocketClient.close();
            log.info("WebSocket connection closed.");
        } else {
            log.warn("WebSocket client is not connected, no need to close.");
        }
    }
}
