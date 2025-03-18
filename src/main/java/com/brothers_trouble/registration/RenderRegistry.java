package com.brothers_trouble.registration;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.entity.PostItEntity;
import net.minecraft.client.renderer.entity.EntityRenderers;
import com.brothers_trouble.entity_render.PostItRender;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

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
