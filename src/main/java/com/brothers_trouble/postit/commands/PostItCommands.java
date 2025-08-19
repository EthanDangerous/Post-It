package com.brothers_trouble.postit.commands;

import com.brothers_trouble.postit.PostIt;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.context.CommandContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

public class PostItCommands {
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher){
        dispatcher.register(Commands.literal(PostIt.MODID)
                .requires(source -> source.hasPermission(1))
                .then(Commands.literal("testCommand")
                    .executes(command -> {return testCommand(command);})
        ));
    }

    private static int testCommand(CommandContext<CommandSourceStack> command){
        System.out.println("TEST COMMAND RUN");
//        return Command.SINGLE_SUCCESS;
        return 1;
    }
}
