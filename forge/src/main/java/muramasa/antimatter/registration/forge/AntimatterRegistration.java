package muramasa.antimatter.registration.forge;

import com.google.gson.JsonObject;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.MaterialDataInit;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.integration.kubejs.AntimatterKubeJS;
import muramasa.antimatter.recipe.forge.condition.ConfigCondition;
import muramasa.antimatter.recipe.forge.condition.TomlConfigCondition;
import muramasa.antimatter.recipe.ingredient.IAntimatterIngredientSerializer;
import muramasa.antimatter.recipe.ingredient.IngredientSerializer;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.recipe.material.MaterialSerializer;
import muramasa.antimatter.recipe.serializer.AntimatterRecipeSerializer;
import muramasa.antimatter.registration.*;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.worldgen.feature.IAntimatterFeature;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import java.util.List;

@Mod.EventBusSubscriber(modid = Ref.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AntimatterRegistration {

    @SubscribeEvent
    public static void onRegister(final RegistryEvent.Register<?> e) {
        final String domain = ModLoadingContext.get().getActiveNamespace();
        List<IAntimatterRegistrar> list2 = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).toList();
        if (list2.size() < 4) {
            Antimatter.LOGGER.info("Mod ID: " + domain + " & event: " + e.getRegistry().getRegistryName());
        }
        onRegister(domain, e);
        onRegister(Ref.SHARED_ID, e);
        List<IAntimatterRegistrar> list = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).toList();
        list.forEach(r -> {
            onRegister(r.getId(), e);
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onRegister(final String domain, final RegistryEvent.Register<?> e) {
        ModContainer previous = ModLoadingContext.get().getActiveContainer();
        ModContainer newContainer = ModList.get().getModContainerById(domain).orElse(null);
        if (newContainer == null) return;
        if (!domain.equals(Ref.ID)){
            ModLoadingContext.get().setActiveContainer(newContainer);
        }
        if (domain.equals(Ref.ID)) {
            List<IAntimatterRegistrar> list = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).toList();
            if (e.getRegistry() == ForgeRegistries.BLOCKS) {
                AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
                AntimatterAPI.all(SoundEvent.class, t -> {
                    if (t.getRegistryName() == null) t.setRegistryName(t.getLocation());
                });
                MaterialEvent event = new MaterialEvent();
                MaterialDataInit.onMaterialEvent(event);
                list.forEach(r -> r.onMaterialEvent(event));
                if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
                    AntimatterKubeJS.loadMaterialEvent(event);
                }
                Data.postInit();
            }
            AntimatterAPI.all(IRegistryEntryProvider.class, domain, p -> p.onRegistryBuild(getRegistryType(e.getRegistry())));
            AntimatterAPI.all(IRegistryEntryProvider.class, Ref.SHARED_ID, p -> p.onRegistryBuild(getRegistryType(e.getRegistry())));
            list.forEach(r -> AntimatterAPI.all(IRegistryEntryProvider.class, r.getDomain(), p -> p.onRegistryBuild(getRegistryType(e.getRegistry()))));
        }
        if (e.getRegistry() == ForgeRegistries.BLOCKS) {
            AntimatterAPI.all(Block.class, domain, (b, d, i) -> {
                if (b.getRegistryName() == null)
                    b.setRegistryName(d, i);
                if (!(b instanceof IItemBlockProvider pb) || pb.generateItemBlock()) {
                    AntimatterAPI.register(Item.class, i, d, b instanceof IItemBlockProvider pb ? pb.getItemBlock() : new AntimatterItemBlock(b));
                }
                ((IForgeRegistry) e.getRegistry()).register(b);
            });

        } else if (e.getRegistry() == ForgeRegistries.ITEMS) {
            AntimatterAPI.all(Item.class, domain, (i, d, id) -> {
                if (i.getRegistryName() == null)
                    i.setRegistryName(d, id);
                ((IForgeRegistry) e.getRegistry()).register(i);
            });
            registerTools(domain, e.getRegistry());
        } else if (e.getRegistry() == ForgeRegistries.BLOCK_ENTITIES) {
            AntimatterAPI.all(BlockEntityType.class, domain, (t, d, i) -> {
                if (t.getRegistryName() == null) t.setRegistryName(d, i);
                ((IForgeRegistry) e.getRegistry()).register(t);
            });
        } else if (e.getRegistry() == ForgeRegistries.FLUIDS) {
            AntimatterAPI.all(AntimatterFluid.class, domain, f -> {
                if (f.getFluid().getRegistryName() == null) f.getFluid().setRegistryName(domain, f.getId());
                if (f.getFlowingFluid().getRegistryName() == null) f.getFlowingFluid().setRegistryName(domain, "flowing_".concat(f.getId()));
                ((IForgeRegistry) e.getRegistry()).registerAll(f.getFluid(), f.getFlowingFluid());
            });
        } else if (e.getRegistry() == ForgeRegistries.CONTAINERS) {
            AntimatterAPI.all(MenuType.class, domain, (h, d, i) -> {
                if (h.getRegistryName() == null) h.setRegistryName(d, i);
                ((IForgeRegistry) e.getRegistry()).register(h);
            });
        } else if (e.getRegistry() == ForgeRegistries.SOUND_EVENTS) {
            AntimatterAPI.all(SoundEvent.class, domain, (t, d, i) -> {
                if (t.getRegistryName() == null) t.setRegistryName(d, i);
                ((IForgeRegistry) e.getRegistry()).register(t);
            });
        } else if (e.getRegistry() == ForgeRegistries.RECIPE_SERIALIZERS) {
            //TODO better solution for this
            AntimatterAPI.all(IAntimatterIngredientSerializer.class, domain, (s, d, i) -> {
                IIngredientSerializer<?> serializer = new IIngredientSerializer() {
                    @Override
                    public Ingredient parse(FriendlyByteBuf arg) {
                        return s.parse(arg);
                    }

                    @Override
                    public Ingredient parse(JsonObject jsonObject) {
                        return s.parse(jsonObject);
                    }

                    @Override
                    public void write(FriendlyByteBuf arg, Ingredient arg2) {
                        s.write(arg, arg2);
                    }
                };
                AntimatterAPI.register(IIngredientSerializer.class, i, d, serializer);
                CraftingHelper.register(new ResourceLocation(d, i), serializer);
            });
            if (domain.equals(Ref.ID)) {
                CraftingHelper.register(ConfigCondition.Serializer.INSTANCE);
                CraftingHelper.register(TomlConfigCondition.Serializer.INSTANCE);
            }
            AntimatterAPI.all(RecipeSerializer.class, domain, (r, d, i) -> {
                if (r.getRegistryName() == null){
                    r.setRegistryName(new ResourceLocation(d, i));
                }
                ((IForgeRegistry) e.getRegistry()).register(r);
            });
        } else if (e.getRegistry() == ForgeRegistries.FEATURES) {
            AntimatterAPI.all(IAntimatterFeature.class, domain,(t, d, i) -> {
                if (t.asFeature().getRegistryName() == null) t.asFeature().setRegistryName(d, i);
                ((IForgeRegistry) e.getRegistry()).register(t.asFeature());
            });
        }
        if (!domain.equals(Ref.ID)){
            ModLoadingContext.get().setActiveContainer(previous);
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

    public static RegistryType getRegistryType(IForgeRegistry<?> registry){
        if (registry == ForgeRegistries.BLOCKS) return RegistryType.BLOCKS;
        if (registry == ForgeRegistries.ITEMS) return RegistryType.ITEMS;
        if (registry == ForgeRegistries.FLUIDS) return RegistryType.FLUIDS;
        if (registry == ForgeRegistries.BLOCK_ENTITIES) return RegistryType.BLOCK_ENTITIES;
        return RegistryType.WORLD;
    }
}
