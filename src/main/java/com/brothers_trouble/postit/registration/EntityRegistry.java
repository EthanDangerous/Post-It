package com.brothers_trouble.postit.registration;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PostItEntity;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class EntityRegistry {
    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PostIt.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PostItEntity>> POST_IT_NOTE_ENTITY =
            ENTITY_TYPES.register("post_it_note",
                    () -> EntityType.Builder.<PostItEntity>of(PostItEntity::new, MobCategory.MISC) // Change category if needed
                            .sized(0.25F, 0.1F)
                            .build("post_it_note"));

    public static void register(IEventBus eventBus) {
        ENTITY_TYPES.register(eventBus);
    }
}
