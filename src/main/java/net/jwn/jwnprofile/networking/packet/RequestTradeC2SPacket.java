package net.jwn.jwnprofile.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.jwn.jwnprofile.trade.TradeSession;
import net.jwn.jwnprofile.trade.TradeSessionManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.chat.Component;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.UUID;

public record RequestTradeC2SPacket(UUID recipient) implements CustomPacketPayload {
    public static final Type<RequestTradeC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "reqest_trade_c_to_s_packet"));

    public static final StreamCodec<ByteBuf, RequestTradeC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC,RequestTradeC2SPacket::recipient,
            RequestTradeC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final RequestTradeC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // SERVER
            if (context.player() instanceof ServerPlayer player) {
                ServerPlayer recipientPlayer = player.level().getServer().getPlayerList().getPlayer(data.recipient);
                if (recipientPlayer == null) {
                    player.displayClientMessage(Component.translatable("jwnprofile.profile.offline_message"), false);
                    return;
                }
                if (TradeSessionManager.tradingPlayer.containsKey(context.player().getUUID())) {
                    player.displayClientMessage(Component.translatable("jwnprofile.profile.cannot_while_trading"), false);
                    return;
                } else if (TradeSessionManager.tradingPlayer.containsKey(data.recipient)) {
                    player.displayClientMessage(Component.translatable("jwnprofile.profile.other_player_already_trading"), false);
                    return;
                }

                TradeSession session = TradeSessionManager.create(context.player().getUUID(), data.recipient);
                context.player().openMenu(session, buf -> {
                    buf.writeUUID(context.player().getUUID());
                    buf.writeUUID(data.recipient);
                });

                TradeSessionManager.tradingPlayer.put(context.player().getUUID(), session.id());
                TradeSessionManager.tradingPlayer.put(data.recipient, session.id());

                RequestTradeS2CPacket packet = new RequestTradeS2CPacket(context.player().getUUID(), context.player().getPlainTextName());
                PacketDistributor.sendToPlayer(recipientPlayer, packet);

                player.openMenu(session, buf -> {
                    buf.writeUUID(session.playerA());
                    buf.writeUUID(session.playerB());
                });
            }
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}