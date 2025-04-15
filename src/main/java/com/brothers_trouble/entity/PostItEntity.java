package com.brothers_trouble.entity;

import com.brothers_trouble.menu.PostItMenu;
import com.brothers_trouble.menu.screen.PostItScreen;
import com.brothers_trouble.registration.GUIRegistry;
import com.brothers_trouble.registration.ItemRegistry;
import com.mojang.logging.LogUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.SimpleMenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuConstructor;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.AABB;
import org.apache.logging.log4j.core.jmx.Server;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;

import java.util.UUID;

public class PostItEntity extends Entity {
    public static final EntityDataAccessor<Integer> DATA_SIDE = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Direction> DATA_HORIZ = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.DIRECTION);
//    public static final EntityDataAccessor<Component> TEXT_DATA = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.COMPONENT);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TEXT_LINE_WIDTH = 90;
    private static final int TEXT_LINE_HEIGHT = 10;
    private Component textComponent = null;
    private ItemStack noteItem;
    private Player player;
    private Level level;
    private SignText text;
//    private PostItMenu menu;

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, Direction face, Direction facing, ItemStack item, Player player) {
        super(entityType, level);
        this.noteItem = item;
        this.player = player;
        this.level = level;
        this.textComponent = item.get(ItemRegistry.NOTE);
        this.text = new SignText();
//        this.menu = menu;
        if(face != null){
            this.getEntityData().set(DATA_SIDE, face.get3DDataValue());
        }
        if(facing != null){
            this.getEntityData().set(DATA_HORIZ, facing);
        }
        makeBoundingBox();
        System.out.println("EntityDataAccessor: " + this.getEntityData().get(DATA_SIDE));
    }

    public @NotNull InteractionResult interact(Player player, @NotNull InteractionHand hand){
        if(player.isShiftKeyDown()){
            System.out.println("Interaction 1");
//            player.addItem(noteItem);
            this.kill();
            return InteractionResult.SUCCESS;
        }else{
            System.out.println("Interaction 2");
            if(Minecraft.getInstance() != null){
                openScreen();
            }
            return InteractionResult.SUCCESS;
        }
    }

    public SignText getText(){
        return this.text;
    }

    public int getMaxTextLineWidth() {
        return 90;
    }

    public int getTextLineHeight() {
        return 10;
    }

    public boolean playerIsTooFarAwayToEdit(UUID uuid) {
        Player player = this.level.getPlayerByUUID(uuid);
        return player == null || !player.canInteractWithBlock(this.getBlockPos(), 4.0);
    }

    public BlockPos getBlockPos(){
        return this.getBlockPos();
    }

    public boolean setText(SignText text) {
        this.text = text;
        this.markUpdated();
        return true;
    }

    private void markUpdated() {
//        this.setChanged();
//        this.level.sendBlockUpdated(this.getBlockPos(), this.getBlockState(), this.getBlockState(), 3);
    }

    public void openScreen() {
//        player.openMenu(new SimpleMenuProvider((MenuConstructor) menu, Component.translatable("Post it note")));
//        player.openMenu(this);
//        ServerPlayer player1 = (ServerPlayer) player;
//        player1.openMenu(new SimpleMenuProvider(
//                (containerId, playerInventory, player) -> new PostItMenu(containerId, playerInventory),
//                Component.translatable("menu.title.examplemod.mymenu")
//        ));
//        ServerPlayer.openMenu(menuProvider, bytebuf -> {});
        if(Minecraft.getInstance() != null && level().isClientSide()){
//            Minecraft.getInstance().setScreen(new PostItMenu(this.textComponent));
            Minecraft.getInstance().setScreen(new PostItScreen(this, this.textComponent));
        }
    }

    @Override
    protected @NotNull AABB makeBoundingBox() {
        float thickness = 0.05f;
        float length = 0.25f;
        float offset = thickness/2;
        if(getSide() == Direction.UP){
            return AABB.ofSize(position().add(0, offset, 0), length, thickness, length);
        }else if(getSide() == Direction.DOWN){
            return AABB.ofSize(position().subtract(0, offset, 0), length, thickness, length);
        }else if(getSide() == Direction.NORTH){
            return AABB.ofSize(position().subtract(0, 0, offset), length, length, thickness);
        }else if(getSide() == Direction.EAST){
            return AABB.ofSize(position().add(offset, 0, 0), thickness, length, length);
        }else if(getSide() == Direction.SOUTH){
            return AABB.ofSize(position().add(0, 0, offset), length, length, thickness);
        }else if(getSide() == Direction.WEST){
            return AABB.ofSize(position().subtract(offset, 0.0, 0), thickness, length, length);
        }
        return AABB.ofSize(position(), 1.0f, 1.0f, 1.0f);
    }

    public Direction getSide() {
        return Direction.from3DDataValue(this.getEntityData().get(DATA_SIDE));
    }

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level) {
        super(entityType, level);
        this.makeBoundingBox();
    }

    @Override
    public boolean isPickable() {
        return true;
    }

    @Override
    protected void defineSynchedData(SynchedEntityData.Builder builder) {
        builder.define(DATA_SIDE, 0);
        builder.define(DATA_HORIZ, Direction.NORTH);
//        builder.define(TEXT_DATA, textComponent);
    }

    @Override
    protected void readAdditionalSaveData(CompoundTag compoundTag) {
        if (compoundTag.contains("Rotation")) {
            this.getEntityData().set(DATA_SIDE, compoundTag.getInt("Rotation"));
        }
    }

    @Override
    protected void addAdditionalSaveData(CompoundTag compoundTag) {
        compoundTag.putInt("Rotation", this.getEntityData().get(DATA_SIDE));
    }

//    @Override
//    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
//        return new PostItMenu(i, inventory, this);
//    }
}
