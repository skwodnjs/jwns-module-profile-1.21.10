package net.jwn.jwnsprofilemod.screen;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.jwn.jwnsprofilemod.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;

import java.util.List;

public class GuestbookScreen extends Screen {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "textures/gui/guestbook_gui.png");
    private static final ResourceLocation BACK_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "back_button");
    private static final ResourceLocation BACK_BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "back_button_highlighted");
    private static final ResourceLocation FRONT_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "front_button");
    private static final ResourceLocation FRONT_BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "front_button_highlighted");
    private static final ResourceLocation EDIT_BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "edit_button");
    private static final ResourceLocation EDIT_BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "edit_button_highlighted");

    private final ProfileData.PlayerProfile profile;

    protected GuestbookScreen(ProfileData.PlayerProfile profile) {
        super(Component.translatable("jwnsprofilemod.guestbook.title", profile.getName()));
        this.profile = profile;
    }

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;
    private static final int DRAW_WIDTH = 176;
    private static final int DRAW_HEIGHT = 166;

    int x;
    int y;
    int index = 0;

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;

        addRenderableWidget(new ImageButton(
                x + 158, y + 11, 7, 7, new WidgetSprites(EDIT_BUTTON, EDIT_BUTTON_PRESSED),
                button -> Minecraft.getInstance().setScreen(new GuestbookEditScreen(profile))
        ));

        addRenderableWidget(new ImageButton(
                x + 56, y + 147, 14, 14, new WidgetSprites(BACK_BUTTON, BACK_BUTTON_PRESSED),
                button -> {
                    if (index != 0) index -= 3;
                }
        ));
        addRenderableWidget(new ImageButton(
                x + 106, y + 147, 14, 14, new WidgetSprites(FRONT_BUTTON, FRONT_BUTTON_PRESSED),
                button -> {
                    if (profile.getGuestbook().size() > index + 3) index += 3;
                }
        ));
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

        for (int i = 0; i < 3; i++) {
            if (index + i < profile.getGuestbook().size()) {
                ProfileData.GuestbookEntry entry = profile.getGuestbook().get(index + i);
                String time = java.time.Instant.ofEpochMilli(entry.time())
                        .atZone(java.time.ZoneId.systemDefault())
                        .format(java.time.format.DateTimeFormatter.ofPattern("yyyy/M/d HH:mm"));
                graphics.drawString(this.font, entry.writer() + " (" + time + ")", x + 11, y + 23 + i * 42, 0xFF000000, false);
                Component text = Component.literal(entry.message());
                int maxWidth = 154;
                List<FormattedCharSequence> lines = Functions.wrapByCharacter(text, maxWidth, this.font);
                int lineHeight = this.font.lineHeight;
                for (int j = 0; j < lines.size() && j < 3; j++) {
                    graphics.drawString(this.font, lines.get(j), x + 11, y + 34 + i * 42 + j * lineHeight,
                            0xFF000000, false);
                }
            }
        }
    }
}
