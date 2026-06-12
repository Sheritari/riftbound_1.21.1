package com.riftbound.network;

import com.riftbound.RiftboundMod;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;

public record SyncScreenPayload(MenuTabPayload.Tab tab) implements CustomPacketPayload {
    public static final Type<SyncScreenPayload> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(RiftboundMod.MOD_ID, "sync_screen"));

    public static final StreamCodec<RegistryFriendlyByteBuf, SyncScreenPayload> STREAM_CODEC = StreamCodec.of(
            (buf, payload) -> buf.writeEnum(payload.tab()),
            buf -> new SyncScreenPayload(buf.readEnum(MenuTabPayload.Tab.class))
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }
}
