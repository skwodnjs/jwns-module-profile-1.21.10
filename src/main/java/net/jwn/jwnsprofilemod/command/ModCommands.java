package net.jwn.jwnsprofilemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
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
    }

    private static class TestCommand {
        private TestCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
            dispatcher.register(Commands.literal("test").executes(this::execute));
        }
        private int execute(CommandContext<CommandSourceStack> context) {


            
            return 1;
        }
    }
}
