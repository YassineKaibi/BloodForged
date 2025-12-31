package com.bloodforged.item;

import com.bloodforged.component.ModDataComponents;
import net.minecraft.component.type.TooltipDisplayComponent;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.tooltip.TooltipType;
import net.minecraft.text.Text;
import net.minecraft.util.Formatting;

import java.util.function.Consumer;

public class ToolPartItem extends Item {

    private final String partType; // "blade", "handle", "guard"

    public ToolPartItem(Settings settings, String partType) {
        super(settings);
        this.partType = partType;
    }

    @Override
    @SuppressWarnings("deprecation") // TODO: Update when new tooltip API is stable
    public void appendTooltip(ItemStack stack, TooltipContext context, TooltipDisplayComponent displayComponent, Consumer<Text> textConsumer, TooltipType type) {
        super.appendTooltip(stack, context, displayComponent, textConsumer, type);

        // Show material
        String material = stack.get(ModDataComponents.MATERIAL);
        if (material != null) {
            textConsumer.accept(Text.literal("Material: " + material).formatted(Formatting.GRAY));
        }

        // Show quality
        Integer quality = stack.get(ModDataComponents.QUALITY);
        if (quality != null) {
            Formatting color = getQualityColor(quality);
            textConsumer.accept(Text.literal("Quality: " + quality + "%").formatted(color));
        }
    }

    private Formatting getQualityColor(int quality) {
        if (quality >= 100) return Formatting.GOLD;
        if (quality >= 80) return Formatting.GREEN;
        if (quality >= 60) return Formatting.YELLOW;
        if (quality >= 40) return Formatting.WHITE;
        return Formatting.GRAY;
    }

    public String getPartType() {
        return partType;
    }
}