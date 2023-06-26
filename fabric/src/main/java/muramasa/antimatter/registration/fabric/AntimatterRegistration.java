package muramasa.antimatter.registration.fabric;



import com.google.gson.JsonObject;
import io.github.fabricators_of_create.porting_lib.crafting.CraftingHelper;
import io.github.tropheusj.serialization_hooks.ingredient.IngredientDeserializer;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Data;
import muramasa.antimatter.MaterialDataInit;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.AntimatterItemBlock;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.fabric.AntimatterAPIImpl;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.integration.kubejs.AntimatterKubeJS;
import muramasa.antimatter.recipe.ingredient.IAntimatterIngredientSerializer;
import muramasa.antimatter.recipe.ingredient.IngredientSerializer;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.material.MaterialSerializer;
import muramasa.antimatter.recipe.serializer.AntimatterRecipeSerializer;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.IItemBlockProvider;
import muramasa.antimatter.registration.IRegistryEntryProvider;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.antimatter.registration.RegistryType;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.tool.armor.AntimatterArmorType;
import muramasa.antimatter.worldgen.feature.IAntimatterFeature;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.crafting.Ingredient;
import net.minecraft.world.item.crafting.RecipeSerializer;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntityType;

import java.util.List;

public class AntimatterRegistration {

    public static void onRegister(){
        onRegister(Ref.ID);
        onRegister(Ref.SHARED_ID);
        AntimatterAPI.all(IAntimatterRegistrar.class).stream().filter(a -> FabricLoader.getInstance().isModLoaded(a.getDomain())).forEach(a -> {
            onRegister(a.getDomain());
        });
    }

    public static void onRegister(final String domain) {
        if (domain.equals(Ref.ID)) {
            AntimatterAPI.onRegistration(RegistrationEvent.DATA_INIT);
            List<IAntimatterRegistrar> list = AntimatterAPI.all(IAntimatterRegistrar.class).stream().sorted((c1, c2) -> Integer.compare(c2.getPriority(), c1.getPriority())).toList();
            MaterialEvent event = new MaterialEvent();
            MaterialDataInit.onMaterialEvent(event);
            list.forEach(r -> r.onMaterialEvent(event));
            if (AntimatterAPI.isModLoaded(Ref.MOD_KJS)) {
                AntimatterKubeJS.loadMaterialEvent(event);
            }
            Data.postInit();
            for (RegistryType type : RegistryType.values()){
                AntimatterAPI.all(IRegistryEntryProvider.class, domain, p -> p.onRegistryBuild(type));
                AntimatterAPI.all(IRegistryEntryProvider.class, Ref.SHARED_ID, p -> p.onRegistryBuild(type));
                list.forEach(r -> AntimatterAPI.all(IRegistryEntryProvider.class, r.getDomain(), p -> p.onRegistryBuild(type)));
            }
        }
        AntimatterAPI.all(Block.class, domain, (b, d, i) -> {
            if (!(b instanceof IItemBlockProvider bp) || bp.generateItemBlock()) {
                AntimatterAPI.register(Item.class, i, d, b instanceof IItemBlockProvider bp ? bp.getItemBlock() : new AntimatterItemBlock(b));
            }
            Registry.register(Registry.BLOCK, new ResourceLocation(d, i), b);
        });
        AntimatterAPI.all(Item.class, domain, (i, d, id) -> {
            Registry.register(Registry.ITEM, new ResourceLocation(d, id), i);
            AntimatterAPIImpl.registerItemTransferAPI(i);
        });
        registerTools(domain);
        AntimatterAPI.all(BlockEntityType.class, domain, (t, d, i) -> {
            Registry.register(Registry.BLOCK_ENTITY_TYPE, new ResourceLocation(d, i), t);
        });
        AntimatterAPI.all(AntimatterFluid.class, domain, f -> {
            Registry.register(Registry.FLUID, new ResourceLocation(domain, f.getId()), f.getFluid());
            Registry.register(Registry.FLUID, new ResourceLocation(domain, "flowing_" + f.getId()), f.getFlowingFluid());
        });
        AntimatterAPI.all(MenuType.class, domain, (h, d, i) -> {
            Registry.register(Registry.MENU, new ResourceLocation(d, i), h);
        });
        AntimatterAPI.all(SoundEvent.class, domain, (t, d, i) -> {
            Registry.register(Registry.SOUND_EVENT, new ResourceLocation(d, i), t);
        });
        //TODO porting lib compat
        AntimatterAPI.all(IAntimatterIngredientSerializer.class, domain, (s, d, i) -> {
            CraftingHelper.register(new ResourceLocation(d, i), new IngredientDeserializer() {
                @Override
                public Ingredient fromNetwork(FriendlyByteBuf buffer) {
                    return s.parse(buffer);
                }

                @Override
                public Ingredient fromJson(JsonObject object) {
                    return s.parse(object);
                }
            });
        });
        AntimatterAPI.all(RecipeSerializer.class, domain, (r, d, i) -> {
            Registry.register(Registry.RECIPE_SERIALIZER, new ResourceLocation(d, i), r);
        });
        AntimatterAPI.all(IAntimatterFeature.class, domain,(t, d, i) -> {
            Registry.register(Registry.FEATURE, new ResourceLocation(d, i), t.asFeature());
        });
    }

    public static void registerTools(String domain) {
        AntimatterAPI.all(AntimatterToolType.class, domain, t -> {
            if (t.isPowered()) {
                for (IAntimatterTool i : t.instantiatePoweredTools(domain)) {
                    Registry.register(Registry.ITEM, new ResourceLocation(domain, i.getId()), i.getItem());
                }
            } else {
                IAntimatterTool i = t.instantiateTools(domain);
                Registry.register(Registry.ITEM, new ResourceLocation(domain, i.getId()), i.getItem());
            }
        });
        AntimatterAPI.all(AntimatterArmorType.class, domain, t -> {
            List<IAntimatterArmor> i = t.instantiateTools(domain);
            i.forEach(a -> {
                Registry.register(Registry.ITEM, new ResourceLocation(Ref.SHARED_ID, a.getId()), a.getItem());
            });

        });
    }
}
