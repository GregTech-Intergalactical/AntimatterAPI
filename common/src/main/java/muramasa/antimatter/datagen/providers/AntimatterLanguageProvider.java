package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.*;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.item.*;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.BasicMultiMachine;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.*;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.ore.CobbleStoneType;
import muramasa.antimatter.ore.StoneType;
import muramasa.antimatter.pipe.BlockItemPipe;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.devtech.arrp.json.lang.JLang;
import net.minecraft.Util;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.level.block.Block;
import org.apache.commons.lang3.StringUtils;

import java.io.IOException;
import java.util.Collection;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

import static muramasa.antimatter.data.AntimatterMaterials.Wood;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterLanguageProvider implements DataProvider, IAntimatterProvider {

    private final String providerDomain, providerName, locale;
    private final Object2ObjectMap<String, String> data = new Object2ObjectRBTreeMap<>();

    private static final Object2ObjectMap<String, Object2ObjectMap<String, Object2ObjectMap<String, String>>> GLOBAL_DATA = new Object2ObjectRBTreeMap<>();

    public AntimatterLanguageProvider(String providerDomain, String providerName, String locale) {
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.locale = locale;
    }

    @Override
    public final void run() {
        addTranslations();
        GLOBAL_DATA.computeIfAbsent(providerDomain, d -> new Object2ObjectRBTreeMap<>()).merge(locale, data, (oldV, newV) -> {
            oldV.putAll(newV);
            return oldV;
        });
    }

    @Override
    public final void onCompletion() {
        overrides();
    }

    public static void postCompletion(){
        GLOBAL_DATA.forEach((domain, map) -> {
            map.forEach((locale, data) -> {
                JLang lang = JLang.lang();
                data.forEach(lang::entry);
                AntimatterDynamics.DYNAMIC_RESOURCE_PACK.addLang(new ResourceLocation(domain, locale), lang);
            });
        });

    }

    @Override
    public void run(HashCache cache) throws IOException {
    }

    protected void addTranslations() {
        if (locale.startsWith("en")) english(providerDomain, locale);
        if (providerDomain.equals(Ref.ID)) processAntimatterTranslations();
    }

    private String tryComponent(String lang, IAntimatterObject object, Supplier<String> otherwise) {
        String component = object.getLang(lang);
        if (component != null) {
            return component;
        }
        return otherwise.get();
    }

    protected void overrides(){

    }

    protected void english(String domain, String locale) {
        AntimatterAPI.all(ItemBasic.class, domain).forEach(i -> {
            add(i, lowerUnderscoreToUpperSpaced(i.getId()));
            if (!i.getTooltip().isEmpty()){
                add("tooltip." + i.getDomain() + "." + i.getId().replace("/", "."), i.getTooltip());
            }
        });
        AntimatterAPI.all(Machine.class, domain).forEach(i -> {
            if (!i.hasTierSpecificLang()){
                add("machine." + i.getId(), i.getLang(locale).concat(" (%s)"));
            }
            Collection<Tier> tiers = i.getTiers();
            tiers.forEach(t -> {
                if (i.hasTierSpecificLang()) {
                    add("machine." + i.getId() + "." + t.getId(), i.getLang(locale).concat(t == Tier.NONE ? "" : " (%s)"));
                }
                add(i.getBlockState(t), i.getLang(locale).concat(t == Tier.NONE ? "" : " (" + t.getId().toUpperCase(Locale.ROOT) + ")"));
                if (i instanceof BasicMultiMachine<?>) {
                    add(i.getDomain() + ".ponder." + i.getIdFromTier(t) + ".header", i.getLang(locale).concat(t == Tier.NONE ? "" : " (" + t.getId().toUpperCase(Locale.ROOT) + ")").concat(" Multiblock"));
                }
            });
        });
        AntimatterAPI.all(Enchantment.class, domain, (en, d, i) -> {
            add("enchantment." + d + "." + i, lowerUnderscoreToUpperSpaced(i));
        });

        if (domain.equals(Ref.ID)) {
            AntimatterAPI.all(IAntimatterTool.class, t -> {
                String customName = t.getAntimatterToolType().getCustomName().isEmpty() ? Utils.getLocalizedType(t.getAntimatterToolType()) : t.getAntimatterToolType().getCustomName();
                if (t.getAntimatterToolType().isPowered()) {
                    String defaultName = Utils.getLocalizedType(t.getAntimatterToolType());
                    add(t.getItem().getDescriptionId(), Utils.lowerUnderscoreToUpperSpacedRotated(t.getId()).replace(defaultName, customName));
                } else {
                    if (t.getAntimatterToolType().isSimple()){
                        add(t.getItem().getDescriptionId(), Utils.getLocalizedType(t.getPrimaryMaterial(t.getItem().getDefaultInstance())) + " " + customName);
                    } else {
                        add(t.getItem().getDescriptionId(), customName);
                    }
                }

            });
            AntimatterAPI.all(BlockPipe.class).forEach(s -> {
                String str = s.getSize().getId();
                //hmmmm
                if (str.equals("vtiny")) str = "very_tiny";
                if (s.getType() instanceof Cable) {
                    str = s.getSize().getCableThickness() + "x";
                }
                if (s instanceof BlockItemPipe<?> itemPipe && itemPipe.isRestricted()){
                    str = "restrictive_" + str;
                }
                String prefix = str.contains("_") ? Utils.lowerUnderscoreToUpperSpaced(str) : str.substring(0, 1).toUpperCase() + str.substring(1);
                add(s, StringUtils.join(prefix, " ", Utils.getLocalizedType(s.getType().getMaterial()), " ", Utils.lowerUnderscoreToUpperSpaced(s.getType().getType())));
            });
            AntimatterAPI.all(Material.class).forEach(m -> add("material.".concat(m.getId()), getLocalizedType(m)));
            AntimatterAPI.all(BlockOre.class, o -> {
                String nativeSuffix = o.getMaterial().getElement() != null ? "Native " : "";
                if (o.getOreType() == AntimatterMaterialTypes.ORE)
                    add(o, String.join("", getLocalizeStoneType(o.getStoneType()) + " ", nativeSuffix, getLocalizedType(o.getMaterial()), " Ore"));
                else
                    add(o, String.join("", "Small ", getLocalizeStoneType(o.getStoneType()) + " ", nativeSuffix, getLocalizedType(o.getMaterial()), " Ore"));
            });

            AntimatterAPI.all(BlockOreStone.class, o -> {
                add(o, getLocalizedType(o.getMaterial()));
            });
            AntimatterAPI.all(BlockStone.class).forEach(s -> {
                String localized = getLocalizedType(s);
                if (s.getSuffix().contains("mossy")) localized = "Mossy " + localized.replace(" Mossy", "");
                if (s.getSuffix().contains("chiseled")) localized = "Chiseled " + localized.replace(" Chiseled", "");
                if (s.getSuffix().contains("cracked")) localized = "Cracked " + localized.replace(" Cracked", "");
                if (s.getSuffix().contains("smooth")) localized = "Smooth " + localized.replace(" Smooth", "");
                add(s, localized);
            });
            AntimatterAPI.all(BlockStoneSlab.class).forEach(s -> {
                String localized = getLocalizedType(s);
                if (s.getSuffix().contains("mossy")) localized = "Mossy " + localized.replace(" Mossy", "");
                if (s.getSuffix().contains("chiseled")) localized = "Chiseled " + localized.replace(" Chiseled", "");
                if (s.getSuffix().contains("cracked")) localized = "Cracked " + localized.replace(" Cracked", "");
                if (s.getSuffix().contains("smooth")) localized = "Smooth " + localized.replace(" Smooth", "");
                add(s, localized);
            });
            AntimatterAPI.all(BlockStoneStair.class).forEach(s -> {
                String localized = getLocalizedType(s);
                if (s.getSuffix().contains("mossy")) localized = "Mossy " + localized.replace(" Mossy", "");
                if (s.getSuffix().contains("chiseled")) localized = "Chiseled " + localized.replace(" Chiseled", "");
                if (s.getSuffix().contains("cracked")) localized = "Cracked " + localized.replace(" Cracked", "");
                if (s.getSuffix().contains("smooth")) localized = "Smooth " + localized.replace(" Smooth", "");
                add(s, localized);
            });
            AntimatterAPI.all(BlockStoneWall.class).forEach(s -> {
                String localized = getLocalizedType(s);
                if (s.getSuffix().contains("mossy")) localized = "Mossy " + localized.replace(" Mossy", "");
                if (s.getSuffix().contains("chiseled")) localized = "Chiseled " + localized.replace(" Chiseled", "");
                if (s.getSuffix().contains("cracked")) localized = "Cracked " + localized.replace(" Cracked", "");
                if (s.getSuffix().contains("smooth")) localized = "Smooth " + localized.replace(" Smooth", "");
                add(s, localized);
            });
            AntimatterAPI.all(AntimatterFluid.class).forEach((AntimatterFluid s) -> {
                add(Util.makeDescriptionId("fluid_type", s.getLoc()), tryComponent(locale, s, () -> lowerUnderscoreToUpperSpaced(s.getId())));
                Item bucket = AntimatterAPI.get(Item.class, s.getId() + "_bucket", Ref.SHARED_ID);
                if (bucket != null) add(bucket, tryComponent(locale, s, () -> lowerUnderscoreToUpperSpaced(s.getId())) + " Bucket");
            });
            AntimatterAPI.all(BlockStorage.class).forEach(block -> {
                MaterialType<?> type = block.getType();
                if (type == AntimatterMaterialTypes.BLOCK)
                    add(block, String.join("","Block of ", getLocalizedType(block.getMaterial())));
                else if (type == AntimatterMaterialTypes.RAW_ORE_BLOCK)
                    add(block, String.join("","Block of Raw ", getLocalizedType(block.getMaterial())));
                else {
                    add(block, String.join("", getLocalizedType(block.getMaterial()), " ", getLocalizedType(block.getType())));
                }
            });
            AntimatterAPI.all(BlockSurfaceRock.class).forEach(b -> {
                add(b, String.join("", getLocalizeStoneType(b.getStoneType()) + " ", getLocalizedType(b.getMaterial()), " Surface Rock"));
            });
            AntimatterAPI.all(MaterialType.class).stream().filter(t -> t instanceof MaterialTypeBlock<?> || t instanceof MaterialTypeItem<?>).forEach(t -> {
                if (t.get() instanceof MaterialTypeBlock.IOreGetter){
                    AntimatterAPI.all(StoneType.class, s -> {
                        add(Ref.ID + ".rei.group." + t.getId() + "." + s.getId(), getLocalizedType(s) + " " + getLocalizedType(t) + "s");
                    });
                    if (t != AntimatterMaterialTypes.ROCK){
                        return;
                    }
                }
                String[] split = getLocalizedMaterialType(t);
                if (t == AntimatterMaterialTypes.CRUSHED)
                    add(Ref.ID + ".rei.group." + t.getId(), String.join("", "Crushed Ores"));
                else if (t == AntimatterMaterialTypes.CRUSHED_PURIFIED)
                    add(Ref.ID + ".rei.group." + t.getId(), String.join("", "Purified Ores"));
                else if (t == AntimatterMaterialTypes.CRUSHED_REFINED)
                    add(Ref.ID + ".rei.group." + t.getId(), String.join("", "Refined Ores"));
                else if (t == AntimatterMaterialTypes.RAW_ORE_BLOCK)
                    add(Ref.ID + ".rei.group." + t.getId(), "Raw Ore Blocks");
                else if (t == AntimatterMaterialTypes.ITEM_CASING)
                    add(Ref.ID + ".rei.group." + t.getId(), String.join("", "Item Casings"));
                else if (split.length > 1) {
                    if (t.isSplitName())
                        add(Ref.ID + ".rei.group." + t.getId(), String.join("", split[0], " ", split[1], "s"));
                    else
                        add(Ref.ID + ".rei.group." + t.getId(), String.join("", split[1], " ", split[0], "s"));
                } else add(Ref.ID + ".rei.group." + t.getId(), split[0] + "s");
            });
            AntimatterAPI.all(StoneType.class, s -> {
                if (s instanceof CobbleStoneType){
                    add(Ref.ID + ".rei.group." + s.getId(), getLocalizedType(s));
                }
            });
            AntimatterAPI.all(MaterialItem.class).forEach(item -> {
                MaterialType<?> type = item.getType();
                String dust = item.getMaterial().has(MaterialTags.RUBBERTOOLS) ? " Pulp" : " Dust";
                String nativeSuffix = item.getMaterial().getElement() != null ? "Native " : "";
                if (type == AntimatterMaterialTypes.ROCK) add(item, String.join("", getLocalizedType(item.getMaterial()), " Bearing Rock"));
                else if (type == AntimatterMaterialTypes.CRUSHED)
                    add(item, String.join("", "Crushed ", nativeSuffix, getLocalizedType(item.getMaterial()), " Ore"));
                else if (type == AntimatterMaterialTypes.CRUSHED_PURIFIED)
                    add(item, String.join("", "Purified ", nativeSuffix, getLocalizedType(item.getMaterial()), " Ore"));
                else if (type == AntimatterMaterialTypes.CRUSHED_REFINED)
                    add(item, String.join("", "Refined ", nativeSuffix, getLocalizedType(item.getMaterial()), " Ore"));
                else if (type == AntimatterMaterialTypes.DUST_TINY)
                    add(item, String.join("", "Tiny ", getLocalizedType(item.getMaterial()), dust));
                else if (type == AntimatterMaterialTypes.DUST_SMALL)
                    add(item, String.join("", "Small ", getLocalizedType(item.getMaterial()), dust));
                else if (type == AntimatterMaterialTypes.DUST_IMPURE)
                    add(item, String.join("", "Impure ", getLocalizedType(item.getMaterial()), dust));
                else if (type == AntimatterMaterialTypes.DUST_PURE)
                    add(item, String.join("", "Pure ", getLocalizedType(item.getMaterial()), dust));
                else if (type == AntimatterMaterialTypes.RAW_ORE)
                    add(item, String.join("", "Raw ", nativeSuffix, getLocalizedType(item.getMaterial())));
                else if (type == AntimatterMaterialTypes.ITEM_CASING)
                    add(item, String.join("", getLocalizedType(item.getMaterial()), " Item Casings"));
                else if (type == AntimatterMaterialTypes.GEM)
                    add(item, getLocalizedType(item.getMaterial()));
                else {
                    String[] split = getLocalizedMaterialType(type);
                    if (split.length > 1) {
                        if (type.isSplitName())
                            add(item, String.join("", split[0], " ", getLocalizedType(item.getMaterial()), " ", split[1]));
                        else
                            add(item, String.join("", getLocalizedType(item.getMaterial()), " ", split[1], " ", split[0]));
                    } else {
                        String plateReplacement = item.getMaterial() == Wood ? "Plank" : "Sheet";
                        if (item.getMaterial().has(MaterialTags.RUBBERTOOLS)) split[0] = split[0].replace("Dust", "Pulp").replace("Nugget", "Chip").replace("Ingot", "Bar").replace("Plate", plateReplacement);
                        add(item, String.join("", getLocalizedType(item.getMaterial()), " ", split[0]));
                    }
                }
            });
            AntimatterAPI.all(IAntimatterArmor.class, t -> {
                add(t.getItem().getDescriptionId(), Utils.getLocalizedType(t.getMat()) + " " + Utils.getLocalizedType(t.getAntimatterArmorType()));
            });
            customTranslations();
            pipeTranslations();
            AntimatterAPI.all(RecipeMap.class, t -> {
                String id = "jei.category." + t.getId();
                add(id, Utils.lowerUnderscoreToUpperSpaced(t.getId().replace('.', '_'), 0));
            });
        }





    }

    protected void customTranslations() {
        add("machine.voltage.in", "Voltage IN");
        add("machine.voltage.out", "Voltage OUT");
        add("machine.power.capacity", "Capacity");
        add("machine.tank.capacity", "Stores %sMb of fluid");
        add("machine.structure.form", "Right click structure to form it after placing blocks");
        add("generic.amp", "Amperage");
        add("antimatter.tooltip.formula", "Hold Shift to show formula.");
        add("antimatter.tooltip.chemical_formula", "Formula");
        add("antimatter.tooltip.mass", "Mass");
        add("antimatter.tooltip.more", "Hold Shift to show more information.");
        add("antimatter.tooltip.stacks", "Stacks");
        add("antimatter.tooltip.fluid.amount", "Amount: %s");
        add("antimatter.tooltip.fluid.temp", "Temperature: %s K");
        add("antimatter.tooltip.fluid.liquid", "State: Liquid");
        add("antimatter.tooltip.fluid.gas", "State: Gas");
        add("antimatter.tooltip.cover.output.no_input", "Inputs blocked");
        add("antimatter.tooltip.cover.output.allow_input", "Inputs allowed");
        add("generic.tier", "Tier");
        add("generic.voltage", "Voltage");
        //Is this loss?
        add("generic.loss", "Loss (per block)");
        add("message.discharge.on", "Discharge enabled");
        add("message.discharge.off", "Discharge disabled");
        add("item.charge", "Energy");
        add("item.reusable", "Reusable");
        add("item.amps", "Warning: outputs %s amps");
        add("antimatter.tooltip.material_primary", "Primary Material: %s");
        add("antimatter.tooltip.material_secondary", "Secondary Material: %s");
        add("antimatter.tooltip.dye_color", "Handle Color: %s");
        add("antimatter.gui.show_recipes", "Show Recipes");
        add("antimatter.tooltip.bandwidth", "Bandwidth: %s");
        add("antimatter.tooltip.capacity", "Capacity: %s");
        add("antimatter.tooltip.stepsize", "Stepsize: %s");
        add("antimatter.tooltip.gas_proof", "Can handle Gases");
        add("antimatter.tooltip.acid_proof", "Can handle Acids");
        add("antimatter.tooltip.max_temperature", "Max Temperature");
        add("antimatter.tooltip.energy", "Energy");
        add("antimatter.tooltip.heat_capacity", "Heat capacity");
        add("antimatter.tooltip.heat_capacity_total", "Heat capacity (total)");
        add("antimatter.tooltip.material_modid", "Added by: %s");
        add("antimatter.tooltip.occurrence", "Indicates occurrence of %s");
        add("antimatter.tooltip.behaviour.aoe_enabled", "%s Enabled");
        add("antimatter.tooltip.behaviour.aoe_disabled", "%s Disabled");
        add("antimatter.tooltip.behaviour.aoe_right_click", "Sneak right click to Enable/Disable %s");
        add("antimatter.tooltip.io_widget.fluid", "Fluid Auto-Output");
        add("antimatter.tooltip.io_widget.item", "Item Auto-Output");
        add("antimatter.behaviour.3x3", "3x3 Mining");
        add("antimatter.behaviour.1x0x2", "1x2 Mining");
    }

    private final void pipeTranslations() {
        add("antimatter.pipe.cable.info", "Transmits amperages between machines. \nFor each cable the cable loss is subtracted \nfrom the total energy.");
        add("antimatter.pipe.item.info", "Transfers up to capacity item stacks per tick. \nThis capacity is per stack and not per item transferred.");
        add("antimatter.pipe.fluid.info", "Transfers up to capacity per tick, with a buffer of 20 times the capacity. \nEvery tick the capacity of the pipe is replenished, up to 20 times. \nThis allows large transfers at once, but \n" +
                "continuous transfers is limited by capacity");
    }

    private void processAntimatterTranslations() {
        add(Ref.TAB_BLOCKS, "Antimatter Blocks");
        add(Ref.TAB_ITEMS, "Antimatter Items");
        add(Ref.TAB_MACHINES, "Antimatter Machines");
        add(Ref.TAB_MATERIALS, "Antimatter Material Items");
        add(Ref.TAB_TOOLS, "Antimatter Tools");
    }

    @Override
    public String getName() {
        return providerName;
    }

    public void addBlock(Supplier<? extends Block> key, String name) {
        add(key.get(), name);
    }

    public void add(Block key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addItem(Supplier<? extends Item> key, String name) {
        add(key.get(), name);
    }

    public void add(Item key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addItemStack(Supplier<ItemStack> key, String name) {
        add(key.get(), name);
    }

    public void add(ItemStack key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addEnchantment(Supplier<? extends Enchantment> key, String name) {
        add(key.get(), name);
    }

    public void add(Enchantment key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addEffect(Supplier<? extends MobEffect> key, String name) {
        add(key.get(), name);
    }

    public void add(MobEffect key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addEntityType(Supplier<? extends EntityType<?>> key, String name) {
        add(key.get(), name);
    }

    public void add(EntityType<?> key, String name) {
        add(key.getDescriptionId(), name);
    }

    public void addItemGroup(Supplier<? extends CreativeModeTab> key, String name) {
        add(key.get(), name);
    }

    public void add(CreativeModeTab key, String name) {
        add("itemGroup." + key.getRecipeFolderName(), name);
    }

    public void add(String key, String value) {
        try {
            if (data.containsKey(key)) {
                throw new IllegalStateException("Duplicate translation key " + key + ", Name is " + value);
            }
            data.put(key, value);
        } catch (IllegalStateException e){
            e.printStackTrace();
        }
    }

    public void override(String key, String value) {
        data.put(key, value);
    }

    public void override(String domain, String key, String value) {
        Map<String, Object2ObjectMap<String, String>> mapMap = GLOBAL_DATA.get(domain);
        if (mapMap != null){
            Map<String, String> map = mapMap.get(locale);
            if (map != null){
                map.put(key, value);
            }
        }
    }

}
