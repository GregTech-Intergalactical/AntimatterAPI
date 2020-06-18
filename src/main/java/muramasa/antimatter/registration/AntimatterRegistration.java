package muramasa.antimatter.registration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.gui.MenuHandlerCover;
import muramasa.antimatter.gui.MenuHandlerMachine;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterTool;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.tileentity.TileEntityType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

public final class AntimatterRegistration {

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onRegister(final RegistryEvent.Register<?> e) {
        final String domain = ModLoadingContext.get().getActiveNamespace();
        if (e.getRegistry() == ForgeRegistries.BLOCKS && domain.equals(Ref.ID)) {
            AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
        }
        AntimatterAPI.all(IRegistryEntryProvider.class, domain, p -> p.onRegistryBuild(e.getRegistry()));
        if (e.getRegistry() == ForgeRegistries.BLOCKS) {
            AntimatterAPI.all(Block.class, domain, b -> {
                if (b instanceof IAntimatterObject) b.setRegistryName(domain, ((IAntimatterObject) b).getId());
                AntimatterAPI.register(Item.class, b.getRegistryName().toString(), b instanceof IItemBlockProvider ? ((IItemBlockProvider) b).getItemBlock() : new AntimatterItemBlock(b));
                ((IForgeRegistry) e.getRegistry()).register(b);
            });
        } else if (e.getRegistry() == ForgeRegistries.ITEMS) {
            AntimatterAPI.all(Item.class, domain, i -> {
                if (i instanceof IAntimatterObject) i.setRegistryName(domain, ((IAntimatterObject) i).getId());
                ((IForgeRegistry) e.getRegistry()).register(i);
            });
            registerTools(domain, e.getRegistry());
        } else if (e.getRegistry() == ForgeRegistries.TILE_ENTITIES) {
            AntimatterAPI.all(TileEntityType.class, domain, t -> ((IForgeRegistry) e.getRegistry()).register(t));
        } else if (e.getRegistry() == ForgeRegistries.FLUIDS) {
            AntimatterAPI.all(AntimatterFluid.class, domain, f -> {
                ((IForgeRegistry) e.getRegistry()).registerAll(f.getFluid(), f.getFlowingFluid());
            });
        } else if (e.getRegistry() == ForgeRegistries.CONTAINERS) {
            AntimatterAPI.all(MenuHandlerMachine.class, domain, h -> ((IForgeRegistry) e.getRegistry()).register(h.getContainerType()));
            AntimatterAPI.all(MenuHandlerCover.class, domain, h -> ((IForgeRegistry) e.getRegistry()).register(h.getContainerType()));
        } else if (e.getRegistry() == ForgeRegistries.SOUND_EVENTS) {
            //TODO better solution for this
            if (domain.equals(Ref.ID)) ((IForgeRegistry) e.getRegistry()).registerAll(Ref.DRILL, Ref.WRENCH);
        } else if (e.getRegistry() == ForgeRegistries.RECIPE_SERIALIZERS) {
            //TODO better solution for this
            if (domain.equals(Ref.ID)) CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerTools(String domain, IForgeRegistry registry) {
        AntimatterAPI.all(AntimatterToolType.class, domain, t -> {
            if (t.isPowered()) {
                for (IAntimatterTool i : t.instantiatePoweredTools(domain)) {
                    i.getItem().setRegistryName(domain, i.getId());
                    registry.register(i.getItem());
                }
            }
            else {
                IAntimatterTool i = t.instantiateTools(domain);
                i.getItem().setRegistryName(domain, i.getId());
                registry.register(i.getItem());
            }
        });
    }
}
