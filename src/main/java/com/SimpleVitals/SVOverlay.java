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

        int currentTick = client.getTickCount();
        boolean isTargetingCombatant = false;

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
                // Show everything
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

        int torsoZOffset = 150;
        Point characterCenter = Perspective.localToCanvas(client, playerLocalLocation, client.getPlane(), torsoZOffset);
        if (characterCenter == null)
        {
            return null;
        }

        // Handle text scaling dynamically based on icon size selection
        SVSize hudSizeConfig = config.iconSize();
        int hudIconSize = hudSizeConfig.getSize();

        Font baseFont = FontManager.getRunescapeSmallFont();
        if (hudSizeConfig == SVSize.MEDIUM)
        {
            graphics.setFont(baseFont.deriveFont(baseFont.getSize() * 1.7f));
        }
        else if (hudSizeConfig == SVSize.LARGE)
        {
            graphics.setFont(baseFont.deriveFont(baseFont.getSize() * 2.2f));
        }
        else
        {
            graphics.setFont(baseFont);
        }

        int leftX = characterCenter.getX() - config.leftOffset();
        int rightX = characterCenter.getX() + config.rightOffset();

        int leftY = characterCenter.getY() + config.verticalOffset();
        int rightY = characterCenter.getY() + config.verticalOffset();

        // Add vertical buffer dynamically based on selected icon height to prevent overlap
        int ySpacing = hudIconSize + (hudSizeConfig == SVSize.LARGE ? 6 : 3);

        int currentHP = client.getBoostedSkillLevel(Skill.HITPOINTS);
        int currentPrayer = client.getBoostedSkillLevel(Skill.PRAYER);

        boolean hpAlertActive = currentHP < config.hpThreshold();
        boolean prayerAlertActive = currentPrayer < config.prayerThreshold();

        boolean flashToggle = (System.currentTimeMillis() / 500) % 2 == 0;
        boolean alertsOn = config.alertMode() == SVAlerts.ON;

        if (config.hpSide() != SVPosition.OFF && showEverythingElse)
        {
            boolean isRight = config.hpSide() == SVPosition.RIGHT;
            Color hpColor = Color.GREEN;

            if (hpAlertActive)
            {
                hpColor = (alertsOn && flashToggle) ? Color.GREEN : Color.RED;
            }

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.SKILL_HITPOINTS, String.valueOf(currentHP), hpColor, !isRight, config.showHpIcon(), hudIconSize);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

        if (config.prayerSide() != SVPosition.OFF && showPrayerCounterDigit)
        {
            boolean isRight = config.prayerSide() == SVPosition.RIGHT;
            Color prayerColor = Color.CYAN;

            if (prayerAlertActive)
            {
                prayerColor = (alertsOn && flashToggle) ? Color.CYAN : Color.RED;
            }

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.SKILL_PRAYER, String.valueOf(currentPrayer), prayerColor, !isRight, config.showPrayerIcon(), hudIconSize);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

        if (config.runSide() != SVPosition.OFF && showEverythingElse)
        {
            int runEnergy = client.getEnergy() / 100;
            boolean isRight = config.runSide() == SVPosition.RIGHT;

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.MINIMAP_ORB_RUN_ICON, String.valueOf(runEnergy), Color.YELLOW, !isRight, config.showRunIcon(), hudIconSize);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

        if (config.specSide() != SVPosition.OFF && showEverythingElse)
        {
            int specEnergy = client.getVarpValue(VarPlayer.SPECIAL_ATTACK_PERCENT) / 10;
            boolean isRight = config.specSide() == SVPosition.RIGHT;

            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            renderIconAndText(graphics, targetX, targetY, SpriteID.MINIMAP_ORB_SPECIAL_ICON, String.valueOf(specEnergy), Color.ORANGE, !isRight, config.showSpecIcon(), hudIconSize);
            if (isRight) rightY += ySpacing; else leftY += ySpacing;
        }

        if (config.statsSide() != SVPosition.OFF && showEverythingElse)
        {
            boolean isRight = config.statsSide() == SVPosition.RIGHT;
            int targetX = isRight ? rightX : leftX;
            int targetY = isRight ? rightY : leftY;

            Skill[] combatSkills = {Skill.ATTACK, Skill.STRENGTH, Skill.DEFENCE, Skill.RANGED, Skill.MAGIC};

            for (Skill skill : combatSkills)
            {
                int boosted = client.getBoostedSkillLevel(skill);
                int real = client.getRealSkillLevel(skill);

                if (config.hideUnboostedStats() && boosted == real)
                {
                    continue;
                }

                targetY = addStatTextAndIcon(graphics, targetX, targetY, skill, getSkillSpriteId(skill), ySpacing, !isRight, hudIconSize);
            }

            if (isRight) rightY = targetY; else leftY = targetY;
        }

        if (config.activePrayersSide() != SVPrayerPosition.OFF && showActivePrayerRow)
        {
            SVPrayerPosition placement = config.activePrayersSide();
            if (prayerOn)
            {
                int pSize = config.activePrayersSize().getSize();
                int iconPadding = 2;
                int totalIconsWidth = (activeSprites.size() * pSize) + ((activeSprites.size() - 1) * iconPadding);

                int targetX = 0;
                int targetY = 0;

                if (placement == SVPrayerPosition.TOP)
                {
                    targetX = characterCenter.getX() - (totalIconsWidth / 2);
                    targetY = characterCenter.getY() + config.verticalOffset() - ySpacing + config.prayersVerticalOffset();
                }
                else if (placement == SVPrayerPosition.BOTTOM)
                {
                    targetX = characterCenter.getX() - (totalIconsWidth / 2);
                    targetY = characterCenter.getY() + config.verticalOffset() + ySpacing + config.prayersVerticalOffset();
                }
                else if (placement == SVPrayerPosition.RIGHT)
                {
                    targetX = rightX;
                    targetY = rightY;
                }
                else if (placement == SVPrayerPosition.LEFT)
                {
                    targetX = leftX;
                    targetY = leftY;
                }

                int currentXOffset = 0;
                for (int spriteId : activeSprites)
                {
                    BufferedImage rawPrayerSprite = spriteManager.getSprite(spriteId, 0);
                    if (rawPrayerSprite != null)
                    {
                        BufferedImage scaledPrayerIcon = scaleImage(rawPrayerSprite, pSize, pSize);
                        int adjustedY = targetY - (scaledPrayerIcon.getHeight() / 2) - 4;

                        if (placement == SVPrayerPosition.LEFT)
                        {
                            int leftAlignAnchor = targetX + pSize + 1;
                            graphics.drawImage(scaledPrayerIcon, leftAlignAnchor - currentXOffset - pSize, adjustedY, null);
                        }
                        else
                        {
                            graphics.drawImage(scaledPrayerIcon, targetX + currentXOffset, adjustedY, null);
                        }
                        currentXOffset += pSize + iconPadding;
                    }
                }

                if (placement == SVPrayerPosition.RIGHT) rightY += pSize + 2;
                else if (placement == SVPrayerPosition.LEFT) leftY += pSize + 2;
            }
        }

        return null;
    }

    private int getSkillSpriteId(Skill skill)
    {
        switch (skill)
        {
            case ATTACK: return SpriteID.SKILL_ATTACK;
            case STRENGTH: return SpriteID.SKILL_STRENGTH;
            case DEFENCE: return SpriteID.SKILL_DEFENCE;
            case RANGED: return SpriteID.SKILL_RANGED;
            case MAGIC: return SpriteID.SKILL_MAGIC;
            default: return SpriteID.SKILL_HITPOINTS;
        }
    }

    private int addStatTextAndIcon(Graphics2D graphics, int x, int y, Skill skill, int spriteId, int yOffset, boolean mirrorLayout, int size)
    {
        int boosted = client.getBoostedSkillLevel(skill);
        int real = client.getRealSkillLevel(skill);
        Color statColor = Color.WHITE;
        if (boosted > real)
        {
            statColor = Color.GREEN;
        }
        else if (boosted < real)
        {
            statColor = Color.RED;
        }

        renderIconAndText(graphics, x, y, spriteId, String.valueOf(boosted), statColor, mirrorLayout, config.showStatsIcons(), size);
        return y + yOffset;
    }

    private void renderIconAndText(Graphics2D graphics, int x, int y, int spriteId, String text, Color color, boolean mirrorLayout, boolean showIcon, int size)
    {
        FontMetrics metrics = graphics.getFontMetrics();

        // Dynamically compute width alignment buffers based on the scaling text width
        int fixedTextWidth = metrics.stringWidth("000");
        int padding = 4;

        int totalRowWidth = fixedTextWidth;
        if (showIcon)
        {
            totalRowWidth += padding + size;
        }

        int baseRowX = mirrorLayout ? (x - totalRowWidth) : x;

        // Shift text drawing baseline alignment to accurately center text against scaled icons
        int textYOffset = y + (metrics.getAscent() / 2) - 4;

        if (!showIcon)
        {
            int textX = mirrorLayout ? (baseRowX + fixedTextWidth - metrics.stringWidth(text)) : baseRowX;
            OverlayUtil.renderTextLocation(graphics, new net.runelite.api.Point(textX, textYOffset), text, color);
            return;
        }

        BufferedImage rawIcon = spriteManager.getSprite(spriteId, 0);
        if (rawIcon != null)
        {
            BufferedImage scaledIcon = scaleImage(rawIcon, size, size);
            int iconY = y - (scaledIcon.getHeight() / 2) - 4;

            int iconX;
            int textX;

            if (mirrorLayout)
            {
                iconX = baseRowX;
                textX = baseRowX + size + padding + (fixedTextWidth - metrics.stringWidth(text));
            }
            else
            {
                textX = baseRowX;
                iconX = baseRowX + fixedTextWidth + padding;
            }

            graphics.drawImage(scaledIcon, iconX, iconY, null);
            OverlayUtil.renderTextLocation(graphics, new net.runelite.api.Point(textX, textYOffset), text, color);
        }
        else
        {
            int textX = mirrorLayout ? (baseRowX + fixedTextWidth - metrics.stringWidth(text)) : baseRowX;
            OverlayUtil.renderTextLocation(graphics, new net.runelite.api.Point(textX, textYOffset), text, color);
        }
    }

    private BufferedImage scaleImage(BufferedImage img, int newWidth, int newHeight)
    {
        Image tmp = img.getScaledInstance(newWidth, newHeight, Image.SCALE_SMOOTH);
        BufferedImage imgResized = new BufferedImage(newWidth, newHeight, BufferedImage.TYPE_INT_ARGB);
        Graphics2D g2d = imgResized.createGraphics();
        g2d.drawImage(tmp, 0, 0, null);
        g2d.dispose();
        return imgResized;
    }
}
