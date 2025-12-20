package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TradeRequestHandleS2CPacket(String request, String name) implements CustomPacketPayload {
    public static final CustomPacketPayload.Type<TradeRequestHandleS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "requset_trade_handle_packet"));

    public static final StreamCodec<ByteBuf, TradeRequestHandleS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, TradeRequestHandleS2CPacket::request,
            ByteBufCodecs.STRING_UTF8, TradeRequestHandleS2CPacket::name,
            TradeRequestHandleS2CPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final TradeRequestHandleS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {

        });
    }
}
