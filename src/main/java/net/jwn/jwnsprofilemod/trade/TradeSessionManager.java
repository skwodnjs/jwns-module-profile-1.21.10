package net.jwn.jwnsprofilemod.trade;

import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

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
        if (playerA != null) {
            tradingPlayer.remove(playerA.getUUID());
            giveOfferToPlayer(playerA, session.offerA());
        }
        if (playerA != null && playerA.containerMenu instanceof TradeMenu) {
            playerA.closeContainer();
            playerA.displayClientMessage(Component.translatable("jwnsprofilemod.profile.trade_reject"), false);
        }

        ServerPlayer playerB = server.getPlayerList().getPlayer(session.playerB());
        if (playerB != null) {
            tradingPlayer.remove(playerB.getUUID());
            giveOfferToPlayer(playerB, session.offerB());
        }
        if (playerB != null && playerB.containerMenu instanceof TradeMenu) {
            System.out.println(playerB.getPlainTextName());
            playerB.closeContainer();
            playerB.displayClientMessage(Component.translatable("jwnsprofilemod.profile.trade_reject"), false);
        }

        SESSIONS.remove(session.id());
    }

    public static void sessionSuccessed(TradeSession session, MinecraftServer server) {
        ServerPlayer playerA = server.getPlayerList().getPlayer(session.playerA());
        if (playerA != null) {
            tradingPlayer.remove(playerA.getUUID());
            giveOfferToPlayer(playerA, session.offerB());
        }
        if (playerA != null && playerA.containerMenu instanceof TradeMenu) {
            playerA.closeContainer();
            playerA.displayClientMessage(Component.translatable("jwnsprofilemod.profile.trade_success"), false);
        }

        ServerPlayer playerB = server.getPlayerList().getPlayer(session.playerB());
        if (playerB != null) {
            tradingPlayer.remove(playerB.getUUID());
            giveOfferToPlayer(playerB, session.offerA());
        }
        if (playerB != null && playerB.containerMenu instanceof TradeMenu) {
            System.out.println(playerB.getPlainTextName());
            playerB.closeContainer();
            playerB.displayClientMessage(Component.translatable("jwnsprofilemod.profile.trade_success"), false);
        }

        SESSIONS.remove(session.id());
    }

    private static void giveOfferToPlayer(ServerPlayer player, Container offer) {
        for (int i = 0; i < offer.getContainerSize(); i++) {
            ItemStack stack = offer.getItem(i);
            if (!stack.isEmpty()) {
                ItemStack copy = stack.copy();
                boolean inserted = player.getInventory().add(copy);
                if (!inserted) {
                    player.drop(copy, false);
                }
                offer.setItem(i, ItemStack.EMPTY);
            }
        }
        offer.setChanged();
    }
}
