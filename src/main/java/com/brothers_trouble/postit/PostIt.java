package com.brothers_trouble.postit;

import com.brothers_trouble.postit.item.PostItItem;
import com.brothers_trouble.postit.registration.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.level.block.Blocks;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.config.ModConfig;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.fml.event.lifecycle.FMLCommonSetupEvent;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;
import net.neoforged.neoforge.event.server.ServerStartingEvent;



// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(PostIt.MODID)
public class PostIt
{
    // Define mod id in a common place for everything to reference
    public static final String MODID = "postit";
    // Directly reference a slf4j logger
    public static final Logger LOGGER = LogUtils.getLogger();

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public PostIt(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        EntityRegistry.register(modEventBus);
        RenderRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        ModelRegistry.register(modEventBus);
        RecipeRegistry.register(modEventBus);
        PacketRegistry.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (PostIt) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
    }

    public static ResourceLocation locate(String name) {
        return ResourceLocation.fromNamespaceAndPath(MODID, name);
    }

    private void commonSetup(final FMLCommonSetupEvent event)
    {
        // Some common setup code
        LOGGER.info("HELLO FROM COMMON SETUP");

        if (Config.logDirtBlock)
            LOGGER.info("DIRT BLOCK >> {}", BuiltInRegistries.BLOCK.getKey(Blocks.DIRT));

        LOGGER.info(Config.magicNumberIntroduction + Config.magicNumber);

        Config.items.forEach((item) -> LOGGER.info("ITEM >> {}", item.toString()));
    }

    // Add the example block item to the building blocks tab
    private void addCreative(BuildCreativeModeTabContentsEvent event)
    {
        if (event.getTabKey() == CreativeModeTabs.FUNCTIONAL_BLOCKS)
            event.accept(ItemRegistry.POST_IT_NOTE.get());
    }

    // You can use SubscribeEvent and let the Event Bus discover methods to call
    @SubscribeEvent
    public void onServerStarting(ServerStartingEvent event)
    {
        // Do something when the server starts
        LOGGER.info("HELLO from server starting");
    }

    // You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
    @EventBusSubscriber(modid = MODID, bus = EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
    public static class ClientModEvents
    {

        @SubscribeEvent
        public static void onRegisterItemColorHandlers(RegisterColorHandlersEvent.Item event){
            event.register((stack, tintIndex) -> {
                return DyedItemColor.getOrDefault(stack, PostItItem.DEFAULT_COLOR);
//                if(tintInt == 0){
////                    return Color.HSBtoRGB(0.5F, 0.5F, 1F);
//                    return Color.GREEN.getRGB();
////                    return new Color(tintInt, false);
////                    return tintInt;
//                }if(tintInt == 15){
////                    return Color.HSBtoRGB(0.5F, 0.5F, 1F);
//                    return Color.RED.getRGB();
////                    return 15;
//                } else{
//                    return -1;
//                }
            }, ItemRegistry.POST_IT_NOTE.get());
        }
//                DyedItemColor tintInt = stack.getOrDefault(DataComponents.DYED_COLOR, null);
//                int tintInt = PostItItem.getDyeColor(stack);

        @SubscribeEvent
        public static void onClientSetup(FMLClientSetupEvent event)
        {
            // Some client setup code
            LOGGER.info("HELLO FROM CLIENT SETUP");
            LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
        }

//        @SubscribeEvent
//        public static void registerItemColors(RegisterColorHandlersEvent.Item event){
//            event.register(
//                    (stack, tintIndex) -> {
//                        boolean enabled = Optional.ofNullable(stack.get(ModDataComponents.ITEM_ENABLED)).orElse(false);
//                        if(enabled){
//                            if(tintIndex == 1){
//                                return 0xFFFFFFFF;
//                            } else if(tintIndex == 2){
//                                double SCALAR = 1.5f;
//                                double hue = ((440f * (1.0d/SCALAR)) + (System.currentTimeMillis() % (360 * (1.0d/SCALAR))));
//                                return Mth.hsvToRgb((float) (( hue / 360.0f) * (0.4f * SCALAR)), 0.55F, 1.0F) | 0xFF000000;
//                            }
//                        } else {
//                            if(tintIndex == 1){
//                                return 0x00000000;
//                            } else if(tintIndex == 2){
//                                return 0x00000000;
//                            }
//                        }
//                        return -1;
//                    }, ModItems.ELECTRIC_SWORD.get());
//            event.register((stack, idx) -> {
//                if(stack.getItem() instanceof EnergyStoringItem energyStoringItem){
//                    return 0xFF000000 | Mth.hsvToRgb(0.0f,
//                            Math.max(0.3f, ((float)energyStoringItem.getEnergyStored(stack) / energyStoringItem.getMaxEnergyStored(stack))), 1.0f);
//                }
//                return -1;
//            }, ModItems.HUGE_BATTERY.get());
//        }
    }
}