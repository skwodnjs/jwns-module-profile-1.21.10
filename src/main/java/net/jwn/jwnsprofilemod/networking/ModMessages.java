package net.jwn.jwnsprofilemod.networking;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.client.CloseTradeToastS2CPacketHandler;
import net.jwn.jwnsprofilemod.networking.client.OpenProfileScreenS2CPacketHandler;
import net.jwn.jwnsprofilemod.networking.client.RequestTradeS2CPacketHandler;
import net.jwn.jwnsprofilemod.networking.packet.*;
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
        registrar.playBidirectional(
                RequestTradeS2CPacket.TYPE,
                RequestTradeS2CPacket.STREAM_CODEC,
                RequestTradeS2CPacket::handle
        );
        registrar.playBidirectional(
                CloseTradeToastS2CPacket.TYPE,
                CloseTradeToastS2CPacket.STREAM_CODEC,
                CloseTradeToastS2CPacket::handle
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
        registrar.playToServer(
                RequestTradeC2SPacket.TYPE,
                RequestTradeC2SPacket.STREAM_CODEC,
                RequestTradeC2SPacket::handle
        );
        registrar.playToServer(
                TradeCanceledC2SPacket.TYPE,
                TradeCanceledC2SPacket.STREAM_CODEC,
                TradeCanceledC2SPacket::handle
        );
    }

    @SubscribeEvent
    public static void register(RegisterClientPayloadHandlersEvent event) {
        event.register(
                OpenProfileScreenS2CPacket.TYPE,
                OpenProfileScreenS2CPacketHandler::handle
        );
        event.register(
                RequestTradeS2CPacket.TYPE,
                RequestTradeS2CPacketHandler::handle
        );
        event.register(
                CloseTradeToastS2CPacket.TYPE,
                CloseTradeToastS2CPacketHandler::handle
        );
    }
}
