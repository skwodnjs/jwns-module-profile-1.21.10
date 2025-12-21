package net.jwn.jwnsprofilemod.screen;

import com.mojang.authlib.GameProfile;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.packet.TradeCanceledC2SPacket;
import net.jwn.jwnsprofilemod.networking.packet.TradeReadyC2SPacket;
import net.jwn.jwnsprofilemod.trade.TradeMenu;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.client.multiplayer.PlayerInfo;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.PlayerSkin;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

public class TradeScreen extends AbstractContainerScreen<TradeMenu> {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "textures/gui/trade_gui.png");
    private static final ResourceLocation CHECK_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "check_button");
    private static final ResourceLocation CHECK_BUTTON_HIGHLIGHTED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "check_button_highlighted");
    private static final ResourceLocation CHECK_BUTTON_DISABLED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "check_button_disabled");
    private static final ResourceLocation ME = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "me");

    public TradeScreen(TradeMenu menu, Inventory inventory, Component title) {
        super(menu, inventory, title);
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

        leftPos = x;
        topPos = y;

        addRenderableWidget(new ImageButton(
                x + 20 + 159 * menu.getPosition(), y + 44, 7, 7,
                new WidgetSprites(CHECK_BUTTON, CHECK_BUTTON_DISABLED, CHECK_BUTTON_HIGHLIGHTED), button -> {
                    if (Minecraft.getInstance().player != null) {
                        TradeReadyC2SPacket packet = new TradeReadyC2SPacket(Minecraft.getInstance().player.getUUID());
                        ClientPacketDistributor.sendToServer(packet);
                        button.active = false;
                    }
                }
        ));

        if (Minecraft.getInstance().getConnection() != null) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(this.menu.playerAUUID);
            if (info != null) {
                GameProfile profile = info.getProfile();
                Minecraft.getInstance().getSkinManager().get(profile)
                        .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin1 = playerSkin));
            }
        } else {
            Minecraft.getInstance().getSkinManager().get(new GameProfile(this.menu.playerAUUID, ""))
                    .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin1 = playerSkin));
        }

        if (Minecraft.getInstance().getConnection() != null) {
            PlayerInfo info = Minecraft.getInstance().getConnection().getPlayerInfo(this.menu.playerBUUID);
            if (info != null) {
                GameProfile profile = info.getProfile();
                Minecraft.getInstance().getSkinManager().get(profile)
                        .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin2 = playerSkin));
            }
        } else {
            Minecraft.getInstance().getSkinManager().get(new GameProfile(this.menu.playerBUUID, ""))
                    .thenAccept(skin -> skin.ifPresent(playerSkin -> cachedSkin2 = playerSkin));
        }

    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        setFocused(null);

        super.render(graphics, mouseX, mouseY, partialTicks);

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
        if (cachedSkin2 != null && menu.isPlayerBJoined.get() == 1) {
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

        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, ME, x + 21 + 168 * menu.getPosition(), y + 35, 8, 5);

        boolean theOtherPlayerIsReady =
            menu.getPosition() == 0 ? menu.isPlayerBReady.get() == 1 :
            menu.getPosition() == 1 && menu.isPlayerAReady.get() == 1;

        if (theOtherPlayerIsReady) {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CHECK_BUTTON_DISABLED, x + 20 + 159 * (1 - menu.getPosition()), y + 44, 7, 7);
        } else {
            graphics.blitSprite(RenderPipelines.GUI_TEXTURED, CHECK_BUTTON, x + 20 + 159 * (1 - menu.getPosition()), y + 44, 7, 7);
        }
    }

    @Override
    protected void renderBg(GuiGraphics graphics, float v, int i, int i1) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, BG, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );
    }

    @Override
    public void onClose() {
        super.onClose();
        TradeCanceledC2SPacket packet = new TradeCanceledC2SPacket();
        ClientPacketDistributor.sendToServer(packet);
    }
}
