package net.jwn.jwnprofile.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.jwn.jwnprofile.profile.ProfileData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.PacketDistributor;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record RequestPlayerListScreenC2SPacket(UUID player) implements CustomPacketPayload {
    public static final Type<RequestPlayerListScreenC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "reqest_player_list_screen_packet"));

    public static final StreamCodec<ByteBuf, RequestPlayerListScreenC2SPacket> STREAM_CODEC = StreamCodec.composite(
            UUIDUtil.STREAM_CODEC, RequestPlayerListScreenC2SPacket::player,
            RequestPlayerListScreenC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final RequestPlayerListScreenC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            // SERVER
            if (context.player() instanceof ServerPlayer player) {
                MinecraftServer server = player.level().getServer();
                ProfileData profileData = ProfileData.get(server);
                Collection<ProfileData.PlayerProfile> playerProfiles = profileData.getAllProfiles();
                List<UUID> uuids = server.getPlayerList().getPlayers().stream().map(ServerPlayer::getUUID).toList();
                PacketDistributor.sendToPlayer(player, new OpenPlayerListScreenS2CPacket(playerProfiles, uuids));
            }
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}