package com.brothers_trouble.menu;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.menu.widget.CloseWidget;
import net.minecraft.client.gui.Font;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.EditBox;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public class PostItMenu extends Screen {
    private static final Component TITLE = Component.translatable("Post it note");
    private final HeaderAndFooterLayout layout;
//    private int i = (this.width - 160) / 2;
//    private int j = (this.height - 160) / 2;
    private Level level;

    public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/gui/note/example_container.png");

    public PostItMenu() {
        super(Component.empty());
//        this.level = level;
        this.layout = new HeaderAndFooterLayout(this);
        this.addRenderableWidget(new CloseWidget((this.width)-16, (this.height)-16, 16, 16));

//        setBackgroundTexture();
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
        super.render(guiGraphics, mouseX, mouseY, partialTick);
//        Component page = Component.literal(String.format("%d / %d", tabPage + 1, maxPages + 1));
//        int width = this.font.width(page);
//        guiGraphics.drawString(this.font, page.getVisualOrderText(), i + 126 - width / 2, j - 44, -1);
        guiGraphics.blit(BACKGROUND_TEXTURE, (this.width - 160)/2, (this.height - 160)/2, 0, 0, 160, 160);

    }

//    public void setBackgroundTexture(){
//        super.MENU_BACKGROUND = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/entity/post_it_note.png");
//    }
}
