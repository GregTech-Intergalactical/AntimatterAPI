package muramasa.antimatter.registration.forge;

import com.google.gson.JsonObject;
import muramasa.antimatter.*;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.integration.kubejs.AntimatterKubeJS;
import muramasa.antimatter.recipe.forge.condition.ConfigCondition;
import muramasa.antimatter.recipe.forge.condition.TomlConfigCondition;
import muramasa.antimatter.recipe.ingredient.IAntimatterIngredientSerializer;
import muramasa.antimatter.registration.*;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.worldgen.feature.IAntimatterFeature;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.common.crafting.IIngredientSerializer;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.ModContainer;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.registries.ForgeRegistries;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.RegisterEvent;

import java.util.List;

@Mod.EventBusSubscriber(modid = Ref.ID, bus = Mod.EventBusSubscriber.Bus.MOD)
public final class AntimatterRegistration {

    @SubscribeEvent
    public static void onRegister(final RegisterEvent e) {
        final String domain = ModLoadingContext.get().getActiveNamespace();
        List<IAntimatterRegistrar> list2 = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).toList();
        if (list2.size() < 4) {
            Antimatter.LOGGER.info("Mod ID: " + domain + " & event: " + e.getRegistryKey().location());
        }
        onRegister(domain, e);
        onRegister(Ref.SHARED_ID, e);
        List<IAntimatterRegistrar> list = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).toList();
        list.forEach(r -> {
            onRegister(r.getId(), e);
        });
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void onRegister(final String domain, RegisterEvent e) {
        ModContainer previous = ModLoadingContext.get().getActiveContainer();
        ModContainer newContainer = ModList.get().getModContainerById(domain).orElse(null);
        if (newContainer == null) return;
        if (!domain.equals(Ref.ID)){
            ModLoadingContext.get().setActiveContainer(newContainer);
        }
        if (domain.equals(Ref.ID)) {
            List<IAntimatterRegistrar> list = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).filter(IAntimatterRegistrar::isEnabled).toList();
            if (e.getRegistryKey() == ForgeRegistries.Keys.BLOCKS) {
                AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
                MaterialEvent event = new MaterialEvent();
                MaterialDataInit.onMaterialEvent(event);
                list.forEach(r -> r.onMaterialEvent(event));
                if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
                    AntimatterKubeJS.loadMaterialEvent(event);
                }
                Data.postInit();
            }
            AntimatterAPI.all(IRegistryEntryProvider.class, domain, p -> p.onRegistryBuild(getRegistryType(e.getRegistryKey())));
            AntimatterAPI.all(IRegistryEntryProvider.class, Ref.SHARED_ID, p -> p.onRegistryBuild(getRegistryType(e.getRegistryKey())));
            list.forEach(r -> AntimatterAPI.all(IRegistryEntryProvider.class, r.getDomain(), p -> p.onRegistryBuild(getRegistryType(e.getRegistryKey()))));
        }
        if (e.getRegistryKey() == ForgeRegistries.Keys.BLOCKS) {
            AntimatterAPI.all(Block.class, domain, (b, d, i) -> {
                if (!(b instanceof IItemBlockProvider pb) || pb.generateItemBlock()) {
                    AntimatterAPI.register(Item.class, i, d, b instanceof IItemBlockProvider pb ? pb.getItemBlock() : new AntimatterItemBlock(b));
                }
                ForgeRegistries.BLOCKS.register(new ResourceLocation(d, i), b);
            });

        } else if (e.getRegistryKey() == ForgeRegistries.Keys.ITEMS) {
            AntimatterAPI.all(Item.class, domain, (i, d, id) -> {
                ForgeRegistries.ITEMS.register(new ResourceLocation(d, id), i);
            });
            if (domain.equals(Ref.SHARED_ID)) registerTools(domain, ForgeRegistries.ITEMS);
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.BLOCK_ENTITY_TYPES) {
            AntimatterAPI.all(BlockEntityType.class, domain, (t, d, i) -> {
                ForgeRegistries.BLOCK_ENTITY_TYPES.register(new ResourceLocation(d, i), t);
            });
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.FLUIDS) {
            AntimatterAPI.all(AntimatterFluid.class, domain, f -> {
                ForgeRegistries.FLUIDS.register(new ResourceLocation(domain, f.getId()), f.getFluid());
                ForgeRegistries.FLUIDS.register(new ResourceLocation(domain, "flowing_" + f.getId()), f.getFlowingFluid());
            });
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.MENU_TYPES) {
            AntimatterAPI.all(MenuType.class, domain, (h, d, i) -> {
                ForgeRegistries.MENU_TYPES.register(new ResourceLocation(d, i), h);
            });
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.SOUND_EVENTS) {
            AntimatterAPI.all(SoundEvent.class, domain, (t, d, i) -> {
                ForgeRegistries.SOUND_EVENTS.register(new ResourceLocation(d, i), t);
            });
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.RECIPE_SERIALIZERS) {
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
                ForgeRegistries.RECIPE_SERIALIZERS.register(new ResourceLocation(d, i), r);
            });
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.FEATURES) {
            AntimatterAPI.all(IAntimatterFeature.class, domain,(t, d, i) -> {
                ForgeRegistries.FEATURES.register(new ResourceLocation(d, i), t.asFeature());
            });
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.ENCHANTMENTS){
            AntimatterAPI.all(Enchantment.class, domain, (en, d, i) -> {
                ForgeRegistries.ENCHANTMENTS.register(new ResourceLocation(d, i), en);
            });
        }else if (e.getRegistryKey() == ForgeRegistries.Keys.BIOME_MODIFIERS){
            e.getForgeRegistry().register(new ResourceLocation(Ref.ID, "modifier"), new AntimatterBiomeModifier());
        } else if (e.getRegistryKey() == ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS){
            e.getForgeRegistry().register(new ResourceLocation(Ref.ID, "modifier"), AntimatterBiomeModifier.CODEC);
        }
        if (!domain.equals(Ref.ID)){
            ModLoadingContext.get().setActiveContainer(previous);
        }
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public static void registerTools(String domain, IForgeRegistry registry) {
        AntimatterAPI.all(AntimatterToolType.class, t -> {
            List<IAntimatterTool> tools = t.isPowered() ? t.instantiatePoweredTools(domain) : t.instantiateTools(domain);
            for (IAntimatterTool i : tools) {
                registry.register(i.getLoc(), i.getItem());
            }
        });
        AntimatterAPI.all(AntimatterArmorType.class, t -> {
            List<IAntimatterArmor> i = t.instantiateTools();
            i.forEach(a -> {
                registry.register(a.getLoc(), a.getItem());
            });

        });
    }

    public static RegistryType getRegistryType(ResourceKey<?> registry){
        if (registry == ForgeRegistries.Keys.BLOCKS) return RegistryType.BLOCKS;
        if (registry == ForgeRegistries.Keys.ITEMS) return RegistryType.ITEMS;
        if (registry == ForgeRegistries.Keys.FLUIDS) return RegistryType.FLUIDS;
        if (registry == ForgeRegistries.Keys.BLOCK_ENTITY_TYPES) return RegistryType.BLOCK_ENTITIES;
        return RegistryType.WORLD;
    }
}
