package net.jwn.jwnsprofilemod.trade;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.toasts.Toast;
import net.minecraft.client.gui.components.toasts.ToastManager;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.resources.ResourceLocation;

public class TradeRequestToast implements Toast {

    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "message");

    private static final long SHOW_TIME_MS = 3000L;

    private long firstShown = -1L;
    private Visibility visibility = Visibility.SHOW;

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
        graphics.blitSprite(RenderPipelines.GUI_TEXTURED, BG, 0, 0, 160, 64);
    }
}
