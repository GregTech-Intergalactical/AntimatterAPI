package muramasa.antimatter.registration;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.integration.kubejs.AntimatterKubeJS;
import muramasa.antimatter.recipe.condition.ConfigCondition;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.material.MaterialSerializer;
import muramasa.antimatter.recipe.serializer.RecipeSerializer;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.worldgen.feature.AntimatterFeature;
import net.minecraft.block.Block;
import net.minecraft.inventory.container.ContainerType;
import net.minecraft.item.Item;
import net.minecraft.item.crafting.IRecipeSerializer;
import net.minecraft.tileentity.TileEntityType;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;

import java.util.List;
import java.util.stream.Collectors;

public final class AntimatterRegistration {
    public static Dist side;

    @SubscribeEvent
    public static void onRegister(final RegistryEvent.Register<?> e) {
        final String domain = ModLoadingContext.get().getActiveNamespace();
        onRegister(domain, e);
        if (domain.equals(Ref.ID)) {
            onRegister(Ref.MOD_KJS, e);
            onRegister(Ref.SHARED_ID, e);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onRegister(final String domain, final RegistryEvent.Register<?> e) {

        if (domain.equals(Ref.ID)) {
            if (e.getRegistry() == ForgeRegistries.BLOCKS) {
                AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
                if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
                    AntimatterKubeJS.loadStartupScripts();
                }
                Data.postInit(side);
            }
            AntimatterAPI.all(IRegistryEntryProvider.class, domain, p -> p.onRegistryBuild(e.getRegistry()));
            AntimatterAPI.all(IRegistryEntryProvider.class, Ref.SHARED_ID, p -> p.onRegistryBuild(e.getRegistry()));
            List<IAntimatterRegistrar> list = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).collect(Collectors.toList());
            list.forEach(r -> AntimatterAPI.all(IRegistryEntryProvider.class, r.getDomain(), p -> p.onRegistryBuild(e.getRegistry())));
        }
        if (e.getRegistry() == ForgeRegistries.BLOCKS) {
            AntimatterAPI.all(Block.class, domain, b -> {
                if (b instanceof IAntimatterObject && b.getRegistryName() == null)
                    b.setRegistryName(((IAntimatterObject) b).getDomain(), ((IAntimatterObject) b).getId());
                if (!(b instanceof IItemBlockProvider) || ((IItemBlockProvider)b).generateItemBlock()) {
                    AntimatterAPI.register(Item.class, b.getRegistryName().getPath(), b.getRegistryName().getNamespace(), b instanceof IItemBlockProvider ? ((IItemBlockProvider) b).getItemBlock() : new AntimatterItemBlock(b));
                }
                ((IForgeRegistry) e.getRegistry()).register(b);
            });

        } else if (e.getRegistry() == ForgeRegistries.ITEMS) {
            AntimatterAPI.all(Item.class, domain, i -> {
                if (i instanceof IAntimatterObject && i.getRegistryName() == null)
                    i.setRegistryName(((IAntimatterObject) i).getDomain(), ((IAntimatterObject) i).getId());
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
            AntimatterAPI.all(ContainerType.class, domain, h -> ((IForgeRegistry) e.getRegistry()).register(h));
        } else if (e.getRegistry() == ForgeRegistries.SOUND_EVENTS) {
            //TODO better solution for this
            if (domain.equals(Ref.ID)) ((IForgeRegistry) e.getRegistry()).registerAll(Ref.DRILL, Ref.WRENCH);
        } else if (e.getRegistry() == ForgeRegistries.RECIPE_SERIALIZERS) {
            //TODO better solution for this
            if (domain.equals(Ref.ID)) {
                CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
                CraftingHelper.register(new ResourceLocation("antimatter", "material"), PropertyIngredient.Serializer.INSTANCE);
                ((IForgeRegistry<IRecipeSerializer<?>>) e.getRegistry()).register(RecipeSerializer.INSTANCE);
                ((IForgeRegistry<IRecipeSerializer<?>>) e.getRegistry()).register(MaterialSerializer.INSTANCE);
            }
        } else if (e.getRegistry() == ForgeRegistries.FEATURES) {
            AntimatterAPI.all(AntimatterFeature.class, domain, ForgeRegistries.FEATURES::register);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerTools(String domain, IForgeRegistry registry) {
        AntimatterAPI.all(AntimatterToolType.class, domain, t -> {
            if (t.isPowered()) {
                for (IAntimatterTool i : t.instantiatePoweredTools(domain)) {
                    if (i.getItem().getRegistryName() == null) i.getItem().setRegistryName(domain, i.getId());
                    registry.register(i.getItem());
                }
            } else {
                IAntimatterTool i = t.instantiateTools(domain);
                if (i.getItem().getRegistryName() == null) i.getItem().setRegistryName(domain, i.getId());
                registry.register(i.getItem());
            }
        });
        AntimatterAPI.all(AntimatterArmorType.class, domain, t -> {
            IAntimatterArmor i = t.instantiateTools(domain);
            if (i.getItem().getRegistryName() == null) i.getItem().setRegistryName(domain, i.getId());
            registry.register(i.getItem());
        });
    }
}
