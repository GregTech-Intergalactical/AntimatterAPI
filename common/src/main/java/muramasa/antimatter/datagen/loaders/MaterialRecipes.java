package muramasa.antimatter.datagen.loaders;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.datagen.builder.AntimatterCookingRecipeBuilder;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.ore.BlockOre;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.recipe.ingredient.RecipeIngredient;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.data.recipes.SimpleCookingRecipeBuilder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.stream.Collectors;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.Data.*;
import static muramasa.antimatter.material.MaterialTags.GRINDABLE;
import static muramasa.antimatter.recipe.RecipeBuilders.DUST_BUILDER;
import static muramasa.antimatter.recipe.RecipeBuilders.DUST_TWO_BUILDER;
import static muramasa.antimatter.util.Utils.getConventionalMaterialType;
import static muramasa.antimatter.util.Utils.getConventionalStoneType;

public class MaterialRecipes {
    public static void init(Consumer<FinishedRecipe> consumer, AntimatterRecipeProvider provider) {
        final CriterionTriggerInstance in = provider.hasSafeItem(WRENCH.getTag());
        DUST.all().forEach(m -> {
            provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_dust_small", "antimatter_dusts", "has_wrench", in, DUST.get(m, 1), of('D', DUST_SMALL.getMaterialTag(m)), "DD", "DD");
            provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_dust_tiny", "antimatter_dusts", "has_wrench", in, DUST.get(m, 1), of('D', DUST_TINY.getMaterialTag(m)), "DDD", "DDD", "DDD");
        });
        ROD.all().forEach(m -> {
            if (m.has(INGOT)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_rod", "antimatter_material", "has_wrench", in, ROD.get(m, 1), of('F', FILE.getTag(), 'I', INGOT.getMaterialTag(m)), "F", "I");
            }
            if (m.has(BOLT)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_bolt", "antimatter_material", "has_wrench", in, BOLT.get(m, 2), of('F', SAW.getTag(), 'I', ROD.getMaterialTag(m)), "F ", " I");
                if (m.has(SCREW)) {
                    provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_screw", "antimatter_material",
                            "has_wrench", in, SCREW.get(m, 1), of('F', FILE.getTag(), 'I', BOLT.getMaterialTag(m)), "FI", "I ");
                }
            }
            if (m.has(RING)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_ring", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                        RING.get(m, 1), ImmutableMap.of('H', HAMMER.getTag(), 'W', ROD.getMaterialTag(m)), "H ", " W");
            }
        });
        ROTOR.all().forEach(m -> {
            provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_rotors", "antimatter_material", "has_screwdriver", provider.hasSafeItem(SCREWDRIVER.getTag()),
                    ROTOR.get(m, 1), ImmutableMap.<Character, Object>builder()
                            .put('S', SCREWDRIVER.getTag())
                            .put('F', FILE.getTag())
                            .put('H', HAMMER.getTag())
                            .put('P', PLATE.getMaterialTag(m))
                            .put('W', SCREW.getMaterialTag(m))
                            .put('R', RING.getMaterialTag(m))
                            .build(),
                    "PHP", "WRF", "PSP");
        });
        PLATE.all().forEach(m -> {
            if (m.has(INGOT)){
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_plate", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                        PLATE.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('H', HAMMER.getTag())
                                .put('P', INGOT.getMaterialTag(m))
                                .build(),
                        "HP", "P ");
            }
            if (m.has(GEAR_SMALL)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_gear_small", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                        GEAR_SMALL.get(m, 1), ImmutableMap.of('H', HAMMER.getTag(),'P', PLATE.getMaterialTag(m)), "P ", " H");
            }
            if (m.has(GEAR)){
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_gear", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                        GEAR.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('W', WRENCH.getTag())
                                .put('P', PLATE.getMaterialTag(m))
                                .put('R', ROD.getMaterialTag(m))
                                .build(),
                        "RPR", "PWP", "RPR");
            }
        });

        DUST.all().forEach(m -> {
            if (m.has(INGOT)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_grind_ingot", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                        DUST.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('M', MORTAR.getTag())
                                .put('I', INGOT.getMaterialTag(m))
                                .build(),
                        "MI");
            }
            if (m.has(ROCK)) {
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_grind_rock", "antimatter_material", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                        DUST.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('M', MORTAR.getTag())
                                .put('I', ROCK.getMaterialTag(m))
                                .build(),
                        "III", "III", "IIM");
            }
        });

        AntimatterAPI.all(BlockOre.class, o -> {
            if (o.getOreType() != ORE) return;
            if (!MaterialTags.SMELT_INTO.getMapping(o.getMaterial()).has(INGOT)) return;
            if (o.getMaterial().has(MaterialTags.NEEDS_BLAST_FURNACE)) return;
            Item ingot = INGOT.get(MaterialTags.SMELT_INTO.getMapping(o.getMaterial()));
            TagKey<Item> oreTag = TagUtils.getForgelikeItemTag(String.join("", getConventionalStoneType(o.getStoneType()), "_", getConventionalMaterialType(o.getOreType()), "/", o.getMaterial().getId()));
            TagKey<Item> ingotTag = TagUtils.getForgelikeItemTag("ingots/".concat(MaterialTags.SMELT_INTO.getMapping(o.getMaterial()).getId()));
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(oreTag, 1), new ItemStack(ingot, MaterialTags.SMELTING_MULTI.getInt(o.getMaterial())), 2.0F, 100)
                    .addCriterion("has_material_" + o.getMaterial().getId(), provider.hasSafeItem(ingotTag))
                    .build(consumer, provider.fixLoc(Ref.ID, o.getId().concat("_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(oreTag, 1), new ItemStack(ingot, MaterialTags.SMELTING_MULTI.getInt(o.getMaterial())), 2.0F, 200)
                    .addCriterion("has_material_" + o.getMaterial().getId(), provider.hasSafeItem(ingotTag))
                    .build(consumer, provider.fixLoc(Ref.ID, o.getId().concat("_to_ingot_smelting")));
        });
        AntimatterAPI.all(Material.class).stream().filter(m -> m.has(RAW_ORE) && MaterialTags.SMELT_INTO.getMapping(m).has(INGOT) && !m.has(MaterialTags.NEEDS_BLAST_FURNACE)).forEach(m -> {
            AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1), new ItemStack(INGOT.get(MaterialTags.SMELT_INTO.getMapping(m)), MaterialTags.SMELTING_MULTI.getInt(m)), 2.0F, 100)
                    .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(INGOT.getMaterialTag(MaterialTags.SMELT_INTO.getMapping(m))))
                    .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot")));
            AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(RAW_ORE.getMaterialTag(m), 1), new ItemStack(INGOT.get(MaterialTags.SMELT_INTO.getMapping(m)), MaterialTags.SMELTING_MULTI.getInt(m)), 2.0F, 200)
                    .addCriterion("has_material_" + m.getId(), provider.hasSafeItem(INGOT.getMaterialTag(MaterialTags.SMELT_INTO.getMapping(m))))
                    .build(consumer, provider.fixLoc(Ref.ID, m.getId().concat("_raw_ore_to_ingot_smelting")));
        });
        DUST.all().forEach(m -> {
            if (m.has(MaterialTags.NEEDS_BLAST_FURNACE) || m.has(MaterialTags.HAS_CUSTOM_SMELTING)) return;
            if (!MaterialTags.DIRECT_SMELT_INTO.getMapping(m).has(INGOT)) return;
            SimpleCookingRecipeBuilder.blasting(DUST.getMaterialIngredient(m, 1), INGOT.get(MaterialTags.DIRECT_SMELT_INTO.getMapping(m)), 0.5F, 100).unlockedBy("has_" + m.getId() + "_dust", provider.hasSafeItem(DUST.getMaterialTag(m))).save(consumer, Ref.SHARED_ID + ":" + m.getId() + "_dust_to_ingot_bl");
            SimpleCookingRecipeBuilder.smelting(DUST.getMaterialIngredient(m, 1), INGOT.get(MaterialTags.DIRECT_SMELT_INTO.getMapping(m)), 0.5F, 200).unlockedBy("has_" + m.getId() + "_dust", provider.hasSafeItem(DUST.getMaterialTag(m))).save(consumer, Ref.SHARED_ID + ":" + m.getId() + "_dust_to_ingot");
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
