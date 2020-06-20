package muramasa.antimatter.gui;
import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;

import javax.annotation.ParametersAreNonnullByDefault;

//An arbitrary menu handler for e.g. guiclass.
public abstract class MenuHandler<T extends Container, U extends Screen & IHasContainer<T>> implements IAntimatterObject, ScreenManager.IScreenFactory<T, U> {

    protected ResourceLocation loc;
    private ContainerType<T> containerType;

    public MenuHandler(String domain, String id) {
        loc = new ResourceLocation(domain, id);
        AntimatterAPI.register(MenuHandler.class, this);
    }

    @Override
    public String getDomain() {
        return loc.getNamespace();
    }

    @Override
    public String getId() {
        return loc.getPath();
    }

    @MethodsReturnNonnullByDefault
    public abstract T getMenu(Object tile, PlayerInventory playerInv, int windowId);

    @ParametersAreNonnullByDefault
    @Override //Overridden to give proper names
    public abstract U create(T container, PlayerInventory inv, ITextComponent name);

    @MethodsReturnNonnullByDefault
    public ContainerType<T> getContainerType() {
        if (containerType == null) {
            containerType = IForgeContainerType.create(this::onContainerCreate);
            containerType.setRegistryName(loc);
        }
        return containerType;
    }

    public T onContainerCreate(int windowId, PlayerInventory inv, PacketBuffer data) {
        return null;
    }
}
