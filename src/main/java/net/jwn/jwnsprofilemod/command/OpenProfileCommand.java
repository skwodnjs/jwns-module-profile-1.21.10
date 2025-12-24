package net.jwn.jwnsprofilemod.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnsprofilemod.networking.packet.OpenProfileScreenS2CPacket;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;

public class OpenProfileCommand {
    public OpenProfileCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("profile")
                .then(Commands.argument("playerName", StringArgumentType.word())
                .suggests((ctx, builder) -> {
                    ProfileData data = ProfileData.get(ctx.getSource().getServer());

                    return SharedSuggestionProvider.suggest(
                            data.getAllProfiles().stream()
                                    .map(ProfileData.PlayerProfile::getName),
                            builder
                    );
                })
                .executes(this::execute)));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        String name = StringArgumentType.getString(context, "playerName");
        MinecraftServer server = context.getSource().getServer();
        ServerPlayer player = server.getPlayerList().getPlayerByName(name);

        ProfileData data = ProfileData.get(context.getSource().getServer());
        ProfileData.PlayerProfile profile;
        OpenProfileScreenS2CPacket packet;

        if (player != null) {
            profile = data.getPlayerProfile(player);
            data.setPlayerLevel(player, player.experienceLevel);
            packet = new OpenProfileScreenS2CPacket(profile, true);
        } else {
            profile = data.getPlayerProfile(name);
            if (profile != null) packet = new OpenProfileScreenS2CPacket(profile, false);
            else {
                if (context.getSource().getPlayer() != null)
                    context.getSource().getPlayer().displayClientMessage(Component.translatable("jwnsprofilemod.profile.search_failed"), false);
                return 0;
            }
        }
        PacketDistributor.sendToPlayer(Objects.requireNonNull(context.getSource().getPlayer()), packet);
        return 1;
    }
}
