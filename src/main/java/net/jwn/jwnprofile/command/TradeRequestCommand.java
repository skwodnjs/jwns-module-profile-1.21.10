package net.jwn.jwnprofile.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.jwn.jwnprofile.networking.packet.RequestTradeS2CPacket;
import net.jwn.jwnprofile.profile.ProfileData;
import net.jwn.jwnprofile.trade.TradeSession;
import net.jwn.jwnprofile.trade.TradeSessionManager;
import net.jwn.jwnprofile.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.commands.SharedSuggestionProvider;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;

import java.util.Objects;
import java.util.UUID;

public class TradeRequestCommand {
    public TradeRequestCommand(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(Commands.literal("trade")
            .then(Commands.literal("request")
                .then(Commands.argument("playerName", StringArgumentType.word())
                        .suggests((ctx, builder) -> {
                            ProfileData data = ProfileData.get(ctx.getSource().getServer());
                            return SharedSuggestionProvider.suggest(
                                    data.getAllProfiles().stream()
                                            .map(ProfileData.PlayerProfile::getName),
                                    builder
                            );
                        })
                        .executes(this::execute))));
    }

    private int execute(CommandContext<CommandSourceStack> context) {
        // SERVER
        ServerPlayer player = context.getSource().getPlayer();
        ProfileData.PlayerProfile profile = ProfileData.get(player.level().getServer()).getPlayerProfile(StringArgumentType.getString(context, "playerName"));

        if (profile == null) {
            player.displayClientMessage(Component.translatable("jwnprofile.message.player_not_exists"), false);
            return 0;
        }

        UUID recipientUUID = profile.getUUID();
        ServerPlayer recipientPlayer = player.level().getServer().getPlayerList().getPlayer(recipientUUID);

        if (!(Functions.isSafe(player))) {
            player.displayClientMessage(Component.translatable("jwnprofile.trade.not_safe"), false);
            return 0;
        }

        if (Objects.equals(player.getUUID(), recipientUUID)) {
            player.displayClientMessage(Component.translatable("jwnprofile.profile.cannot_trade_yourself"), false);
            return 0;
        }

        if (recipientPlayer == null) {
            player.displayClientMessage(Component.translatable("jwnprofile.profile.offline_message"), false);
            return 0;
        }
        if (TradeSessionManager.tradingPlayer.containsKey(player.getUUID())) {
            player.displayClientMessage(Component.translatable("jwnprofile.profile.cannot_while_trading"), false);
            return 0;
        } else if (TradeSessionManager.tradingPlayer.containsKey(recipientUUID)) {
            player.displayClientMessage(Component.translatable("jwnprofile.profile.other_player_already_trading"), false);
            return 0;
        }

        TradeSession session = TradeSessionManager.create(player.getUUID(), recipientUUID);
        player.openMenu(session, buf -> {
            buf.writeUUID(player.getUUID());
            buf.writeUUID(recipientUUID);
        });

        TradeSessionManager.tradingPlayer.put(player.getUUID(), session.id());
        TradeSessionManager.tradingPlayer.put(recipientUUID, session.id());

        RequestTradeS2CPacket packet = new RequestTradeS2CPacket(player.getUUID(), player.getPlainTextName(), player.getPlainTextName());
        PacketDistributor.sendToPlayer(recipientPlayer, packet);

        player.openMenu(session, buf -> {
            buf.writeUUID(session.playerA());
            buf.writeUUID(session.playerB());
        });

        return 1;
    }
}
