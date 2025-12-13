package net.jwn.jwnsprofilemod.profile;

import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.saveddata.SavedData;
import net.minecraft.world.level.saveddata.SavedDataType;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class ProfileData extends SavedData {

    public static final SavedDataType<ProfileData> TYPE =
            new SavedDataType<>(
                    "player_profile_data",
                    ProfileData::new,
                    RecordCodecBuilder.create(instance -> instance.group(
                            Codec.unboundedMap(UUIDUtil.STRING_CODEC, PlayerProfile.CODEC)
                                    .fieldOf("players")
                                    .forGetter(d -> d.players)
                    ).apply(instance, ProfileData::new))
            );

    public static class PlayerProfile {

        private final UUID player;
        private int level;
        private Long lastLogoutAt;
        private String aboutMe;
        private List<GuestbookEntry> guestbook;

        public static final Codec<PlayerProfile> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        UUIDUtil.CODEC.fieldOf("player").forGetter(PlayerProfile::getPlayer),
                        Codec.INT.fieldOf("level").forGetter(PlayerProfile::getLevel),
                        Codec.LONG.fieldOf("last_logout_at").forGetter(PlayerProfile::getLastLogoutAt),
                        Codec.STRING.fieldOf("about_me").forGetter(PlayerProfile::getAboutMe),
                        GuestbookEntry.CODEC.listOf().fieldOf("guestbook").forGetter(PlayerProfile::getGuestbook)
                ).apply(instance, PlayerProfile::new));

        public static final StreamCodec<ByteBuf, PlayerProfile> STREAM_CODEC =
                StreamCodec.composite(
                        UUIDUtil.STREAM_CODEC, PlayerProfile::getPlayer,
                        ByteBufCodecs.VAR_INT, PlayerProfile::getLevel,
                        ByteBufCodecs.VAR_LONG, PlayerProfile::getLastLogoutAt,
                        ByteBufCodecs.STRING_UTF8, PlayerProfile::getAboutMe,
                        GuestbookEntry.STREAM_CODEC.apply(ByteBufCodecs.list()), PlayerProfile::getGuestbook,
                        PlayerProfile::new
                );

        public PlayerProfile(UUID player, int level, Long lastLogoutAt, String aboutMe, List<GuestbookEntry> guestbook) {
            this.player = player;
            this.level = level;
            this.lastLogoutAt = lastLogoutAt;
            this.aboutMe = aboutMe;
            this.guestbook = guestbook;
        }

        public PlayerProfile(UUID player) {
            this(player, 0, 0L, "", List.of());
        }

        /* ===== getters ===== */

        public UUID getPlayer() {
            return player;
        }

        public int getLevel() {
            return level;
        }

        public Long getLastLogoutAt() {
            return lastLogoutAt;
        }

        public String getAboutMe() {
            return aboutMe;
        }

        public List<GuestbookEntry> getGuestbook() {
            return guestbook;
        }

        /* ===== setters ===== */

        public void setLevel(int level) {
            this.level = level;
        }

        public void setLastLogoutAt(Long lastLogoutAt) {
            this.lastLogoutAt = lastLogoutAt;
        }

        public void setAboutMe(String aboutMe) {
            this.aboutMe = aboutMe;
        }

        public void setGuestbook(List<GuestbookEntry> guestbook) {
            this.guestbook = guestbook;
        }
    }

    public record GuestbookEntry(Long time, UUID writer, String message) {
        public static final Codec<GuestbookEntry> CODEC =
                RecordCodecBuilder.create(instance -> instance.group(
                        Codec.LONG.fieldOf("time").forGetter(GuestbookEntry::time),
                        UUIDUtil.CODEC.fieldOf("writer").forGetter(GuestbookEntry::writer),
                        Codec.STRING.fieldOf("message").forGetter(GuestbookEntry::message)
                ).apply(instance, GuestbookEntry::new));

        public static final StreamCodec<ByteBuf, GuestbookEntry> STREAM_CODEC =
                StreamCodec.composite(
                        ByteBufCodecs.VAR_LONG, GuestbookEntry::time,
                        UUIDUtil.STREAM_CODEC, GuestbookEntry::writer,
                        ByteBufCodecs.STRING_UTF8, GuestbookEntry::message,
                        GuestbookEntry::new
                );
    }

    private final Map<UUID, PlayerProfile> players;

    private ProfileData(Map<UUID, PlayerProfile> players) {
        this.players = new HashMap<>(players);
    }

    private ProfileData() {
        this.players = new HashMap<>();
    }

    public static ProfileData get(ServerLevel level) {
        return level.getDataStorage().computeIfAbsent(TYPE);
    }

    public PlayerProfile getProfile(Player player) {
        return players.get(player.getUUID());
    }

    public void createPlayerProfileIfAbsent(Player player) {
        players.computeIfAbsent(player.getUUID(), PlayerProfile::new);
    }

    public void setPlayerLevel(Player player, int level) {
        if (players.get(player.getUUID()) == null) return;
        players.get(player.getUUID()).setLevel(level);
        setDirty();
    }

    public void setPlayerlastLogoutAt(Player player, Long time) {
        if (players.get(player.getUUID()) == null) return;
        players.get(player.getUUID()).setLastLogoutAt(time);
        setDirty();
    }
}
