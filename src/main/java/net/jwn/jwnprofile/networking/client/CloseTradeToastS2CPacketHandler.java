package net.jwn.jwnprofile.networking.client;

import net.jwn.jwnprofile.networking.packet.CloseTradeToastS2CPacket;
import net.jwn.jwnprofile.trade.TradeRequestToast;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class CloseTradeToastS2CPacketHandler {
    public static void handle(final CloseTradeToastS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
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
