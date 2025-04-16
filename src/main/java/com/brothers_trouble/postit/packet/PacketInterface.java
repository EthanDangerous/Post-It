package com.brothers_trouble.postit.packet;

import net.minecraft.client.Minecraft;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;

public interface PacketInterface extends CustomPacketPayload {
    default void handleClient(Minecraft minecraft, Player player){

    }
    default void handleServer(MinecraftServer server, ServerPlayer player){

    }
}
