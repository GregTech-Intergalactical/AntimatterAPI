package muramasa.antimatter.integration.jeirei;

import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.integration.rei.REIUtils;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.map.IRecipeMap;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraftforge.fluids.FluidStack;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AntimatterJEIREIPlugin{
    private static final List<ItemLike> ITEMS_TO_HIDE = new ArrayList<>();
    public static class RegistryValue {
        public IRecipeMap map;
        public GuiData gui;
        public Tier tier;
        public ResourceLocation model;

        public RegistryValue(IRecipeMap map, GuiData gui, Tier tier, ResourceLocation model) {
            this.map = map;
            this.gui = gui;
            this.tier = tier;
            this.model = model;
        }
    }

    private static final Object2ObjectMap<ResourceLocation, RegistryValue> REGISTRY = new Object2ObjectLinkedOpenHashMap<>();

    public static void registerCategory(IRecipeMap map, GuiData gui, Tier tier, ResourceLocation model, boolean override) {
        if (REGISTRY.containsKey(new ResourceLocation(map.getDomain(), map.getId())) && !override) {
            Antimatter.LOGGER.info("Attempted duplicate category registration: " + map.getId());
            return;
        }
        REGISTRY.put(new ResourceLocation(map.getDomain(), map.getId()), new RegistryValue(map, map.getGui() == null ? gui : map.getGui(), tier, model));//new Tuple<>(map, new Tuple<>(gui, tier)));
    }

    public static void registerCategoryModel(IRecipeMap map, ResourceLocation model, Tier tier){
        RegistryValue value = REGISTRY.get(map.getLoc());
        if (value != null) {
            if (value.model == null) {
                value.model = model;
            }
            if (value.tier != tier){
                value.tier = tier;
            }
        }
    }

    public static Object2ObjectMap<ResourceLocation, RegistryValue> getREGISTRY() {
        return REGISTRY;
    }

    public static void showCategory(Machine<?>... types) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_JEI) && !AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            AntimatterJEIPlugin.showCategory(types);
        } else if (AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            REIUtils.showCategory(types);
        }
    }

    //To perform a JEI lookup for fluid. Use defines direction.

    public static void uses(FluidStack val, boolean USE) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_JEI) && !AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            AntimatterJEIPlugin.uses(val, USE);
        } else if (AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            REIUtils.uses(val, USE);
        }
    }

    public static <T> void addModDescriptor(List<Component> tooltip, T t) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_JEI) && !AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            AntimatterJEIPlugin.addModDescriptor(tooltip, t);
        } else if (AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            REIUtils.addModDescriptor(tooltip, t);
        }
    }

    public static void addItemsToHide(ItemLike... itens){
        ITEMS_TO_HIDE.addAll(Arrays.asList(itens));
    }

    public static List<ItemLike> getItemsToHide() {
        return ITEMS_TO_HIDE;
    }
}
