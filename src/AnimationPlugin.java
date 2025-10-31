package net.runelite.client.plugins.aamotherlode;

import javax.inject.Inject;
import lombok.extern.slf4j.Slf4j;
import net.runelite.api.Client;
import net.runelite.api.Player;
import net.runelite.api.events.GameTick;
import net.runelite.client.eventbus.Subscribe;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;

import java.util.Set;

@Slf4j
@PluginDescriptor(
        name = "Animation ID",
        enabledByDefault = false
)
public class AnimationPlugin extends Plugin
{
    @Inject
    private Client client;

    private static final Set<Integer> WALKING_ANIMATION_IDS = Set.of(819);  // Example walking animation ID
    private static final Set<Integer> RUNNING_ANIMATION_IDS = Set.of(824);  // Example running animation ID
    private static final Set<Integer> STANDING_ANIMATION_IDS = Set.of(-1);  // No animation

    @Override
    protected void startUp() throws Exception
    {
        log.info("Animation ID started.");
    }

    @Override
    protected void shutDown() throws Exception
    {
        log.info("Animation ID stopped.");
    }

    @Subscribe
    public void onGameTick(GameTick event)
    {
        Player player = client.getLocalPlayer();
        if (player != null)
        {
            int animation = player.getAnimation();
            if (animation == -1)
            {
                log.info("Player is standing still. Animation ID: -1");
            }
            else if (WALKING_ANIMATION_IDS.contains(animation))
            {
                log.info("Player is walking. Animation ID: " + animation);
            }
            else if (RUNNING_ANIMATION_IDS.contains(animation))
            {
                log.info("Player is running. Animation ID: " + animation);
            }
            else
            {
                log.info("Other animation. Animation ID: " + animation);
            }
        }
    }
}
