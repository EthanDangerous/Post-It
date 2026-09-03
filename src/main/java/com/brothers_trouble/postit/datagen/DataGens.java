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
import net.neoforged.neoforge.common.conditions.ModLoadedCondition;
import net.neoforged.neoforge.data.event.GatherDataEvent;

import java.util.concurrent.CompletableFuture;

@EventBusSubscriber(modid = PostIt.MODID)
public class DataGens {

    private static final ResourceKey<Scribble> MALUM =
            key("malum");
    private static final ResourceKey<Scribble> AD_ASTRA =
            key("ad_astra");
    private static final ResourceKey<Scribble> PASTEL =
            key("pastel");
    private static final ResourceKey<Scribble> CREATE =
            key("create");
    private static final ResourceKey<Scribble> HALCYON =
            key("halcyon");
    private static final ResourceKey<Scribble> AERONAUTICS =
            key("aeronautics");
    private static final ResourceKey<Scribble> OFFROAD =
            key("offroad");
    private static final ResourceKey<Scribble> AETHER =
            key("aether");

    private static ResourceKey<Scribble> key(String path) {
        return ResourceKey.create(Scribble.REGISTRY_KEY, ResourceLocation.fromNamespaceAndPath(PostIt.MODID, path));
    }

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
                    context.register(MALUM,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/malum"), Scribble.Size.SMALL, Weight.of(2)));
                    context.register(AD_ASTRA,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/ad_astra"), Scribble.Size.SMALL, Weight.of(2)));
                    context.register(PASTEL,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/pastel"), Scribble.Size.MEDIUM, Weight.of(2)));
                    context.register(CREATE,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/create"), Scribble.Size.MEDIUM, Weight.of(2)));
                    context.register(HALCYON,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/halcyon"), Scribble.Size.LARGE, Weight.of(2)));
                    context.register(AERONAUTICS,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/aeronautics"), Scribble.Size.LARGE, Weight.of(2)));
                    context.register(OFFROAD,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/offroad"), Scribble.Size.LARGE, Weight.of(2)));
                    context.register(AETHER,
                            new Scribble(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/aether"), Scribble.Size.LARGE, Weight.of(2)));
                }),
                // this is where we could specify what mod needs to be installed for the scribble to be registered
                conditions -> {
                    conditions.accept(MALUM, new ModLoadedCondition("malum"));
                    conditions.accept(AD_ASTRA, new ModLoadedCondition("ad_astra"));
                    conditions.accept(PASTEL, new ModLoadedCondition("pastel"));
                    conditions.accept(CREATE, new ModLoadedCondition("create"));
                    conditions.accept(HALCYON, new ModLoadedCondition("halcyon"));
                    conditions.accept(AERONAUTICS, new ModLoadedCondition("aeronautics"));
                    conditions.accept(OFFROAD, new ModLoadedCondition("offroad"));
                    conditions.accept(AETHER, new ModLoadedCondition("aether"));
                });

        CompletableFuture<HolderLookup.Provider> lookupProvider = event.getLookupProvider();
    }
}
