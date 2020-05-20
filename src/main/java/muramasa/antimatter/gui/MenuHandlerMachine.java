package muramasa.antimatter.gui;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.container.ContainerMachine;
import muramasa.antimatter.gui.screen.ScreenMachine;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityMerger;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import org.lwjgl.system.NonnullDefault;

import javax.annotation.Nullable;

public abstract class MenuHandlerMachine<T extends Container> implements IAntimatterObject, IMenuHandler<T, ScreenMachine> {

    private ResourceLocation registryName;
    private ContainerType<T> containerType = null;

    public MenuHandlerMachine(String domain, String id) {
        this.registryName = new ResourceLocation(domain, id);
        AntimatterAPI.register(MenuHandlerMachine.class, id, this);
    }

    @Override
    public String getId() {
        return registryName.getPath();
    }

    public abstract ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name);

    //@Nullable
   // public abstract T getMenu(TileEntity tile, PlayerInventory playerInv, int windowId);
   // @NonnullDefault
  //  public abstract ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name);

    public ContainerType<T> getContainerType() {
        if (containerType == null) {
            containerType = IForgeContainerType.create((windowId, inv, data) -> {
                TileEntity tile = Utils.getTileFromBuf(data);
                return getMenu(tile, inv, windowId);
            });
            containerType.setRegistryName(registryName);
        }
        return containerType;
    }
}
