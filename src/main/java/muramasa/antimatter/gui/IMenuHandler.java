package muramasa.antimatter.gui;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.gui.container.ContainerCover;
import muramasa.antimatter.gui.screen.ScreenCover;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.client.gui.IHasContainer;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraftforge.common.extensions.IForgeContainerType;
import org.lwjgl.system.NonnullDefault;

//V represents data producer, e.g. a TileEntity or a Cover.
public interface IMenuHandler<T extends Container, U extends Screen> {
    T getMenu(Object tile, PlayerInventory playerInv, int windowId);
    @NonnullDefault

    ContainerType<T> getContainerType();
}
