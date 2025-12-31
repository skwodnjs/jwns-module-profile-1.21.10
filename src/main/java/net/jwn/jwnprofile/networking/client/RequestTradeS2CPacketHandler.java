package net.jwn.jwnprofile.networking.client;

import net.jwn.jwnprofile.networking.packet.RequestTradeS2CPacket;
import net.jwn.jwnprofile.trade.TradeRequestToast;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestTradeS2CPacketHandler {
    public static void handle(final RequestTradeS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // CLIENT
            Minecraft.getInstance().getToastManager().addToast(new TradeRequestToast(data.senderUUID(), data.name()));
            if (Minecraft.getInstance().player != null) {
                Component text = Component.translatable("jwnprofile.message.trade.requested", data.senderName());
                Minecraft.getInstance().player.displayClientMessage(text, false);
            }
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
