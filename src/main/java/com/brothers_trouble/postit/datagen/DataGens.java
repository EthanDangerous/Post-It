package com.brothers_trouble.postit.datagen;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.menu.Scribble;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.DataGenerator;
import net.minecraft.data.PackOutput;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.random.Weight;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = PostIt.MODID)
public class DataGens {
    @SubscribeEvent
    public static void gatherData(GatherDataEvent event) {
        DataGenerator generator = event.getGenerator();
        PackOutput packOutput = generator.getPackOutput();
//        ExistingFileHelper existingFileHelper = event.getExistingFileHelper();

        event.createDatapackRegistryObjects(new RegistrySetBuilder()
                .add(Scribble.REGISTRY_KEY, (context)->{
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "hearts")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/hearts"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "dint")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/dint"), Scribble.Size.SMALL, Weight.of(1)));
                })
        );

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();

//        generator.addProvider(event.includeServer(), new LootTableProvider(packOutput, Collections.emptySet(),
//                List.of(new LootTableProvider.SubProviderEntry(ModBlockLootTableProvider::new, LootContextParamSets.BLOCK)), lookupProvider));
//        generator.addProvider(event.includeServer(), new RecipeGeneration(packOutput, lookupProvider));
//
//        BlockTagsProvider blockTagsProvider = new ModBlockTagProvider(packOutput, lookupProvider, existingFileHelper);
//        generator.addProvider(event.includeServer(), blockTagsProvider);
//        generator.addProvider(event.includeServer(), new ModItemTagProvider(packOutput, lookupProvider, blockTagsProvider.contentsGetter(), existingFileHelper));
//
//        generator.addProvider(event.includeServer(), new ModDataMapProvider(packOutput, lookupProvider));
//
//
//        generator.addProvider(event.includeClient(), new ModItemModelProvider(packOutput, existingFileHelper));
//        generator.addProvider(event.includeClient(), new ModBlockStateProvider(packOutput, existingFileHelper));
    }
}
