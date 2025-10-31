package net.runelite.client.plugins.aamotherlode;

import com.google.inject.Provides;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.*;
import net.runelite.api.coords.WorldPoint;
import net.runelite.api.events.GameTick;
import net.runelite.client.callback.ClientThread;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.plugins.aamotherlode.helpers.PathingHelper;

import javax.inject.Inject;
import java.util.Arrays;
import java.util.List;

@Slf4j
@PluginDescriptor(
        name = "Pathing Plugin WebSocket",
        enabledByDefault = false
)
public class PathingPlugin extends Plugin
{
    @Inject private Client client;
    @Inject private ClientThread clientThread;
    @Inject private PathingHelper pathingHelper;

    private final List<WorldPoint> targets = Arrays.asList(
            new WorldPoint(3749, 5659, 0),
            new WorldPoint(3749, 5667, 0)
    );

    @Provides
    PathingPluginConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(PathingPluginConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        log.info("Pathing plugin started");
        pathingHelper.init(client);
        pathingHelper.followPath(targets, () -> {
            GameObject targetObject = pathingHelper.findGameObject(26674);
            if (targetObject != null)
            {
                pathingHelper.moveToObject(targetObject, () -> {
                    log.info("Target object reached. Restarting sequence.");
                    pathingHelper.followPath(targets, this::onFinalComplete); // Restart loop
                });
            }
        });
    }

    private void onFinalComplete()
    {
        log.info("Completed full cycle.");
    }

    @Override
    protected void shutDown() throws Exception
    {
        log.info("Pathing plugin stopped");
        pathingHelper.shutdown();
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        pathingHelper.updatePathingTick();
    }
}
