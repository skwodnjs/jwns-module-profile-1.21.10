package net.jwn.jwnsprofilemod.screen;

import net.jwn.jwnsprofilemod.JWNsProfileMod;
import net.jwn.jwnsprofilemod.networking.packet.EditAboutMeC2SPacket;
import net.jwn.jwnsprofilemod.profile.ProfileData;
import net.jwn.jwnsprofilemod.util.Functions;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.ImageButton;
import net.minecraft.client.gui.components.MultiLineEditBox;
import net.minecraft.client.gui.components.WidgetSprites;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderPipelines;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.FormattedCharSequence;
import net.neoforged.neoforge.client.network.ClientPacketDistributor;

import java.util.List;

public class ProfileEditScreen extends Screen {
    private static final ResourceLocation BG = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "textures/gui/edit_gui.png");
    private static final ResourceLocation BUTTON = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "button");
    private static final ResourceLocation BUTTON_PRESSED = ResourceLocation.fromNamespaceAndPath(JWNsProfileMod.MOD_ID, "button_highlighted");
    private final ProfileData.PlayerProfile profile;
    private static String aboutMe = "";


    protected ProfileEditScreen(ProfileData.PlayerProfile profile) {
        super(Component.literal(profile.getName()));
        this.profile = profile;
    }

    private static final int IMAGE_WIDTH = 256;
    private static final int IMAGE_HEIGHT = 256;
    private static final int DRAW_WIDTH = 110;
    private static final int DRAW_HEIGHT = 166;

    private static final int BUTTON_WIDTH = 90;
    private static final int BUTTON_HEIGHT = 15;

    int x;
    int y;

    @Override
    protected void init() {
        x = (this.width - DRAW_WIDTH) / 2;
        y = (this.height - DRAW_HEIGHT) / 2;

        ImageButton imageButton2 = new ImageButton(
                (this.width - BUTTON_WIDTH) / 2, y + 105 + 18, BUTTON_WIDTH, BUTTON_HEIGHT, new WidgetSprites(BUTTON, BUTTON_PRESSED),
                button -> this.onClose());
        addRenderableWidget(imageButton2);

        ImageButton imageButton3 = new ImageButton(
                (this.width - BUTTON_WIDTH) / 2, y + 105 + 36, BUTTON_WIDTH, BUTTON_HEIGHT, new WidgetSprites(BUTTON, BUTTON_PRESSED),
                button -> {
                    assert Minecraft.getInstance().player != null;
                    EditAboutMeC2SPacket packet = new EditAboutMeC2SPacket(aboutMe);
                    ClientPacketDistributor.sendToServer(packet);
                    this.onClose();
                });
        addRenderableWidget(imageButton3);

        MultiLineEditBox editBox = MultiLineEditBox.builder().build(
                this.font, 90, 66, Component.empty()
        );
        editBox.setX(x + 10);
        editBox.setY(y + 53);
        editBox.setValueListener(text -> aboutMe = text);
        editBox.setValue(profile.getAboutMe());
        addRenderableWidget(editBox);
    }

    @Override
    public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTicks) {
        graphics.blit(
                RenderPipelines.GUI_TEXTURED, BG, x, y,
                0.0F, 0.0F, DRAW_WIDTH, DRAW_HEIGHT,
                DRAW_WIDTH, DRAW_HEIGHT, IMAGE_WIDTH, IMAGE_HEIGHT
        );

        super.render(graphics, mouseX, mouseY, partialTicks);

        graphics.drawString(this.font, this.title, x + 13, y + 8, 0xFF000000, false);

        Component text = Component.literal(profile.getAboutMe());
        int maxWidth = DRAW_WIDTH - 26;
        List<FormattedCharSequence> lines = Functions.wrapByCharacter(text, maxWidth, this.font);

        int lineHeight = this.font.lineHeight;

        for (int i = 0; i < lines.size(); i++) {
            graphics.drawString(
                    this.font,
                    lines.get(i),
                    x + 13,
                    y + 24 + (i * lineHeight) + 1,
                    0xFF000000,
                    false
            );
        }

        Component text2 = Component.translatable("jwnsprofilemod.profile_edit.exit");
        Component text3 = Component.translatable("jwnsprofilemod.profile_edit.save");

        graphics.drawString(this.font, text2, (this.width - font.width(text2)) / 2, y + 126, 0xFF000000, false);
        graphics.drawString(this.font, text3, (this.width - font.width(text3)) / 2, y + 144, 0xFF000000, false);
    }
}
