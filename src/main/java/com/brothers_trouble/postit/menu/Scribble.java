package com.brothers_trouble.postit.menu;

import com.brothers_trouble.postit.PostIt;
import com.mojang.serialization.Codec;
import com.mojang.serialization.codecs.RecordCodecBuilder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.StringRepresentable;
import net.minecraft.util.random.Weight;
import net.minecraft.util.random.WeightedEntry;

import java.util.Locale;

public record Scribble(ResourceLocation sprite, Size size, Weight weight) implements WeightedEntry {

    public static final ResourceKey<Registry<Scribble>> REGISTRY_KEY = ResourceKey.createRegistryKey(ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribble"));

    public static final Codec<Scribble> CODEC = RecordCodecBuilder.create((instance)->
            instance.group(
                    ResourceLocation.CODEC.fieldOf("sprite").forGetter(Scribble::sprite),
                    Size.CODEC.fieldOf("size").forGetter(Scribble::size),
                    Weight.CODEC.fieldOf("weight").forGetter(Scribble::weight)
            ).apply(instance, Scribble::new)
    );

    @Override
    public Weight getWeight() {
        return weight;
    }

    public enum Size implements StringRepresentable {
        SMALL,
        MEDIUM,
        LARGE;

        public static final Codec<Size> CODEC = StringRepresentable.fromValues(Size::values);

        @Override
        public String getSerializedName() {
            return name().toLowerCase(Locale.ROOT);
        }
    }
}
