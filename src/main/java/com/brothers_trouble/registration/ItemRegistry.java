package com.brothers_trouble.registration;

import com.brothers_trouble.PostIt;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ItemRegistry {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(PostIt.MODID);

    public static final DeferredItem<Item> POST_IT_NOTE = ITEMS.registerItem(
            "post_it_note",
            Item::new, // The factory that the properties will be passed into.
            new Item.Properties() // The properties to use.
    );

    public static void register(IEventBus eventBus){
        ITEMS.register(eventBus);
    }
}
