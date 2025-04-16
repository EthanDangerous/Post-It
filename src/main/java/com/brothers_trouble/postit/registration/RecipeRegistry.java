package com.brothers_trouble.postit.registration;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.recipe.PageCutting;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.crafting.RecipeType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class RecipeRegistry{
    public static final DeferredRegister<RecipeSerializer<?>> SERIALIZERS =
            DeferredRegister.create(Registries.RECIPE_SERIALIZER, PostIt.MODID);
    public static final DeferredRegister<RecipeType<?>> TYPES =
            DeferredRegister.create(Registries.RECIPE_TYPE, PostIt.MODID);

    public static final DeferredHolder<RecipeType<?>, RecipeType<PageCutting>> PAGE_CUTTING_TYPE =
            TYPES.register("page_cutting", () -> RecipeType.register("page_cutting"));

    public static final DeferredHolder<RecipeSerializer<?>, PageCutting.Serializer> PAGE_CUTTING_SERIALIZER =
            SERIALIZERS.register("page_cutting", PageCutting.Serializer::new);

    public static void register(IEventBus eventBus) {
        System.out.println("REGISTERING CUSTOM RECIPES");
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
