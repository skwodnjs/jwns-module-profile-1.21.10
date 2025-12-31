package net.jwn.jwnprofile.screen;

import com.mojang.authlib.GameProfile;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.jwn.jwnprofile.networking.packet.RequestTradeC2SPacket;
import net.jwn.jwnprofile.profile.ProfileData;
import net.jwn.jwnprofile.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.ChatScreen;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.monster.Monster;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.player.PlayerSkin;
import net.minecraft.world.phys.AABB;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.time.Duration;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public class ProfileScreen extends Screen {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "textures/gui/profile_gui.png");
    private static final ResourceLocation BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "button");
    private static final ResourceLocation BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "button_highlighted");
    private static final ResourceLocation EDIT_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "edit_button");
    private static final ResourceLocation EDIT_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "edit_button_highlighted");

    private final ProfileData.PlayerProfile profile;
    private final boolean isOnline;

    public ProfileScreen(ProfileData.PlayerProfile profile, boolean isOnline) {
        super(Component.literal(profile.getName()));
        this.profile = profile;
        this.isOnline = isOnline;
    }

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;
    private static final int DRAW_WIDTH = 110;
    private static final int DRAW_HEIGHT = 166;

    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 15;

    int x;
    int y;

    private volatile PlayerSkin cachedSkin;

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;

        List<Runnable> actions = List.of(
                // 방명록
                () -> Minecraft.getInstance().setScreen(new GuestbookScreen(profile)),
                // 교환 요청
                () -> {
                    if (Minecraft.getInstance().player != null && Minecraft.getInstance().level != null) {
                        AABB box = Minecraft.getInstance().player.getBoundingBox().inflate(8.0);
                        boolean isSafe = Minecraft.getInstance().level.getEntitiesOfClass(
                                Monster.class,
                                box,
                                Entity::isAlive
                        ).isEmpty();
                        if (!(isSafe)) {
                            Minecraft.getInstance().player.displayClientMessage(Component.translatable("jwnprofile.trade.not_safe"), false);
                            onClose();
                        }

                        else {
                            if (Objects.equals(Minecraft.getInstance().player.getUUID(), profile.getUUID())) {
                                Minecraft.getInstance().player.displayClientMessage(Component.translatable("jwnprofile.profile.cannot_trade_yourself"), false);
                                onClose();
                            } else {
                                RequestTradeC2SPacket packet = new RequestTradeC2SPacket(profile.getUUID());
                                ClientPacketDistributor.sendToServer(packet);
                                onClose();
                            }
                        }
                    }
                },
                // 귓속말
                () -> {
                    if (!isOnline && Minecraft.getInstance().player != null) {
                        Minecraft.getInstance().player.displayClientMessage(
                                Component.translatable("jwnprofile.profile.offline_message"), false);
                        onClose();
                    }
                    else {
                        Minecraft.getInstance().setScreen(new ChatScreen("/tell " + profile.getName() + " ", false));
                    }
                }
        );

        for (int i = 0; i < 3; i++) {
            final int idx = i;
            addRenderableWidget(new ImageButton(
                    (this.width - BUTTON_WIDTH) / 2, y + 105 + i * 18, BUTTON_WIDTH, BUTTON_HEIGHT,
                    new WidgetSprites(BUTTON, BUTTON_HIGHLIGHTED), button -> actions.get(idx).run()
            ));
        }

        Player player = Minecraft.getInstance().player;
        assert player != null;
        if (Objects.equals(player.getUUID(), profile.getUUID())) {
            addRenderableWidget(new ImageButton(
                    x + 94, y + 89, 7, 7, new WidgetSprites(EDIT_BUTTON, EDIT_BUTTON_HIGHLIGHTED),
                    button -> Minecraft.getInstance().setScreen(new ProfileEditScreen(profile))
            ));
        }

        if (Minecraft.getInstance().getConnection() != null) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(profile.getUUID());
            if (info != null) {
                GameProfile profile = info.getProfile();
                Minecraft.getInstance().getSkinManager().get(profile)
                        .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin = playerSkin));
            } else {
                Minecraft.getInstance().getSkinManager().get(new GameProfile(profile.getUUID(), profile.getName()))
                        .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin = playerSkin));
            }
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        setFocused(null);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, BG, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );

        if (cachedSkin != null) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, cachedSkin.body().texturePath(), x + 10, y + 11,
                    8.0F, 8.0F, 43, 43,
                    8, 8, 64, 64
            );
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, cachedSkin.body().texturePath(), x + 10, y + 11,
                    40.0F, 8.0F, 43, 43,
                    8, 8, 64, 64
            );
        }

        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.drawString(this.font, this.title, x + 57, y + 12, 0xFF000000, false);
        graphics.drawString(this.font, Component.literal("LV. " + profile.getLevel()), x + 57, y + 23, 0xFF000000, false);

        Component logoutAt;
        if (isOnline) {
            logoutAt = Component.translatable("jwnprofile.profile.status.online");
        } else {
            Instant now = Instant.now();
            Duration duration = Duration.between(Instant.ofEpochMilli(profile.getLastLogoutAt()), now);
            int gap = (duration.toMinutes() <= 60) ? (int) duration.toMinutes() : (int) duration.toHours();
            String key = (duration.toMinutes() <= 60) ? "jwnprofile.minute" : "jwnprofile.hour";
            logoutAt = Component.translatable("jwnprofile.profile.status.offline", gap + " " + Component.translatable(key).getString());
        }

        graphics.drawString(this.font, logoutAt, x + 10, y + 57, 0xFF000000, false);

        Component text = (Objects.equals(profile.getAboutMe(), ""))
                ? Component.translatable("jwnprofile.profile.enter_about_me") : Component.literal(profile.getAboutMe());
        int maxWidth = DRAW_WIDTH - 26;
        int startX = x + 13;
        int startY = y + 73;

        List<FormattedCharSequence> lines = Functions.wrapByCharacter(text, maxWidth, this.font);

        int lineHeight = this.font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(
                    this.font,
                    lines.get(i),
                    startX,
                    startY + (i * lineHeight) + 1,
                    0xFF000000,
                    false
            );
        }

        Component text1;
        if (Minecraft.getInstance().player != null && Objects.equals(Minecraft.getInstance().player.getUUID(), profile.getUUID())) {
            text1 = Component.translatable("jwnprofile.profile.guestbook_me");
        } else {
            text1 = Component.translatable("jwnprofile.profile.guestbook");
        }
        Component text2 = Component.translatable("jwnprofile.profile.trade_request");
        Component text3 = Component.translatable("jwnprofile.profile.whisper");

        graphics.drawString(this.font, text1, (this.width - font.width(text1)) / 2, y + 108, 0xFF000000, false);
        graphics.drawString(this.font, text2, (this.width - font.width(text2)) / 2, y + 126, 0xFF000000, false);
        graphics.drawString(this.font, text3, (this.width - font.width(text3)) / 2, y + 144, 0xFF000000, false);
    }
}
