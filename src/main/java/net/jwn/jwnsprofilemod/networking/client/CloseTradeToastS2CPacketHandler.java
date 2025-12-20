package net.jwn.jwnsprofilemod.networking.client;

import net.jwn.jwnsprofilemod.networking.packet.CloseTradeToastS2CPacket;
import net.jwn.jwnsprofilemod.trade.TradeRequestToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CloseTradeToastS2CPacketHandler {
    public static void handle(final CloseTradeToastS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (data.isCanceled()) context.player().displayClientMessage(Component.literal("거래가 취소되었습니다."), false);
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
