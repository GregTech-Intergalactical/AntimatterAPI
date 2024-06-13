package muramasa.antimatter.gui;

import dev.architectury.injectables.annotations.ExpectPlatform;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.gui.container.IAntimatterContainer;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.MethodsReturnNonnullByDefault;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import org.apache.commons.lang3.function.TriFunction;


//An arbitrary menu handler for e.g. guiclass.
public abstract class MenuHandler<T extends AbstractContainerMenu & IAntimatterContainer> implements IAntimatterObject {

    protected ResourceLocation loc;
    private MenuType<T> containerType;

    public MenuHandler(String domain, String id) {
        loc = new ResourceLocation(domain, id);
        AntimatterAPI.register(MenuHandler.class, this);
        MenuType<?> type = getContainerType();
        AntimatterAPI.register(MenuType.class, id, domain, type);
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
            containerType = create(this::onContainerCreate);
        }
        return containerType;
    }

    static <T extends AbstractContainerMenu> MenuType<T> create(TriFunction<Integer, Inventory, FriendlyByteBuf, T> factory) {
        return AntimatterPlatformUtils.create(factory);
    }

    public abstract T onContainerCreate(int windowId, Inventory inv, FriendlyByteBuf data);

    public String screenID(){
        return "default";
    }

    public String screenDomain(){
        return Ref.ID;
    }
}
