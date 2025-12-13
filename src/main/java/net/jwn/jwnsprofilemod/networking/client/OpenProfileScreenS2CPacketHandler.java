package net.jwn.jwnsprofilemod.networking.client;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.network.handling.IPayloadContext;

//@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID, value = Dist.CLIENT)
public class OpenProfileScreenS2CPacketHandler {
    public static void handle(final OpenProfileScreenS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Minecraft.getInstance().player == null) return;
            Player player = Minecraft.getInstance().player;
            player.displayClientMessage(Component.literal(String.valueOf(data.profile().getLevel())), false);
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
