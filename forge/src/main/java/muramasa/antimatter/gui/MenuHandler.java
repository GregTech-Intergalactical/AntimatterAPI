package muramasa.antimatter.gui;

import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.gui.screen.AntimatterContainerScreen;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.api.distmarker.OnlyIn;
import net.minecraftforge.common.extensions.IForgeMenuType;


//An arbitrary menu handler for e.g. guiclass.
public abstract class MenuHandler<T extends AbstractContainerMenu & IAntimatterContainer> implements IAntimatterObject {

    protected ResourceLocation loc;
    private MenuType<T> containerType;

    public MenuHandler(String domain, String id) {
        loc = new ResourceLocation(domain, id);
        AntimatterAPI.register(MenuHandler.class, this);
        MenuType<?> type = getContainerType();
        AntimatterAPI.register(MenuType.class, type.getRegistryName().toString(), type.getRegistryName().getNamespace(), type);
    }

    @Override
    public String getDomain() {
        return loc.getNamespace();
    }

    @Override
    public String getId() {
        return loc.getPath();
    }

    protected abstract T getMenu(IGuiHandler source, Inventory playerInv, int windowId);

    @MethodsReturnNonnullByDefault
    public final T menu(IGuiHandler source, Inventory playerInv, int windowId) {
        T t = getMenu(source, playerInv, windowId);
        //Gui Entrypoint for server.
        if (!source.isRemote()) t.source().init();
        return t;
    }

    @MethodsReturnNonnullByDefault
    public MenuType<T> getContainerType() {
        if (containerType == null) {
            containerType = IForgeMenuType.create(this::onContainerCreate);
            containerType.setRegistryName(loc);
        }
        return containerType;
    }

    public abstract T onContainerCreate(int windowId, Inventory inv, FriendlyByteBuf data);

    //This has to be Object or else the runtime dist cleaner murders antimatter. It should actually return
    //the appropriate IScreenManager.IScreenFactory
    @OnlyIn(Dist.CLIENT)
    public Object screen() {
        return (MenuScreens.ScreenConstructor) (a, b, c) -> new AntimatterContainerScreen(a, b, c);
    }
}
