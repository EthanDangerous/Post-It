package com.brothers_trouble.postit;

import com.brothers_trouble.postit.commands.PostItCommands;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.event.RegisterCommandsEvent;

@EventBusSubscriber(modid = PostIt.MODID, bus = EventBusSubscriber.Bus.GAME)
public class ForgeModEvents {
    @SubscribeEvent
    public static void registerCommands(RegisterCommandsEvent event) {
        PostItCommands.register(event.getDispatcher());
    }
}