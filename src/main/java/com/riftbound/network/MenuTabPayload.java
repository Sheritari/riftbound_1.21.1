package com.riftbound.network;

import com.riftbound.RiftboundMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record MenuTabPayload(Tab tab) implements CustomPacketPayload {
    public enum Tab {
        INVENTORY,
        TRANSMUTATION
    }

    public static final Type<MenuTabPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RiftboundMod.MOD_ID, "menu_tab"));

    public static final StreamCodec<RegistryFriendlyByteBuf, MenuTabPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeEnum(payload.tab()),
            buf -> new MenuTabPayload(buf.readEnum(Tab.class))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
