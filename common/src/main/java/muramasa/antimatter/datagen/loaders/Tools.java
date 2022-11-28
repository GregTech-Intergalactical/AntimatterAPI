package muramasa.antimatter.datagen.loaders;

import com.google.common.collect.ImmutableMap;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.data.AntimatterDefaultTools;
import muramasa.antimatter.data.AntimatterMaterialTypes;
import muramasa.antimatter.data.AntimatterMaterials;
import muramasa.antimatter.datagen.providers.AntimatterRecipeProvider;
import muramasa.antimatter.material.Material;
import muramasa.antimatter.material.MaterialTags;
import muramasa.antimatter.recipe.ingredient.PropertyIngredient;
import muramasa.antimatter.tool.AntimatterToolType;
import muramasa.antimatter.util.AntimatterPlatformUtils;
import muramasa.antimatter.util.TagUtils;
import net.minecraft.advancements.CriterionTriggerInstance;
import net.minecraft.data.recipes.FinishedRecipe;
import net.minecraft.tags.ItemTags;

import java.util.Collections;
import java.util.function.Consumer;
import java.util.function.Function;

import static com.google.common.collect.ImmutableMap.of;
import static muramasa.antimatter.material.MaterialTags.FLINT;
import static muramasa.antimatter.material.MaterialTags.HANDLE;
import static muramasa.antimatter.material.MaterialTags.RUBBERTOOLS;
import static muramasa.antimatter.recipe.RecipeBuilders.*;

public class Tools {
    public static void init(Consumer<FinishedRecipe> consumer, AntimatterRecipeProvider provider) {
        final CriterionTriggerInstance in = provider.hasSafeItem(AntimatterDefaultTools.WRENCH.getTag());

        if (AntimatterAPI.isModLoaded(Ref.MOD_TOP)) {
            provider.addToolRecipe(PROBE_BUILDER.get(AntimatterDefaultTools.HELMET.getId()), consumer, Ref.ID, "helmet_with_probe", "antimatter_armor",
                    "has_helmet", provider.hasSafeItem(AntimatterDefaultTools.HELMET.getToolStack(Material.NULL).getItem()), Collections.singletonList(AntimatterDefaultTools.HELMET.getToolStack(Material.NULL)), of('H', PropertyIngredient.builder("helmet").itemStacks(AntimatterDefaultTools.HELMET.getToolStack(Material.NULL).getItem()).build(), 'P', AntimatterPlatformUtils.getItemFromID(Ref.MOD_TOP, "probe")), "HP");
        }

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.HAMMER.getId()), consumer, Ref.ID, AntimatterDefaultTools.HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.HAMMER.getToolStack(Material.NULL, Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.INGOT, AntimatterMaterialTypes.GEM).tool(AntimatterDefaultTools.HAMMER, true).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SOFT_HAMMER.getId()), consumer, Ref.ID, AntimatterDefaultTools.SOFT_HAMMER.getId() + "_" + "recipe", "antimatter_tools",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.SOFT_HAMMER.getToolStack(Material.NULL, Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.INGOT, AntimatterMaterialTypes.GEM).tool(AntimatterDefaultTools.SOFT_HAMMER, true).tags(RUBBERTOOLS).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        if (Material.get("wood") != Material.NULL && Material.get("wood").has(MaterialTags.TOOLS) && MaterialTags.TOOLS.getToolData(Material.get("wood")).toolTypes().contains(AntimatterDefaultTools.SOFT_HAMMER)) {
            provider.addToolRecipe(WOOD_TOOL_BUILDER.get(AntimatterDefaultTools.SOFT_HAMMER.getId()), consumer, Ref.ID, AntimatterDefaultTools.SOFT_HAMMER.getId() + "_wood_" + "recipe", "antimatter_tools",
                    "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.SOFT_HAMMER.getToolStack(Material.get("wood"), Material.NULL)), of('I', ItemTags.PLANKS, 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II ", "IIR", "II ");
        }

        provider.addToolRecipe(ARMOR_BUILDER.get(AntimatterDefaultTools.HELMET.getId()), consumer, Ref.ID, AntimatterDefaultTools.HELMET.getId() + "_recipe", "antimatter_helmets",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.HELMET.getToolStack(Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(MaterialTags.ARMOR).build(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "III", "IHI");
        provider.addToolRecipe(ARMOR_BUILDER.get(AntimatterDefaultTools.CHESTPLATE.getId()), consumer, Ref.ID, AntimatterDefaultTools.CHESTPLATE.getId() + "_recipe", "antimatter_chestplates",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.CHESTPLATE.getToolStack(Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(MaterialTags.ARMOR).build(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "IHI", "III", "III");
        provider.addToolRecipe(ARMOR_BUILDER.get(AntimatterDefaultTools.LEGGINGS.getId()), consumer, Ref.ID, AntimatterDefaultTools.LEGGINGS.getId() + "_recipe", "antimatter_leggings",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.LEGGINGS.getToolStack(Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(MaterialTags.ARMOR).build(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "III", "IHI", "I I");
        provider.addToolRecipe(ARMOR_BUILDER.get(AntimatterDefaultTools.BOOTS.getId()), consumer, Ref.ID, AntimatterDefaultTools.BOOTS.getId() + "_recipe", "antimatter_boots",
                "has_wrench", in, Collections.singletonList(AntimatterDefaultTools.BOOTS.getToolStack(Material.NULL)), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(MaterialTags.ARMOR).build(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "I I", "IHI");

            /*addToolRecipe(TOOL_BUILDER.get(PLUNGER.getId()), consumer, muramasa.antimatter.Ref.ID, PLUNGER.getId() + "_recipe", "antimatter_plungers",
                    "has_wrench", in, Collections.singletonList(PLUNGER.getToolStack(NULL, NULL)),
                    of('W', WIRE_CUTTER.getTag(), 'I',  PropertyIngredient.of(INGOT, "primary"), 'S', Tags.Items.SLIMEBALLS, 'R', PropertyIngredient.builder("secondary").types(ROD).tags(RUBBERTOOLS).build(), 'F', FILE.getTag()), "WIS", " RI", "R F");*/

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.WRENCH.getId()), consumer, Ref.ID, AntimatterDefaultTools.WRENCH.getId() + "_recipe", "antimatter_wrenches",
                "has_wrench", in, AntimatterDefaultTools.WRENCH.getToolStack(Material.NULL, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tool(AntimatterDefaultTools.WRENCH, true).build(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "IHI", "III", " I ");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.MORTAR.getId()), consumer, Ref.ID, AntimatterDefaultTools.MORTAR.getId() + "_recipe", "antimatter_mortars",
                "has_wrench", in, AntimatterDefaultTools.MORTAR.getToolStack(Material.NULL, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.INGOT, AntimatterMaterialTypes.GEM).tool(AntimatterDefaultTools.MORTAR, true).build(), 'S', TagUtils.getForgelikeItemTag("stone")), " I ", "SIS", "SSS");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.FILE.getId()), consumer, Ref.ID, AntimatterDefaultTools.FILE.getId() + "_recipe", "antimatter_files",
                "has_wrench", in, AntimatterDefaultTools.FILE.getToolStack(Material.NULL, Material.NULL), of('P', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tool(AntimatterDefaultTools.FILE, true).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "P", "P", "R");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SCREWDRIVER.getId()), consumer, Ref.ID, AntimatterDefaultTools.SCREWDRIVER.getId() + "_recipe", "antimatter_screwdrivers",
                "has_wrench", in, AntimatterDefaultTools.SCREWDRIVER.getToolStack(Material.NULL, Material.NULL),
                of('M', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.ROD).tool(AntimatterDefaultTools.SCREWDRIVER, true).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build(), 'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), " FM", " MH", "R  ");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SAW.getId()), consumer, Ref.ID, AntimatterDefaultTools.SAW.getId() + "_recipe", "antimatter_saws",
                "has_wrench", in, AntimatterDefaultTools.SAW.getToolStack(Material.NULL, Material.NULL), of('P', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tool(AntimatterDefaultTools.SAW, true).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build(), 'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "PPR", "FH ");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.WIRE_CUTTER.getId()), consumer, Ref.ID, AntimatterDefaultTools.WIRE_CUTTER.getId() + "_recipe_noscrew", "antimatter_files",
                "has_wrench", in, AntimatterDefaultTools.WIRE_CUTTER.getToolStack(Material.NULL, Material.NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").inverse().tool(AntimatterDefaultTools.SCREWDRIVER, true).types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(AntimatterMaterialTypes.SCREW).build()).put('R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()).put('F', AntimatterDefaultTools.FILE.getTag()).put('H', AntimatterDefaultTools.HAMMER.getTag())
                                .put('S', AntimatterDefaultTools.SCREWDRIVER.getTag())
                , "PFP", "HPS", "R R");
        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.WIRE_CUTTER.getId()), consumer, Ref.ID, AntimatterDefaultTools.WIRE_CUTTER.getId() + "_recipe_screw", "antimatter_files",
                "has_wrench", in, AntimatterDefaultTools.WIRE_CUTTER.getToolStack(Material.NULL, Material.NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(AntimatterMaterialTypes.SCREW).tool(AntimatterDefaultTools.SCREWDRIVER, true).build()).put('R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()).put('F', AntimatterDefaultTools.FILE.getTag()).put('H', AntimatterDefaultTools.HAMMER.getTag())
                                .put('S', AntimatterDefaultTools.SCREWDRIVER.getTag()).put('W', PropertyIngredient.of(AntimatterMaterialTypes.SCREW, "primary"))
                , "PFP", "HPS", "RWR");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.BRANCH_CUTTER.getId()), consumer, Ref.ID, AntimatterDefaultTools.BRANCH_CUTTER.getId() + "_recipe_noscrew", "antimatter_files",
                "has_wrench", in, AntimatterDefaultTools.BRANCH_CUTTER.getToolStack(Material.NULL, Material.NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").inverse().tool(AntimatterDefaultTools.SCREWDRIVER, true).types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(AntimatterMaterialTypes.SCREW).build()).put('R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()).put('F', AntimatterDefaultTools.FILE.getTag())
                                .put('S', AntimatterDefaultTools.SCREWDRIVER.getTag())
                , "PFP", "PSP", "R R");
        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.BRANCH_CUTTER.getId()), consumer, Ref.ID, AntimatterDefaultTools.BRANCH_CUTTER.getId() + "_recipe_screw", "antimatter_files",
                "has_wrench", in, AntimatterDefaultTools.BRANCH_CUTTER.getToolStack(Material.NULL, Material.NULL), b ->
                        b.put('P', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tags(AntimatterMaterialTypes.SCREW).tool(AntimatterDefaultTools.SCREWDRIVER, true).build()).put('R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()).put('F', AntimatterDefaultTools.FILE.getTag())
                                .put('S', AntimatterDefaultTools.SCREWDRIVER.getTag()).put('W', PropertyIngredient.of(AntimatterMaterialTypes.SCREW, "primary"))
                , "PFP", "PSP", "RWR");

        Function<AntimatterToolType, ImmutableMap<Character, Object>> map1 = type -> of('I', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(AntimatterMaterialTypes.INGOT, AntimatterMaterialTypes.GEM).tool(type, true).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build(), 'P', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tool(type, true).build(), 'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag());

        Function<AntimatterToolType, ImmutableMap<Character, Object>> map2 = type -> of('R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build(), 'P', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tool(type, true).build(), 'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag());

        String[] strings1 = new String[]{"PII", "FRH", " R "};
        String[] strings3 = new String[]{" P ", "FPH", " R "};

        String[] strings2 = new String[]{"FPH", " R ", " R "};
        String[] strings2Gem = new String[]{"FGH", " R ", " R "};

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.PICKAXE.getId()), consumer, Ref.ID, AntimatterDefaultTools.PICKAXE.getId() + "_with", "antimatter_pickaxes",
                "has_wrench", in, AntimatterDefaultTools.PICKAXE.getToolStack(Material.NULL, Material.NULL), map1.apply(AntimatterDefaultTools.PICKAXE), strings1);

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SHOVEL.getId()), consumer, Ref.ID, AntimatterDefaultTools.SHOVEL.getId() + "_with", "antimatter_shovels",
                "has_wrench", in, AntimatterDefaultTools.SHOVEL.getToolStack(Material.NULL, Material.NULL), map2.apply(AntimatterDefaultTools.SHOVEL), strings2);

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.AXE.getId()), consumer, Ref.ID, AntimatterDefaultTools.AXE.getId() + "_with", "antimatter_axes",
                "has_wrench", in, AntimatterDefaultTools.AXE.getToolStack(Material.NULL, Material.NULL), map1.apply(AntimatterDefaultTools.AXE), "PIH", "PR ", "FR ");


        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SWORD.getId()), consumer, Ref.ID, AntimatterDefaultTools.SWORD.getId() + "_with", "antimatter_swords",
                "has_wrench", in, AntimatterDefaultTools.SWORD.getToolStack(Material.NULL, Material.NULL), map2.apply(AntimatterDefaultTools.SWORD), " P ", "FPH", " R ");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.HOE.getId()), consumer, Ref.ID, AntimatterDefaultTools.HOE.getId() + "_with", "antimatter_swords",
                "has_wrench", in, AntimatterDefaultTools.HOE.getToolStack(Material.NULL, Material.NULL), map1.apply(AntimatterDefaultTools.HOE), "PIH", "FR ", " R ");

        provider.addToolRecipe(CROWBAR_BUILDER.get(AntimatterDefaultTools.CROWBAR.getId()), consumer, Ref.ID, AntimatterDefaultTools.CROWBAR.getId() + "_recipe", "antimatter_crowbars",
                "has_wrench", in, AntimatterDefaultTools.CROWBAR.getToolStack(Material.NULL, Material.NULL), of('H', AntimatterDefaultTools.HAMMER.getTag(), 'C', PropertyIngredient.builder("secondary").itemTags(TagUtils.getForgelikeItemTag("dyes")).build(), 'R', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.ROD).tool(AntimatterDefaultTools.CROWBAR, true).build(), 'F', AntimatterDefaultTools.FILE.getTag()), "HCR", "CRC", "RCF");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.KNIFE.getId()), consumer, Ref.ID, AntimatterDefaultTools.KNIFE.getId() + "_with", "antimatter_knives",
                "has_wrench", in, AntimatterDefaultTools.KNIFE.getToolStack(Material.NULL, Material.NULL), of('P', PropertyIngredient.builder("primary").inverse().tags(FLINT).types(AntimatterMaterialTypes.PLATE, AntimatterMaterialTypes.GEM).tool(AntimatterDefaultTools.KNIFE, true).build(), 'S', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build(), 'F', AntimatterDefaultTools.FILE.getTag(), 'H', AntimatterDefaultTools.HAMMER.getTag()), "FP", "HS");

        // List<Material> handleMats = AntimatterAPI.all(Material.class).stream().filter(m -> (m.getDomain().equals(providerDomain) && m.has(HANDLE))).collect(Collectors.toList());

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.PLUNGER.getId()), consumer, Ref.ID, AntimatterDefaultTools.PLUNGER.getId() + "_", "antimatter_plungers",
                "has_wrench", in, AntimatterDefaultTools.PLUNGER.getToolStack(Material.NULL, Material.NULL),
                of('W', AntimatterDefaultTools.WIRE_CUTTER.getTag(), 'R', PropertyIngredient.builder("primary").tool(AntimatterDefaultTools.PLUNGER, true).types(AntimatterMaterialTypes.ROD).build(), 'I', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.PLATE).tags(HANDLE, RUBBERTOOLS).build(), 'F', AntimatterDefaultTools.FILE.getTag()), "WII", " RI", "R F");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.PICKAXE.getId()), consumer, Ref.ID, "flint_" + AntimatterDefaultTools.PICKAXE.getId() + "_" + "recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(AntimatterMaterialTypes.GEM.getMaterialTag(AntimatterMaterials.Flint)), AntimatterDefaultTools.PICKAXE.getToolStack(AntimatterMaterials.Flint, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "III", " R ", " R ");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.AXE.getId()), consumer, Ref.ID, "flint_" + AntimatterDefaultTools.AXE.getId() + "_" + "recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(AntimatterMaterialTypes.GEM.getMaterialTag(AntimatterMaterials.Flint)), AntimatterDefaultTools.AXE.getToolStack(AntimatterMaterials.Flint, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II", "IR", " R");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SWORD.getId()), consumer, Ref.ID, "flint_" + AntimatterDefaultTools.SWORD.getId() + "_" + "recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(AntimatterMaterialTypes.GEM.getMaterialTag(AntimatterMaterials.Flint)), AntimatterDefaultTools.SWORD.getToolStack(AntimatterMaterials.Flint, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "I", "I", "R");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.SHOVEL.getId()), consumer, Ref.ID, "flint_" + AntimatterDefaultTools.SHOVEL.getId() + "_" + "recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(AntimatterMaterialTypes.GEM.getMaterialTag(AntimatterMaterials.Flint)), AntimatterDefaultTools.SHOVEL.getToolStack(AntimatterMaterials.Flint, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "I", "R", "R");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.HOE.getId()), consumer, Ref.ID, "flint_" + AntimatterDefaultTools.HOE.getId() + "_" + "recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(AntimatterMaterialTypes.GEM.getMaterialTag(AntimatterMaterials.Flint)), AntimatterDefaultTools.HOE.getToolStack(AntimatterMaterials.Flint, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "II", " R", " R");

        provider.addToolRecipe(TOOL_BUILDER.get(AntimatterDefaultTools.KNIFE.getId()), consumer, Ref.ID, "flint_" + AntimatterDefaultTools.KNIFE.getId() + "_" + "recipe", "antimatter_tools",
                "has_flint", provider.hasSafeItem(AntimatterMaterialTypes.GEM.getMaterialTag(AntimatterMaterials.Flint)), AntimatterDefaultTools.KNIFE.getToolStack(AntimatterMaterials.Flint, Material.NULL), of('I', PropertyIngredient.builder("primary").types(AntimatterMaterialTypes.GEM).tags(FLINT).build(), 'R', PropertyIngredient.builder("secondary").types(AntimatterMaterialTypes.ROD).tags(HANDLE).build()), "RI");
    }
}
