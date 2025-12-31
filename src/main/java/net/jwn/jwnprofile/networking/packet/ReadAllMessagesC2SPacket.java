package net.jwn.jwnprofile.networking.packet;

import io.netty.buffer.ByteBuf;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.jwn.jwnprofile.profile.ProfileData;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.neoforged.neoforge.network.handling.IPayloadContext;

public record ReadAllMessagesC2SPacket() implements CustomPacketPayload {
    public static final Type<ReadAllMessagesC2SPacket> TYPE =
            new Type<>(ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "read_messages_packet"));

    public static final StreamCodec<ByteBuf, ReadAllMessagesC2SPacket> STREAM_CODEC = StreamCodec.unit(new ReadAllMessagesC2SPacket());

    @Override
    public Type<? extends CustomPacketPayload> type() {
        return TYPE;
    }

    public static void handle(final ReadAllMessagesC2SPacket data, final IPayloadContext context) {
        context.enqueueWork(() -> {
            if (context.player() instanceof ServerPlayer player) {
                ProfileData.get(player.level().getServer()).getPlayerProfile(player).readAllMessages();
            }
        });
    }
}
