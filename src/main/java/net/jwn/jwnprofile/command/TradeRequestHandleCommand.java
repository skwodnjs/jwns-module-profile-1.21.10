package net.jwn.jwnprofile.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnprofile.networking.packet.CloseTradeToastS2CPacket;
import net.jwn.jwnprofile.trade.TradeSession;
import net.jwn.jwnprofile.trade.TradeSessionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class TradeRequestHandleCommand {
    public TradeRequestHandleCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("trade")
            .then(Commands.literal("accept").executes(this::accept))
            .then(Commands.literal("reject").executes(this::reject)));
    }

    private int accept(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            System.out.println(isSafe(player));
            if (!(isSafe(player))) {
                player.displayClientMessage(Component.translatable("jwnprofile.trade.not_safe"), false);
                return 1;
            }

            TradeSession session = TradeSessionManager.get(player);
            if (session != null) {
                player.openMenu(session, buf -> {
                    buf.writeUUID(session.playerA());
                    buf.writeUUID(session.playerB());
                });
                CloseTradeToastS2CPacket packet = new CloseTradeToastS2CPacket();
                PacketDistributor.sendToPlayer(player, packet);
            } else {
                player.displayClientMessage(Component.translatable("jwnprofile.trade.cannot_accept"), false);
            }
        }
        return 1;
    }

    private int reject(CommandContext<CommandSourceStack> context) {
        ServerPlayer player = context.getSource().getPlayer();
        if (player != null) {
            TradeSession session = TradeSessionManager.get(player);
            if (session != null) {
                TradeSessionManager.sessionClose(session, player.level().getServer());
                Player target = player.level().getServer().getPlayerList().getPlayer(
                        Objects.equals(player.getUUID(), session.playerA()) ? session.playerB() : session.playerA()
                );
                if (target != null) target.displayClientMessage(Component.translatable("jwnprofile.profile.trade_rejected"), false);
                player.displayClientMessage(Component.translatable("jwnprofile.profile.trade_reject"), false);
            } else {
                player.displayClientMessage(Component.translatable("jwnprofile.trade.cannot_decline"), false);
            }
        }
        return 1;
    }

    private boolean isSafe(ServerPlayer player) {
        AABB box = player.getBoundingBox().inflate(8.0);

        return player.level().getEntitiesOfClass(
                Monster.class,
                box,
                Entity::isAlive
        ).isEmpty();
    }
}