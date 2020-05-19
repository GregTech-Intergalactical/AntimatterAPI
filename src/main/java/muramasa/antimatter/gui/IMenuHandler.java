package muramasa.antimatter.gui;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import org.lwjgl.system.NonnullDefault;

//An arbitrary menu handler for e.g. guiclass.
public interface IMenuHandler<T extends Container, U extends Screen> {
    T getMenu(Object tile, PlayerInventory playerInv, int windowId);
    @NonnullDefault

    ContainerType<T> getContainerType();
}
