package com.riftbound.item;

import com.riftbound.loot.ItemLootCategory;

/**
 * Describes how an item participates in affix rolling and world loot.
 */
public interface ItemLootProfile extends ItemBaseLevelProvider {
    ItemLootCategory getLootCategory();

    default boolean canReceiveAffixes() {
        return true;
    }
}
