package muramasa.antimatter;

import muramasa.antimatter.event.MaterialEvent;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.TextureSet;
import net.minecraft.data.loot.BlockLoot;
import net.minecraft.world.item.Tiers;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.storage.loot.LootTable;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.Element.Au;
import static muramasa.antimatter.material.Element.Cu;
import static muramasa.antimatter.material.Element.Fe;
import static muramasa.antimatter.material.TextureSet.*;
import static muramasa.antimatter.material.TextureSet.NONE;
import static net.minecraft.world.item.Tiers.GOLD;
import static net.minecraft.world.item.Tiers.IRON;
import static net.minecraft.world.item.Tiers.NETHERITE;

public class MaterialDataInit {
    public static void onMaterialEvent(MaterialEvent event){
        event.setMaterial(Data.NULL).addTools(5.0F, 5, Integer.MAX_VALUE, 3/*, ImmutableMap.of(Enchantments.BLOCK_FORTUNE, 3)*/).addHandleStat(0, 0.0F);
        event.setMaterial(Data.Stone).asDust(DUST_IMPURE, GEAR).addHandleStat(-10, -0.5F);
        event.setMaterial(Granite).asDust(ROCK);
        event.setMaterial(Diorite).asDust(ROCK);
        event.setMaterial(Andesite).asDust(ROCK);
        event.setMaterial(Deepslate).asDust(ROCK);
        event.setMaterial(Tuff).asDust(ROCK);

        event.setMaterial(Gravel).asDust(ROCK);
        event.setMaterial(Dirt).asDust(ROCK);
        event.setMaterial(Sand).asDust(ROCK);
        event.setMaterial(RedSand).asDust(ROCK);
        event.setMaterial(Sandstone).asDust(ROCK);
        event.setMaterial(Blackstone).asDust();

        event.setMaterial(Basalt).asDust(ROCK);
        event.setMaterial(Endstone).asDust();
        event.setMaterial(Netherrack).asDust();
        event.setMaterial(Prismarine).asDust();
        event.setMaterial(DarkPrismarine).asDust();

        event.setMaterial(Iron).asMetal(1811, 0).asOre(1, 5, true).asPlasma().addTools(IRON.getAttackDamageBonus(), IRON.getSpeed(), 256, IRON.getLevel(), of(Enchantments.SHARPNESS, 1));
        event.setMaterial(Gold).asMetal(1337, 0).asOre(1, 5, true).addTools(GOLD.getAttackDamageBonus(), GOLD.getSpeed(), GOLD.getUses(), GOLD.getLevel()).harvestLevel(2);
        //cause 1.18
        event.setMaterial(Copper).asMetal(1357, 0).asOre(1, 5, true);

        event.setMaterial(Glowstone).asDust();
        event.setMaterial(Sugar).asDust();
        event.setMaterial(Bone).addHandleStat(12, 0.0F);
        event.setMaterial(Wood).asDust(PLATE).addTools(Tiers.WOOD.getAttackDamageBonus(), Tiers.WOOD.getSpeed(), 16, Tiers.WOOD.getLevel(), of(), SOFT_HAMMER).addHandleStat(12, 0.0F);
        event.setMaterial(Blaze).asDust().addHandleStat(-10, -0.5F, of(Enchantments.FIRE_ASPECT, 1));
        event.setMaterial(Flint).asDust(GEM, MaterialTags.FLINT).addTools(1.25F, 2.5F, 128, 1, of(Enchantments.FIRE_ASPECT, 1), PICKAXE, AXE, SHOVEL, SWORD, HOE, MORTAR, KNIFE);

        event.setMaterial(Charcoal).asDust(BLOCK);
        event.setMaterial(Coal).asGemBasic(false).asOre(0, 2, true, ORE_STONE);
        MaterialTags.CUSTOM_ORE_STONE_DROPS.add(Coal, b -> BlockLoot.createOreDrop(b, GEM.get(Coal)));
        event.setMaterial(Diamond).asGemBasic(false).asOre(3, 7, true).addTools(Tiers.DIAMOND.getAttackDamageBonus(), Tiers.DIAMOND.getSpeed(), Tiers.DIAMOND.getUses(), Tiers.DIAMOND.getLevel());
        event.setMaterial(Emerald).asGemBasic(false).asOre(3, 7, true).harvestLevel(2);
        event.setMaterial(EnderPearl).asGemBasic(false);
        event.setMaterial(EnderEye).asGemBasic(false);
        event.setMaterial(Lapis).asGemBasic(false).asOre(2, 5, true).harvestLevel(1);
        event.setMaterial(Redstone).asDust().asOre(1, 5, true).harvestLevel(2);
        event.setMaterial(Quartz).asDust();
        event.setMaterial(Netherite).asMetal(2246, 1300).addTools(3.0F, 10, 500, NETHERITE.getLevel(), of(Enchantments.FIRE_ASPECT, 3)).addArmor(new int[]{0, 1, 1, 0}, 0.5F, 0.1F, 20);
        event.setMaterial(NetherizedDiamond).asGemBasic(false).addTools(4.0F, 12, NETHERITE.getUses(), NETHERITE.getLevel(), of(Enchantments.FIRE_ASPECT, 3, Enchantments.SHARPNESS, 4)).addArmor(new int[]{1, 1, 2, 1}, 3.0F, 0.1F, 37, of(Enchantments.ALL_DAMAGE_PROTECTION, 4));
        event.setMaterial(NetheriteScrap).asDust(CRUSHED, CRUSHED_PURIFIED, CRUSHED_CENTRIFUGED, RAW_ORE, DUST_IMPURE, DUST_PURE);

        event.setMaterial(Lava).asFluid(0, 1300);
        event.setMaterial(Water).asFluid();
    }
}
