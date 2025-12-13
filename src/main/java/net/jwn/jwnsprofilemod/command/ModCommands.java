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
            ServerPlayer player = context.getSource().getPlayer();
            if (player == null) return 0;

            String name = "JWN__";
            ServerPlayer target = player.level().getServer().getPlayerList().getPlayer(name);
            ProfileData data = ProfileData.get(player.level());

            ProfileData.PlayerProfile profile;
            if (target != null) {
                profile = data.getProfile(target);
                data.setPlayerLevel(target, target.experienceLevel);
            } else {
                profile = data.getProfile(name);
            }

            if (profile == null) return 0;
            PacketDistributor.sendToPlayer(player, new OpenProfileScreenS2CPacket(profile, target != null));
            return 1;
        }
    }
}
