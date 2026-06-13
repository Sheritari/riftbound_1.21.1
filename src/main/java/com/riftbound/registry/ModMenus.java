package com.riftbound.registry;

import com.riftbound.RiftboundMod;
import com.riftbound.menu.CageOfTradeMenu;
import com.riftbound.menu.TransmutationMenu;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.extensions.IMenuTypeExtension;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public final class ModMenus {
    public static final DeferredRegister<MenuType<?>> MENUS =
            DeferredRegister.create(Registries.MENU, RiftboundMod.MOD_ID);

    public static final DeferredHolder<MenuType<?>, MenuType<CageOfTradeMenu>> CAGE_OF_TRADE =
            MENUS.register("cage_of_trade", () -> IMenuTypeExtension.create(CageOfTradeMenu::new));

    public static final DeferredHolder<MenuType<?>, MenuType<TransmutationMenu>> TRANSMUTATION =
            MENUS.register("transmutation", () -> IMenuTypeExtension.create((windowId, inventory, player) ->
                    new TransmutationMenu(windowId, inventory)));

    private ModMenus() {
    }

    public static void register(IEventBus bus) {
        MENUS.register(bus);
    }
}
