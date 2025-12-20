package net.jwn.jwnsprofilemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.packet.RequestTradeS2CPacket;
import net.jwn.jwnsprofilemod.networking.packet.TradeRequestHandleS2CPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.world.entity.player.Player;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;
import net.neoforged.neoforge.network.PacketDistributor;

@EventBusSubscriber(modid = JWNsProfileMod.MOD_ID)
public class ModCommands {
    @SubscribeEvent
    public static void onRegisterCommandsEvent(RegisterCommandsEvent event) {
        new TestCommand(event.getDispatcher());
        new OpenProfileCommand(event.getDispatcher());
        new TradeRequestHandleCommand(event.getDispatcher());
    }

    private static class TestCommand {
        private TestCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
            dispatcher.register(Commands.literal("test").executes(this::execute));
        }
        private int execute(CommandContext<CommandSourceStack> context) {
            if (context.getSource().getPlayer() != null) {
                Player player = context.getSource().getPlayer();
                RequestTradeS2CPacket packet = new RequestTradeS2CPacket(player.getUUID(), player.getPlainTextName());
                PacketDistributor.sendToPlayer(context.getSource().getPlayer(), packet);
            }
            return 1;
        }
    }
}
