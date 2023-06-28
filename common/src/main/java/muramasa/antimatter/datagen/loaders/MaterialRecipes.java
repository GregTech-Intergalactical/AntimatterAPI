package muramasa.antimatter.datagen.loaders;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.AntimatterConfig;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.datagen.builder.AntimatterCookingRecipeBuilder;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.material.MaterialType;
import muramasa.antimatter.material.MaterialTypeItem;
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
import static muramasa.antimatter.data.AntimatterDefaultTools.*;
import static muramasa.antimatter.data.AntimatterMaterialTypes.*;
import static muramasa.antimatter.material.MaterialTags.*;
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
        AntimatterMaterialTypes.INGOT.all().forEach(m -> {
            if (m.has(AntimatterMaterialTypes.NUGGET) && m != AntimatterMaterials.Iron && m != AntimatterMaterials.Gold){
                provider.addItemRecipe(consumer, Ref.ID, m.getId() + "_ingot", "ingots", "has_nugget", provider.hasSafeItem(AntimatterMaterialTypes.NUGGET.getMaterialTag(m)), AntimatterMaterialTypes.INGOT.get(m), ImmutableMap.of('I', AntimatterMaterialTypes.NUGGET.getMaterialTag(m)), "III", "III", "III");
                provider.shapeless(consumer,"nugget_" + m.getId() + "_from_ingot", "ingots", "has_ingot", provider.hasSafeItem(AntimatterMaterialTypes.INGOT.getMaterialTag(m)), AntimatterMaterialTypes.NUGGET.get(m, 9), AntimatterMaterialTypes.INGOT.getMaterialTag(m));
            }
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
                if (!m.has(NOSMASH)){
                    provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_ring", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                            AntimatterMaterialTypes.RING.get(m, craftingMultiplier), ImmutableMap.of('H', AntimatterDefaultTools.HAMMER.getTag(), 'W', AntimatterMaterialTypes.ROD.getMaterialTag(m)), "H ", " W");
                }
            }
            if (m.has(ROD_LONG)){
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_rod_from_long_rod", "rods", "has_saw", provider.hasSafeItem(SAW.getTag()), ROD.get(m, 2),
                        ImmutableMap.of('S', SAW.getTag(), 'R', ROD_LONG.getMaterialTag(m)), "SR");
                if (!m.has(NOSMASH)){
                    provider.addStackRecipe(consumer, Ref.ID, "", "rods", "has_hammer", provider.hasSafeItem(HAMMER.getTag()), ROD_LONG.get(m, 1),
                            ImmutableMap.of('S', HAMMER.getTag(), 'R', ROD.getMaterialTag(m)), "RSR");
                }
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
            if (!m.has(NOSMASH)){
                if (m.has(AntimatterMaterialTypes.INGOT)){
                    Object[] array = AntimatterConfig.GAMEPLAY.LOSSY_PART_CRAFTING ? new Object[]{AntimatterDefaultTools.HAMMER.getTag(), AntimatterMaterialTypes.INGOT.getMaterialTag(m), AntimatterMaterialTypes.INGOT.getMaterialTag(m)} : new Object[]{AntimatterDefaultTools.HAMMER.getTag(), AntimatterMaterialTypes.INGOT.getMaterialTag(m)};
                    provider.shapeless(consumer, "", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()), AntimatterMaterialTypes.PLATE.get(m, 1), array);
                }
                if (m.has(AntimatterMaterialTypes.GEAR_SMALL)) {
                    provider.addStackRecipe(consumer, Ref.ID, "", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                            AntimatterMaterialTypes.GEAR_SMALL.get(m, 1), ImmutableMap.of('H', AntimatterDefaultTools.HAMMER.getTag(),'P', AntimatterMaterialTypes.PLATE.getMaterialTag(m)), "P ", " H");
                }
                if (m.has(AntimatterMaterialTypes.ITEM_CASING)) {
                    provider.addStackRecipe(consumer, Ref.ID, "", "antimatter_material", "has_hammer", provider.hasSafeItem(AntimatterDefaultTools.HAMMER.getTag()),
                            AntimatterMaterialTypes.ITEM_CASING.get(m, 1), ImmutableMap.of('H', AntimatterDefaultTools.HAMMER.getTag(),'P', AntimatterMaterialTypes.PLATE.getMaterialTag(m)), "H P");
                }
                if (m.has(FOIL)){
                    provider.addStackRecipe(consumer, Ref.ID, "", "antimatter_materials", "has_hammer", provider.hasSafeItem(HAMMER.getTag()),
                            FOIL.get(m, 2), of('H', HAMMER.getTag(), 'P', PLATE.getMaterialTag(m)), "HP");
                    if (m.has(WIRE_FINE)){
                        provider.addItemRecipe(consumer, Ref.ID, "", "antimatter_materials", "has_wire_cutters", provider.hasSafeItem(WIRE_CUTTER.getTag()),
                                WIRE_FINE.get(m), of('F', FOIL.getMaterialTag(m), 'W', WIRE_CUTTER.getTag()), "FW");
                    }
                }
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
            if (m.has(AntimatterMaterialTypes.RING)) {
                if (m.has(RUBBERTOOLS)){
                    provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_ring", "antimatter_material", "has_wire_cutter", provider.hasSafeItem(AntimatterDefaultTools.WIRE_CUTTER.getTag()),
                            AntimatterMaterialTypes.RING.get(m, craftingMultiplier), ImmutableMap.of('H', AntimatterDefaultTools.WIRE_CUTTER.getTag(), 'W', AntimatterMaterialTypes.PLATE.getMaterialTag(m)), "H ", " W");
                }
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
                provider.addStackRecipe(consumer, Ref.ID, m.getId() + "_grind_rock", "antimatter_material", "has_mortar", provider.hasSafeItem(AntimatterDefaultTools.MORTAR.getTag()),
                        AntimatterMaterialTypes.DUST.get(m, 1), ImmutableMap.<Character, Object>builder()
                                .put('M', AntimatterDefaultTools.MORTAR.getTag())
                                .put('I', AntimatterMaterialTypes.ROCK.getMaterialTag(m))
                                .build(),
                        "II ", "IIM");
                provider.shapeless(consumer, m.getId() + "_grind_rock_2", "antimatter_material", "has_mortar", provider.hasSafeItem(AntimatterDefaultTools.MORTAR.getTag()), AntimatterMaterialTypes.DUST_SMALL.get(m, 1),
                        AntimatterDefaultTools.MORTAR.getTag(), AntimatterMaterialTypes.ROCK.getMaterialTag(m));
            }
            if (m.has(AntimatterMaterialTypes.CRUSHED)){
                provider.shapeless(consumer, m.getId() + "_grind_crushed", "antimatter_material", "has_mortar", provider.hasSafeItem(AntimatterDefaultTools.MORTAR.getTag()), AntimatterMaterialTypes.DUST_IMPURE.get(m, 1),
                        AntimatterDefaultTools.MORTAR.getTag(), AntimatterMaterialTypes.CRUSHED.getMaterialTag(m));
            }
        });
        AntimatterMaterialTypes.RAW_ORE.all().stream().filter(m -> !m.has(HAS_CUSTOM_SMELTING) && SMELT_INTO.getMapping(m).has(AntimatterMaterialTypes.INGOT) && !m.has(HAS_CUSTOM_SMELTING)).forEach(m -> {
            if (m != AntimatterMaterials.Iron && m != AntimatterMaterials.Copper && m != AntimatterMaterials.Gold) {
                addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.RAW_ORE, AntimatterMaterialTypes.INGOT, 1, m, SMELT_INTO.getMapping(m));
            }
            addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.ORE, AntimatterMaterialTypes.INGOT, 1, m, SMELT_INTO.getMapping(m));
            if (m != SMELT_INTO.getMapping(m) || !m.has(AntimatterMaterialTypes.NUGGET)) return;
            addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.CRUSHED, AntimatterMaterialTypes.NUGGET, 12, m);
            addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.DUST_IMPURE, INGOT, 1, m);
            addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.CRUSHED_PURIFIED, AntimatterMaterialTypes.NUGGET, 11, m);
            addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.DUST_PURE, INGOT, 1, m);
            addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.CRUSHED_REFINED, AntimatterMaterialTypes.NUGGET, 10, m);
        });
        AntimatterMaterialTypes.DUST.all().forEach(m -> {
            if (m.has(MaterialTags.HAS_CUSTOM_SMELTING)) return;
            if (!DIRECT_SMELT_INTO.getMapping(m).has(AntimatterMaterialTypes.INGOT)) return;
            addSmeltingRecipe(consumer, provider, AntimatterMaterialTypes.DUST, AntimatterMaterialTypes.INGOT, 1, m, DIRECT_SMELT_INTO.getMapping(m));
        });
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> consumer, AntimatterRecipeProvider provider, MaterialType<?> input, MaterialTypeItem<?> output, int amount, Material in){
        addSmeltingRecipe(consumer, provider, input, output, amount, in, in);
    }

    private static void addSmeltingRecipe(Consumer<FinishedRecipe> consumer, AntimatterRecipeProvider provider, MaterialType<?> input, MaterialTypeItem<?> output, int amount, Material in, Material out){
        AntimatterCookingRecipeBuilder.blastingRecipe(RecipeIngredient.of(input.getMaterialTag(in), 1), new ItemStack(output.get(out), MaterialTags.SMELTING_MULTI.getInt(in) * amount), 2.0F, 100)
                .addCriterion("has_material_" + in.getId(), provider.hasSafeItem(output.getMaterialTag(out)))
                .build(consumer, provider.fixLoc(Ref.ID, in.getId().concat("_" + input.getId() + "_to_" + output.getId())));
        AntimatterCookingRecipeBuilder.smeltingRecipe(RecipeIngredient.of(input.getMaterialTag(in), 1), new ItemStack(output.get(out), MaterialTags.SMELTING_MULTI.getInt(in) * amount), 2.0F, 200)
                .addCriterion("has_material_" + in.getId(), provider.hasSafeItem(output.getMaterialTag(out)))
                .build(consumer, provider.fixLoc(Ref.ID, in.getId().concat("_" + input.getId() + "_to_" + output.getId() + "_smelting")));
    }
}
