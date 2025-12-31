package com.bloodforged.screen;

import com.bloodforged.BloodForged;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.ingame.HandledScreen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

/**
 * AnvilScreen - Client-side rendering for smithing anvil GUI.
 *
 * RESPONSIBILITIES:
 * - Draw background texture
 * - Render progress bars (temperature, progress, quality)
 * - Display mini-game visuals (targets, effects)
 * - Handle mouse input for mini-game
 * - Show tooltips
 *
 * RENDERING LAYERS (back to front):
 * 1. Background texture (anvil GUI image)
 * 2. Slot overlays
 * 3. Progress bars
 * 4. Mini-game elements (targets)
 * 5. Item stacks
 * 6. Tooltips
 *
 * COORDINATES:
 * - GUI is 176x166 pixels (standard Minecraft GUI size)
 * - Centered on screen
 * - Origin (0,0) is top-left of GUI
 */
public class AnvilScreen extends HandledScreen<AnvilScreenHandler> {

    /**
     * Background texture location.
     * Points to: assets/bloodforged/textures/gui/anvil.png
     */
    private static final Identifier TEXTURE =
            Identifier.of(BloodForged.MOD_ID, "textures/gui/anvil.png");

    // === CONSTRUCTOR ===

    public AnvilScreen(AnvilScreenHandler handler, PlayerInventory inventory, Text title) {
        super(handler, inventory, title);

        // Set GUI dimensions (must match texture size)
        this.backgroundWidth = 176;
        this.backgroundHeight = 166;
    }

    // === RENDERING ===

    /**
     * Initialize GUI.
     * Called when GUI opens.
     */
    @Override
    protected void init() {
        super.init();

        // Center title
        this.titleX = (this.backgroundWidth - this.textRenderer.getWidth(this.title)) / 2;
    }

    /**
     * Main render method.
     * Called every frame.
     *
     * @param context Drawing context
     * @param mouseX Mouse X position
     * @param mouseY Mouse Y position
     * @param delta Frame delta time
     */
    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        // Render background (darkens screen behind GUI)
        this.renderBackground(context, mouseX, mouseY, delta);

        // Render GUI itself
        super.render(context, mouseX, mouseY, delta);

        // Render tooltips (must be last to draw on top)
        this.drawMouseoverTooltip(context, mouseX, mouseY);
    }

    /**
     * Draw the GUI background.
     *
     * This draws:
     * - Main GUI texture
     * - Progress bars
     * - Temperature indicator
     * - Quality display
     */
    @Override
    protected void drawBackground(DrawContext context, float delta, int mouseX, int mouseY) {
        // Calculate GUI position (centered on screen)
        int x = (width - backgroundWidth) / 2;
        int y = (height - backgroundHeight) / 2;

        // Draw main background texture
        context.drawTexture(RenderLayer::getGuiTextured, TEXTURE, x, y, 0, 0, backgroundWidth, backgroundHeight, 256, 256);

        // Get data from handler
        int temperature = handler.getTemperature();
        int progress = handler.getProgress();
        int quality = handler.getQuality();
        boolean isSmithing = handler.isSmithing();

        // === DRAW PROGRESS BARS ===

        // Temperature bar (red/orange gradient)
        if (temperature > 0) {
            drawTemperatureBar(context, x, y, temperature);
        }

        // Progress bar (green)
        if (progress > 0) {
            drawProgressBar(context, x, y, progress);
        }

        // Quality bar (color based on quality)
        drawQualityBar(context, x, y, quality);

        // Mini-game area highlight when active
        if (isSmithing) {
            drawSmithingArea(context, x, y);
        }
    }

    /**
     * Draw temperature bar.
     * Red → Orange → Yellow → White as temperature increases.
     */
    private void drawTemperatureBar(DrawContext context, int guiX, int guiY, int temperature) {
        // Bar position (bottom left of GUI)
        int barX = guiX + 10;
        int barY = guiY + 60;
        int barWidth = 70;
        int barHeight = 5;

        // Calculate fill percentage (0-1400°C range)
        float fillPercent = Math.min(temperature / 1400.0f, 1.0f);
        int fillWidth = (int) (barWidth * fillPercent);

        // Choose color based on temperature
        int color;
        if (temperature < 800) {
            color = 0xFF666666; // Dark gray (too cold)
        } else if (temperature < 1000) {
            color = 0xFFFF6600; // Orange
        } else if (temperature < 1200) {
            color = 0xFFFFAA00; // Yellow-orange
        } else {
            color = 0xFFFFFF00; // Yellow-white (ideal)
        }

        // Draw background (black)
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);

        // Draw fill
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, color);

        // Draw border
        context.drawBorder(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 0xFFFFFFFF);

        // Draw text
        String tempText = temperature + "°C";
        context.drawText(textRenderer, tempText, barX + barWidth + 5, barY - 2, 0xFFFFFFFF, false);
    }

    /**
     * Draw progress bar.
     * Shows how close smithing is to completion.
     */
    private void drawProgressBar(DrawContext context, int guiX, int guiY, int progress) {
        int barX = guiX + 10;
        int barY = guiY + 68;
        int barWidth = 70;
        int barHeight = 5;

        // Calculate fill
        float fillPercent = progress / 100.0f;
        int fillWidth = (int) (barWidth * fillPercent);

        // Green color
        int color = 0xFF00FF00;

        // Draw
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, color);
        context.drawBorder(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 0xFFFFFFFF);

        // Text
        String progressText = progress + "%";
        context.drawText(textRenderer, progressText, barX + barWidth + 5, barY - 2, 0xFFFFFFFF, false);
    }

    /**
     * Draw quality bar.
     * Color changes based on quality level.
     */
    private void drawQualityBar(DrawContext context, int guiX, int guiY, int quality) {
        int barX = guiX + 10;
        int barY = guiY + 76;
        int barWidth = 70;
        int barHeight = 5;

        // Calculate fill (0-120 range)
        float fillPercent = quality / 120.0f;
        int fillWidth = (int) (barWidth * fillPercent);

        // Color based on quality
        int color;
        if (quality < 40) {
            color = 0xFFAA0000; // Red (poor)
        } else if (quality < 60) {
            color = 0xFFAAAA00; // Yellow (mediocre)
        } else if (quality < 80) {
            color = 0xFF00AA00; // Green (good)
        } else if (quality < 100) {
            color = 0xFF00AAFF; // Cyan (great)
        } else {
            color = 0xFFFFAA00; // Gold (masterwork!)
        }

        // Draw
        context.fill(barX, barY, barX + barWidth, barY + barHeight, 0xFF000000);
        context.fill(barX, barY, barX + fillWidth, barY + barHeight, color);
        context.drawBorder(barX - 1, barY - 1, barWidth + 2, barHeight + 2, 0xFFFFFFFF);

        // Text
        String qualityText = quality + "%";
        context.drawText(textRenderer, qualityText, barX + barWidth + 5, barY - 2, color, false);
    }

    /**
     * Draw the smithing area highlight when mini-game is active.
     * Shows where player should click.
     */
    private void drawSmithingArea(DrawContext context, int guiX, int guiY) {
        // Anvil work surface area
        int areaX = guiX + 40;
        int areaY = guiY + 25;
        int areaWidth = 96;
        int areaHeight = 40;

        // Draw semi-transparent overlay
        context.fill(areaX, areaY, areaX + areaWidth, areaY + areaHeight, 0x44FFFFFF);

        // Draw border
        context.drawBorder(areaX, areaY, areaWidth, areaHeight, 0xFFFFFFFF);

        // TODO: Draw target circle for mini-game (Phase 3 advanced)
        // This would show a moving target the player must click
    }

    /**
     * Handle mouse clicks.
     * Used for mini-game interaction.
     */
    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        // Check if clicking in smithing area
        if (handler.isSmithing()) {
            int guiX = (width - backgroundWidth) / 2;
            int guiY = (height - backgroundHeight) / 2;

            int areaX = guiX + 40;
            int areaY = guiY + 25;
            int areaWidth = 96;
            int areaHeight = 40;

            // If clicked in area
            if (mouseX >= areaX && mouseX <= areaX + areaWidth &&
                    mouseY >= areaY && mouseY <= areaY + areaHeight) {

                // TODO: Send packet to server with click position
                // Server validates and updates quality/progress

                // For now, just log
                BloodForged.LOGGER.info("Clicked smithing area at: " + mouseX + ", " + mouseY);

                return true;
            }
        }

        return super.mouseClicked(mouseX, mouseY, button);
    }
}