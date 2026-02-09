package net.jwn.jwnprofile.event;

import net.jwn.jwnprofile.JWNsProfileMod;
import net.jwn.jwnprofile.networking.packet.RequestPlayerListScreenC2SPacket;
import net.jwn.jwnprofile.util.Keybinding;
import net.minecraft.client.Minecraft;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.InputEvent;
import net.neoforged.neoforge.client.event.RegisterKeyMappingsEvent;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID, value = Dist.CLIENT)
public class ClientEvents {
    @SubscribeEvent
    public static void onKeyInput(InputEvent.Key event) {
        if (Keybinding.PLAYER_LIST_KEY.consumeClick()) {
            if (Minecraft.getInstance().player != null) {
                ClientPacketDistributor.sendToServer(new RequestPlayerListScreenC2SPacket(Minecraft.getInstance().player.getUUID()));
            }
        }
    }

    @SubscribeEvent
    public static void onKeyRegister(RegisterKeyMappingsEvent event) {
        event.register(Keybinding.PLAYER_LIST_KEY);
    }
}
