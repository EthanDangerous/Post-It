package com.brothers_trouble.postit.menu.screen;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.item.PostItItem;
import com.brothers_trouble.postit.menu.Scribble;
import com.brothers_trouble.postit.menu.widget.CloseWidget;
import com.brothers_trouble.postit.menu.widget.ScribbleWidget;
import com.brothers_trouble.postit.registration.ItemRegistry;
import com.brothers_trouble.postit.registration.PacketRegistry;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.util.RandomSource;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandom;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.DyedItemColor;
import net.minecraft.world.level.block.entity.SignText;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.IntStream;

import static net.minecraft.util.FastColor.ARGB32.*;

@OnlyIn(Dist.CLIENT)
public class NoteScreen extends Screen {
	@Nullable
	private final PostItEntity note;
	@Nullable
	private final InteractionHand hand;

	private final int color;
    private SignText text;
	private final String[] messages;
	private int frame;
	private int line;
	@Nullable
	private TextFieldHelper signField;
	private CloseWidget closeWidget;
	private ScribbleWidget scribble1;

	private final ResourceLocation scribbleTexture;
	private static final ResourceLocation FALLBACK_SCRIBBLE_TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "scribbles/dint");

	private static final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/gui/note/post_it_gui.png");

	private static final RandomSource RANDOM = RandomSource.create();



	public NoteScreen(PostItEntity note, boolean isFiltered) {
		this(note, isFiltered, Component.translatable("note.postit.edit"));
	}

	public NoteScreen(PostItEntity note, boolean isFiltered, Component title) {
		super(title);
		scribbleTexture = getScribble();
		this.note = note;
		this.hand = null;
		this.color = note.color();
		this.text = note.text();
		this.messages = IntStream.range(0, 4).mapToObj(i -> this.text.getMessage(i, isFiltered)).map(Component::getString).toArray(String[]::new);
	}

	public NoteScreen(ItemStack stack, InteractionHand hand, boolean isFiltered) {
		this(stack, hand, isFiltered, Component.translatable("note.postit.edit"));
	}

	public NoteScreen(ItemStack stack, InteractionHand hand, boolean isFiltered, Component title) {
		super(title);
		scribbleTexture = getScribble();
		this.note = null;
		this.hand = hand;
		this.color = DyedItemColor.getOrDefault(stack, PostItItem.DEFAULT_COLOR);
		this.text = stack.getOrDefault(ItemRegistry.NOTE_TEXT_COMPONENT, new SignText());
		this.messages = IntStream.range(0, 4).mapToObj(i -> this.text.getMessage(i, isFiltered)).map(Component::getString).toArray(String[]::new);
	}

	private ResourceLocation getScribble(){
		ArrayList<Scribble> scribbleList = getScribbleList();

		while(scribble1 == null){
			int index = (int)(Math.random() * scribbleList.size());
			if(scribbleList.get(index).size() == Scribble.Size.SMALL){
				return scribbleList.get(index).sprite();
			}
		}
		return null;

//		return WeightedRandom.getRandomItem(RANDOM,
//						scribbles
//								.holders()
//								.map((scribbleReference) -> WeightedEntry.wrap(scribbleReference, scribbleReference.value().weight()))
//								.toList()
//				)
//				.map(WeightedEntry.Wrapper::data)
//				.map((holder)->holder.value().sprite())
//				.orElse(FALLBACK_SCRIBBLE_TEXTURE);


	}


	private ArrayList<Scribble> getScribbleList() {
		Registry<Scribble> registry = Minecraft.getInstance()
				.level
				.registryAccess()
				.registryOrThrow(Scribble.REGISTRY_KEY);

		ArrayList<Scribble> scribbleList = new ArrayList<>();
		for (Scribble scribble : registry) {
			scribbleList.add(scribble);
		}
		return scribbleList;
	}

//	private Iterator<Scribble> getValidScribble(){
//		var scribbles = new java.util.ArrayList<>(Minecraft.getInstance()
//                .level
//                .registryAccess()
//                .registryOrThrow(Scribble.REGISTRY_KEY)
//                .stream()
//                .toList());
//		var totalWeight = WeightedRandom.getTotalWeight(scribbles);
//		int small = 0;
//		int medium = 0;
//		int large = 0;
//
//		while (true) {
//			Optional<Scribble> optionalScribble = WeightedRandom.getRandomItem(RANDOM, scribbles, totalWeight);
//			if(optionalScribble.isEmpty()){
//				break;
//			}
//			Scribble scribble = optionalScribble.get();
//			scribbles.remove(scribble);
//			boolean valid = switch (scribble.size()){
//				case SMALL -> small < 1;
//				case MEDIUM -> medium < 2;
//				case LARGE -> large < 2;
//			};
//			if (!valid) {
//				continue;
//			}
//
//			/* use scribble*/
//		}
//	}

	@Override
	protected void init() {
		assert this.minecraft != null;

		this.closeWidget = this.addRenderableWidget(new CloseWidget((this.width + (164-68)) / 2, (this.height - (160-32)) / 2, 16, 16));
		this.scribble1 = this.addRenderableOnly(new ScribbleWidget((this.width - (164-20)) / 2, (this.height - (160-20)) / 2, 42, 32, scribbleTexture));
		this.signField = new TextFieldHelper(
				() -> this.messages[this.line],
				this::setMessage,
				TextFieldHelper.createClipboardGetter(this.minecraft),
				TextFieldHelper.createClipboardSetter(this.minecraft),
				string -> this.minecraft.font.width(string) <= PostItEntity.MAX_TEXT_WIDTH
		);
	}

	@Override
	public void tick() {
		this.frame++;
		if (!this.isValid()) {
			this.onDone();
		}
	}

	private boolean isValid() {
		if (this.minecraft == null || this.minecraft.player == null) return false;

		if (this.note != null) {
			return !this.note.isRemoved() && this.minecraft.player.canInteractWithEntity(this.note, 4.0);
		}
		// item mode: close the screen if they somehow stopped holding a note (dropped it, swapped
		// hotbar slot, etc) while editing.
		return this.hand != null && this.minecraft.player.getItemInHand(this.hand).is(ItemRegistry.POST_IT_NOTE);
	}

	@Override
	public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
		assert this.signField != null;
		if (keyCode == 265) {
			this.line = this.line - 1 & 3;
			this.signField.setCursorToEnd();
			return true;
		} else if (keyCode == 264 || keyCode == 257 || keyCode == 335) {
			this.line = this.line + 1 & 3;
			this.signField.setCursorToEnd();
			return true;
		} else return this.signField.keyPressed(keyCode) || super.keyPressed(keyCode, scanCode, modifiers);
	}

	@Override
	public boolean charTyped(char codePoint, int modifiers) {
		assert this.signField != null;
		this.signField.charTyped(codePoint);
		return true;
	}

	private int getTextColor() {
		return this.text.hasGlowingText() ? this.text.getColor().getTextColor() : SignRenderer.getDarkColor(this.text);
	}

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.closeWidget.setColor(this.getTextColor());
		this.scribble1.setColor(this.getTextColor());
		super.render(guiGraphics, mouseX, mouseY, partialTick);

		guiGraphics.pose().pushPose();
		guiGraphics.pose().translate(this.width / 2.0F, this.height / 2.0f, 50.0F);
		guiGraphics.pose().scale(1.75f, 1.75f, 1.75f);
		this.renderSignText(guiGraphics);

		guiGraphics.pose().popPose();
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderTransparentBackground(guiGraphics);

		float r = red(this.color);
		float g = green(this.color);
		float b = blue(this.color);

		RenderSystem.setShaderColor(r/255, g/255, b/255, 1.0F);
		guiGraphics.blit(BACKGROUND_TEXTURE, (this.width - 164)/2, (this.height - 160)/2, 0, 0, 164, 160);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);
	}

	@Override
	public void onClose() {
		this.onDone();
	}

	@Override
	public void removed() {
		assert this.minecraft != null;
		if (this.note != null) {
			PacketDistributor.sendToServer(PacketRegistry.UpdateNoteTextPacket.create(this.note, text));
		} else if (this.hand != null) {
			PacketDistributor.sendToServer(new PacketRegistry.UpdateHeldNoteTextPacket(this.hand, text));
		}
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
	protected Vector3f getSignTextScale() {
		return TEXT_SCALE;
	}

	private void renderSignText(GuiGraphics guiGraphics) {
		assert this.signField != null;
		guiGraphics.pose().translate(0.0F, 0.0F, 4.0F);
		Vector3f vector3f = this.getSignTextScale();
		guiGraphics.pose().scale(vector3f.x(), vector3f.y(), vector3f.z());
		int textColor = this.getTextColor();
		boolean cursorBlink = this.frame / 6 % 2 == 0;
		int cursorPos = this.signField.getCursorPos();
		int selectionPos = this.signField.getSelectionPos();
		int lineOffset = 4 * PostItEntity.TEXT_LINE_HEIGHT / 2;
		int lineY = this.line * PostItEntity.TEXT_LINE_HEIGHT - lineOffset;

		for (int m = 0; m < this.messages.length; m++) {
			String message = this.messages[m];
			if (message == null) continue;

			if (this.font.isBidirectional()) message = this.font.bidirectionalShaping(message);

			int xOffset = -this.font.width(message) / 2;
			guiGraphics.drawString(this.font, message, xOffset, m * PostItEntity.TEXT_LINE_HEIGHT - lineOffset, textColor, false);
			if (m == this.line && cursorPos >= 0 && cursorBlink) {
				int selected = this.font.width(message.substring(0, Math.min(cursorPos, message.length())));
				int cursorX = selected - this.font.width(message) / 2;
				if (cursorPos >= message.length()) {
					guiGraphics.drawString(this.font, "_", cursorX, lineY, textColor, false);
				}
			}
		}

		for (int m = 0; m < this.messages.length; m++) {
			String message = this.messages[m];
			if (message == null || m != this.line || cursorPos < 0) continue;

			int o = this.font.width(message.substring(0, Math.min(cursorPos, message.length())));
			int p = o - this.font.width(message) / 2;
			if (cursorBlink && cursorPos < message.length()) guiGraphics.fill(p, lineY - 1, p + 1, lineY + PostItEntity.TEXT_LINE_HEIGHT, 0xFF000000 | textColor);

			if (selectionPos == cursorPos) continue;
			int minPos = Math.min(cursorPos, selectionPos);
			int maxPos = Math.max(cursorPos, selectionPos);
			int off1 = this.font.width(message.substring(0, minPos)) - this.font.width(message) / 2;
			int off2 = this.font.width(message.substring(0, maxPos)) - this.font.width(message) / 2;
			int minX = Math.min(off1, off2);
			int maxX = Math.max(off1, off2);
			guiGraphics.fill(RenderType.guiTextHighlight(), minX, lineY, maxX, lineY + PostItEntity.TEXT_LINE_HEIGHT, 0xff0000ff);
		}
	}

	private void setMessage(String message) {
		this.messages[this.line] = message;
		this.text = this.text.setMessage(this.line, Component.literal(message));
		// live-updates the in-world entity's text as you type, purely as visual feedback while the
		// screen is open; there's nothing to live-update when editing a held item, so skip it there.
		if (this.note != null) this.note.setText(this.text);
	}

	private void onDone() {
		assert this.minecraft != null;
		this.minecraft.setScreen(null);
	}
}