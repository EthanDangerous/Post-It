//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package com.brothers_trouble.menu.screen;

import com.brothers_trouble.entity.PostItEntity;
import com.brothers_trouble.menu.widget.CloseWidget;
import com.mojang.blaze3d.platform.Lighting;
import java.util.stream.IntStream;
import javax.annotation.Nullable;

import net.minecraft.client.Minecraft;
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
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import org.joml.Vector3f;

import static com.brothers_trouble.menu.PostItMenu.BACKGROUND_TEXTURE;

@OnlyIn(Dist.CLIENT)
public class PostItScreen extends Screen {
    //this is the actual entity you are clicking
    private final PostItEntity note;

    //this is the widget to close the GUI
    private CloseWidget closeWidget;

    //this is the SignText object that actually contains our text
    private SignText text;

    //this has the list of all of the messages that are being made
    private final String[] messages;

    private int frame;
    private int line;

    //this should help out with the text fields?
    @Nullable
    private TextFieldHelper signField;

    //this is the scale of the text
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);

    //constructor, for when it is first created
    public PostItScreen(PostItEntity note, SignText text) {
        //creates the title of the GUI
        super(Component.translatable("POST IT NOTE TEXT"));
        //sets note to the entity being selected
        this.note = note;
        //should get the SignText from the note entity
        this.text = text;
        if(this.text == null){
            System.out.println("\n\nTHE getText() ISNT WORKING\n\n");
        }
//        this.text = new SignText();

        //this should create the messages list with this thing \/ no clue how it works but it should work
        //text.getMessage is being problematic here
        this.messages = IntStream.range(0, 4).mapToObj((p_277214_) -> this.text.getMessage(p_277214_, false)).map(Component::getString).toArray((x$0) -> new String[x$0]);
    }

    //when the GUI is actually initialized (is it each time its opened?)
    protected void init() {
        //adds a done button
//        this.addRenderableWidget(Button.builder(CommonComponents.GUI_DONE, (p_251194_) -> {
//            this.onDone();
//        }).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build());

        //this is for the close button
        this.closeWidget = new CloseWidget((this.width+144)/2, (this.height-176)/2, 16, 16);
        this.addRenderableWidget(closeWidget);

        //this should create the text field and get the players clipboard?
        this.signField = new TextFieldHelper(() -> this.messages[this.line], this::setMessage, TextFieldHelper.createClipboardGetter(this.minecraft), TextFieldHelper.createClipboardSetter(this.minecraft), (p_280850_) -> this.minecraft.font.width(p_280850_) <= this.note.getMaxTextLineWidth());
    }

    //this is just for the blinking cursor
    public void tick() {
        ++this.frame;
        if (!this.isValid()) {
//            this.onDone();
        }
    }

    //check this later, not right now.
    private boolean isValid() {
//        return this.minecraft != null && this.minecraft.player != null && !this.note.isRemoved() && !this.note.playerIsTooFarAwayToEdit(this.minecraft.player.getUUID());
        return true;
    }

    //whenever a player presses any button
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

    //when a player types a character
    public boolean charTyped(char codePoint, int modifiers) {
        //checks if its a valid character, puts it in the cursor position
        this.signField.charTyped(codePoint);
        return true;
    }

    //this is the render for the GUI
    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        Lighting.setupForFlatItems();
        guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
        this.renderSign(guiGraphics);
        Lighting.setupFor3DItems();
    }

    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        this.renderTransparentBackground(guiGraphics);
        this.renderBlurredBackground(partialTick);
        this.renderMenuBackground(guiGraphics);
        guiGraphics.blit(BACKGROUND_TEXTURE, (this.width - 160)/2, (this.height - 160)/2, 0, 0, 160, 160);
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

        return TEXT_SCALE;
    }

    protected void offsetSign(GuiGraphics guiGraphics, BlockState state) {
        guiGraphics.pose().translate((float)this.width / 2.0F, 90.0F, 50.0F);
    }

    //this should render eveything needed in the sign
    private void renderSign(GuiGraphics guiGraphics) {
        //these are commented out because they take from the blockstate of the sign. later it would be good to use this to get the colors of the notes
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
        if(Minecraft.getInstance() != null){
            Minecraft.getInstance().setScreen(null);
        }
    }
}
