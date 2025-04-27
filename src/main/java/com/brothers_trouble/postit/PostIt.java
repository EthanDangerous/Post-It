package com.brothers_trouble.postit;

import com.brothers_trouble.postit.item.PostItItem;
import com.brothers_trouble.postit.registration.ItemRegistry;
import com.brothers_trouble.postit.registration.ModelRegistry;
//import com.brothers_trouble.postit.registration.RecipeRegistry;
import com.brothers_trouble.postit.registration.RecipeRegistry;
import com.brothers_trouble.postit.registration.RenderRegistry;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.item.*;
import net.minecraft.world.item.component.DyedItemColor;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;
import net.neoforged.neoforge.registries.*;
import org.slf4j.Logger;

import com.mojang.logging.LogUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.food.FoodProperties;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.material.MapColor;
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
import net.minecraft.client.color.item.ItemColor;
import net.neoforged.neoforge.client.event.RegisterColorHandlersEvent;

import java.awt.*;

import static com.brothers_trouble.postit.registration.EntityRegistry.ENTITY_TYPES;


// The value here should match an entry in the META-INF/neoforge.mods.toml file
@Mod(PostIt.MODID)
public class PostIt
{
    // Define mod id in a common place for everything to reference
//    public static final String MODID = "data/postit";
    public static final String MODID = "postit";
    // Directly reference a slf4j logger
    private static final Logger LOGGER = LogUtils.getLogger();
    // Create a Deferred Register to hold Blocks which will all be registered under the "postit" namespace
    public static final DeferredRegister.Blocks BLOCKS = DeferredRegister.createBlocks(MODID);
    // Create a Deferred Register to hold Items which will all be registered under the "postit" namespace
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(MODID);
    // Create a Deferred Register to hold CreativeModeTabs which will all be registered under the "postit" namespace
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS = DeferredRegister.create(Registries.CREATIVE_MODE_TAB, MODID);

    // Creates a new Block with the id "postit:example_block", combining the namespace and path
    public static final DeferredBlock<Block> EXAMPLE_BLOCK = BLOCKS.registerSimpleBlock("example_block", BlockBehaviour.Properties.of().mapColor(MapColor.STONE));
    // Creates a new BlockItem with the id "postit:example_block", combining the namespace and path
    public static final DeferredItem<BlockItem> EXAMPLE_BLOCK_ITEM = ITEMS.registerSimpleBlockItem("example_block", EXAMPLE_BLOCK);

    // Creates a new food item with the id "postit:example_id", nutrition 1 and saturation 2
    public static final DeferredItem<Item> EXAMPLE_ITEM = ITEMS.registerSimpleItem("example_item", new Item.Properties().food(new FoodProperties.Builder()
            .alwaysEdible().nutrition(1).saturationModifier(2f).build()));

    // Creates a creative tab with the id "postit:example_tab" for the example item, that is placed after the combat tab
    public static final DeferredHolder<CreativeModeTab, CreativeModeTab> EXAMPLE_TAB = CREATIVE_MODE_TABS.register("example_tab", () -> CreativeModeTab.builder()
            .title(Component.translatable("itemGroup.postit")) //The language key for the title of your CreativeModeTab
            .withTabsBefore(CreativeModeTabs.COMBAT)
            .icon(() -> EXAMPLE_ITEM.get().getDefaultInstance())
            .displayItems((parameters, output) -> {
                output.accept(EXAMPLE_ITEM.get()); // Add the example item to the tab. For your own tabs, this method is preferred over the event
            }).build());

    // The constructor for the mod class is the first code that is run when your mod is loaded.
    // FML will recognize some parameter types like IEventBus or ModContainer and pass them in automatically.
    public PostIt(IEventBus modEventBus, ModContainer modContainer)
    {
        // Register the commonSetup method for modloading
        modEventBus.addListener(this::commonSetup);

        // Register the Deferred Register to the mod event bus so blocks get registered
        BLOCKS.register(modEventBus);
        // Register the Deferred Register to the mod event bus so items get registered
        ITEMS.register(modEventBus);
        ENTITY_TYPES.register(modEventBus);
        RenderRegistry.register(modEventBus);
        ItemRegistry.register(modEventBus);
        ModelRegistry.register(modEventBus);
        RecipeRegistry.register(modEventBus);
        // Register the Deferred Register to the mod event bus so tabs get registered
        CREATIVE_MODE_TABS.register(modEventBus);

        // Register ourselves for server and other game events we are interested in.
        // Note that this is necessary if and only if we want *this* class (PostIt) to respond directly to events.
        // Do not add this line if there are no @SubscribeEvent-annotated functions in this class, like onServerStarting() below.
        NeoForge.EVENT_BUS.register(this);

        // Register the item to a creative tab
        modEventBus.addListener(this::addCreative);

        // Register our mod's ModConfigSpec so that FML can create and load the config file for us
        modContainer.registerConfig(ModConfig.Type.COMMON, Config.SPEC);
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
        if (event.getTabKey() == CreativeModeTabs.BUILDING_BLOCKS)
            event.accept(EXAMPLE_BLOCK_ITEM);

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
                return PostItItem.getDyeColor(stack);
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
