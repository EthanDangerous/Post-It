package com.brothers_trouble.registration;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.item.PostItItem;
import net.minecraft.core.component.DataComponentType;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.ComponentSerialization;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PostIt.MODID);

    public static final DeferredRegister<DataComponentType<?>> COMPONENTS = DeferredRegister.create(Registries.DATA_COMPONENT_TYPE, PostIt.MODID);



    public static final DeferredHolder<Item, Item> POST_IT_NOTE = ITEMS.register("post_it_note",
            () -> new PostItItem(new Item.Properties()));

    public static final DeferredHolder<DataComponentType<?>, DataComponentType<Component>> NOTE = COMPONENTS.register("note",
            () -> DataComponentType.<Component>builder().persistent(ComponentSerialization.FLAT_CODEC).networkSynchronized(ComponentSerialization.STREAM_CODEC).build());

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
        COMPONENTS.register(eventBus);
    }
}
