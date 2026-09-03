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
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "swirl")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/swirl"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "flower")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/flower"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "planet")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/planet"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "blocks")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/blocks"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "sparkles")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/sparkles"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "eye")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/eye"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "ghast_1")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/ghast_1"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "ghast_2")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/ghast_2"), Scribble.Size.SMALL, Weight.of(1)));

                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "stick_steve")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/stick_steve"), Scribble.Size.MEDIUM, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "wheat")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/wheat"), Scribble.Size.MEDIUM, Weight.of(1)));

                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "failure")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/failure"), Scribble.Size.LARGE, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "skyblock")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/skyblock"), Scribble.Size.LARGE, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "ruined_portal")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/ruined_portal"), Scribble.Size.LARGE, Weight.of(1)));

                    // empty scribbles
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "empty_small")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/empty_small"), Scribble.Size.SMALL, Weight.of(10)));

                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "empty_medium_1")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/empty_medium_1"), Scribble.Size.MEDIUM, Weight.of(5)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "empty_medium_2")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/empty_medium_2"), Scribble.Size.MEDIUM, Weight.of(5)));

                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "empty_large_1")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/empty_large_1"), Scribble.Size.LARGE, Weight.of(5)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "empty_large_2")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/empty_large_2"), Scribble.Size.LARGE, Weight.of(5)));


                    // modded scribbles:
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "malum")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/malum"), Scribble.Size.SMALL, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "ad_astra")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/ad_astra"), Scribble.Size.SMALL, Weight.of(1)));

                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "pastel")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/pastel"), Scribble.Size.MEDIUM, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "create")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/create"), Scribble.Size.MEDIUM, Weight.of(1)));

                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "halcyon")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/halcyon"), Scribble.Size.LARGE, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "aeronautics")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/aeronautics"), Scribble.Size.LARGE, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "offroad")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/offroad"), Scribble.Size.LARGE, Weight.of(1)));
                    context.register(
                            ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "aether")),
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/aether"), Scribble.Size.LARGE, Weight.of(1)));
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
