package com.SimpleVitals;

import net.runelite.api.*;
import net.runelite.api.Point;
import net.runelite.api.coords.LocalPoint;
import net.runelite.client.game.SpriteManager;
import net.runelite.client.ui.FontManager;
import net.runelite.client.ui.overlay.*;

import javax.inject.Inject;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.List;

public class SVOverlay extends Overlay
{
    private final Client client;
    private final SVConfig config;
    private final SpriteManager spriteManager;

    private static final int ICON_WIDTH = 14;
    private static final int ICON_HEIGHT = 14;

    private int lastCombatTick = 0;

    @Inject
    private SVOverlay(Client client, SVConfig config, SpriteManager spriteManager)
    {
        this.client = client;
        this.config = config;
        this.spriteManager = spriteManager;
        setLayer(OverlayLayer.UNDER_WIDGETS);
        setPosition(OverlayPosition.DYNAMIC);
        setPriority(OverlayPriority.HIGH);

    }

    @Override
    public Dimension render(Graphics2D graphics)
    {
        Player player = client.getLocalPlayer();
        if (player == null)
        {
            return null;
        }
// --- 1. EVALUATE BASIC COMBAT STATE & TIMEOUTS ---
        int currentTick = client.getTickCount();
        boolean isTargetingCombatant = false;

// Block standard NPC/player dialogue text boxes from artificially resetting the combat timeline
        boolean isTalking = client.getWidget(231, 0) != null || client.getWidget(229, 0) != null;

        if (player.getInteracting() != null && !isTalking)
        {
            if (player.getInteracting().getCombatLevel() > 0)
            {
                isTargetingCombatant = true;
            }
        }

        if (isTargetingCombatant || player.getHealthScale() > 0)
        {
            lastCombatTick = currentTick;
        }

// Determine if the main hiding system wants the HUD hidden right now
        int requiredTimeoutTicks = (int) (config.combatTimeoutSeconds() / 0.6);
        boolean pastHideDelay = (currentTick - lastCombatTick) > requiredTimeoutTicks;
        boolean hidden = config.hideOutOfCombat() && pastHideDelay;

        List<Integer> activeSprites = SVPrayerIcon.activeSpriteIds(client);
        boolean prayerOn = !activeSprites.isEmpty();

        boolean showActivePrayerRow = true;
        boolean showPrayerCounterDigit = true;
        boolean showEverythingElse = true;

        if (hidden)
        {
            SVIdleHold holdSetting = config.idleHold();

            if (holdSetting == SVIdleHold.NONE || !prayerOn)
            {
                return null;
            }
            else if (holdSetting == SVIdleHold.WHOLE_HUD)
            {

            }
            else if (holdSetting == SVIdleHold.ACTIVE_PRAYER)
            {
                showActivePrayerRow = true;
                showPrayerCounterDigit = false;
                showEverythingElse = false;
            }
            else if (holdSetting == SVIdleHold.PRAYER_COUNTER)
            {
                showActivePrayerRow = false;
                showPrayerCounterDigit = true;
                showEverythingElse = false;
            }
            else if (holdSetting == SVIdleHold.ACTIVE_AND_COUNTER)
            {
                showActivePrayerRow = true;
                showPrayerCounterDigit = true;
                showEverythingElse = false;
            }
        }

        LocalPoint playerLocalLocation = player.getLocalLocation();
        if (playerLocalLocation == null)
        {
            return null;
        }

// Static height anchor locks calculations right onto the character's torso
        int torsoZOffset = 150;
        Point characterCenter = Perspective.localToCanvas(client, playerLocalLocation, client.getPlane(), torsoZOffset);
        if (characterCenter == null)
        {
            return null;
        }

        graphics.setFont(FontManager.getRunescapeSmallFont());

// Horizontal separation lines leveraging the split configuration sliders
        int leftX = characterCenter.getX() - config.leftOffset();
        int rightX = characterCenter.getX() + config.rightOffset();

        int leftY = characterCenter.getY() + config.verticalOffset();
        int rightY = characterCenter.getY() + config.verticalOffset();
        int ySpacing = 16;

// Fetch primary statistics metrics data logs
        int currentHP = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);

        boolean hpAlertActive = currentHP < config.hpThreshold();
        boolean prayerAlertActive = currentPrayer < config.prayerThreshold();

        boolean flashToggle = (System.currentTimeMillis() / 500) % 2 == 0;
        boolean alertsOn = config.alertMode() == SVAlerts.ON;

// --- Render Vitals & Stats ---
        if (config.hpSide() != SVPosition.OFF && showEverythingElse)
        {
            boolean isRight = config.hpSide() == SVPosition.RIGHT;
            Color hpColor = Color.GREEN;

            if (hpAlertActive)
            {
                if (alertsOn)
                {
                    hpColor = flashToggle ? Color.RED : Color.GREEN;
                }
                else
                {
                    hpColor = Color.RED;
                }
            }

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.SKILL_HITPOINTS, String.valueOf(currentHP), hpColor, !isRight);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

// PRAYER COUNTER:
        if (config.prayerSide() != SVPosition.OFF && showPrayerCounterDigit)
        {
            boolean isRight = config.prayerSide() == SVPosition.RIGHT;
            Color prayerColor = Color.CYAN;

            if (prayerAlertActive)
            {
                if (alertsOn)
                {
                    prayerColor = flashToggle ? Color.RED : Color.CYAN;
                }
                else
                {
                    prayerColor = Color.RED;
                }
            }

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.SKILL_PRAYER, String.valueOf(currentPrayer), prayerColor, !isRight);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

        if (config.runSide() != SVPosition.OFF && showEverythingElse)
        {
            int runEnergy = client.getEnergy() / 100;
            boolean isRight = config.runSide() == SVPosition.RIGHT;

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.MINIMAP_ORB_RUN_ICON, String.valueOf(runEnergy), Color.YELLOW, !isRight);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

        if (config.specSide() != SVPosition.OFF && showEverythingElse)
        {
            int specEnergy = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
            boolean isRight = config.specSide() == SVPosition.RIGHT;

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.MINIMAP_ORB_SPECIAL_ICON, String.valueOf(specEnergy), Color.ORANGE, !isRight);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

        if (config.statsSide() != SVPosition.OFF && showEverythingElse)
        {
            boolean isRight = config.statsSide() == SVPosition.RIGHT;
            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            targetY = addStatTextAndIcon(graphics, targetX, targetY, Skill.ATTACK, SpriteID.SKILL_ATTACK, ySpacing, !isRight);
            targetY = addStatTextAndIcon(graphics, targetX, targetY, Skill.STRENGTH, SpriteID.SKILL_STRENGTH, ySpacing, !isRight);
            targetY = addStatTextAndIcon(graphics, targetX, targetY, Skill.DEFENCE, SpriteID.SKILL_DEFENCE, ySpacing, !isRight);
            targetY = addStatTextAndIcon(graphics, targetX, targetY, Skill.RANGED, SpriteID.SKILL_RANGED, ySpacing, !isRight);
            targetY = addStatTextAndIcon(graphics, targetX, targetY, Skill.MAGIC, SpriteID.SKILL_MAGIC, ySpacing, !isRight);

            if (isRight) rightY = targetY; else leftY = targetY;
        }

// ACTIVE PRAYERS ROW:
        if (config.activePrayersSide() != SVPosition.OFF && showActivePrayerRow)
        {
            boolean isRight = config.activePrayersSide() == SVPosition.RIGHT;
            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            if (prayerOn)
            {
                int currentXOffset = 0;
                int iconPadding = 2;

                for (int spriteId : activeSprites)
                {
                    BufferedImage rawPrayerSprite = spriteManager.getSprite(spriteId, 0);
                    if (rawPrayerSprite != null)
                    {
                        BufferedImage scaledPrayerIcon = scaleImage(rawPrayerSprite, ICON_WIDTH, ICON_HEIGHT);int adjustedY = targetY - (scaledPrayerIcon.getHeight() / 2) - 4;if (isRight){graphics.drawImage(scaledPrayerIcon, targetX + currentXOffset, adjustedY, null);}else{int leftAlignAnchor = targetX + ICON_WIDTH + 1;graphics.drawImage(scaledPrayerIcon, leftAlignAnchor - currentXOffset - ICON_WIDTH, adjustedY, null);}currentXOffset += ICON_WIDTH + iconPadding;}}if (isRight) rightY += ySpacing; else leftY += ySpacing;}}return null;}private int addStatTextAndIcon(Graphics2D graphics, int x, int y, Skill skill, int spriteId, int yOffset, boolean mirrorLayout){int boosted = client.getBoostedSkillLevel(skill);int real = client.getRealSkillLevel(skill);Color statColor = Color.WHITE;if (boosted > real){statColor = Color.GREEN;}else if (boosted < real){statColor = Color.RED;}renderIconAndText(graphics, x, y, spriteId, String.valueOf(boosted), statColor, mirrorLayout);return y + yOffset;}private void renderIconAndText(Graphics2D graphics, int x, int y, int spriteId, String text, Color color, boolean mirrorLayout){BufferedImage rawIcon = spriteManager.getSprite(spriteId, 0);if (rawIcon != null){BufferedImage scaledIcon = scaleImage(rawIcon, ICON_WIDTH, ICON_HEIGHT);int iconY = y - (scaledIcon.getHeight() / 2) - 4;if (mirrorLayout){OverlayUtil.renderTextLocation(graphics, new net.runelite.api.Point(x + scaledIcon.getWidth() + 4, y), text, color);graphics.drawImage(scaledIcon, x, iconY, null);}else{graphics.drawImage(scaledIcon, x, iconY, null);OverlayUtil.renderTextLocation(graphics, new net.runelite.api.Point(x + scaledIcon.getWidth() + 4, y), text, color);}}else{OverlayUtil.renderTextLocation(graphics, new net.runelite.api.Point(x, y), text, color);}}private BufferedImage scaleImage(BufferedImage img, int newWidth, int newHeight){Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);BufferedImage imgResized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);Graphics2D g2d = imgResized.createGraphics();g2d.drawImage(tmp, 0, 0, null);g2d.dispose();return imgResized;}}