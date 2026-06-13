package com.riftbound.loot;

import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.util.RandomSource;

import java.util.List;

/**
 * POE-style rare item names: cosmetic prefix and suffix around the base item name.
 * @see <a href="https://www.poewiki.net/wiki/Rare_Item_Name_Index">Rare Item Name Index</a>
 */
public final class RareNameGenerator {
    private static final List<String> RARE_PREFIX_IDS = List.of(
            "agony", "apocalypse", "armageddon", "beast", "behemoth", "blight", "blood", "bramble", "brimstone", "brood",
            "carrion", "cataclysm", "chimeric", "corpse", "corruption", "damnation", "death", "demon", "dire", "dragon",
            "dread", "doom", "dusk", "eagle", "empyrean", "fate", "foe", "gale", "ghoul", "gloom", "glyph", "golem", "grim",
            "hate", "havoc", "honour", "horror", "hypnotic", "kraken", "loath", "maelstrom", "mind", "miracle", "morbid",
            "oblivion", "onslaught", "pain", "pandemonium", "phoenix", "plague", "rage", "rapture", "rune", "skull", "sol",
            "soul", "sorrow", "spirit", "storm", "tempest", "torment", "vengeance", "victory", "viper", "vortex", "woe", "wrath"
    );

    /** All Swords suffix pool (22) from the PoE Wiki weapons table. */
    private static final List<String> SWORD_SUFFIX_IDS = List.of(
            "bane", "barb", "beak", "bite", "edge", "fang", "gutter", "hunger", "impaler", "needle", "razor", "saw",
            "scalpel", "scratch", "sever", "skewer", "slicer", "song", "spike", "spiker", "stinger", "thirst"
    );

    private RareNameGenerator() {
    }

    public static RareNameRoll rollSwordName(RandomSource random) {
        String prefixId = RARE_PREFIX_IDS.get(random.nextInt(RARE_PREFIX_IDS.size()));
        String suffixId = SWORD_SUFFIX_IDS.get(random.nextInt(SWORD_SUFFIX_IDS.size()));
        return new RareNameRoll(prefixId, suffixId);
    }

    public static MutableComponent toComponent(RareNameRoll rareName) {
        return Component.translatable(
                "rare_name.riftbound.format.shard_blade",
                Component.translatable(prefixKey(rareName.prefixId())),
                Component.translatable("item.riftbound.shard_blade"),
                Component.translatable(suffixKey(rareName.suffixId()))
        );
    }

    public static String prefixKey(String prefixId) {
        return "rare_name.riftbound.prefix." + prefixId;
    }

    public static String suffixKey(String suffixId) {
        return "rare_name.riftbound.suffix.sword." + suffixId;
    }
}
