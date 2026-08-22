package com.SimpleVitals;

import net.runelite.client.config.*;

@ConfigGroup("simplevitals")
public interface SVConfig extends Config
{
// ==========================================
// 1. SECTION DECLARATIONS
// ==========================================

    @ConfigSection(
            name = "OUTSIDE OF COMBAT",
            description = "Configure when and how the HUD hides outside of combat encounters",
            position = 0
    )
    String hidingSection = "hidingSection";

    @ConfigSection(
            name = "VITAL THRESHOLDS",
            description = "Configure the thresholds Hitpoints and Prayer",
            position = 1
    )
    String alertsSection = "alertsSection";

    @ConfigSection(
            name = "LAYOUT & POSITIONING",
            description = "Configure the look of the overlay",
            position = 2
    )
    String layoutSection = "layoutSection";

// ==========================================
// 2. COMBAT & IDLE HIDING SECTION ITEMS
// ==========================================

    @ConfigItem(
            keyName = "hideOutOfCombat",
            name = "Hide outside of Combat",
            description = "Hide the HUD when not in combat",
            position = 0,
            section = hidingSection
    )
    default boolean hideOutOfCombat() { return false; }

    @Range(min = 1, max = 30)
    @ConfigItem(
            keyName = "combatTimeoutSeconds",
            name = "Hide after (Seconds)",
            description = "How long the HUD will remain visible after combat ends",
            position = 1,
            section = hidingSection
    )
    default int combatTimeoutSeconds() { return 5; }

    @ConfigItem(
            keyName = "idleHold",
            name = "Display prayer",
            description = "What is shown if HUD is hidden, but prayers remain on",
            position = 2,
            section = hidingSection
    )
    default SVIdleHold idleHold() { return SVIdleHold.ACTIVE_AND_COUNTER; }

// ==========================================
// 3. LOW VITAL ALERTS SECTION ITEMS
// ==========================================

    @ConfigItem(
            keyName = "alertMode",
            name = "Alerts",
            description = "Toggle alerts for HP & Prayer",
            position = 0,
            section = alertsSection
    )
    default SVAlerts alertMode() { return SVAlerts.ON; }

    @Range(min = 1, max = 99)
    @ConfigItem(
            keyName = "hpThreshold",
            name = "HP threshold",
            description = "Trigger alert when HP falls below this",
            position = 1,
            section = alertsSection
    )
    default int hpThreshold() { return 20; }

    @Range(min = 1, max = 99)
    @ConfigItem(
            keyName = "prayerThreshold",
            name = "Prayer threshold",
            description = "Trigger alert when Prayer falls below this",
            position = 2,
            section = alertsSection
    )
    default int prayerThreshold() { return 10; }

// ==========================================
// 4. LAYOUT & POSITIONING SECTION ITEMS
// ==========================================

    @ConfigItem(
            keyName = "hpSide",
            name = "Hitpoints",
            description = "Choose display layout side or turn off",
            position = 0,
            section = layoutSection
    )
    default SVPosition hpSide() { return SVPosition.RIGHT; }

    @ConfigItem(
            keyName = "prayerSide",
            name = "Prayer",
            description = "Choose display layout side or turn off",
            position = 1,
            section = layoutSection
    )
    default SVPosition prayerSide() { return SVPosition.RIGHT; }

    @ConfigItem(
            keyName = "runSide",
            name = "Run Energy",
            description = "Choose display layout side or turn off",
            position = 2,
            section = layoutSection
    )
    default SVPosition runSide() { return SVPosition.RIGHT; }

    @ConfigItem(
            keyName = "specSide",
            name = "Special Attack",
            description = "Choose display layout side or turn off",
            position = 3,
            section = layoutSection
    )
    default SVPosition specSide() { return SVPosition.RIGHT; }

    @ConfigItem(
            keyName = "statsSide",
            name = "Combat Stats",
            description = "Choose display layout side or turn off",
            position = 4,
            section = layoutSection
    )
    default SVPosition statsSide() { return SVPosition.LEFT; }

    @ConfigItem(
            keyName = "activePrayersSide",
            name = "Active Prayers",
            description = "Choose display layout side or turn off",
            position = 5,
            section = layoutSection
    )
    default SVPosition activePrayersSide() { return SVPosition.RIGHT; }

    @Range(min = -500, max = 500)
    @ConfigItem(
            keyName = "leftOffset",
            name = "Adjust left column",
            description = "Adjust horizontal distance for elements on the LEFT side",
            position = 6,
            section = layoutSection
    )
    default int leftOffset() { return 70; }

    @Range(min = -500, max = 500)
    @ConfigItem(
            keyName = "rightOffset",
            name = "Adjust right column",
            description = "Adjust horizontal distance for elements on the RIGHT side",
            position = 7,
            section = layoutSection
    )
    default int rightOffset() { return 35; }

    @Range(min = -500, max = 500)
    @ConfigItem(
            keyName = "verticalOffset",
            name = "Adjust height",
            description = "Adjust vertical start baseline position up or down",
            position = 8,
            section = layoutSection
    )
    default int verticalOffset() { return 0; }

}