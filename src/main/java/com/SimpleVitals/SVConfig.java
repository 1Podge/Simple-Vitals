package com.SimpleVitals;

import net.runelite.client.config.Config;
import net.runelite.client.config.ConfigGroup;
import net.runelite.client.config.ConfigItem;
import net.runelite.client.config.Range;

@ConfigGroup("simplevitals")
public interface SVConfig extends Config
{
    // --- ALERT CONFIGURATION ---
    @ConfigItem(keyName = "alertMode", name = "Alerts", description = "Toggle alerts for individual lowered stats or low vitals", position = 0)
    default SVAlerts alertMode() { return SVAlerts.OFF; }

    @Range(min = 1, max = 99)
    @ConfigItem(keyName = "hpThreshold", name = "HP Low Threshold", description = "Trigger alert when HP falls below this", position = 1)
    default int hpThreshold() { return 20; }

    @Range(min = 1, max = 99)
    @ConfigItem(keyName = "prayerThreshold", name = "Prayer Low Threshold", description = "Trigger alert when Prayer falls below this", position = 2)
    default int prayerThreshold() { return 10; }

    // --- POSITION SELECTIONS ---
    @ConfigItem(keyName = "hpSide", name = "Hitpoints", description = "Choose display layout side or turn off", position = 3)
    default SVPosition hpSide() { return SVPosition.RIGHT; }

    @ConfigItem(keyName = "prayerSide", name = "Prayer", description = "Choose display layout side or turn off", position = 4)
    default SVPosition prayerSide() { return SVPosition.RIGHT; }

    @ConfigItem(keyName = "runSide", name = "Run Energy", description = "Choose display layout side or turn off", position = 5)
    default SVPosition runSide() { return SVPosition.RIGHT; }

    @ConfigItem(keyName = "specSide", name = "Special Attack", description = "Choose display layout side or turn off", position = 6)
    default SVPosition specSide() { return SVPosition.RIGHT; }

    @ConfigItem(keyName = "statsSide", name = "Combat Stats", description = "Choose display layout side or turn off", position = 7)
    default SVPosition statsSide() { return SVPosition.LEFT; }

    @ConfigItem(keyName = "activePrayersSide", name = "Active Prayers", description = "Choose display layout side or turn off", position = 8)
    default SVPosition activePrayersSide() { return SVPosition.RIGHT; }

    // --- POSITIONING TWEAKS ---
    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "leftOffset", name = "Left Column Distance", description = "Adjust horizontal distance for elements on the LEFT side", position = 9)
    default int leftOffset() { return 70; }

    @Range(min = 0, max = 300)
    @ConfigItem(keyName = "rightOffset", name = "Right Column Distance", description = "Adjust horizontal distance for elements on the RIGHT side", position = 10)
    default int rightOffset() { return 35; }

    @Range(min = -200, max = 200)
    @ConfigItem(keyName = "verticalOffset", name = "Vertical Offset", description = "Adjust vertical start baseline position up or down", position = 11)
    default int verticalOffset() { return 0; }

}