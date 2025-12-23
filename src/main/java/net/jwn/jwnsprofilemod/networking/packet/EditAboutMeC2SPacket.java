package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record EditAboutMeC2SPacket(String aboutMe) implements CustomPacketPayload {
    public static final Type<EditAboutMeC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "edit_about_me_packet"));

    public static final StreamCodec<ByteBuf, EditAboutMeC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ByteBufCodecs.STRING_UTF8, EditAboutMeC2SPacket::aboutMe,
            EditAboutMeC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final EditAboutMeC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            ProfileData profileData = ProfileData.get(serverPlayer.level().getServer());
            profileData.setPlayerAboutMe(serverPlayer, data.aboutMe());
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
