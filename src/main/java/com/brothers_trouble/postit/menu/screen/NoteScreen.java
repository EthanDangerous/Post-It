package com.brothers_trouble.postit.menu.screen;

import com.brothers_trouble.postit.PostIt;
import com.brothers_trouble.postit.entity.PostItEntity;
import com.brothers_trouble.postit.registration.PacketRegistry;
import com.mojang.blaze3d.platform.Lighting;
import com.mojang.blaze3d.systems.RenderSystem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.components.Button;
import net.minecraft.client.gui.font.TextFieldHelper;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.blockentity.SignRenderer;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.block.entity.SignText;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.api.distmarker.OnlyIn;
import net.neoforged.neoforge.network.PacketDistributor;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.joml.Vector3f;
import static net.minecraft.util.FastColor.ARGB32.*;
import java.util.stream.IntStream;

@OnlyIn(Dist.CLIENT)
public class NoteScreen extends Screen {
	private final PostItEntity note;
	private SignText text;
	private final String[] messages;
	private int frame;
	private int line;
	@Nullable
	private TextFieldHelper signField;

    private final ResourceLocation BACKGROUND_TEXTURE = ResourceLocation.fromNamespaceAndPath(PostIt.MODID, "textures/gui/note/example_container.png");

	public NoteScreen(PostItEntity note, boolean isFiltered) {
		this(note, isFiltered, Component.translatable("note.postit.edit"));
	}

	public NoteScreen(PostItEntity note, boolean isFiltered, Component title) {
		super(title);
		this.note = note;
		this.text = note.text();
		this.messages = IntStream.range(0, 4).mapToObj(i -> this.text.getMessage(i, isFiltered)).map(Component::getString).toArray(String[]::new);
	}

	@Override
	protected void init() {
		this.addRenderableWidget(
				Button.builder(CommonComponents.GUI_DONE, button -> this.onDone()).bounds(this.width / 2 - 100, this.height / 4 + 144, 200, 20).build()
		);
		assert this.minecraft != null;
		this.signField = new TextFieldHelper(
				() -> this.messages[this.line],
				this::setMessage,
				TextFieldHelper.createClipboardGetter(this.minecraft),
				TextFieldHelper.createClipboardSetter(this.minecraft),
				string -> this.minecraft.font.width(string) <= this.note.maxTextLineWidth()
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
		return this.minecraft != null
				&& this.minecraft.player != null
				&& !this.note.isRemoved()
				&& this.minecraft.player.canInteractWithEntity(this.note, 4.0);
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

	@Override
	public void render(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		super.render(guiGraphics, mouseX, mouseY, partialTick);
		guiGraphics.flush();

		Lighting.setupForFlatItems();
		this.renderSign(guiGraphics);

		guiGraphics.drawCenteredString(this.font, this.title, this.width / 2, 40, 16777215);
		guiGraphics.flush();
		Lighting.setupFor3DItems();
	}

	@Override
	public void renderBackground(@NotNull GuiGraphics guiGraphics, int mouseX, int mouseY, float partialTick) {
		this.renderTransparentBackground(guiGraphics);
	}

	@Override
	public void onClose() {
		this.onDone();
	}

	@Override
	public void removed() {
		assert this.minecraft != null;
		PacketDistributor.sendToServer(PacketRegistry.UpdateNoteTextPacket.create(this.note, text));
	}

	@Override
	public boolean isPauseScreen() {
		return false;
	}

	private static final Vector3f TEXT_SCALE = new Vector3f(0.9765628F, 0.9765628F, 0.9765628F);
	protected Vector3f getSignTextScale() {
		return TEXT_SCALE;
	}

	protected void offsetSign(GuiGraphics guiGraphics) {
		guiGraphics.pose().translate(this.width / 2.0F, this.height / 2.0F, 50.0F);
	}

	private void renderSign(GuiGraphics guiGraphics) {
		guiGraphics.pose().pushPose();
		this.offsetSign(guiGraphics);

		// TODO: render background here
		float r = red(note.color());
		float g = green(note.color());
		float b = blue(note.color());

		RenderSystem.setShaderColor(r/255, g/255, b/255, 1.0F);
		guiGraphics.blit(BACKGROUND_TEXTURE, -160/2, -160/2, 0, 0, 160, 160);
		RenderSystem.setShaderColor(1.0F, 1.0F, 1.0F, 1.0F);

		this.renderSignText(guiGraphics);
		guiGraphics.pose().popPose();
	}

	private void renderSignText(GuiGraphics guiGraphics) {
		assert this.signField != null;
		guiGraphics.pose().translate(0.0F, 0.0F, 4.0F);
		Vector3f vector3f = this.getSignTextScale();
		guiGraphics.pose().scale(vector3f.x(), vector3f.y(), vector3f.z());
		int textColor = this.text.hasGlowingText() ? this.text.getColor().getTextColor() : SignRenderer.getDarkColor(this.text);
		boolean cursorBlink = this.frame / 6 % 2 == 0;
		int cursorPos = this.signField.getCursorPos();
		int selectionPos = this.signField.getSelectionPos();
		int lineOffset = 4 * this.note.maxTextLineHeight() / 2;
		int lineY = this.line * this.note.maxTextLineHeight() - lineOffset;

		for (int m = 0; m < this.messages.length; m++) {
			String message = this.messages[m];
			if (message == null) continue;

			if (this.font.isBidirectional()) message = this.font.bidirectionalShaping(message);

			int xOffset = -this.font.width(message) / 2;
			guiGraphics.drawString(this.font, message, xOffset, m * this.note.maxTextLineHeight() - lineOffset, textColor, false);
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
			if (cursorBlink && cursorPos < message.length()) guiGraphics.fill(p, lineY - 1, p + 1, lineY + this.note.maxTextLineHeight(), 0xFF000000 | textColor);

			if (selectionPos == cursorPos) continue;
			int minPos = Math.min(cursorPos, selectionPos);
			int maxPos = Math.max(cursorPos, selectionPos);
			int off1 = this.font.width(message.substring(0, minPos)) - this.font.width(message) / 2;
			int off2 = this.font.width(message.substring(0, maxPos)) - this.font.width(message) / 2;
			int minX = Math.min(off1, off2);
			int maxX = Math.max(off1, off2);
			guiGraphics.fill(RenderType.guiTextHighlight(), minX, lineY, maxX, lineY + this.note.maxTextLineHeight(), 0xff0000ff);
		}
	}

	private void setMessage(String message) {
		this.messages[this.line] = message;
		this.text = this.text.setMessage(this.line, Component.literal(message));
		this.note.setText(this.text);
	}

	private void onDone() {
		assert this.minecraft != null;
		this.minecraft.setScreen(null);
	}
}
