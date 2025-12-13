package net.jwn.jwnsprofilemod.screen;

import com.mojang.authlib.GameProfile;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.PlayerSkin;

public class TradeScreen extends Screen {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "textures/gui/trade_gui.png");
    private static final ResourceLocation CHECK_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "check_button");
    private static final ResourceLocation CHECK_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "check_button_highlighted");
    private static final ResourceLocation CHECK_BUTTON_DISABLED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "check_button_disabled");

    private final ProfileData.PlayerProfile profile;

    protected TradeScreen(ProfileData.PlayerProfile profile) {
        super(Component.translatable("jwnsprofilemod.trade.title"));
        this.profile = profile;
    }

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;
    private static final int DRAW_WIDTH = 206;
    private static final int DRAW_HEIGHT = 166;

    int x;
    int y;

    private volatile PlayerSkin cachedSkin1;
    private volatile PlayerSkin cachedSkin2;

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;

        addRenderableWidget(new ImageButton(
                x + 20, y + 44, 7, 7,
                new WidgetSprites(CHECK_BUTTON, CHECK_BUTTON_DISABLED, CHECK_BUTTON_HIGHLIGHTED), button -> {
                    button.active = false;
                }
        ));

        Minecraft.getInstance().getSkinManager().get(Minecraft.getInstance().getGameProfile())
                .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin1 = playerSkin));
        Minecraft.getInstance().getSkinManager().get(new GameProfile(profile.getUuid(), profile.getName()))
                .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin2 = playerSkin));
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        setFocused(null);
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, BG, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );

        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.drawString(this.font, this.title, x + 8, y + 8, 0xFF000000, false);

        if (cachedSkin1 != null) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, cachedSkin1.body().texturePath(), x + 11, y + 22,
                    8.0F, 8.0F, 16, 16,
                    8, 8, 64, 64
            );
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, cachedSkin1.body().texturePath(), x + 11, y + 22,
                    40.0F, 8.0F, 16, 16,
                    8, 8, 64, 64
            );
        }
        if (cachedSkin2 != null) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, cachedSkin2.body().texturePath(), x + 179, y + 22,
                    8.0F, 8.0F, 16, 16,
                    8, 8, 64, 64
            );
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, cachedSkin2.body().texturePath(), x + 179, y + 22,
                    40.0F, 8.0F, 16, 16,
                    8, 8, 64, 64
            );
        }

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CHECK_BUTTON_DISABLED, x + 179, y + 44, 7, 7);
    }
}
