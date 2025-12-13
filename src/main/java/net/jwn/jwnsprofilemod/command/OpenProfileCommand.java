package net.jwn.jwnsprofilemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class OpenProfileCommand {
    public OpenProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("profile")
                .then(Commands.argument("playerName", StringArgumentType.string())
                        .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "playerName");
        MinecraftServer server = context.getSource().getServer();
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);

        ProfileData data = ProfileData.get(context.getSource().getLevel());
        ProfileData.PlayerProfile profile;
        OpenProfileScreenS2CPacket packet;

        if (player != null) {
            profile = data.getProfile(player);
            data.setPlayerLevel(player, player.experienceLevel);
            packet = new OpenProfileScreenS2CPacket(profile, true);
        } else {
            profile = data.getProfile(name);
            packet = new OpenProfileScreenS2CPacket(profile, false);
        }
        if (profile == null && context.getSource().getPlayer() != null) {
            context.getSource().getPlayer().displayClientMessage(Component.translatable("jwnsprofilemod.profile.search_failed"), false);
        }

        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }
}
