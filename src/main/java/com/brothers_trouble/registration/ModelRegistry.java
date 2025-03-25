package com.brothers_trouble.registration;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.model.PostItModel;
import com.brothers_trouble.entity.entity_render.PostItRender;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;

@EventBusSubscriber(modid = PostIt.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelRegistry {
//    public static final ModelLayerLocation POST_IT_MODEL_LAYER =
//            new ModelLayerLocation(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "post_it_note"), "main");

    @SubscribeEvent
    public static void registerLayerDefinitions(EntityRenderersEvent.RegisterLayerDefinitions event) {
        event.registerLayerDefinition(PostItModel.LAYER_LOCATION, PostItModel::createBodyLayer);
    }

    @SubscribeEvent
    public static void registerEntityRenderers(EntityRenderersEvent.RegisterRenderers event) {
        event.registerEntityRenderer(EntityRegistry.POST_IT_NOTE_ENTITY.get(), PostItRender::new);
    }

    @SubscribeEvent
    public static void onClientSetup(FMLClientSetupEvent event) {
        event.enqueueWork(() -> {
            // Any additional client setup can go here if needed
//            ClientHooks.registerLayerDefinition(PostItRender.modelLocation, PostItRender.Model::createLayer);
        });
    }

    public static void register(IEventBus eventBus) {
        eventBus.register(ModelRegistry.class);
    }
}
