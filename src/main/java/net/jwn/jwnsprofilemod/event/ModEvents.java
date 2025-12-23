package net.jwn.jwnsprofilemod.event;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.packet.CloseTradeToastS2CPacket;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.jwn.jwnsprofilemod.trade.TradeMenu;
import net.jwn.jwnsprofilemod.trade.TradeSession;
import net.jwn.jwnsprofilemod.trade.TradeSessionManager;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.tick.ServerTickEvent;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            ProfileData profileData = ProfileData.get(serverPlayer.level().getServer());
            profileData.createPlayerProfileIfAbsent(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            ProfileData profileData = ProfileData.get(serverPlayer.level().getServer());
            profileData.setPlayerLevel(serverPlayer, serverPlayer.experienceLevel);
            profileData.setPlayerlastLogoutAt(serverPlayer, System.currentTimeMillis());
        }
    }

    @SubscribeEvent
    public static void onServerTickEvent(ServerTickEvent.Post event) {
        List<TradeSession> sessions = new ArrayList<>(TradeSessionManager.SESSIONS.values());

        for (TradeSession session : sessions) {
            MinecraftServer server = event.getServer();

            ServerPlayer playerA = server.getPlayerList().getPlayer(session.playerA());
            ServerPlayer playerB = server.getPlayerList().getPlayer(session.playerB());

            // 둘 다 접속중
            if (playerA == null || playerB == null) {
                TradeSessionManager.sessionClose(session, server);
                ServerPlayer target = server.getPlayerList().getPlayer(playerA == null ? session.playerB() : session.playerA());
                if (target != null) target.displayClientMessage(Component.translatable("jwnsprofilemod.trade.canceled"), false);
                continue;
            }

            // 상태 처리
            if (!session.isPlayerAJoined() && playerA.containerMenu instanceof TradeMenu) {
                session.playerAIsJoined();
            }
            if (!session.isPlayerBJoined() && playerB.containerMenu instanceof TradeMenu) {
                session.playerBIsJoined();
            }

            // 닫음 처리
            if (session.isPlayerAJoined() && !(playerA.containerMenu instanceof TradeMenu)) {
                if (session.isPlayerBJoined())
                    playerA.displayClientMessage(Component.translatable("jwnsprofilemod.trade.canceled"), false);
                tradeCanceled(session, server, playerA);
            }
            if (session.isPlayerBJoined() && !(playerB.containerMenu instanceof TradeMenu)) {
                if (session.isPlayerAJoined())
                    playerB.displayClientMessage(Component.translatable("jwnsprofilemod.trade.canceled"), false);
                tradeCanceled(session, server, playerB);
            }
        }
    }
    private static void tradeCanceled(TradeSession session, MinecraftServer server, ServerPlayer player) {
        if (!TradeSessionManager.SESSIONS.containsKey(session.id())) return;
        TradeSessionManager.sessionClose(session, server);
        ServerPlayer target = server.getPlayerList().getPlayer(
                Objects.equals(player.getUUID(), session.playerA()) ? session.playerB() : session.playerA()
        );
        if (target != null) {
            target.displayClientMessage(Component.translatable("jwnsprofilemod.trade.canceled"), false);
            CloseTradeToastS2CPacket packet = new CloseTradeToastS2CPacket();
            PacketDistributor.sendToPlayer(target, packet);
        }
    }
}
