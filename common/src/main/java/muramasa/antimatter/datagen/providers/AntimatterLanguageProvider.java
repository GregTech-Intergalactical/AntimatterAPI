package muramasa.antimatter.datagen.providers;

import it.unimi.dsi.fastutil.objects.Object2ObjectMap;
import it.unimi.dsi.fastutil.objects.Object2ObjectRBTreeMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.block.BlockStone;
import muramasa.antimatter.block.BlockStoneSlab;
import muramasa.antimatter.block.BlockStoneStair;
import muramasa.antimatter.block.BlockStoneWall;
import muramasa.antimatter.block.BlockStorage;
import muramasa.antimatter.datagen.AntimatterDynamics;
import muramasa.antimatter.datagen.IAntimatterProvider;
import muramasa.antimatter.fluid.AntimatterFluid;
import muramasa.antimatter.item.DebugScannerItem;
import muramasa.antimatter.item.ItemBasic;
import muramasa.antimatter.item.ItemBattery;
import muramasa.antimatter.item.ItemCover;
import muramasa.antimatter.item.ItemFluidCell;
import muramasa.antimatter.item.ItemMultiTextureBattery;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.machine.types.Machine;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialItem;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.ore.BlockOreStone;
import muramasa.antimatter.pipe.BlockPipe;
import muramasa.antimatter.pipe.types.Cable;
import muramasa.antimatter.pipe.types.FluidPipe;
import muramasa.antimatter.pipe.types.ItemPipe;
import muramasa.antimatter.recipe.map.RecipeMap;
import muramasa.antimatter.registration.IAntimatterObject;
import muramasa.antimatter.tool.IAntimatterArmor;
import muramasa.antimatter.tool.IAntimatterTool;
import muramasa.antimatter.util.Utils;
import net.devtech.arrp.json.lang.JLang;
import net.minecraft.data.DataProvider;
import net.minecraft.data.HashCache;
import net.minecraft.network.chat.TextComponent;
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
import java.util.function.Supplier;

import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.util.Utils.*;

public class AntimatterLanguageProvider implements DataProvider, IAntimatterProvider {

    private final String providerDomain, providerName, locale;
    private final Object2ObjectMap<String, String> data = new Object2ObjectRBTreeMap<>();

    public AntimatterLanguageProvider(String providerDomain, String providerName, String locale) {
        this.providerDomain = providerDomain;
        this.providerName = providerName;
        this.locale = locale;
    }

    @Override
    public void run() {
        addTranslations();
    }

    @Override
    public void onCompletion() {
        JLang lang = JLang.lang();
        data.forEach(lang::entry);
        AntimatterDynamics.DYNAMIC_RESOURCE_PACK.addLang(new ResourceLocation(providerDomain, locale), lang);
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

    protected void english(String domain, String locale) {
        AntimatterAPI.all(ItemBasic.class, domain).forEach(i -> add(i, lowerUnderscoreToUpperSpaced(i.getId())));
        AntimatterAPI.all(ItemFluidCell.class, domain).forEach(i -> add(i, lowerUnderscoreToUpperSpaced(i.getId())));
        AntimatterAPI.all(DebugScannerItem.class, domain).forEach(i -> add(i, lowerUnderscoreToUpperSpaced(i.getId())));
        AntimatterAPI.all(ItemCover.class, domain).forEach(i -> add(i, lowerUnderscoreToUpperSpaced(i.getId())));
        AntimatterAPI.all(ItemBattery.class, domain).forEach(i -> add(i, lowerUnderscoreToUpperSpaced(i.getId())));
        AntimatterAPI.all(ItemMultiTextureBattery.class, domain).forEach(i -> add(i, lowerUnderscoreToUpperSpaced(i.getId())));
        AntimatterAPI.all(Machine.class, domain).forEach(i -> {
            if (!i.hasTierSpecificLang()){
                add("machine." + i.getId(), i.getLang(locale).concat(" (%s)"));
                return;
            }
            Collection<Tier> tiers = i.getTiers();
            tiers.forEach(t -> add("machine." + i.getId() + "." + t.getId(), i.getLang(locale).concat(" (%s)")));
        });

        AntimatterAPI.all(IAntimatterTool.class, domain, t -> {
            if (t.getAntimatterToolType().isPowered()) {
                add(t.getItem().getDescriptionId(), Utils.lowerUnderscoreToUpperSpacedRotated(t.getId()));
            } else {
                add(t.getItem().getDescriptionId(), Utils.lowerUnderscoreToUpperSpaced(t.getId()));
            }

        });

        if (domain.equals(Ref.ID)) {
            AntimatterAPI.all(BlockPipe.class).forEach(s -> {
                String str = s.getSize().getId();
                //hmmmm
                if (str.equals("vtiny")) str = "very tiny";
                if (s.getType() instanceof Cable) {
                    str = s.getSize().getCableThickness() + "x";
                }
                String strd = s.getType().getId().split("_")[0];
                if (s.getType() instanceof FluidPipe || s.getType() instanceof ItemPipe) {
                    strd = s.getType().getType() + " Pipe";
                }
                add(s, StringUtils.join(str.substring(0, 1).toUpperCase() + str.substring(1), " ", lowerUnderscoreToUpperSpaced(s.getType().getMaterial().getId()), " ", strd.substring(0, 1).toUpperCase() + strd.substring(1)));
            });
            AntimatterAPI.all(Material.class).forEach(m -> add("material.".concat(m.getId()), getLocalizedType(m)));
            AntimatterAPI.all(BlockOre.class, o -> {
                if (o.getOreType() == ORE)
                    add(o, String.join("", getLocalizeStoneType(o.getStoneType()) + " ", getLocalizedType(o.getMaterial()), " Ore"));
                else
                    add(o, String.join("", "Small ", getLocalizeStoneType(o.getStoneType()) + " ", getLocalizedType(o.getMaterial()), " Ore"));
            });

            AntimatterAPI.all(BlockOreStone.class, o -> {
                add(o, getLocalizedType(o.getMaterial()));
            });
            AntimatterAPI.all(BlockStone.class).forEach(s -> add(s, getLocalizedType(s).replaceAll("Stone ", "")));
            AntimatterAPI.all(BlockStoneSlab.class).forEach(s -> add(s, getLocalizedType(s).replaceAll("Stone ", "")));
            AntimatterAPI.all(BlockStoneStair.class).forEach(s -> add(s, getLocalizedType(s).replaceAll("Stone ", "")));
            AntimatterAPI.all(BlockStoneWall.class).forEach(s -> add(s, getLocalizedType(s).replaceAll("Stone ", "")));
            AntimatterAPI.all(AntimatterFluid.class).forEach(s -> {
                add(s.getAttributes().getTranslationKey(), tryComponent(locale, s, () -> lowerUnderscoreToUpperSpaced(s.getId())));
                Item bucket = AntimatterAPI.get(Item.class, s.getId() + "_bucket", Ref.SHARED_ID);
                if (bucket != null) add(bucket, tryComponent(locale, s, () -> lowerUnderscoreToUpperSpaced(s.getId())) + " Bucket");
            });
            AntimatterAPI.all(BlockStorage.class).forEach(block -> {
                MaterialType<?> type = block.getType();
                if (type == BLOCK)
                    add(block, String.join("","Block of ", getLocalizedType(block.getMaterial())));
                else if (type == RAW_ORE_BLOCK)
                    add(block, String.join("","Block of Raw ", getLocalizedType(block.getMaterial())));
                else {
                    add(block, String.join("", getLocalizedType(block.getMaterial()), " ", getLocalizedType(block.getType())));
                }
            });
            AntimatterAPI.all(MaterialItem.class).forEach(item -> {
                MaterialType<?> type = item.getType();
                if (type == ROCK) add(item, String.join("", getLocalizedType(item.getMaterial()), " Bearing Rock"));
                else if (type == CRUSHED)
                    add(item, String.join("", "Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
                else if (type == CRUSHED_PURIFIED)
                    add(item, String.join("", "Purified Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
                else if (type == CRUSHED_CENTRIFUGED)
                    add(item, String.join("", "Centrifuged Crushed ", getLocalizedType(item.getMaterial()), " Ore"));
                else if (type == DUST_TINY)
                    add(item, String.join("", "Tiny ", getLocalizedType(item.getMaterial()), " Dust"));
                else if (type == DUST_SMALL)
                    add(item, String.join("", "Small ", getLocalizedType(item.getMaterial()), " Dust"));
                else if (type == DUST_IMPURE)
                    add(item, String.join("", "Impure ", getLocalizedType(item.getMaterial()), " Dust"));
                else if (type == DUST_PURE)
                    add(item, String.join("", "Pure ", getLocalizedType(item.getMaterial()), " Dust"));
                else if (type == RAW_ORE)
                    add(item, String.join("", "Raw ", getLocalizedType(item.getMaterial())));
                else {
                    String[] split = getLocalizedMaterialType(type);
                    if (split.length > 1) {
                        if (type.isSplitName())
                            add(item, String.join("", split[0], " ", getLocalizedType(item.getMaterial()), " ", split[1]));
                        else
                            add(item, String.join("", getLocalizedType(item.getMaterial()), " ", split[1], " ", split[0]));
                    } else add(item, String.join("", getLocalizedType(item.getMaterial()), " ", split[0]));
                }
            });
            AntimatterAPI.all(IAntimatterArmor.class, domain, t -> {
                add(t.getItem().getDescriptionId(), Utils.lowerUnderscoreToUpperSpacedRotated(t.getId()));
            });
        }


        AntimatterAPI.all(RecipeMap.class, domain, t -> {
            String id = "jei.category." + t.getId();
            add(id, Utils.lowerUnderscoreToUpperSpaced(t.getId().replace('.', '_'), 0));
        });
        customTranslations();
        pipeTranslations();
    }

    protected void customTranslations() {
        add("machine.voltage.in", "Voltage in");
        add("machine.power.capacity", "Capacity");
        add("generic.amp", "Amperage");
        add("antimatter.tooltip.formula", "Hold Shift to show formula.");
        add("antimatter.tooltip.chemical_formula", "Formula");
        add("antimatter.tooltip.mass", "Mass");
        add("antimatter.tooltip.more", "Hold Shift to show more information.");
        add("antimatter.tooltip.stacks", "Stacks");
        add("generic.tier", "Tier");
        add("generic.voltage", "Voltage");
        //Is this loss?
        add("generic.loss", "Loss (per block)");
        add("message.discharge.on", "Discharge enabled");
        add("message.discharge.off", "Discharge disabled");
        add("item.charge", "Energy");
        add("item.reusable", "Reusable");
        add("antimatter.tooltip.material_primary", "Primary Material");
        add("antimatter.tooltip.material_secondary", "Secondary Material");
        add("antimatter.gui.show_recipes", "Show Recipes");
        add("antimatter.tooltip.pressure", "Pressure");
        add("antimatter.tooltip.capacity", "Capacity");
        add("antimatter.tooltip.max_temperature", "Max Temperature");
        add("antimatter.tooltip.energy", "Energy");
        add("antimatter.tooltip.heat_capacity", "Heat capacity");
        add("antimatter.tooltip.heat_capacity_total", "Heat capacity (total)");
        add("antimatter.tooltip.material_modid", "Added by: %s");
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

}
