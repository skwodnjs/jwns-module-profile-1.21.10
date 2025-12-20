package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.trade.TradeSession;
import net.jwn.jwnsprofilemod.trade.TradeSessionManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record CreateTradeMenuC2SPacket(UUID playerB) implements CustomPacketPayload {
    public static final Type<CreateTradeMenuC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "create_trade_menu_packet"));

    public static final StreamCodec<ByteBuf, CreateTradeMenuC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, CreateTradeMenuC2SPacket::playerB,
            CreateTradeMenuC2SPacket::new);

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final CreateTradeMenuC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                if (TradeSessionManager.tradingPlayerUUID.contains(data.playerB())) {
                    player.displayClientMessage(
                            Component.translatable("jwnsprofilemod.profile.already_trading"), false);
                } else {
                    TradeSessionManager.tradingPlayerUUID.add(context.player().getUUID());
                    TradeSession session = TradeSessionManager.create(player.level(), player.getUUID(), data.playerB());
                    player.openMenu(session, buf -> {
                        buf.writeUUID(session.playerA());
                        buf.writeUUID(session.playerB());
                    });
                }
            }
        });
    }
}
