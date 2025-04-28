package com.brothers_trouble.postit.registration;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.recipe.NoteColoring;
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

    public static final DeferredHolder<RecipeType<?>, RecipeType<NoteColoring>> NOTE_COLORING_TYPE =
            TYPES.register("note_coloring", () -> RecipeType.register("note_coloring"));

    public static final DeferredHolder<RecipeSerializer<?>, NoteColoring.Serializer> NOTE_COLORING_SERIALIZER =
            SERIALIZERS.register("note_coloring", NoteColoring.Serializer::new);

    public static void register(IEventBus eventBus) {
        System.out.println("REGISTERING CUSTOM RECIPES");
        SERIALIZERS.register(eventBus);
        TYPES.register(eventBus);
    }
}
