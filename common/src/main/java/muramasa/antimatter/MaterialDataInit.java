package muramasa.antimatter;

import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.tool.AntimatterToolType;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.List;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.data.AntimatterMaterialTypes.*;
import static muramasa.antimatter.data.AntimatterMaterialTypes.PICKAXE_HEAD;
import static muramasa.antimatter.material.MaterialTags.MOLTEN;
import static muramasa.antimatter.material.MaterialTags.QUARTZ_LIKE_BLOCKS;
import static net.minecraft.world.item.Tiers.*;

public class MaterialDataInit {
    public static void onMaterialEvent(MaterialEvent event){
        //why?
        //event.setMaterial(Data.NULL).addTools(5.0F, 5, Integer.MAX_VALUE, 3/*, ImmutableMap.of(Enchantments.BLOCK_FORTUNE, 3)*/).addHandleStat(0, 0.0F);
        event.setMaterial(AntimatterMaterials.Stone).asDust(AntimatterMaterialTypes.GEAR, ROCK).addHandleStat(-10, -0.5F);
        AntimatterMaterials.Stone.remove(PLATE);
        event.setMaterial(AntimatterMaterials.Granite).asDust(AntimatterMaterialTypes.ROCK);
        event.setMaterial(AntimatterMaterials.Diorite).asDust(AntimatterMaterialTypes.ROCK);
        event.setMaterial(AntimatterMaterials.Andesite).asDust(AntimatterMaterialTypes.ROCK);
        event.setMaterial(AntimatterMaterials.Deepslate).asDust(AntimatterMaterialTypes.ROCK);
        event.setMaterial(AntimatterMaterials.Tuff).asDust(AntimatterMaterialTypes.ROCK);

        event.setMaterial(AntimatterMaterials.Sand).asDust();
        event.setMaterial(AntimatterMaterials.RedSand).asDust();
        event.setMaterial(AntimatterMaterials.Blackstone).asDust(AntimatterMaterialTypes.ROCK);

        event.setMaterial(AntimatterMaterials.Basalt).asDust(AntimatterMaterialTypes.ROCK);
        event.setMaterial(AntimatterMaterials.Endstone).asDust();
        event.setMaterial(AntimatterMaterials.Netherrack).asDust();
        event.setMaterial(AntimatterMaterials.Prismarine).asDust();
        event.setMaterial(AntimatterMaterials.DarkPrismarine).asDust();
        AntimatterToolType[] vanillaToolTypes = new AntimatterToolType[]{AntimatterDefaultTools.PICKAXE, AntimatterDefaultTools.AXE, AntimatterDefaultTools.SHOVEL, AntimatterDefaultTools.SWORD, AntimatterDefaultTools.HOE};

        event.setMaterial(AntimatterMaterials.Iron).asMetal(1811, PICKAXE_HEAD, AXE_HEAD, SHOVEL_HEAD, HOE_HEAD, SWORD_BLADE).asOre(1, 5, true).tool().toolDamage(IRON.getAttackDamageBonus()).toolSpeed(IRON.getSpeed()).toolDurability(256).toolQuality(IRON.getLevel()).blacklistToolTypes(vanillaToolTypes).build();
        event.setMaterial(AntimatterMaterials.Gold).asMetal(1337, PICKAXE_HEAD, AXE_HEAD, SHOVEL_HEAD, HOE_HEAD, SWORD_BLADE).asOre(1, 5, true).tool().toolDamage(GOLD.getAttackDamageBonus()).toolSpeed(GOLD.getSpeed()).toolDurability(GOLD.getUses()).toolQuality(GOLD.getLevel()).blacklistToolTypes(vanillaToolTypes).toolEnchantments(of(Enchantments.SMITE, 3)).build().harvestLevel(2);
        //cause 1.18
        event.setMaterial(AntimatterMaterials.Copper).asMetal(1357).asOre(1, 5, true).harvestLevel(1);

        event.setMaterial(AntimatterMaterials.Glowstone).asDust();
        event.setMaterial(AntimatterMaterials.Sugar).asDust();
        event.setMaterial(AntimatterMaterials.Bone).addHandleStat(12, 0.0F);
        event.setMaterial(AntimatterMaterials.Wood).asDust(PLATE, MaterialTags.RUBBERTOOLS, MaterialTags.WOOD)
                .tool().toolDamage(WOOD.getAttackDamageBonus()).toolSpeed(WOOD.getSpeed()).toolDurability(16).toolQuality(WOOD.getLevel()).allowedToolTypes(List.of(AntimatterDefaultTools.SOFT_HAMMER)).build();
        event.setMaterial(AntimatterMaterials.Blaze).asDust().addHandleStat(-10, -0.5F, of(Enchantments.FIRE_ASPECT, 1));

        event.setMaterial(AntimatterMaterials.Flint).asDust(AntimatterMaterialTypes.GEM, MaterialTags.FLINT)
                .tool().toolDamage(1.25f).toolSpeed(STONE.getSpeed()).toolDurability(128).toolQuality(1)
                .toolEnchantments(of(Enchantments.FIRE_ASPECT, 1)).allowedToolTypes(List.of(AntimatterDefaultTools.PICKAXE, AntimatterDefaultTools.AXE, AntimatterDefaultTools.SHOVEL, AntimatterDefaultTools.SWORD, AntimatterDefaultTools.HOE, AntimatterDefaultTools.MORTAR, AntimatterDefaultTools.KNIFE)).build();

        event.setMaterial(AntimatterMaterials.Charcoal).asDust(AntimatterMaterialTypes.BLOCK);
        event.setMaterial(AntimatterMaterials.Coal).asGemBasic(false).asOre(0, 2, true, AntimatterMaterialTypes.ORE_STONE);
        event.setMaterial(AntimatterMaterials.Diamond).asGemBasic(false, PICKAXE_HEAD, AXE_HEAD, SHOVEL_HEAD, HOE_HEAD, SWORD_BLADE).asOre(3, 7, true)
                .tool().toolDamage(DIAMOND.getAttackDamageBonus()).toolSpeed(DIAMOND.getSpeed()).toolDurability(DIAMOND.getUses()).toolQuality(DIAMOND.getLevel()).blacklistToolTypes(vanillaToolTypes).build();
        event.setMaterial(AntimatterMaterials.Emerald).asGemBasic(false).asOre(3, 7, true).harvestLevel(2);
        event.setMaterial(AntimatterMaterials.EnderPearl).asGemBasic(false);
        event.setMaterial(AntimatterMaterials.EnderEye).asGemBasic(false);
        event.setMaterial(AntimatterMaterials.Lapis).asGemBasic(false).asOre(2, 5, true).harvestLevel(1);
        event.setMaterial(AntimatterMaterials.Redstone).asOre(1, 5, true, MOLTEN).harvestLevel(2);
        event.setMaterial(AntimatterMaterials.Quartz).asOre(1, 5, true, QUARTZ_LIKE_BLOCKS).harvestLevel(1);
        event.setMaterial(AntimatterMaterials.Netherite).asMetal(2246, PLATE, ROD);
                //.addTools(3.0F, 10, 500, NETHERITE.getLevel(), of(Enchantments.FIRE_ASPECT, 3)).addArmor(new int[]{0, 1, 1, 0}, 0.5F, 0.1F, 20);
        event.setMaterial(AntimatterMaterials.NetherizedDiamond).asGemBasic(false)
                .tool().toolDamage(4).toolSpeed(12).toolDurability(NETHERITE.getUses()).toolQuality(NETHERITE.getLevel()).toolEnchantments(of(Enchantments.FIRE_ASPECT, 3, Enchantments.SHARPNESS, 4)).build()
                .addArmor(new int[]{1, 1, 2, 1}, 3.0F, 0.1F, 37, of(Enchantments.ALL_DAMAGE_PROTECTION, 4));
        event.setMaterial(AntimatterMaterials.NetheriteScrap).asDust(AntimatterMaterialTypes.CRUSHED, AntimatterMaterialTypes.RAW_ORE);

        event.setMaterial(AntimatterMaterials.Lava).asFluid(0, 1300);
        event.setMaterial(AntimatterMaterials.Water).asFluid();
    }
}
