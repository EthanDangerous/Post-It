package com.brothers_trouble.postit.entity;

import com.brothers_trouble.postit.menu.screen.PostItScreen;
import com.brothers_trouble.postit.registration.ItemRegistry;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import com.mojang.logging.LogUtils;
import com.mojang.serialization.DataResult;
import com.mojang.serialization.DynamicOps;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.core.HolderLookup;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.NbtOps;
import net.minecraft.nbt.Tag;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentUtils;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.SignText;
import net.minecraft.world.phys.AABB;
import org.jetbrains.annotations.NotNull;
import org.slf4j.Logger;

import java.util.Objects;
import java.util.UUID;
import java.util.function.Consumer;

public class PostItEntity extends Entity {
    public static final EntityDataAccessor<Integer> DATA_SIDE = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.INT);
    public static final EntityDataAccessor<Direction> DATA_HORIZ = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.DIRECTION);
//    public static final EntityDataAccessor<Component> TEXT_DATA = SynchedEntityData.defineId(PostItEntity.class, EntityDataSerializers.COMPONENT);
    private static final Logger LOGGER = LogUtils.getLogger();
    private static final int MAX_TEXT_LINE_WIDTH = 90;
    private static final int TEXT_LINE_HEIGHT = 10;
    private Component textComponent = null;
    public ItemStack noteItem;
    private Player player;
    private Level level;
    private static SignText text;

    public PostItEntity(EntityType<? extends PostItEntity> entityType, Level level, Direction face, Direction facing, ItemStack item, Player player) {
        super(entityType, level);
        this.noteItem = item;
        this.player = player;
        this.level = level;
        this.textComponent = item.get(ItemRegistry.NOTE);
        this.text = new SignText();
        if(face != null){
            this.getEntityData().set(DATA_SIDE, face.get3DDataValue());
        }
        if(facing != null){
            this.getEntityData().set(DATA_HORIZ, facing);
        }
        makeBoundingBox();
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
        return 120;
    }

    public int getTextLineHeight() {
        return 12;
    }

    public boolean playerIsTooFarAwayToEdit(UUID uuid) {
        Player player = this.level.getPlayerByUUID(uuid);
        return player == null || !player.canInteractWithBlock(this.getBlockPos(), 4.0);
    }

    public BlockPos getBlockPos(){
        return this.getOnPos();
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
        if(Minecraft.getInstance() != null && level().isClientSide()){
            Minecraft.getInstance().setScreen(new PostItScreen(this, this.text));
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

    protected void saveAdditional(CompoundTag tag, HolderLookup.Provider registries) {
//        super.addAdditionalSaveData(tag);
        DynamicOps<Tag> dynamicops = registries.createSerializationContext(NbtOps.INSTANCE);
        DataResult var10000 = SignText.DIRECT_CODEC.encodeStart(dynamicops, this.text);
        Logger var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial((Consumer<String>) var10001).ifPresent((p_277417_) -> {
            tag.put("text", (Tag) p_277417_);
        });
    }

    protected void loadAdditional(CompoundTag tag, HolderLookup.Provider registries) {
//        super.readAdditionalSaveData(tag);
        DynamicOps<Tag> dynamicops = registries.createSerializationContext(NbtOps.INSTANCE);
        DataResult var10000;
        Logger var10001;
        var10000 = SignText.DIRECT_CODEC.parse(dynamicops, tag.getCompound("front_text"));
        var10001 = LOGGER;
        Objects.requireNonNull(var10001);
        var10000.resultOrPartial((Consumer<String>) var10001).ifPresent((p_278212_) -> {
            this.text = this.loadLines((SignText) p_278212_);
        });
    }

    private SignText loadLines(SignText text) {
        for(int i = 0; i < 4; ++i) {
            Component component = this.loadLine(text.getMessage(i, false));
            Component component1 = this.loadLine(text.getMessage(i, true));
            text = text.setMessage(i, component, component1);
        }

        return text;
    }

    private Component loadLine(Component lineText) {
        Level var3 = this.level;
        if (var3 instanceof ServerLevel serverlevel) {
            try {
                return ComponentUtils.updateForEntity(createCommandSourceStack(), lineText, (Entity)null, 0);
            } catch (CommandSyntaxException var4) {
            }
        }

        return lineText;
    }

//    @Override
//    public @Nullable AbstractContainerMenu createMenu(int i, Inventory inventory, Player player) {
//        return new PostItMenu(i, inventory, this);
//    }
}
