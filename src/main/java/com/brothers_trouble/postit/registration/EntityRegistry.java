package com.brothers_trouble.postit.registration;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PostItEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.syncher.EntityDataSerializer;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.block.entity.SignText;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.NotNull;

public class EntityRegistry {
    public static final StreamCodec<ByteBuf, SignText> NOTE_TEXT_STREAM_CODEC = ByteBufCodecs.fromCodec(SignText.DIRECT_CODEC);

    public static final DeferredRegister<EntityType<?>> ENTITY_TYPES =
            DeferredRegister.create(BuiltInRegistries.ENTITY_TYPE, PostIt.MODID);

    public static final DeferredRegister<EntityDataSerializer<?>> DATA_SERIALIZERS =
            DeferredRegister.create(NeoForgeRegistries.ENTITY_DATA_SERIALIZERS, PostIt.MODID);

    public static final DeferredHolder<EntityType<?>, EntityType<PostItEntity>> POST_IT_NOTE_ENTITY =
            ENTITY_TYPES.register("post_it_note",
                    () -> EntityType.Builder.<PostItEntity>of(PostItEntity::new, MobCategory.MISC) // Change category if needed
                            .sized(0.25F, 0.1F)
                            .build("post_it_note"));

    public static final EntityDataSerializer<SignText> NOTE_TEXT_DATA_SERIALIZER =  new EntityDataSerializer<>() {
        @Override
        public @NotNull StreamCodec<? super RegistryFriendlyByteBuf, SignText> codec() {
            return NOTE_TEXT_STREAM_CODEC;
        }

        public @NotNull SignText copy(@NotNull SignText text) {
            return new SignText(text.getMessages(false), text.getMessages(true), text.getColor(), text.hasGlowingText());
        }
    };

    public static final DeferredHolder<EntityDataSerializer<?>, EntityDataSerializer<SignText>> NOTE_TEXT_DATA =
            DATA_SERIALIZERS.register("note_text", () -> NOTE_TEXT_DATA_SERIALIZER);

    public static void register(IEventBus eventBus) {
        DATA_SERIALIZERS.register(eventBus);
        ENTITY_TYPES    .register(eventBus);
    }
}
