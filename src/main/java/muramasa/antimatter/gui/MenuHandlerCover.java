package muramasa.antimatter.gui;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.screen.ScreenCover;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tile.TileEntityMachine;
import muramasa.antimatter.util.Utils;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.Direction;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import org.lwjgl.system.NonnullDefault;

public abstract class MenuHandlerCover<M extends Container>  implements IAntimatterObject, IMenuHandler<M, ScreenCover> {

    private ResourceLocation registryName;
    private ContainerType<M> containerType = null;

    public MenuHandlerCover(String domain, String id) {
        this.registryName = new ResourceLocation(domain, id);
        AntimatterAPI.register(MenuHandlerCover.class, id, this);
    }

    @Override
    public String getId() {
        return registryName.getPath();
    }

 //   @Nullable
   // public abstract M getMenu(Cover tile, PlayerInventory playerInv, int windowId);

   @NonnullDefault
    public abstract ScreenCover getScreen(ContainerCover container, PlayerInventory inv, ITextComponent name);


    public M onContainerCreate(int windowId, PlayerInventory inv, PacketBuffer data) {
        TileEntity tile = Utils.getTileFromBuf(data);
        if (tile instanceof TileEntityMachine) {
            Direction dir = Direction.byIndex(data.readInt());
            return getMenu(((TileEntityMachine)tile).coverHandler.get().getCover(dir),inv,windowId);
        }
        return null;
    }

    public ContainerType<M> getContainerType() {
        if (containerType == null) {
            containerType = IForgeContainerType.create(this::onContainerCreate);
            containerType.setRegistryName(registryName);
        }
        return containerType;
    }
}
