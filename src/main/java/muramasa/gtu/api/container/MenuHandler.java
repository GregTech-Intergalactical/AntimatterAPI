package muramasa.gtu.api.container;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.registration.IGregTechObject;
import muramasa.gtu.api.util.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;

import javax.annotation.Nullable;

public abstract class MenuHandler<T extends Container> implements IGregTechObject {

    private ResourceLocation registryName;
    private ContainerType<T> containerType = null;

    public MenuHandler(ResourceLocation loc) {
        this.registryName = loc;
        GregTechAPI.register(MenuHandler.class, this);
    }

    public MenuHandler(String name) {
        this(new ResourceLocation(Ref.MODID, name));
    }

    @Override
    public String getId() {
        return registryName.getPath();
    }

    @Nullable
    public abstract T getMenu(TileEntity tile, PlayerInventory playerInv, int windowId);

    @Nullable
    public abstract ScreenMachine getScreen(ContainerMachine container, PlayerInventory inv, ITextComponent name);

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
