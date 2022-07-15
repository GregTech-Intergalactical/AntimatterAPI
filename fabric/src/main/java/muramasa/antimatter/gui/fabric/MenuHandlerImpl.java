package muramasa.antimatter.gui.fabric;

import net.fabricmc.fabric.api.screenhandler.v1.ScreenHandlerRegistry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.apache.commons.lang3.function.TriFunction;

public class MenuHandlerImpl {
    public static <T extends AbstractContainerMenu> MenuType<T> create(ResourceLocation id, TriFunction<Integer, Inventory, FriendlyByteBuf, T> factory) {
        return ScreenHandlerRegistry.registerExtended(id, factory::apply);
    }
}
