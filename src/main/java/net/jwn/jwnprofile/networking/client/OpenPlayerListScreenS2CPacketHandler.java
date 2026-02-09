package net.jwn.jwnprofile.networking.client;

import net.jwn.jwnprofile.networking.packet.OpenPlayerListScreenS2CPacket;
import net.jwn.jwnprofile.screen.PlayerListScreen;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class OpenPlayerListScreenS2CPacketHandler {
    public static void handle(final OpenPlayerListScreenS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().setScreen(new PlayerListScreen(data.playerProfiles(), data.uuids()));
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
