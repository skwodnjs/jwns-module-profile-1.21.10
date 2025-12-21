package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.trade.TradeSession;
import net.jwn.jwnsprofilemod.trade.TradeSessionManager;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;
import java.util.UUID;

public record TradeReadyC2SPacket(UUID playerID) implements CustomPacketPayload {
    public static final Type<TradeReadyC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "trade_ready_packet"));

    public static final StreamCodec<ByteBuf, TradeReadyC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, TradeReadyC2SPacket::playerID,
            TradeReadyC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final TradeReadyC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Player player = context.player().level().getServer().getPlayerList().getPlayer(data.playerID);
            if (player == null) return;
            TradeSession session = TradeSessionManager.get(player);
            if (session == null) return;
            if (Objects.equals(session.playerA(), data.playerID)) session.playerAisReady();
            else if (Objects.equals(session.playerB(), data.playerID)) session.playerBisReady();
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
