package com.brothers_trouble.postit.registration;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.item.PostItItem;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.entity.SignText;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.CommonHooks;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PostIt.MODID);

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, PostIt.MODID);

    public static final DeferredHolder<Item, Item> POST_IT_NOTE = ITEMS.register("post_it_note",
            () -> new PostItItem(new Item.Properties()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<SignText>> NOTE_TEXT_COMPONENT = COMPONENTS.register("note_text",
            () -> DataComponentType.<SignText>builder().persistent(SignText.DIRECT_CODEC).networkSynchronized(PacketRegistry.NOTE_TEXT_STREAM_CODEC).build());

    public static void register(IEventBus eventBus){
        CommonHooks.markComponentClassAsValid(SignText.class);

        ITEMS.register(eventBus);
        COMPONENTS.register(eventBus);
    }
}
