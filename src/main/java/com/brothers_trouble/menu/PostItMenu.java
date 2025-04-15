package com.brothers_trouble.menu;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.menu.widget.CloseWidget;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.FocusableTextWidget;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.Level;


public class PostItMenu extends Screen {
    private static final Component TITLE = Component.translatable("Post it note");
    private final Component TEXT = Component.translatable("TEXT HERE");
    private final HeaderAndFooterLayout layout;
    private Level level;
    private CloseWidget closeWidget;
    private Component textComponent = null;
//    private TextWidget textWidget;
    private FocusableTextWidget textWidget;
//    private WritableBookContent content = WritableBookContent.EMPTY;

    private TextFieldHelper pageEdit  = new TextFieldHelper(this::getCurrentPageText, this::setCurrentPageText, this::getClipboard, this::setClipboard, (p_280853_) -> {
        return p_280853_.length() < 1024 && this.font.wordWrapHeight(p_280853_, 114) <= 128;
    });

    public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/gui/note/example_container.png");

    public PostItMenu(Component component) {
        super(Component.empty());
        this.textComponent = component;
        this.layout = new HeaderAndFooterLayout(this);
    }

    @Override
    public void init(){
        this.closeWidget = new CloseWidget((this.width+144)/2, (this.height-176)/2, 16, 16);
//        this.textWidget = new TextWidget((this.width+144)/2, (this.height-176)/2, 16, 16, TEXT, this.font);
        this.addRenderableWidget(closeWidget);
//        this.addRenderableWidget(textWidget);
    }

    @Override
    public void renderBackground(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick){
        this.renderBlurredBackground(partialTick);
        this.renderMenuBackground(guiGraphics);
        guiGraphics.blit(BACKGROUND_TEXTURE, (this.width - 160)/2, (this.height - 160)/2, 0, 0, 160, 160);
    }

    public void render(GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
//        renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        this.renderBackground(guiGraphics, mouseX, mouseY, partialTick);
        super.render(guiGraphics, mouseX, mouseY, partialTick);
//        closeWidget.renderWidget(guiGraphics, 10, 10);
    }

    private void setClipboard(String clipboardValue) {
        if (this.minecraft != null) {
            TextFieldHelper.setClipboardContents(this.minecraft, clipboardValue);
        }

    }

    private String getClipboard() {
        return this.minecraft != null ? TextFieldHelper.getClipboardContents(this.minecraft) : "";
    }

    private String getCurrentPageText() {
        return "";
    }

    private void setCurrentPageText(String text) {

    }
}
//package com.brothers_trouble.menu;
//
//import com.brothers_trouble.PostIt;
//import com.brothers_trouble.entity.PostItEntity;
//import com.brothers_trouble.registration.GUIRegistry;
//import net.minecraft.core.BlockPos;
//import net.minecraft.network.RegistryFriendlyByteBuf;
//import net.minecraft.network.chat.Component;
//import net.minecraft.resources.ResourceLocation;
//import net.minecraft.world.MenuProvider;
//import net.minecraft.world.SimpleMenuProvider;
//import net.minecraft.world.entity.Entity;
//import net.minecraft.world.entity.player.Inventory;
//import net.minecraft.world.entity.player.Player;
//import net.minecraft.world.inventory.AbstractContainerMenu;
//import net.minecraft.world.item.ItemStack;
//import net.minecraft.world.level.Level;
//
//import java.util.Optional;
//
//public class PostItMenu extends AbstractContainerMenu {
//    private static final Component TITLE = Component.translatable("Post it note");
//    private Level level;
//
//    public static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/gui/note/example_container.png");
//
//    public PostItMenu(int containerID, Inventory inv, RegistryFriendlyByteBuf data) {
//        super(GUIRegistry.POST_IT_MENU.get(), containerID);
//    }
//
////    public PostItMenu(int containerID, Inventory inv, Entity entity) {
////        super(GUIRegistry.POST_IT_MENU.get(), containerID);
////    }
//
//    public static MenuProvider getServerMenuProvider(PostItEntity entity)
//    {
//        return new SimpleMenuProvider((id, playerInventory, serverPlayer) -> new PostItMenu(id, playerInventory, entity), TITLE);
//    }
//
//    @Override
//    public ItemStack quickMoveStack(Player player, int i) {
//        return null;
//    }
//
//    @Override
//    public boolean stillValid(Player player) {
//        return false;
//    }
//}
