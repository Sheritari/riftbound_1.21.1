package com.riftbound.loot;

public record RareNameRoll(String prefixId, String suffixId) {
    public String store() {
        return prefixId + "/" + suffixId;
    }

    public static RareNameRoll parseStored(String stored) {
        int separator = stored.indexOf('/');
        if (separator <= 0 || separator >= stored.length() - 1) {
            throw new IllegalArgumentException("Invalid rare name: " + stored);
        }
        return new RareNameRoll(stored.substring(0, separator), stored.substring(separator + 1));
    }
}
