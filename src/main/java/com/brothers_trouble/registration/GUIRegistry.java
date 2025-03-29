package com.brothers_trouble.registration;

import com.brothers_trouble.PostIt;
import com.brothers_trouble.menu.PostItMenu;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.network.IContainerFactory;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class GUIRegistry {
    public static final DeferredRegister<MenuType<?>> MENUS = DeferredRegister.create(BuiltInRegistries.MENU, PostIt.MODID);
//    public static final Supplier<MenuType<PostItMenu>> POST_IT_MENU = register(PostItMenu::new, "post_it_menu");

    private static <T extends AbstractContainerMenu> Supplier<MenuType<T>> register(IContainerFactory<T> factory, String name) {
        return MENUS.register(name, () -> IMenuTypeExtension.create(factory));
    }
}