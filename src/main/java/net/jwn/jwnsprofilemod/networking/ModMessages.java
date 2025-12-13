package net.jwn.jwnsprofilemod.networking;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.client.OpenProfileScreenS2CPacketHandler;
import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.network.event.RegisterClientPayloadHandlersEvent;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;

@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID)
public class ModMessages {
    @SubscribeEvent
    public static void register(RegisterPayloadHandlersEvent event) {
        final PayloadRegistrar registrar = event.registrar("1");
        registrar.playBidirectional(
                OpenProfileScreenS2CPacket.TYPE,
                OpenProfileScreenS2CPacket.STREAM_CODEC,
                OpenProfileScreenS2CPacket::handle
        );
    }

    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(
                OpenProfileScreenS2CPacket.TYPE,
                OpenProfileScreenS2CPacketHandler::handle
        );
    }
}
