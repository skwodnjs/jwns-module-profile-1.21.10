package net.jwn.jwnprofile.screen;

import com.mojang.authlib.GameProfile;
import net.jwn.jwnprofile.JWNsProfileMod;
import net.jwn.jwnprofile.profile.ProfileData;
import net.jwn.jwnprofile.util.Keybinding;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.input.InputWithModifiers;
import net.minecraft.client.input.KeyEvent;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class PlayerListScreen extends Screen {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "textures/gui/player_list_gui.png");
    private static final ResourceLocation PLAYER_LIST_BOX = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "player_list_box");
    private static final ResourceLocation SEARCH_BAR = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "search_bar");
    private static final ResourceLocation SCROLL_BAR = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "scroll_bar");
    private static final ResourceLocation DOT_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "dot_button");

    class ProfileButton extends ImageButton {
        public ProfileButton(int x, int y, int width, int height, WidgetSprites sprites, ProfileData.PlayerProfile profile, boolean isOnline) {
            super(x, y, width, height, sprites, button -> {});
            this.profile = profile;
            this.isOnline = isOnline;
        }

        public ProfileData.PlayerProfile profile;
        public boolean isOnline;

        @Override
        public void onPress(InputWithModifiers input) {
            OnPress press = button -> Minecraft.getInstance().setScreen(new ProfileScreen(profile, isOnline));
            press.onPress(this);
        }
    }

    public PlayerListScreen(Collection<ProfileData.PlayerProfile> playerProfiles, List<UUID> uuids) {
        super(Component.literal("TITLE"));
        this.profiles = new ArrayList<>(playerProfiles);
        this.uuids = uuids;
        this.skins = new PlayerSkin[this.profiles.size()];

        var conn = Minecraft.getInstance().getConnection();

        for (int i = 0; i < this.profiles.size(); i++) {
            final int idx = i;
            ProfileData.PlayerProfile profile = this.profiles.get(i);
            GameProfile gameProfile = null;

            if (conn != null) {
                PlayerInfo info = conn.getPlayerInfo(profile.getUUID());
                if (info != null) gameProfile = info.getProfile();
            }

            if (gameProfile == null) {
                gameProfile = new GameProfile(profile.getUUID(), profile.getName());
            }

            Minecraft.getInstance().getSkinManager().get(gameProfile).thenAccept(
                    opt -> opt.ifPresent(skin -> this.skins[idx] = skin)
            );
        }
    }

    List<ProfileData.PlayerProfile> profiles;
    List<UUID> uuids;
    private final PlayerSkin[] skins;

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;
    private static final int DRAW_WIDTH = 147;
    private static final int DRAW_HEIGHT = 166;

    private static final int BUTTON_WIDTH = 12;
    private static final int BUTTON_HEIGHT = 12;

    int x;
    int y;

    ProfileButton[] buttons = new ProfileButton[4];

    int size;
    int index = 0;
    int maxIndex;
    int scrollbarSize;

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;

        size = profiles.size();
        if (size == 0)  maxIndex = 0;
        else maxIndex = (size + 4 - 1) / 4 - 1;

        if (maxIndex == 0) scrollbarSize = 114;
        else scrollbarSize = 114 * (maxIndex + 1) / (maxIndex + 5);

        for (int i = 0; i < 4; i++) {
            buttons[i] = new ProfileButton(
                    x + 116,
                    y + 36 + (i * 30),
                    BUTTON_WIDTH,
                    BUTTON_HEIGHT,
                    new WidgetSprites(DOT_BUTTON),
                    null,
                    false
            );
            this.addRenderableWidget(buttons[i]);
        }
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {

        graphics.blit(RenderPipelines.GUI_TEXTURED, BG, x, y, 0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT, DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT);
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SEARCH_BAR, x + 25, y + 14, 111, 10);
        int scrollGap = maxIndex == 0 ? 0 : index * (114 - scrollbarSize) / maxIndex;
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, SCROLL_BAR, x + 135, y + 30 + scrollGap, 2, scrollbarSize);

        for (int i = 0; i < 4; i++) {
            if (index * 4 + i < size) {
                buttons[i].visible = true;
                buttons[i].profile = profiles.get(index * 4 + i);
                buttons[i].isOnline = uuids.contains(profiles.get(index * 4 + i).getUUID());
                graphics.blitSprite(RenderPipelines.GUI_TEXTURED, PLAYER_LIST_BOX, x + 12, y + 30 + i * 30, 121, 24);
                graphics.drawString(this.font, profiles.get(index * 4 + i).getName(), x + 36, y + 38 + i * 30, 0xFF000000, false);
                if (skins[index * 4 + i] != null) {
                    graphics.blit(
                            RenderPipelines.GUI_TEXTURED, skins[index * 4 + i].body().texturePath(), x + 16, y + 34 + i * 30,
                            8.0F, 8.0F, 16, 16,
                            8, 8, 64, 64
                    );
                }
            } else {
                buttons[i].visible = false;
            }
        }

        super.render(graphics, mouseX, mouseY, partialTicks);
    }

    private void clampIndex() {
        if (index < 0) index = 0;
        if (index > maxIndex) index = maxIndex;
    }

    private void scrollPage(int delta) {
        if (size <= 4) return;
        index += delta;
        clampIndex();
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double scrollX, double scrollY) {
        if (scrollY > 0) scrollPage(-1);      // wheel up
        else if (scrollY < 0) scrollPage(1);  // wheel down
        return true;
    }

    @Override
    public boolean keyPressed(KeyEvent event) {
        if (event.key() == Keybinding.PLAYER_LIST_KEY.getKey().getValue()) {
            this.onClose();
            return true;
        }

        return super.keyPressed(event);
    }
}
