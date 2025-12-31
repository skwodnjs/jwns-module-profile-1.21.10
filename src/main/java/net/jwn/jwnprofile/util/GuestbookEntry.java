package net.jwn.jwnprofile.util;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.UUID;

public class GuestbookEntry {

    private final long time;
    private final String writer;
    private final UUID writerUUID;
    private final String message;
    private boolean read;

    public GuestbookEntry(long time, String writer, UUID writerUUID, String message, boolean read) {
        this.time = time;
        this.writer = writer;
        this.writerUUID = writerUUID;
        this.message = message;
        this.read = read;
    }

    public long time() {
        return time;
    }

    public String writer() {
        return writer;
    }

    public UUID writerUUID() {
        return writerUUID;
    }

    public String message() {
        return message;
    }

    public boolean isRead() {
        return read;
    }

    public void setRead(boolean read) {
        this.read = read;
    }

    public static final Codec<GuestbookEntry> CODEC =
            RecordCodecBuilder.create(instance -> instance.group(
                    Codec.LONG.fieldOf("time").forGetter(GuestbookEntry::time),
                    Codec.STRING.fieldOf("writer").forGetter(GuestbookEntry::writer),
                    UUIDUtil.CODEC.fieldOf("writerUUID").forGetter(GuestbookEntry::writerUUID),
                    Codec.STRING.fieldOf("message").forGetter(GuestbookEntry::message),
                    Codec.BOOL.fieldOf("read").forGetter(GuestbookEntry::isRead)
            ).apply(instance, GuestbookEntry::new));

    public static final StreamCodec<ByteBuf, GuestbookEntry> STREAM_CODEC =
            StreamCodec.composite(
                    ByteBufCodecs.VAR_LONG, GuestbookEntry::time,
                    ByteBufCodecs.STRING_UTF8, GuestbookEntry::writer,
                    UUIDUtil.STREAM_CODEC, GuestbookEntry::writerUUID,
                    ByteBufCodecs.STRING_UTF8, GuestbookEntry::message,
                    ByteBufCodecs.BOOL, GuestbookEntry::isRead,
                    GuestbookEntry::new
            );
}
