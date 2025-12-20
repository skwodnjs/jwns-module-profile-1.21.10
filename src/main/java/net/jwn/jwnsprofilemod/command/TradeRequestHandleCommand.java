package net.jwn.jwnsprofilemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsprofilemod.networking.packet.TradeRequestHandleS2CPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

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
        String name = StringArgumentType.getString(context, "playerName");
        TradeRequestHandleS2CPacket packet = new TradeRequestHandleS2CPacket("accept", name);
        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }

    private int reject(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "playerName");
        TradeRequestHandleS2CPacket packet = new TradeRequestHandleS2CPacket("reject", name);
        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }
}
