package net.jwn.jwnsprofilemod.networking;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.client.OpenProfileScreenS2CPacketHandler;
import net.jwn.jwnsprofilemod.networking.packet.AddGuestbookMessageC2SPacket;
import net.jwn.jwnsprofilemod.networking.packet.EditAboutMeC2SPacket;
import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsprofilemod.networking.packet.CreateTradeMenuC2SPacket;
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
        registrar.playToServer(
                EditAboutMeC2SPacket.TYPE,
                EditAboutMeC2SPacket.STREAM_CODEC,
                EditAboutMeC2SPacket::handle
        );
        registrar.playToServer(
                AddGuestbookMessageC2SPacket.TYPE,
                AddGuestbookMessageC2SPacket.STREAM_CODEC,
                AddGuestbookMessageC2SPacket::handle
        );
        registrar.playToServer(
                CreateTradeMenuC2SPacket.TYPE,
                CreateTradeMenuC2SPacket.STREAM_CODEC,
                CreateTradeMenuC2SPacket::handle
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
