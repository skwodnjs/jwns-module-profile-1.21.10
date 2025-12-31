package net.jwn.jwnprofile.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record CloseTradeToastS2CPacket() implements CustomPacketPayload {
    public static final Type<CloseTradeToastS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "close_trade_toast_packet"));

    public static final StreamCodec<ByteBuf, CloseTradeToastS2CPacket> STREAM_CODEC = StreamCodec.unit(new CloseTradeToastS2CPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final CloseTradeToastS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {

        });
    }
}
