package muramasa.antimatter.integration.jeirei;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;

public class AntimatterJEIREIPlugin{
    private static final List<Consumer<List<ItemLike>>> ITEMS_TO_HIDE = new ArrayList<>();
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

    public static String intToSuperScript(long i){
        String intString = String.valueOf(i);
        StringBuilder builder = new StringBuilder();
        for (char c : intString.toCharArray()) {
            builder.append(charToSuperScript(c));
        }
        return builder.toString();
    }

    private static String charToSuperScript(char c){
        return switch (c){
            case '0' -> "⁰";
            case '1' -> "¹";
            case '2' -> "²";
            case '3' -> "³";
            case '4' -> "⁴";
            case '5' -> "⁵";
            case '6' -> "⁶";
            case '7' -> "⁷";
            case '8' -> "⁸";
            case '9' -> "⁹";
            default -> String.valueOf(c);
        };
    }

    public static Object2ObjectMap<ResourceLocation, RegistryValue> getREGISTRY() {
        return REGISTRY;
    }

    public static void showCategory(Machine<?> type, Tier tier) {
        if (AntimatterAPI.isModLoaded(Ref.MOD_JEI) && !AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            AntimatterJEIPlugin.showCategory(type, tier);
        } else if (AntimatterAPI.isModLoaded(Ref.MOD_REI)){
            REIUtils.showCategory(type, tier);
        }
    }

    //To perform a JEI lookup for fluid. Use defines direction.

    public static void uses(FluidHolder val, boolean USE) {
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

    public static void addItemsToHide(ItemLike... items){
        addItemsToHide(l -> {
            l.addAll(Arrays.asList(items));
        });
    }

    public static void addItemsToHide(Consumer<List<ItemLike>> listConsumer){
        ITEMS_TO_HIDE.add(listConsumer);
    }

    public static List<Consumer<List<ItemLike>>> getItemsToHide() {
        return ITEMS_TO_HIDE;
    }
}
