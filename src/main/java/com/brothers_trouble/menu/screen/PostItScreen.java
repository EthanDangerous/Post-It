//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.brothers_trouble.menu.screen;

import com.brothers_trouble.entity.PostItEntity;
import com.mojang.blaze3d.platform.Lighting;
import java.util.stream.IntStream;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.game.ServerboundSignUpdatePacket;
import net.minecraft.world.level.block.SignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.WoodType;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

@OnlyIn(Dist.CLIENT)
public class PostItScreen extends Screen {
    private final PostItEntity note;
    private SignText text;
    private final String[] messages;
    private int frame;
    private int line;
    @Nullable
    private TextFieldHelper signField;

    public PostItScreen(PostItEntity note, Component text) {
        super(Component.translatable("POST IT NOTE TEXT"));
        this.note = note;
        this.messages = (String[])IntStream.range(0, 4).mapToObj((p_277214_) -> {
            return this.text.getMessage(p_277214_, false);
        }).map(Component::getString).toArray((x$0) -> {
            return new String[x$0];
        });
    }

    protected void init() {
        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_251194_) -> {
            this.onDone();
        }).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());
        this.signField = new TextFieldHelper(() -> {
            return this.messages[this.line];
        }, this::setMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (p_280850_) -> {
            return this.minecraft.font.width(p_280850_) <= this.note.getMaxTextLineWidth();
        });
    }

    public void tick() {
        ++this.frame;
        if (!this.isValid()) {
            this.onDone();
        }

    }

    private boolean isValid() {
        return this.minecraft != null && this.minecraft.player != null && !this.note.isRemoved() && !this.note.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
    }

    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 265) {
            this.line = this.line - 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        } else if (keyCode != 264 && keyCode != 257 && keyCode != 335) {
            return this.signField.keyPressed(keyCode) ? true : super.keyPressed(keyCode, scanCode, modifiers);
        } else {
            this.line = this.line + 1 & 3;
            this.signField.setCursorToEnd();
            return true;
        }
    }

    public boolean charTyped(char codePoint, int modifiers) {
        this.signField.charTyped(codePoint);
        return true;
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        Lighting.setupForFlatItems();
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
        this.renderSign(guiGraphics);
        Lighting.setupFor3DItems();
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        this.renderTransparentBackground(guiGraphics);
    }

    public void onClose() {
        this.onDone();
    }

    public void removed() {
        ClientPacketListener clientpacketlistener = this.minecraft.getConnection();
        if (clientpacketlistener != null) {
            clientpacketlistener.send(new ServerboundSignUpdatePacket(this.note.getBlockPos(), true, this.messages[0], this.messages[1], this.messages[2], this.messages[3]));
        }

    }

    public boolean isPauseScreen() {
        return false;
    }

    protected void renderSignBackground(GuiGraphics var1, BlockState var2){

    }

    protected Vector3f getSignTextScale(){

        return null;
    }

    protected void offsetSign(GuiGraphics guiGraphics, BlockState state) {
        guiGraphics.pose().translate((float)this.width / 2.0F, 90.0F, 50.0F);
    }

    private void renderSign(GuiGraphics guiGraphics) {
//        BlockState blockstate = this.note.getBlockState();
//        guiGraphics.pose().pushPose();
//        this.offsetSign(guiGraphics, blockstate);
//        guiGraphics.pose().pushPose();
//        this.renderSignBackground(guiGraphics, blockstate);
//        guiGraphics.pose().popPose();
        this.renderSignText(guiGraphics);
        guiGraphics.pose().popPose();
    }

    private void renderSignText(GuiGraphics guiGraphics) {
        guiGraphics.pose().translate(0.0F, 0.0F, 4.0F);
        Vector3f vector3f = this.getSignTextScale();
        guiGraphics.pose().scale(vector3f.x(), vector3f.y(), vector3f.z());
        int i = this.text.hasGlowingText() ? this.text.getColor().getTextColor() : SignRenderer.getDarkColor(this.text);
        boolean flag = this.frame / 6 % 2 == 0;
        int j = this.signField.getCursorPos();
        int k = this.signField.getSelectionPos();
        int l = 4 * this.note.getTextLineHeight() / 2;
        int i1 = this.line * this.note.getTextLineHeight() - l;

        int j1;
        String s1;
        int l3;
        int i4;
        int j4;
        for(j1 = 0; j1 < this.messages.length; ++j1) {
            s1 = this.messages[j1];
            if (s1 != null) {
                if (this.font.isBidirectional()) {
                    s1 = this.font.bidirectionalShaping(s1);
                }

                l3 = -this.font.width(s1) / 2;
                guiGraphics.drawString(this.font, s1, l3, j1 * this.note.getTextLineHeight() - l, i, false);
                if (j1 == this.line && j >= 0 && flag) {
                    i4 = this.font.width(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
                    j4 = i4 - this.font.width(s1) / 2;
                    if (j >= s1.length()) {
                        guiGraphics.drawString(this.font, "_", j4, i1, i, false);
                    }
                }
            }
        }

        for(j1 = 0; j1 < this.messages.length; ++j1) {
            s1 = this.messages[j1];
            if (s1 != null && j1 == this.line && j >= 0) {
                l3 = this.font.width(s1.substring(0, Math.max(Math.min(j, s1.length()), 0)));
                i4 = l3 - this.font.width(s1) / 2;
                if (flag && j < s1.length()) {
                    guiGraphics.fill(i4, i1 - 1, i4 + 1, i1 + this.note.getTextLineHeight(), -16777216 | i);
                }

                if (k != j) {
                    j4 = Math.min(j, k);
                    int j2 = Math.max(j, k);
                    int k2 = this.font.width(s1.substring(0, j4)) - this.font.width(s1) / 2;
                    int l2 = this.font.width(s1.substring(0, j2)) - this.font.width(s1) / 2;
                    int i3 = Math.min(k2, l2);
                    int j3 = Math.max(k2, l2);
                    guiGraphics.fill(RenderType.guiTextHighlight(), i3, i1, j3, i1 + this.note.getTextLineHeight(), -16776961);
                }
            }
        }

    }

    private void setMessage(String message) {
        this.messages[this.line] = message;
        this.text = this.text.setMessage(this.line, Component.literal(message));
        this.note.setText(this.text);
    }

    private void onDone() {
        this.minecraft.setScreen((Screen)null);
    }
}
