package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.trade.TradeSession;
import net.jwn.jwnsprofilemod.trade.TradeSessionManager;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record TradeCanceledC2SPacket() implements CustomPacketPayload {
    public static final Type<TradeCanceledC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "trade_canceled_packet"));

    public static final StreamCodec<ByteBuf, TradeCanceledC2SPacket> STREAM_CODEC = StreamCodec.unit(new TradeCanceledC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final TradeCanceledC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // SERVER
            if (context.player() instanceof ServerPlayer serverPlayer) {
                TradeSession session = TradeSessionManager.get(serverPlayer);
                if (session != null) {
                    TradeSessionManager.sessionClose(session, serverPlayer.level().getServer());
                    ServerPlayer target = serverPlayer.level().getServer().getPlayerList().getPlayer(session.playerB());
                    if (target != null) {
                        CloseTradeToastS2CPacket packet = new CloseTradeToastS2CPacket(true);
                        PacketDistributor.sendToPlayer(target, packet);
                    }
                }
            }
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
