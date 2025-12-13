package net.jwn.jwnsprofilemod.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record AddGuestbookMessageC2SPacket(ProfileData.PlayerProfile profile, ProfileData.GuestbookEntry guestbookEntry) implements CustomPacketPayload {
    public static final Type<AddGuestbookMessageC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "add_guest_book_packet"));

    public static final StreamCodec<ByteBuf, AddGuestbookMessageC2SPacket> STREAM_CODEC = StreamCodec.composite(
            ProfileData.PlayerProfile.STREAM_CODEC, AddGuestbookMessageC2SPacket::profile,
            ProfileData.GuestbookEntry.STREAM_CODEC, AddGuestbookMessageC2SPacket::guestbookEntry,
            AddGuestbookMessageC2SPacket::new
    );

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final AddGuestbookMessageC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (!(context.player() instanceof ServerPlayer serverPlayer)) return;
            ProfileData profileData = ProfileData.get(serverPlayer.level());
            profileData.addPlayerGuestbook(data.profile().getName(), data.guestbookEntry());
        }).exceptionally(e -> {
            System.err.println(e.getMessage());
            return null;
        });
    }
}
