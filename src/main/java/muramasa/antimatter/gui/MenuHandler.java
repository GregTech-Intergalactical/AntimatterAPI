package muramasa.antimatter.gui;

import mcp.MethodsReturnNonnullByDefault;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.client.gui.ScreenManager;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.network.PacketBuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeContainerType;


//An arbitrary menu handler for e.g. guiclass.
public abstract class MenuHandler<T extends Container & IAntimatterContainer> implements IAntimatterObject {

    protected ResourceLocation loc;
    private ContainerType<T> containerType;

    public MenuHandler(String domain, String id) {
        loc = new ResourceLocation(domain, id);
        AntimatterAPI.register(MenuHandler.class, this);
        ContainerType<?> type = getContainerType();
        AntimatterAPI.registerIfAbsent(ContainerType.class, type.getRegistryName().toString(), () -> type);
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
    protected abstract T getMenu(IGuiHandler source, PlayerInventory playerInv, int windowId);

    @MethodsReturnNonnullByDefault
    public final T menu(IGuiHandler source, PlayerInventory playerInv, int windowId) {
        T t = getMenu(source, playerInv, windowId);
        //Gui Entrypoint for server.
        if (!source.isRemote()) t.source().init();
        return t;
    }

    @MethodsReturnNonnullByDefault
    public ContainerType<T> getContainerType() {
        if (containerType == null) {
            containerType = IForgeContainerType.create(this::onContainerCreate);
            containerType.setRegistryName(loc);
        }
        return containerType;
    }

    public abstract T onContainerCreate(int windowId, PlayerInventory inv, PacketBuffer data);
    //This has to be Object or else the runtime dist cleaner murders antimatter. It should actually return
    //the appropriate IScreenManager.IScreenFactory
    @OnlyIn(Dist.CLIENT)
    public Object screen() {
        return (ScreenManager.IScreenFactory)(a,b,c) -> new AntimatterContainerScreen(a,b,c);
    }
}
