package net.jwn.jwnsprofilemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.trade.TradeSessionManager;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

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
            TradeSessionManager.tradingPlayer.forEach(
                    (uuid, uuid2) -> context.getSource().getPlayer().sendSystemMessage(Component.literal(String.valueOf(uuid)))
            );
            return 1;
        }
    }
}
