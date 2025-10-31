package net.runelite.client.plugins.a1tests.helpers;

import net.runelite.api.Tile;
import net.runelite.api.*;
import net.runelite.api.coords.LocalPoint;
import net.runelite.api.coords.WorldPoint;
import java.util.List;

public class FindObject {

    // ✅ Finds the closest GroundObject that matches any of the specified IDs
    public static GroundObject findClosestGroundObject(Client client, int[] objectIds, int scanRange) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        Tile[][][] tiles = client.getScene().getTiles();
        int plane = client.getPlane();

        GroundObject closest = null;
        int closestDistance = Integer.MAX_VALUE;

        // Scan a square area around the player defined by scanRange
        for (int dx = -scanRange; dx <= scanRange; dx++) {
            for (int dy = -scanRange; dy <= scanRange; dy++) {
                // Create a world point offset from the player's current location
                WorldPoint checkPoint = playerLocation.dx(dx).dy(dy);
                LocalPoint localPoint = LocalPoint.fromWorld(client, checkPoint);

                if (localPoint == null) continue;

                int sceneX = localPoint.getSceneX();
                int sceneY = localPoint.getSceneY();

                // Skip if the tile is out of bounds or null
                if (sceneX < 0 || sceneY < 0 || sceneX >= tiles[plane].length ||
                        tiles[plane][sceneX] == null || sceneY >= tiles[plane][sceneX].length) continue;

                Tile tile = tiles[plane][sceneX][sceneY];
                if (tile == null) continue;

                GroundObject groundObject = tile.getGroundObject();
                // Check if the ground object matches one of the target IDs
                if (groundObject != null && contains(objectIds, groundObject.getId())) {
                    int distance = playerLocation.distanceTo(checkPoint);
                    // Update the closest match if this one is closer
                    if (distance < closestDistance) {
                        closest = groundObject;
                        closestDistance = distance;
                    }
                }
            }
        }

        return closest;
    }

    // ✅ Finds the closest WallObject that matches the given IDs and is reachable by path
    public static WallObject findClosestWallObject(Client client, int[] objectIds, int scanRange, AStarPathfinder pathfinder) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        Tile[][][] tiles = client.getScene().getTiles();
        int plane = client.getPlane();

        WallObject closest = null;
        int closestPathLength = Integer.MAX_VALUE;

        // Scan nearby tiles
        for (int dx = -scanRange; dx <= scanRange; dx++) {
            for (int dy = -scanRange; dy <= scanRange; dy++) {
                WorldPoint checkPoint = playerLocation.dx(dx).dy(dy);
                LocalPoint localPoint = LocalPoint.fromWorld(client, checkPoint);
                if (localPoint == null) continue;

                int sceneX = localPoint.getSceneX();
                int sceneY = localPoint.getSceneY();

                // Check bounds
                if (sceneX < 0 || sceneY < 0 || sceneX >= tiles[plane].length ||
                        tiles[plane][sceneX] == null || sceneY >= tiles[plane][sceneX].length) continue;

                Tile tile = tiles[plane][sceneX][sceneY];
                if (tile == null) continue;

                WallObject wallObject = tile.getWallObject();
                // If wall object is valid and ID matches
                if (wallObject != null && contains(objectIds, wallObject.getId())) {

                    // Define adjacent walkable points near the wall
                    WorldPoint[] adjacentPoints = new WorldPoint[] {
                            checkPoint.dx(1),
                            checkPoint.dx(-1),
                            checkPoint.dy(1),
                            checkPoint.dy(-1)
                    };

                    int bestPathLength = Integer.MAX_VALUE;
                    boolean foundPath = false;

                    // Use pathfinding to check each adjacent point
                    for (WorldPoint adj : adjacentPoints) {
                        List<WorldPoint> path = pathfinder.findPath(playerLocation, adj);
                        if (path != null && path.size() < bestPathLength) {
                            bestPathLength = path.size();
                            foundPath = true;
                        }
                    }

                    // If a path exists, and it's shorter than the current best, update closest
                    if (foundPath && bestPathLength < closestPathLength) {
                        closest = wallObject;
                        closestPathLength = bestPathLength;
                    }
                }
            }
        }
        return closest;
    }

    // 🔍 Finds the closest GameObject matching the given object IDs
    public static GameObject findClosestGameObject(Client client, int[] objectIds, int scanRange) {
        WorldPoint playerLocation = client.getLocalPlayer().getWorldLocation();
        Tile[][][] tiles = client.getScene().getTiles();
        int plane = client.getPlane();

        GameObject closest = null;
        int closestDistance = Integer.MAX_VALUE;

        // Scan all tiles around the player within the range
        for (int dx = -scanRange; dx <= scanRange; dx++) {
            for (int dy = -scanRange; dy <= scanRange; dy++) {
                WorldPoint checkPoint = playerLocation.dx(dx).dy(dy);
                LocalPoint localPoint = LocalPoint.fromWorld(client, checkPoint);
                if (localPoint == null) continue;

                int sceneX = localPoint.getSceneX();
                int sceneY = localPoint.getSceneY();

                // Out of bounds checks
                if (sceneX < 0 || sceneY < 0 || sceneX >= tiles[plane].length ||
                        tiles[plane][sceneX] == null || sceneY >= tiles[plane][sceneX].length) continue;

                Tile tile = tiles[plane][sceneX][sceneY];
                if (tile == null) continue;

                // Check each GameObject on the tile
                for (GameObject gameObject : tile.getGameObjects()) {
                    if (gameObject != null && contains(objectIds, gameObject.getId())) {
                        int distance = playerLocation.distanceTo(checkPoint);
                        // Update closest object
                        if (distance < closestDistance) {
                            closest = gameObject;
                            closestDistance = distance;
                        }
                    }
                }
            }
        }
        return closest;
    }

    // ✅ Utility method to check if array contains a value
    private static boolean contains(int[] array, int value) {
        for (int i : array) {
            if (i == value) {
                return true;
            }
        }
        return false;
    }
}
