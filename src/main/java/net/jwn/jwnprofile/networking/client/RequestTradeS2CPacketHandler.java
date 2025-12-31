package net.jwn.jwnprofile.networking.client;

import net.jwn.jwnprofile.networking.packet.RequestTradeS2CPacket;
import net.jwn.jwnprofile.trade.TradeRequestToast;
import net.minecraft.client.Minecraft;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public class RequestTradeS2CPacketHandler {
    public static void handle(final RequestTradeS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            Minecraft.getInstance().getToastManager().addToast(new TradeRequestToast(data.sender(), data.name()));
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
