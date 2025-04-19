package com.brothers_trouble.postit.event;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.registration.ItemRegistry;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

@EventBusSubscriber(modid = PostIt.MODID, bus = EventBusSubscriber.Bus.MOD)
public class ModEventBusEvents {
    @SubscribeEvent
    public static void registerItemColors(RegisterColorHandlersEvent.Item event){
        event.register(ResourceLocation.fromNamespaceAndPath("postit", "post_it_note"), );
    }
}
