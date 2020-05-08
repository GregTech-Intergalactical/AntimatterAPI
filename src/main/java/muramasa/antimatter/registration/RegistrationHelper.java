package muramasa.antimatter.registration;

import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;

@Mod.EventBusSubscriber(bus=Mod.EventBusSubscriber.Bus.MOD)
public class RegistrationHelper {

    private static List<BlockItem> ITEM_BLOCKS = new ObjectArrayList<>();

    @SubscribeEvent
    public static void onRegistryEvent(RegistryEvent.Register<?> e) {
        String domain = ModLoadingContext.get().getActiveNamespace();
        if (e.getRegistry() == ForgeRegistries.BLOCKS && domain.equals(Ref.ID)) AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);

        AntimatterAPI.all(IRegistryEntryProvider.class, p -> p.onRegistryBuild(domain, e.getRegistry()));
        if (e.getRegistry() == ForgeRegistries.BLOCKS) {
            AntimatterAPI.all(Block.class, domain, b -> {
                if (b instanceof IAntimatterObject) b.setRegistryName(domain, ((IAntimatterObject) b).getId());
                ITEM_BLOCKS.add(b instanceof IItemBlockProvider ? ((IItemBlockProvider) b).getItemBlock() : new AntimatterItemBlock(b));
                ((IForgeRegistry) e.getRegistry()).register(b);
            });
        } else if (e.getRegistry() == ForgeRegistries.ITEMS) {
            ITEM_BLOCKS.forEach(i -> {
                if (i.getRegistryName() != null && i.getRegistryName().getNamespace().equals(domain)) {
                    ((IForgeRegistry) e.getRegistry()).register(i);
                }
            });
            AntimatterAPI.all(Item.class, domain, i -> {
                if (i instanceof IAntimatterObject) i.setRegistryName(domain, ((IAntimatterObject) i).getId());
                ((IForgeRegistry) e.getRegistry()).register(i);
            });
            AntimatterAPI.all(AntimatterToolType.class, domain, t -> {
                if (t.isPowered()) {
                    for (IAntimatterTool i : t.instantiatePoweredTools(domain)) {
                        i.asItem().setRegistryName(domain, i.getId());
                        ((IForgeRegistry) e.getRegistry()).register(i.asItem());
                    }
                }
                else {
                    IAntimatterTool i = t.instantiateTools(domain);
                    i.asItem().setRegistryName(domain, i.getId());
                    ((IForgeRegistry) e.getRegistry()).register(i.asItem());
                }
            });
        }
    }
}
