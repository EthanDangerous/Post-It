package com.brothers_trouble.postit.menu.widget;

import com.brothers_trouble.postit.PostIt;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.AbstractWidget;
import net.minecraft.client.gui.narration.NarrationElementOutput;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

import static net.minecraft.util.FastColor.ARGB32.*;

public class ScribbleWidget extends AbstractWidget {

    public static final ResourceLocation TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/gui/note/post_it_gui.png");

    private int color = 0xFFFFFFFF;

    public ScribbleWidget(int x, int y, int width, int height) {
        super(x, y, width, height, Component.empty());
    }

    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public void renderWidget(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        float r = red(this.color);
        float g = green(this.color);
        float b = blue(this.color);

        RenderSystem.setShaderColor(r / 255, g / 255, b / 255, 1.0F);
        guiGraphics.blit(TEXTURE, getX(), getY(), 176, 0, 16, 16);
        RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
    }

    @Override
    protected void updateWidgetNarration(NarrationElementOutput narrationElementOutput) {}
}
