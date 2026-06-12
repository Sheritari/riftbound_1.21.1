package com.riftbound.loot;

import java.util.Arrays;

public record RolledAffix(String id, double[] values) {
    public RolledAffix(String id, double value) {
        this(id, new double[]{value});
    }

    public double value() {
        return values.length > 0 ? values[0] : 0.0D;
    }

    public double value(int index) {
        return index >= 0 && index < values.length ? values[index] : 0.0D;
    }

    public static RolledAffix ofInts(String id, int... ints) {
        return new RolledAffix(id, Arrays.stream(ints).asDoubleStream().toArray());
    }
}
