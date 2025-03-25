package com.brothers_trouble.menu;

import com.mojang.blaze3d.vertex.VertexConsumer;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.AbstractSignEditScreen;
import net.minecraft.client.renderer.Sheets;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.client.renderer.texture.OverlayTexture;
import net.minecraft.client.resources.model.Material;
import net.minecraft.world.level.block.StandingSignBlock;
import net.minecraft.world.level.block.entity.SignBlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import org.joml.Vector3f;

public class PostItMenu extends AbstractSignEditScreen {

    private SignRenderer.SignModel signModel;
    private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);

    public PostItMenu(SignBlockEntity sign, boolean isFrontText, boolean isFiltered) {
        super(sign, isFrontText, isFiltered);
    }

    @Override
    protected void init() {
        super.init();
        this.signModel = SignRenderer.createSignModel(this.minecraft.getEntityModels(), this.woodType);
    }

    @Override
    protected void renderSignBackground(GuiGraphics guiGraphics, BlockState blockState) {
        if (this.signModel != null) {
            boolean flag = blockState.getBlock() instanceof StandingSignBlock;
            guiGraphics.pose().translate(0.0F, 31.0F, 0.0F);
            guiGraphics.pose().scale(62.500004F, 62.500004F, -62.500004F);
            Material material = Sheets.getSignMaterial(this.woodType);
            VertexConsumer vertexconsumer = material.buffer(guiGraphics.bufferSource(), this.signModel::renderType);
            this.signModel.stick.visible = flag;
            this.signModel.root.render(guiGraphics.pose(), vertexconsumer, 15728880, OverlayTexture.NO_OVERLAY);
        }

    }

    @Override
    protected Vector3f getSignTextScale() {
        return TEXT_SCALE;
    }
}
