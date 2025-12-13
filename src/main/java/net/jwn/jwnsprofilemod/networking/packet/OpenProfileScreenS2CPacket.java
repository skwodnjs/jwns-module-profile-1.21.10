package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record OpenProfileScreenS2CPacket(ProfileData.PlayerProfile profile, boolean isOnline) implements CustomPacketPayload {
    public static final Type<OpenProfileScreenS2CPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "open_profile_packet"));

    public static final StreamCodec<ByteBuf, OpenProfileScreenS2CPacket> STREAM_CODEC = StreamCodec.composite(
            ProfileData.PlayerProfile.STREAM_CODEC, OpenProfileScreenS2CPacket::profile,
            ByteBufCodecs.BOOL, OpenProfileScreenS2CPacket::isOnline,
            OpenProfileScreenS2CPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final OpenProfileScreenS2CPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {

        });
    }
}
