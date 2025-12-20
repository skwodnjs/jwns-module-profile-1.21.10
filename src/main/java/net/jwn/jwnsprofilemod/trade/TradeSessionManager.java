package net.jwn.jwnsprofilemod.trade;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class TradeSessionManager {
    private TradeSessionManager() {}

    public static final Map<UUID, UUID> tradingPlayer = new ConcurrentHashMap<>();

    private static final Map<UUID, TradeSession> SESSIONS = new ConcurrentHashMap<>();

    public static TradeSession create(UUID a, UUID b) {
        TradeSession s = new TradeSession(a, b);
        SESSIONS.put(s.id(), s);
        return s;
    }

    public static TradeSession get(UUID id) {
        return SESSIONS.get(id);
    }

    public static TradeSession get(Player player) {
        if (tradingPlayer.get(player.getUUID()) == null) return null;
        return SESSIONS.get(tradingPlayer.get(player.getUUID()));
    }

    public static void sessionClose(TradeSession session, MinecraftServer server) {
        ServerPlayer playerA = server.getPlayerList().getPlayer(session.playerA());
        if (playerA != null) tradingPlayer.remove(playerA.getUUID());
        if (playerA != null && playerA.containerMenu instanceof TradeMenu) {
            playerA.closeContainer();
            playerA.displayClientMessage(Component.translatable("jwnsprofilemod.profile.trade_reject"), false);
        }

        ServerPlayer playerB = server.getPlayerList().getPlayer(session.playerB());
        if (playerB != null) tradingPlayer.remove(playerB.getUUID());
        if (playerB != null && playerB.containerMenu instanceof TradeMenu) {
            System.out.println(playerB.getPlainTextName());
            playerB.closeContainer();
            playerB.displayClientMessage(Component.translatable("jwnsprofilemod.profile.trade_reject"), false);
        }

        SESSIONS.remove(session.id());
    }
}
