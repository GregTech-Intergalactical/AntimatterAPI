package muramasa.antimatter.datagen.loaders;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.datagen.builder.AntimatterCookingRecipeBuilder;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.data.AntimatterMaterialTypes.ORE;
import static muramasa.antimatter.data.AntimatterMaterialTypes.RAW_ORE;
import static muramasa.antimatter.material.MaterialTags.RUBBERTOOLS;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class MaterialRecipes {
    public static void init(Consumer<FinishedRecipe> consumer, AntimatterRecipeProvider provider) {
        final CriterionTriggerInstance in = provider.hasSafeItem(AntimatterDefaultTools.WRENCH.getTag());
        int craftingMultiplier = AntimatterConfig.GAMEPLAY.LOSSY_PART_CRAFTING ? 1 : 2;
        AntimatterMaterialTypes.DUST.all().forEach(m -> {
            provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_dust_small", "antimatter_dusts", "has_wrench", in, AntimatterMaterialTypes.DUST.get(m, 1), of('D', AntimatterMaterialTypes.DUST_SMALL.getMaterialTag(m)), "DD", "DD");
            provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_dust_tiny", "antimatter_dusts", "has_wrench", in, AntimatterMaterialTypes.DUST.get(m, 1), of('D', AntimatterMaterialTypes.DUST_TINY.getMaterialTag(m)), "DDD", "DDD", "DDD");
        });
        AntimatterMaterialTypes.ROD.all().forEach(m -> {
            if (m.has(AntimatterMaterialTypes.INGOT)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_rod", "antimatter_material", "has_wrench", in, AntimatterMaterialTypes.ROD.get(m, craftingMultiplier), of('F', AntimatterDefaultTools.FILE.getTag(), 'I', AntimatterMaterialTypes.INGOT.getMaterialTag(m)), "F", "I");
            }
            if (m.has(AntimatterMaterialTypes.BOLT)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_bolt", "antimatter_material", "has_wrench", in, AntimatterMaterialTypes.BOLT.get(m, 2 * craftingMultiplier), of('F', AntimatterDefaultTools.SAW.getTag(), 'I', AntimatterMaterialTypes.ROD.getMaterialTag(m)), "F ", " I");
                if (m.has(AntimatterMaterialTypes.SCREW)) {
                    String[] pattern = AntimatterConfig.GAMEPLAY.LOSSY_PART_CRAFTING ? new String[]{"FI", "I "} : new String[]{"F", "I"};
                    provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_screw", "antimatter_material",
                            "has_wrench", in, AntimatterMaterialTypes.SCREW.get(m, 1), of('F', AntimatterDefaultTools.FILE.getTag(), 'I', AntimatterMaterialTypes.BOLT.getMaterialTag(m)), pattern);
                }
            }
            if (m.has(AntimatterMaterialTypes.RING)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_ring", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                        AntimatterMaterialTypes.RING.get(m, craftingMultiplier), ImmutableMap.of('H', AntimatterDefaultTools.HAMMER.getTag(), 'W', AntimatterMaterialTypes.ROD.getMaterialTag(m)), "H ", " W");
            }
        });
        AntimatterMaterialTypes.ROTOR.all().forEach(m -> {
            provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_rotors", "antimatter_material", "has_screwdriver", provider.hasSafeItem(AntimatterDefaultTools.SCREWDRIVER.getTag()),
                    AntimatterMaterialTypes.ROTOR.get(m, 1), ImmutableMap.<Character, Object>builder()
                            .put('S', AntimatterDefaultTools.SCREWDRIVER.getTag())
                            .put('F', AntimatterDefaultTools.FILE.getTag())
                            .put('H', AntimatterDefaultTools.HAMMER.getTag())
                            .put('P', AntimatterMaterialTypes.PLATE.getMaterialTag(m))
                            .put('W', AntimatterMaterialTypes.SCREW.getMaterialTag(m))
                            .put('R', AntimatterMaterialTypes.RING.getMaterialTag(m))
                            .build(),
                    "PHP", "WRF", "PSP");
        });
        AntimatterMaterialTypes.PLATE.all().forEach(m -> {
            if (m.has(AntimatterMaterialTypes.INGOT) && !m.has(RUBBERTOOLS)){
                Object[] array = AntimatterConfig.GAMEPLAY.LOSSY_PART_CRAFTING ? new Object[]{AntimatterDefaultTools.HAMMER.getTag(), AntimatterMaterialTypes.INGOT.getMaterialTag(m), AntimatterMaterialTypes.INGOT.getMaterialTag(m)} : new Object[]{AntimatterDefaultTools.HAMMER.getTag(), AntimatterMaterialTypes.INGOT.getMaterialTag(m)};
                provider.shapeless(consumer, m.getId() + "_plate", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()), AntimatterMaterialTypes.PLATE.get(m, 1), array);
            }
            if (m.has(AntimatterMaterialTypes.GEAR_SMALL)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_gear_small", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                        AntimatterMaterialTypes.GEAR_SMALL.get(m, 1), ImmutableMap.of('H', AntimatterDefaultTools.HAMMER.getTag(),'P', AntimatterMaterialTypes.PLATE.getMaterialTag(m)), "P ", " H");
            }
            if (m.has(AntimatterMaterialTypes.GEAR)){
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_gear", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                        AntimatterMaterialTypes.GEAR.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('W', AntimatterDefaultTools.WRENCH.getTag())
                                .put('P', AntimatterMaterialTypes.PLATE.getMaterialTag(m))
                                .put('R', AntimatterMaterialTypes.ROD.getMaterialTag(m))
                                .build(),
                        "RPR", "PWP", "RPR");
            }
        });

        AntimatterMaterialTypes.DUST.all().forEach(m -> {
            if (m.has(AntimatterMaterialTypes.INGOT)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_grind_ingot", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                        AntimatterMaterialTypes.DUST.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('M', AntimatterDefaultTools.MORTAR.getTag())
                                .put('I', AntimatterMaterialTypes.INGOT.getMaterialTag(m))
                                .build(),
                        "MI");
            }
            if (m.has(AntimatterMaterialTypes.ROCK)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_grind_rock", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                        AntimatterMaterialTypes.DUST.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('M', AntimatterDefaultTools.MORTAR.getTag())
                                .put('I', AntimatterMaterialTypes.ROCK.getMaterialTag(m))
                                .build(),
                        "III", "III", "IIM");
            }
        });

        ORE.all().forEach(m -> {
            if (!m.has(MaterialTags.SMELT_INTO_2)) return;
            if (m.has(MaterialTags.NEEDS_BLAST_FURNACE)) return;
            ItemStack output = MaterialTags.SMELT_INTO_2.get(m).get();
            if (!output.isEmpty()){
                TagKey<Item> ore = ORE.getMaterialTag(m);
                AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(ore, 1), output, 0.7F, 100)
                        .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(ore))
                        .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_to_ingot")));
                AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(ore, 1), output, 0.7F, 200)
                        .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(ore))
                        .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_to_ingot_smelting")));
            }
        });
        RAW_ORE.all().forEach(m -> {
            if (!m.has(MaterialTags.SMELT_INTO_2)) return;
            if (m.has(MaterialTags.NEEDS_BLAST_FURNACE)) return;
            ItemStack output = MaterialTags.SMELT_INTO_2.get(m).get();
            if (!output.isEmpty()){
                TagKey<Item> ore = ORE.getMaterialTag(m);
                AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(ore, 1), output, 0.7F, 100)
                        .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(ore))
                        .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot")));
                AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(ore, 1), output, 0.7F, 200)
                        .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(ore))
                        .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot_smelting")));
            }
        });
        AntimatterAPI.all(BlockOre.class, o -> {
            if (o.getOreType() != ORE) return;
            if (!MaterialTags.SMELT_INTO.getMapping(o.getMaterial()).has(AntimatterMaterialTypes.INGOT)) return;
            if (o.getMaterial().has(MaterialTags.NEEDS_BLAST_FURNACE)) return;
            Item ingot = AntimatterMaterialTypes.INGOT.get(MaterialTags.SMELT_INTO.getMapping(o.getMaterial()));
            TagKey<Item> oreTag = TagUtils.getForgelikeItemTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()));
            TagKey<Item> ingotTag = TagUtils.getForgelikeItemTag("ingots/".concat(MaterialTags.SMELT_INTO.getMapping(o.getMaterial()).getId()));
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(oreTag, 1), new ItemStack(ingot, MaterialTags.SMELTING_MULTI.getInt(o.getMaterial())), 2.0F, 100)
                    .addCriterion("has_material_" + o.getMaterial().getId(), provider.hasSafeItem(ingotTag))
                    .build(consumer, provider.fixLoc(Ref.ID, o.getId().concat("_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(oreTag, 1), new ItemStack(ingot, MaterialTags.SMELTING_MULTI.getInt(o.getMaterial())), 2.0F, 200)
                    .addCriterion("has_material_" + o.getMaterial().getId(), provider.hasSafeItem(ingotTag))
                    .build(consumer, provider.fixLoc(Ref.ID, o.getId().concat("_to_ingot_smelting")));
        });
        AntimatterAPI.all(Material.class).stream().filter(m -> m.has(RAW_ORE) && MaterialTags.SMELT_INTO.getMapping(m).has(AntimatterMaterialTypes.INGOT) && !m.has(MaterialTags.NEEDS_BLAST_FURNACE)).forEach(m -> {
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1), new ItemStack(AntimatterMaterialTypes.INGOT.get(MaterialTags.SMELT_INTO.getMapping(m)), MaterialTags.SMELTING_MULTI.getInt(m)), 2.0F, 100)
                    .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(AntimatterMaterialTypes.INGOT.getMaterialTag(MaterialTags.SMELT_INTO.getMapping(m))))
                    .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1), new ItemStack(AntimatterMaterialTypes.INGOT.get(MaterialTags.SMELT_INTO.getMapping(m)), MaterialTags.SMELTING_MULTI.getInt(m)), 2.0F, 200)
                    .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(AntimatterMaterialTypes.INGOT.getMaterialTag(MaterialTags.SMELT_INTO.getMapping(m))))
                    .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot_smelting")));
        });
        AntimatterMaterialTypes.DUST.all().forEach(m -> {
            if (m.has(MaterialTags.NEEDS_BLAST_FURNACE) || m.has(MaterialTags.HAS_CUSTOM_SMELTING)) return;
            if (!MaterialTags.DIRECT_SMELT_INTO.getMapping(m).has(AntimatterMaterialTypes.INGOT)) return;
            SimpleCookingRecipeBuilder.blasting(AntimatterMaterialTypes.DUST.getMaterialIngredient(m, 1), AntimatterMaterialTypes.INGOT.get(MaterialTags.DIRECT_SMELT_INTO.getMapping(m)), 0.5F, 100).unlockedBy("has_" + m.getId() + "_dust", provider.hasSafeItem(AntimatterMaterialTypes.DUST.getMaterialTag(m))).save(consumer, Ref.SHARED_ID + ":" + m.getId() + "_dust_to_ingot_bl");
            SimpleCookingRecipeBuilder.smelting(AntimatterMaterialTypes.DUST.getMaterialIngredient(m, 1), AntimatterMaterialTypes.INGOT.get(MaterialTags.DIRECT_SMELT_INTO.getMapping(m)), 0.5F, 200).unlockedBy("has_" + m.getId() + "_dust", provider.hasSafeItem(AntimatterMaterialTypes.DUST.getMaterialTag(m))).save(consumer, Ref.SHARED_ID + ":" + m.getId() + "_dust_to_ingot");
        });
        /*AntimatterAPI.all(Material.class).stream().filter(m -> m.has(DUST)).forEach(mat -> {
            Item dust = DUST.get(mat);
            if (mat.has(ROCK)) {
                ITag<Item> rockTag = nc(TagUtils.getForgeItemTag("rocks/".concat(mat.getId())));
                Item rock = ROCK.get(mat);
                Item smallDust = DUST_SMALL.get(mat);
                ShapelessRecipeBuilder.shapelessRecipe(dust)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(nc(MORTAR.getTag()))
                        .addCriterion("has_rock_" + mat.getId(), provider.hasSafeItem(rockTag))
                        .setGroup("rocks_grind_to_dust").build(consumer, provider.fixLoc(Ref.ID, rock.getRegistryName().getPath() + "_grind_to_" + dust.getRegistryName().getPath()));

                ShapelessRecipeBuilder.shapelessRecipe(smallDust)
                        .addIngredient(rockTag).addIngredient(rockTag)
                        .addIngredient(rockTag).addIngredient(rockTag).addIngredient(nc(MORTAR.getTag()))
                        .addCriterion("has_rock_" + mat.getId(), provider.hasSafeItem(getForgeItemTag("rocks/".concat(mat.getId()))))
                        .setGroup("rocks_grind_to_small_dust").build(consumer, provider.fixLoc(Ref.ID, rock.getRegistryName().getPath() + "_grind_to_" + smallDust.getRegistryName().getPath()));
            }
        });*/
    }
}
