package net.jwn.jwnprofile.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.jwn.jwnprofile.profile.ProfileData;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public record OpenPlayerListScreenS2CPacket(Collection<ProfileData.PlayerProfile> playerProfiles, List<UUID> uuids) implements CustomPacketPayload{
    public static final Type<OpenPlayerListScreenS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "open_player_list_screen_packet"));

    private static final StreamCodec<ByteBuf, Collection<ProfileData.PlayerProfile>> PROFILES_CODEC =
            ProfileData.PlayerProfile.STREAM_CODEC.apply(
                    ByteBufCodecs.collection(ArrayList::new)
            );

    private static final StreamCodec<ByteBuf, List<UUID>> UUID_LIST_CODEC =
            UUIDUtil.STREAM_CODEC.apply(
                    ByteBufCodecs.collection(ArrayList::new)
            );

    public static final StreamCodec<ByteBuf, OpenPlayerListScreenS2CPacket> STREAM_CODEC =
            StreamCodec.composite(
                    PROFILES_CODEC, OpenPlayerListScreenS2CPacket::playerProfiles,
                    UUID_LIST_CODEC, OpenPlayerListScreenS2CPacket::uuids,
                    OpenPlayerListScreenS2CPacket::new
            );


    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final OpenPlayerListScreenS2CPacket data, final IPayloadContext context) {

    }
}
