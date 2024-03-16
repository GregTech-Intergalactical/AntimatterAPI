package muramasa.antimatter.integration.jeirei;

import earth.terrarium.botarium.common.fluid.base.FluidHolder;
import it.unimi.dsi.fastutil.objects.Object2ObjectLinkedOpenHashMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectOpenHashMap;
import lombok.Getter;
import muramasa.antimatter.Antimatter;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.gui.GuiData;
import muramasa.antimatter.gui.slot.ISlotProvider;
import muramasa.antimatter.integration.create.client.PonderIntegration;
import muramasa.antimatter.integration.jei.AntimatterJEIPlugin;
import muramasa.antimatter.integration.rei.REIUtils;
import muramasa.antimatter.machine.BlockMachine;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.recipe.map.IRecipeMap;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.structure.Pattern;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.material.Fluid;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Supplier;

import static muramasa.antimatter.gui.SlotType.*;
import static muramasa.antimatter.gui.SlotType.FL_OUT;

public class AntimatterJEIREIPlugin{
    private static final List<Consumer<List<ItemLike>>> ITEMS_TO_HIDE = new ArrayList<>();
    private static final List<Consumer<List<Fluid>>> FLUIDS_TO_HIDE = new ArrayList<>();
    @Getter
    private static final Map<BlockMachine, List<Pattern>> STRUCTURES = new Object2ObjectOpenHashMap<>();
    
    public static final GuiData BACKUP_MAP_GUI = new GuiData(Ref.ID, "backup_map").setSlots(ISlotProvider.DEFAULT()
            .add(IT_IN, 17, 16).add(IT_IN, 35, 16).add(IT_IN, 53, 16).add(IT_IN, 17, 34).add(IT_IN, 35, 34)
            .add(IT_IN, 53, 34).add(IT_OUT, 107, 16).add(IT_OUT, 125, 16).add(IT_OUT, 143, 16).add(IT_OUT, 107, 34)
            .add(IT_OUT, 125, 34).add(IT_OUT, 143, 34).add(FL_IN, 17, 63).add(FL_IN, 35, 63).add(FL_IN, 53, 63)
            .add(FL_OUT, 107, 63).add(FL_OUT, 125, 63).add(FL_OUT, 143, 63));
    public static class RegistryValue {
        public IRecipeMap map;
        public GuiData gui;
        public Tier tier;
        public List<ResourceLocation> workstations = new ArrayList<>();

        public RegistryValue(IRecipeMap map, GuiData gui, Tier tier) {
            this.map = map;
            this.gui = gui;
            this.tier = tier;
        }

        public RegistryValue addWorkstation(ResourceLocation supplier){
            if (supplier != null && !workstations.contains(supplier)) {
                workstations.add(supplier);
            }
            return this;
        }
    }

    @Getter
    private static final Object2ObjectMap<ResourceLocation, RegistryValue> REGISTRY = new Object2ObjectLinkedOpenHashMap<>();

    public static void registerMissingMaps(){
        AntimatterAPI.all(RecipeMap.class).forEach(r -> {
            if (!REGISTRY.containsKey(r.getLoc())){
                registerCategory(r, BACKUP_MAP_GUI, Tier.LV, null, false);
            }
        });
    }

    public static void registerCategory(IRecipeMap map, GuiData gui, Tier tier, ResourceLocation model, boolean override) {
        if (REGISTRY.containsKey(new ResourceLocation(map.getDomain(), map.getId())) && !override) {
            Antimatter.LOGGER.info("Attempted duplicate category registration: " + map.getId());
            return;
        }
        REGISTRY.put(new ResourceLocation(map.getDomain(), map.getId()), new RegistryValue(map, map.getGui() == null ? gui : map.getGui(), tier).addWorkstation(model));//new Tuple<>(map, new Tuple<>(gui, tier)));
    }

    public static boolean containsCategory(IRecipeMap map){
        return REGISTRY.containsKey(map.getLoc());
    }

    public static void registerCategoryWorkstation(IRecipeMap map, ResourceLocation model){
        RegistryValue value = REGISTRY.get(map.getLoc());
        if (value != null) {
            value.addWorkstation(model);
        }
    }

    public static void registerPatternForJei(BasicMultiMachine<?> machine, List<Pattern> patternList){
        machine.getTiers().forEach(t -> {
            registerPatternForJei(machine, t, patternList);
        });
    }
    public static void registerPatternForJei(BasicMultiMachine<?> machine, Tier tier, List<Pattern> patternList){
        STRUCTURES.put(machine.getBlockState(tier), patternList);
        if (AntimatterAPI.isModLoaded(Ref.MOD_CREATE) && AntimatterAPI.getSIDE().isClient()){
            PonderIntegration.registerMultiblock(machine, tier, patternList);
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

    public static void addFluidsToHide(Fluid... fluids){
        addFluidsToHide(l -> {
            l.addAll(Arrays.asList(fluids));
        });
    }

    public static void addFluidsToHide(Consumer<List<Fluid>> listConsumer){
        ITEMS_TO_HIDE.add(listConsumer);
    }

    public static List<Consumer<List<ItemLike>>> getItemsToHide() {
        return ITEMS_TO_HIDE;
    }

    public static List<Consumer<List<Fluid>>> getFluidsToHide() {
        return FLUIDS_TO_HIDE;
    }


}
