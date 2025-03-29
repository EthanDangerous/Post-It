package com.brothers_trouble.menu;

import com.brothers_trouble.PostIt;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;

public class PostItMenu extends Screen {
    private static final Component TITLE = Component.translatable("gui.advancements");
    private final HeaderAndFooterLayout layout;

    public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/gui/note/example_container.png");

    public PostItMenu(Component title) {
        super(title);
        this.layout = new HeaderAndFooterLayout(this);

//        setBackgroundTexture();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
        int i = (this.width - 252) / 2;
        int j = (this.height - 140) / 2;
//        Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
//        int width = this.font.width(page);
//        guiGraphics.drawString(this.font, page.getVisualOrderText(), i + 126 - width / 2, j - 44, -1);
        guiGraphics.blit(BACKGROUND_TEXTURE, 0, 0, 0, 0, 100, 100);
    }

//    public void setBackgroundTexture(){
//        super.MENU_BACKGROUND = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/entity/post_it_note.png");
//    }
}
