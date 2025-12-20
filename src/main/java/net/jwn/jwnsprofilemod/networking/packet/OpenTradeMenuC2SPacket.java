package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenTradeMenuC2SPacket() implements CustomPacketPayload {
    public static final Type<OpenTradeMenuC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "open_trade_menu_packet"));

    public static final StreamCodec<ByteBuf, OpenTradeMenuC2SPacket> STREAM_CODEC = StreamCodec.unit(new OpenTradeMenuC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final OpenTradeMenuC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            context.player().displayClientMessage(Component.literal("HI?!"), false);
        });
    }
}
