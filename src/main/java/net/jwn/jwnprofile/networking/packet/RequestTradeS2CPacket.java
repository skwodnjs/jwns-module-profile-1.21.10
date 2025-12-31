package net.jwn.jwnprofile.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RequestTradeS2CPacket(UUID senderUUID, String senderName, String name) implements CustomPacketPayload{
    public static final CustomPacketPayload.Type<RequestTradeS2CPacket> TYPE =
            new CustomPacketPayload.Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "reqest_trade_s_to_c_packet"));

    public static final StreamCodec<ByteBuf, RequestTradeS2CPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, RequestTradeS2CPacket::senderUUID,
            ByteBufCodecs.STRING_UTF8, RequestTradeS2CPacket::senderName,
            ByteBufCodecs.STRING_UTF8, RequestTradeS2CPacket::name,
            RequestTradeS2CPacket::new
    );

    @Override
    public CustomPacketPayload.Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final RequestTradeS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {

        });
    }
}
