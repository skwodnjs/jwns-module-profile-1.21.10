package net.jwn.jwnsprofilemod.event;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.jwn.jwnsprofilemod.trade.TradeRequestToast;
import net.jwn.jwnsprofilemod.trade.TradeSession;
import net.jwn.jwnsprofilemod.trade.TradeSessionManager;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.entity.player.PlayerEvent;
import net.neoforged.neoforge.event.entity.player.UseItemOnBlockEvent;

@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID)
public class ModEvents {
    @SubscribeEvent
    public static void onPlayerLoggedInEvent(PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            ProfileData profileData = ProfileData.get(serverPlayer.level());
            profileData.createPlayerProfileIfAbsent(serverPlayer);
        }
    }

    @SubscribeEvent
    public static void onPlayerLoggedOutEvent(PlayerEvent.PlayerLoggedOutEvent event) {
        Player player = event.getEntity();
        if (player instanceof ServerPlayer serverPlayer) {
            ProfileData profileData = ProfileData.get(serverPlayer.level());
            profileData.setPlayerLevel(serverPlayer, serverPlayer.experienceLevel);
            profileData.setPlayerlastLogoutAt(serverPlayer, System.currentTimeMillis());

            TradeSession session = TradeSessionManager.get(serverPlayer);
            if (session != null) TradeSessionManager.sessionClose(session, serverPlayer.level().getServer());
        }
    }
}
