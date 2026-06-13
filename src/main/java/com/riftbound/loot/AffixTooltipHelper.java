package com.riftbound.loot;

import net.minecraft.network.chat.Component;

public final class AffixTooltipHelper {
    private AffixTooltipHelper() {
    }

    public static Component describe(RolledAffix affix) {
        AffixDefinition definition = AffixPool.byId(affix.id());
        if (definition == null) {
            return Component.literal(affix.id());
        }

        return switch (definition.id()) {
            case "squires" -> Component.translatable(
                    definition.translationKey() + ".desc",
                    formatInt(affix.value(0)),
                    formatInt(affix.value(1))
            );
            case "heated" -> Component.translatable(
                    definition.translationKey() + ".desc",
                    formatInt(affix.value(0)),
                    formatInt(affix.value(1))
            );
            case "heavy", "serrated", "of_skill", "of_needling", "of_steadiness", "of_success" -> Component.translatable(
                    definition.translationKey() + ".desc",
                    formatInt(affix.value())
            );
            case "glinting" -> Component.translatable(
                    definition.translationKey() + ".desc",
                    formatInt(affix.value())
            );
            case "lacquered" -> Component.translatable(
                    definition.translationKey() + ".desc",
                    formatInt(affix.value())
            );
            case "of_brute" -> Component.translatable(
                    definition.translationKey() + ".desc",
                    formatInt(affix.value())
            );
            case "of_mongoose" -> Component.translatable(
                    definition.translationKey() + ".desc",
                    formatInt(affix.value())
            );
            default -> Component.translatable(definition.translationKey() + ".desc", formatInt(affix.value()));
        };
    }

    private static String formatInt(double value) {
        return Integer.toString((int) Math.round(value));
    }
}
