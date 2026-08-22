package com.SimpleVitals;

import com.google.inject.Provides;
import net.runelite.client.config.ConfigManager;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.plugins.Plugin;
import net.runelite.client.plugins.PluginDescriptor;
import net.runelite.client.ui.overlay.OverlayManager;

import javax.inject.Inject;

@PluginDescriptor(
        name = "Simple Vitals",
        description = "Keep track of your hiptpoints, prayers, buffs and debuffs!",
        tags = {"combat", "overlay", "stats", "vitals"}
)
public class SVPlugin extends Plugin
{
    @Inject
    private OverlayManager overlayManager;

    @Inject
    private SVOverlay overlay;

    @Inject
    private SpriteManager spriteManager;

    @Provides
    SVConfig provideConfig(ConfigManager configManager)
    {
        return configManager.getConfig(SVConfig.class);
    }

    @Override
    protected void startUp() throws Exception
    {
        overlayManager.add(overlay);
    }

    @Override
    protected void shutDown() throws Exception
    {
        overlayManager.remove(overlay);
    }

}