package net.jwn.jwnsprofilemod.trade;

import com.mojang.authlib.GameProfile;
import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.minecraft.world.entity.player.PlayerSkin;

import java.util.List;
import java.util.UUID;

public class TradeRequestToast implements Toast {

    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "message");

    private static final long SHOW_TIME_MS = 3000L;

    private long firstShown = -1L;
    private Visibility visibility = Visibility.SHOW;

    private final String name;
    private volatile PlayerSkin senderSkin;

    public TradeRequestToast(UUID sender, String name){
        this.name = name;
        Minecraft.getInstance().getSkinManager().get(new GameProfile(sender, ""))
                .thenAccept(skin -> skin.ifPresent(playerSkin -> senderSkin = playerSkin));
    }

    @Override
    public Visibility getWantedVisibility() {
        return visibility;
    }

    @Override
    public void update(ToastManager toastManager, long timeSinceLastVisible) {
        if (firstShown < 0) {
            firstShown = timeSinceLastVisible;
        }

        if (timeSinceLastVisible - firstShown >= SHOW_TIME_MS) {
            visibility = Visibility.HIDE;
        }
    }

    @Override
    public void render(GuiGraphics graphics, Font font, long timeSinceLastVisible) {
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BG, 0, 0, 160, 38);

        if (senderSkin != null) {
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, senderSkin.body().texturePath(), 11, 11,
                    8.0F, 8.0F, 16, 16,
                    8, 8, 64, 64
            );
            graphics.blit(
                    RenderPipelines.GUI_TEXTURED, senderSkin.body().texturePath(), 11, 11,
                    40.0F, 8.0F, 16, 16,
                    8, 8, 64, 64
            );
        }

        Component text = Component.literal(name + "님이 교환을 요청하였습니다.");
        int maxWidth = 118;
        List<FormattedCharSequence> lines = Functions.wrapByCharacter(text, maxWidth, font);
        int lineHeight = font.lineHeight;
        for (int j = 0; j < lines.size() && j < 3; j++) {
            graphics.drawString(font, lines.get(j), 32, 11 + j * lineHeight,0xFF000000, false);
        }
    }
}
