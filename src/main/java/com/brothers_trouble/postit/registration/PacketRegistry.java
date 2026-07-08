package com.brothers_trouble.postit.registration;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PostItEntity;
import io.netty.buffer.ByteBuf;
import net.minecraft.core.UUIDUtil;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.entity.SignText;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.network.event.RegisterPayloadHandlersEvent;
import net.neoforged.neoforge.network.handling.IPayloadContext;
import net.neoforged.neoforge.network.registration.PayloadRegistrar;
import org.jetbrains.annotations.NotNull;

import java.util.UUID;

public class PacketRegistry {
	public static final StreamCodec<ByteBuf, SignText> NOTE_TEXT_STREAM_CODEC = ByteBufCodecs.fromCodec(SignText.DIRECT_CODEC);

	private static final StreamCodec<ByteBuf, InteractionHand> HAND_STREAM_CODEC = ByteBufCodecs.BOOL.map(
			offHand -> offHand ? InteractionHand.OFF_HAND : InteractionHand.MAIN_HAND,
			hand -> hand == InteractionHand.OFF_HAND
	);

	public record UpdateNoteTextPacket(UUID noteId, SignText text) implements CustomPacketPayload {
		public static final Type<UpdateNoteTextPacket> TYPE = new Type<>(PostIt.locate("update_note_text"));
		public static final StreamCodec<FriendlyByteBuf, UpdateNoteTextPacket> CODEC = StreamCodec.composite(UUIDUtil.STREAM_CODEC, UpdateNoteTextPacket::noteId, PacketRegistry.NOTE_TEXT_STREAM_CODEC, UpdateNoteTextPacket::text, UpdateNoteTextPacket::new);

		@Override
		public @NotNull Type<UpdateNoteTextPacket> type() {
			return TYPE;
		}

		public static UpdateNoteTextPacket create(PostItEntity note, SignText text) {
			return new UpdateNoteTextPacket(note.getUUID(), text);
		}

		public static void handle(UpdateNoteTextPacket packet, IPayloadContext ctx) {
			final ServerLevel level = (ServerLevel) ctx.player().level();
			level.getServer().execute(() -> {
				if (!(level.getEntity(packet.noteId) instanceof PostItEntity note)) return;
				note.setText(packet.text);
			});
		}
	}

	public record UpdateHeldNoteTextPacket(InteractionHand hand, SignText text) implements CustomPacketPayload {
		public static final Type<UpdateHeldNoteTextPacket> TYPE = new Type<>(PostIt.locate("update_held_note_text"));
		public static final StreamCodec<FriendlyByteBuf, UpdateHeldNoteTextPacket> CODEC = StreamCodec.composite(
				HAND_STREAM_CODEC, UpdateHeldNoteTextPacket::hand,
				PacketRegistry.NOTE_TEXT_STREAM_CODEC, UpdateHeldNoteTextPacket::text,
				UpdateHeldNoteTextPacket::new);

		@Override
		public @NotNull Type<UpdateHeldNoteTextPacket> type() {
			return TYPE;
		}

		public static void handle(UpdateHeldNoteTextPacket packet, IPayloadContext ctx) {
			ctx.player().level().getServer().execute(() -> {
				ItemStack stack = ctx.player().getItemInHand(packet.hand());
				if (stack.is(ItemRegistry.POST_IT_NOTE)) {
					stack.set(ItemRegistry.NOTE_TEXT_COMPONENT, packet.text());
				}
			});
		}
	}

	public static void registerPackets(RegisterPayloadHandlersEvent event) {
		final PayloadRegistrar registrar = event.registrar("1");
		registrar.playToServer(UpdateNoteTextPacket.TYPE, UpdateNoteTextPacket.CODEC, UpdateNoteTextPacket::handle);
		registrar.playToServer(UpdateHeldNoteTextPacket.TYPE, UpdateHeldNoteTextPacket.CODEC, UpdateHeldNoteTextPacket::handle);
	}

	public static void register(IEventBus eventBus) {
		eventBus.addListener(PacketRegistry::registerPackets);
	}
}