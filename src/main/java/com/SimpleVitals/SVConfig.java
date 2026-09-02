package com.SimpleVitals;

import net.runelite.client.config.*;

@ConfigGroup("simplevitals")
public interface SVConfig extends Config
{
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

    @ConfigSection(
            name = "ICON VISIBILITY",
            description = "Choose which specific icons to display next to the values",
            position = 3
    )
    String iconsSection = "iconsSection";

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
            keyName = "hideUnboostedStats",
            name = "Show altered stats only",
            description = "Only show combat stats when they are boosted or debuffed",
            position = 5,
            section = layoutSection
    )
    default boolean hideUnboostedStats() { return false; }

    @ConfigItem(
            keyName = "iconSize",
            name = "HUD Icon & Text Size",
            description = "Choose the size of the vitals, combat stats, and their text font",
            position = 6,
            section = layoutSection
    )
    default SVSize iconSize() { return SVSize.SMALL; }

    @ConfigItem(
            keyName = "activePrayersSide",
            name = "Active Prayers",
            description = "Choose display layout side or turn off",
            position = 7,
            section = layoutSection
    )
    default SVPrayerPosition activePrayersSide() { return SVPrayerPosition.TOP; }

    @ConfigItem(
            keyName = "activePrayersSize",
            name = "Active Prayers Size",
            description = "Choose the size of the active prayer icons overlay",
            position = 8,
            section = layoutSection
    )
    default SVSize activePrayersSize() { return SVSize.MEDIUM; }

    @Range(min = -500, max = 500)
    @ConfigItem(
            keyName = "leftOffset",
            name = "Adjust left column",
            description = "Adjust horizontal distance for elements on the LEFT side",
            position = 9,
            section = layoutSection
    )
    default int leftOffset() { return 25; }

    @Range(min = -500, max = 500)
    @ConfigItem(
            keyName = "rightOffset",
            name = "Adjust right column",
            description = "Adjust horizontal distance for elements on the RIGHT side",
            position = 10,
            section = layoutSection
    )
    default int rightOffset() { return 25; }

    @Range(min = -500, max = 500)
    @ConfigItem(
            keyName = "verticalOffset",
            name = "Adjust height",
            description = "Adjust vertical start baseline position up or down",
            position = 11,
            section = layoutSection
    )
    default int verticalOffset() { return 0; }

    @Range(min = -500, max = 500)
    @ConfigItem(
            keyName = "prayersVerticalOffset",
            name = "Adjust prayers height",
            description = "Adjust vertical start baseline position for TOP/BOTTOM active prayers",
            position = 12,
            section = layoutSection
    )
    default int prayersVerticalOffset() { return -25; }

    @ConfigItem(
            keyName = "showHpIcon",
            name = "Show Hitpoints Icon",
            description = "Toggle the icon for Hitpoints",
            position = 0,
            section = iconsSection
    )
    default boolean showHpIcon() { return true; }

    @ConfigItem(
            keyName = "showPrayerIcon",
            name = "Show Prayer Icon",
            description = "Toggle the icon for the Prayer counter",
            position = 1,
            section = iconsSection
    )
    default boolean showPrayerIcon() { return true; }

    @ConfigItem(
            keyName = "showRunIcon",
            name = "Show Run Icon",
            description = "Toggle the icon for Run Energy",
            position = 2,
            section = iconsSection
    )
    default boolean showRunIcon() { return true; }

    @ConfigItem(
            keyName = "showSpecIcon",
            name = "Show Special Attack Icon",
            description = "Toggle the icon for Special Attack",
            position = 3,
            section = iconsSection
    )
    default boolean showSpecIcon() { return true; }

    @ConfigItem(
            keyName = "showStatsIcons",
            name = "Show Combat Stats Icons",
            description = "Toggle the icons for combat boosts (Attack, Strength, etc.)",
            position = 4,
            section = iconsSection
    )
    default boolean showStatsIcons() { return true; }
}
