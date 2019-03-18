package muramasa.gregtech.integration.forestry;

import muramasa.gregtech.api.data.Machines;
import muramasa.gregtech.api.enums.ItemType;
import muramasa.gregtech.api.interfaces.GregTechRegistrar;
import muramasa.gregtech.api.materials.Material;
import muramasa.gregtech.api.recipe.RecipeBuilder;
import muramasa.gregtech.api.util.Utils;
import net.minecraft.item.ItemStack;

import java.util.Arrays;

import static muramasa.gregtech.api.data.Materials.*;
import static muramasa.gregtech.api.materials.ItemFlag.*;
import static muramasa.gregtech.api.materials.MaterialSet.*;
import static muramasa.gregtech.api.materials.RecipeFlag.*;

public class ForestryRegistrar extends GregTechRegistrar {

    public static boolean EASY_COMB_RECIPES = false;

    private static RecipeBuilder RB = new RecipeBuilder();

    //TODO
    public ItemStack FR_WAX = null;

    public static Material Apatite;

    @Override
    public void onMaterialRegistration() {
         Apatite = new Material("Apatite", 0xc8c8ff, DIAMOND).asGemBasic(false, ORE).add(Calcium, 5, Phosphate, 3, Chlorine, 1);
    }

    @Override
    public void onMaterialInit() {
        ELEC.add(Apatite);
        NOSMELT.add(Apatite);
        NOSMASH.add(Apatite);
        CRYSTALLIZE.add(Apatite);
        Apatite.setOreMulti(4).setSmeltingMulti(4).setByProductMulti(2);
        Apatite.addByProduct(Phosphorus);
        Phosphorus.addByProduct(Apatite);
    }

    @Override
    public void onCraftingRecipeRegistration() {

    }

    @Override
    public void onMachineRecipeRegistration() {
        ItemStack comb;

        //Organic Line
        comb = ItemType.CombLignite.get(1);
        addSpecialCent(comb, new int[]{90}, Lignite.getGem(1));
        addProcess(comb, Lignite);
        comb = ItemType.CombCoal.get(1);
        addSpecialCent(comb, new int[]{40}, Coal.getGem(1));
        addProcess(comb, Coal);
        comb = ItemType.CombResin.get(1);
        addSpecialCent(comb, new int[]{70}, ItemType.StickyResin.get(1));
        comb = ItemType.CombOil.get(1);
        addSpecialCent(comb, new int[]{70}, ItemType.DropOil.get(2));
        addProcess(comb, Oilsands);

        //Gem Line
        comb = ItemType.CombStone.get(1);
        addSpecialCent(comb, new int[]{70, 20, 20}, Stone.getDust(1), Salt.getDust(1), RockSalt.getDust(1));
        addProcess(comb, Soapstone);
        addProcess(comb, Talc);
        addProcess(comb, Apatite);
        addProcess(comb, Phosphate);
        addProcess(comb, Phosphorus);
        comb = ItemType.CombCertus.get(1);
        addProcess(comb, CertusQuartz);
        addProcess(comb, Quartzite);
        addProcess(comb, Barite);
        comb = ItemType.CombRedstone.get(1);
        addProcess(comb, Redstone);
        //TODO TC compat addProcess(comb, Cinnabar);
        comb = ItemType.CombLapis.get(1);
        addProcess(comb, Lapis);
        addProcess(comb, Sodalite);
        addProcess(comb, Lazurite);
        addProcess(comb, Calcite);
        comb = ItemType.CombRuby.get(1);
        addProcess(comb, Ruby);
        addProcess(comb, Redstone);
        comb = ItemType.CombSapphire.get(1);
        addProcess(comb, Sapphire);
        addProcess(comb, GreenSapphire);
        addProcess(comb, Almandine);
        addProcess(comb, Pyrope);
        comb = ItemType.CombDiamond.get(1);
        addProcess(comb, Diamond);
        addProcess(comb, Graphite);
        comb = ItemType.CombOlivine.get(1);
        addProcess(comb, Olivine);
        addProcess(comb, Bentonite);
        addProcess(comb, Magnesite);
        addProcess(comb, Glauconite);
        comb = ItemType.CombEmerald.get(1);
        addProcess(comb, Emerald);
        addProcess(comb, Beryllium);
        addProcess(comb, Thorium);

        //Metals Line
        comb = ItemType.CombSlag.get(1);
        addSpecialCent(comb, new int[]{50, 20, 20}, Stone.getDust(1), GraniteBlack.getDust(1), GraniteRed.getDust(1));
        addProcess(comb, Salt);
        addProcess(comb, RockSalt);
        addProcess(comb, Lepidolite);
        addProcess(comb, Spodumene);
        addProcess(comb, Monazite);
        comb = ItemType.CombCopper.get(1);
        addSpecialCent(comb, new int[]{70}, Copper.getDustT(1));
        addProcess(comb, Copper);
        addProcess(comb, Tetrahedrite);
        addProcess(comb, Chalcopyrite);
        addProcess(comb, Malachite);
        addProcess(comb, Pyrite);
        addProcess(comb, Stibnite);
        comb = ItemType.CombTin.get(1);
        addSpecialCent(comb, new int[]{60}, Tin.getDustT(1));
        addProcess(comb, Tin);
        addProcess(comb, Cassiterite);
        comb = ItemType.CombLead.get(1);
        addSpecialCent(comb, new int[]{45}, Lead.getDustT(1));
        addProcess(comb, Lead);
        addProcess(comb, Galena);
        comb = ItemType.CombIron.get(1);
        addProcess(comb, Iron);
        addProcess(comb, Magnetite);
        addProcess(comb, BrownLimonite);
        addProcess(comb, YellowLimonite);
        addProcess(comb, VanadiumMagnetite);
        addProcess(comb, BandedIron);
        addProcess(comb, Pyrite);
        //TODO GC Compat if (ProcessingModSupport.aEnableGCMarsMats) addProcess(comb, MeteoricIron);
        comb = ItemType.CombSteel.get(1);
        addProcess(comb, Iron, Steel);
        addProcess(comb, Magnetite, Steel);
        addProcess(comb, BrownLimonite, Steel);
        addProcess(comb, YellowLimonite, Steel);
        addProcess(comb, VanadiumMagnetite, VanadiumSteel);
        addProcess(comb, BandedIron, Steel);
        addProcess(comb, Pyrite, Steel);
        //TODO GC Compat if (ProcessingModSupport.aEnableGCMarsMats) addProcess(comb, MeteoricIron, MeteoricSteel);
        addProcess(comb, Molybdenite);
        addProcess(comb, Molybdenum);
        comb = ItemType.CombNickel.get(1);
        addProcess(comb, Nickel);
        addProcess(comb, Garnierite);
        addProcess(comb, Pentlandite);
        addProcess(comb, Cobaltite);
        addProcess(comb, Wulfenite);
        addProcess(comb, Powellite);
        comb = ItemType.CombZinc.get(1);
        addProcess(comb, Zinc);
        addProcess(comb, Sphalerite);
        addProcess(comb, Sulfur);
        comb = ItemType.CombSilver.get(1);
        addSpecialCent(comb, new int[]{30}, Silver.getDustT(1));
        addProcess(comb, Silver);
        addProcess(comb, Galena);
        comb = ItemType.CombGold.get(1);
        addProcess(comb, Gold);
        addProcess(comb, Magnetite, Gold);

        //Rare Metals Line
        comb = ItemType.CombAluminium.get(1);
        addProcess(comb, 60, Aluminium);
        addProcess(comb, Bauxite);
        comb = ItemType.CombManganese.get(1);
        addProcess(comb, 30, Manganese);
        addProcess(comb, Grossular);
        addProcess(comb, Spessartine);
        addProcess(comb, Pyrolusite);
        addProcess(comb, Tantalite);
        comb = ItemType.CombTitanium.get(1);
        addProcess(comb, Titanium);
        addProcess(comb, Ilmenite);
        addProcess(comb, Bauxite);
        comb = ItemType.CombChrome.get(1);
        addProcess(comb, 50, Chrome);
        addProcess(comb, Ruby);
        //TODO ? addProcess(comb, Chromite, 50);
        addProcess(comb, Redstone);
        addProcess(comb, Neodymium);
        addProcess(comb, Bastnasite);
        comb = ItemType.CombTungsten.get(1);
        addProcess(comb, Tungstate);
        addProcess(comb, Scheelite);
        addProcess(comb, Lithium);
        comb = ItemType.CombPlatinum.get(1);
        addProcess(comb, 40, Platinum);
        addProcess(comb, 40, Sheldonite);
        addProcess(comb, 40, Palladium);
        comb = ItemType.CombIridium.get(1);
        addProcess(comb, 20, Iridium);
        addProcess(comb, 20, Osmium);

        //Radioactive Line
        comb = ItemType.CombUranium.get(1);
        addProcess(comb, 50, Uranium);
        addProcess(comb, 50, Pitchblende);
        addProcess(comb, 50, Uraninite);
        addProcess(comb, 50, Uranium235);
        comb = ItemType.CombPlutonium.get(1);
        addProcess(comb, 10, Plutonium);
        addProcess(comb, 5, Uranium235, Plutonium);
        comb = ItemType.CombNaquadah.get(1);
        addProcess(comb, 10, Naquadah);
        addProcess(comb, 10, NaquadahEnriched);
        addProcess(comb, 10, Naquadria);
    }

    public void addSpecialCent(ItemStack stack, int[] chances, ItemStack... outputs) {
        int[] chancesCopy = Arrays.copyOf(chances, chances.length);
        chancesCopy[chances.length] = 30;
        ItemStack[] outputsCopy = Arrays.copyOf(outputs, outputs.length);
        outputsCopy[outputs.length] = FR_WAX;
        RB.get(Machines.CENTRIFUGE).ii(stack).io(outputs).chances(chances).add(128, 5);
        //TODO RecipeManagers.centrifugeManager.addRecipe(40, stack, ImmutableMap.of(aOutput, chance * 0.01f, ItemList.FR_Wax.get(1, new Object[0]), 0.3f,aOutput2,chance2 * 0.01f,aOutput3,chance3*0.01f));
    }
    
    public void addProcess(ItemStack stack, Material... materials) {
        addProcess(stack, 100, materials);
    }

    public void addProcess(ItemStack stack, int chance, Material... materials) {
        if (!EASY_COMB_RECIPES) {
            if (materials.length == 0) return;
            RB.get(Machines.CHEMICAL_REACTOR).ii(Utils.ca(9, stack), materials[0].getCrushed(1)).fi(Water.getLiquid(1000)).io(materials.length == 2 ? materials[1].getCrushedP(4) : materials[0].getCrushedP(4)).fo(materials[0].getByProducts().isEmpty() ? null : materials[0].getByProducts().get(0).getLiquid(144)).add(96, 24);
            RB.get(Machines.AUTOCLAVE).ii(Utils.ca(16, stack)).fi(UUMatter.getLiquid(Math.max(1, ((materials[0].getMass()+9)/10)))).io(materials[0].getCrushedP(1)).add(materials[0].getMass() * 128, 384);
        } else {
            RB.get(Machines.CENTRIFUGE).ii(stack).io(materials[0].getDustT(1), FR_WAX).chances(chance, 30).add(128, 5);
            //TODO RecipeManagers.centrifugeManager.addRecipe(40, stack, ImmutableMap.of(materials[0].getDustT(1), /* TODO chance will be wrong */chance * 0.01f, FR_WAX, 0.3f));
        }
    }
}
