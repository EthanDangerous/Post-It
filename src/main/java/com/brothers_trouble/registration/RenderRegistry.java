package com.brothers_trouble.registration;

import net.minecraft.client.renderer.entity.EntityRenderers;
import com.brothers_trouble.entity.entity_render.PostItRender;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;

public class RenderRegistry {
    public static void registerRenderers(final FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            EntityRenderers.register(EntityRegistry.POST_IT_NOTE_ENTITY.get(), PostItRender::new);
        });
    }

    public static void register(IEventBus eventBus) {
        eventBus.addListener(RenderRegistry::registerRenderers);
    }
}
