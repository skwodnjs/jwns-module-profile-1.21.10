package net.jwn.jwnsprofilemod.networking.client;

import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsprofilemod.screen.ProfileScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenProfileScreenS2CPacketHandler {
    public static void handle(final OpenProfileScreenS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new ProfileScreen(data.profile(), data.isOnline()));
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
