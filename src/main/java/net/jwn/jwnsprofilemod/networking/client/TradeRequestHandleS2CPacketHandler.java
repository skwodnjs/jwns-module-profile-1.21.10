package net.jwn.jwnsprofilemod.networking.client;

import net.jwn.jwnsprofilemod.networking.packet.TradeRequestHandleS2CPacket;
import net.jwn.jwnsprofilemod.trade.TradeRequestToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Objects;

public class TradeRequestHandleS2CPacketHandler {
    public static void handle(final TradeRequestHandleS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (Objects.equals(data.request(), "accept")) {
                context.player().displayClientMessage(Component.literal(data.name() + "님의 거래 요청을 수락하셨습니다."), false);
            } else if (Objects.equals(data.request(), "reject")) {
                context.player().displayClientMessage(Component.literal(data.name() + "님의 거래 요청을 거절하셨습니다."), false);
            }

            ToastManager manager = Minecraft.getInstance().getToastManager();
            TradeRequestToast toast = manager.getToast(TradeRequestToast.class, TradeRequestToast.TOKEN);
            if (toast != null) {
                toast.hide();
            }
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
