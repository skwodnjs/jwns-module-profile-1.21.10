package net.jwn.jwnsprofilemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsprofilemod.networking.packet.CloseTradeToastS2CPacket;
import net.jwn.jwnsprofilemod.trade.TradeSession;
import net.jwn.jwnsprofilemod.trade.TradeSessionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

public class TradeRequestHandleCommand {
    public TradeRequestHandleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("trade")
            .then(Commands.literal("accept")
                .then(Commands.argument("playerName", StringArgumentType.string())
                    .executes(this::accept)))
            .then(Commands.literal("reject")
                .then(Commands.argument("playerName", StringArgumentType.string())
                    .executes(this::reject))));
    }

    private int accept(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            TradeSession session = TradeSessionManager.get(player);
            if (session != null) {
                session.playerBIsJoined();
                player.openMenu(session, buf -> {
                    buf.writeUUID(session.playerA());
                    buf.writeUUID(session.playerB());
                });
                CloseTradeToastS2CPacket packet = new CloseTradeToastS2CPacket(false);
                PacketDistributor.sendToPlayer(player, packet);
            }

        }
        return 1;
    }

    private int reject(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            TradeSession session = TradeSessionManager.get(player);
            TradeSessionManager.sessionClose(session , player.level().getServer());
            String name = StringArgumentType.getString(context, "playerName");
            player.displayClientMessage(Component.literal(name + "님의 거래 요청을 거절하셨습니다."), false);
        }
        return 1;
    }
}