package com.brothers_trouble.postit.registration;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.*;
import com.brothers_trouble.postit.entity.entity_render.*;
import com.brothers_trouble.postit.model.PostItModel;
import net.minecraft.resources.*;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.EntityRenderersEvent;
import software.bernie.geckolib.model.*;

@EventBusSubscriber(modid = PostIt.MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class ModelRegistry {
    
    public static final GeoModel<PostItEntity> POST_IT_NOTE_MODEL = new DefaultedEntityGeoModel<>(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "post_it_note"));
    
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
