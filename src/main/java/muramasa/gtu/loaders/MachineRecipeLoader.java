package muramasa.gtu.loaders;

import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.recipe.RecipeAdder;
import muramasa.gtu.api.recipe.RecipeBuilder;
import muramasa.gtu.api.util.Utils;
import muramasa.gtu.common.Data;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.FurnaceRecipes;
import net.minecraftforge.oredict.OreDictionary;

import static muramasa.gtu.api.data.Machines.*;
import static muramasa.gtu.api.data.Materials.*;
import static muramasa.gtu.api.materials.GenerationFlag.INGOT;
import static muramasa.gtu.api.materials.RecipeFlag.NOBBF;
import static muramasa.gtu.api.materials.RecipeFlag.NOSMELT;

public class MachineRecipeLoader {

    public static RecipeBuilder RB = new RecipeBuilder();

    public static void init() {

        OreDictionary.getOres("logWood").forEach(i -> COKE_OVEN.RB().ii(Utils.ca(2, i), Coal.getDust(1)).io(Charcoal.getGem(1)).fo(Creosote.getLiquid(250)).add(3600));
        COKE_OVEN.RB().ii(Coal.getGem(1)).io(CoalCoke.getGem(1)).fo(Creosote.getLiquid(500)).add(3600);
        COKE_OVEN.RB().ii(Lignite.getGem(1)).io(LigniteCoke.getGem(1)).fo(Creosote.getLiquid(750)).add(3600);

        //Add Basic blasting for mixed metals
        GenerationFlag.DUST.getMats().forEach(m -> {
            if (m.getDirectSmeltInto() != m && !m.has(NOSMELT) && !(m.needsBlastFurnace() || m.getDirectSmeltInto().needsBlastFurnace()) && !m.has(NOBBF)) {
                if (m.getDirectSmeltInto().has(INGOT)) { //TODO INGOT check was added to avoid DOES NOT GENERATE: P(INGOT) M(mercury)
                    RecipeAdder.addBasicBlast(new ItemStack[]{m.getDust(2)}, new ItemStack[]{m.getDirectSmeltInto().getIngot(MaterialRecipeLoader.aMixedOreYieldCount)}, 2, 2400);
                }
            }
        });

        /** Temp Testing Recipes **/ //TODO remove
        COMBUSTION_ENGINE.RB().fi(Diesel.getLiquid(1)).fo(CarbonDioxide.getGas(1)).add(1, 0, 1024);

        FurnaceRecipes.instance().getSmeltingList().forEach((k, v) -> MULTI_SMELTER.RB().ii(k).io(v).add(60, 2));

        IMPLOSION_COMPRESSOR.RB().ii(Data.IridiumAlloyIngot.get(1)).io(Data.IridiumReinforcedPlate.get(1), DarkAsh.getDustT(4)).add(20, 30);

        BLAST_FURNACE.RB().ii(Tungsten.getIngot(1), Steel.getIngot(1)).io(TungstenSteel.getIngotH(2), DarkAsh.getDustS(1)).add(Math.max(TungstenSteel.getMass() / 80L, 1L) * TungstenSteel.getBlastTemp(), 480, TungstenSteel.getBlastTemp());
        BLAST_FURNACE.RB().ii(Tungsten.getIngot(1), Carbon.getDust(1)).io(TungstenCarbide.getIngotH(1), Ash.getDustS(2)).add(Math.max(TungstenCarbide.getMass() / 40L, 1L) * TungstenCarbide.getBlastTemp(), 480, TungstenCarbide.getBlastTemp());
        BLAST_FURNACE.RB().ii(Vanadium.getIngot(3), Gallium.getIngot(1)).io(VanadiumGallium.getIngotH(4), DarkAsh.getDustS(2)).add(Math.max(VanadiumGallium.getMass() / 40L, 1L) * VanadiumGallium.getBlastTemp(), 480, VanadiumGallium.getBlastTemp());
        BLAST_FURNACE.RB().ii(Niobium.getIngot(1), Titanium.getIngot(1)).io(NiobiumTitanium.getIngotH(2), DarkAsh.getDustS(1)).add(Math.max(NiobiumTitanium.getMass() / 80L, 1L) * NiobiumTitanium.getBlastTemp(), 480, NiobiumTitanium.getBlastTemp());
        BLAST_FURNACE.RB().ii(Nickel.getIngot(4), Chrome.getIngot(1)).io(Nichrome.getIngotH(5), DarkAsh.getDustS(2)).add(Math.max(Nichrome.getMass() / 32L, 1L) * Nichrome.getBlastTemp(), 480, Nichrome.getBlastTemp());
        BLAST_FURNACE.RB().ii(Ruby.getDust(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(400, 100, 1200);
        BLAST_FURNACE.RB().ii(Ruby.getGem(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(320, 100, 1200);
        //RB.get(BLAST_FURNACE).ii(GreenSapphire.getDust(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(400, 100, 1200);
        //RB.get(BLAST_FURNACE).ii(GreenSapphire.getGem(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(320, 100, 1200);
        BLAST_FURNACE.RB().ii(Sapphire.getDust(1)).io(Aluminium.getNugget(3)).add(400, 100, 1200);
        BLAST_FURNACE.RB().ii(Sapphire.getGem(1)).io(Aluminium.getNugget(3)).add(320, 100, 1200);
        BLAST_FURNACE.RB().ii(Ilmenite.getDust(1), Carbon.getDust(1)).io(WroughtIron.getNugget(4), Rutile.getDustT(4)).add(800, 500, 1700);
        BLAST_FURNACE.RB().ii(Magnesium.getDust(2)).fi(Titaniumtetrachloride.getLiquid(1000)).io(Titanium.getIngotH(1), MagnesiumChloride.getDust(2)).add(800, 480, Titanium.getBlastTemp() + 200);

        BLAST_FURNACE.RB().ii(Galena.getDust(1)).fi(Oxygen.getGas(2000)).io(Silver.getNugget(4), Lead.getNugget(4)).add(400, 500, 1500);
        BLAST_FURNACE.RB().ii(Magnetite.getDust(1)).fi(Oxygen.getGas(2000)).io(WroughtIron.getNugget(4), DarkAsh.getDustS(1)).add(400, 500, 1000);
        BLAST_FURNACE.RB().ii(Iron.getIngot(1)).fi(Oxygen.getGas(1000)).io(Steel.getIngot(1), DarkAsh.getDustS(1)).add(500, 120, 1000);
        BLAST_FURNACE.RB().ii(WroughtIron.getIngot(1)).fi(Oxygen.getGas(1000)).io(Steel.getIngot(1), DarkAsh.getDustS(1)).add(100, 120, 1000);
        BLAST_FURNACE.RB().ii(Copper.getDust(1)).fi(Oxygen.getGas(1000)).io(AnnealedCopper.getIngot(1)).add(500, 120, 1200);
        BLAST_FURNACE.RB().ii(Copper.getIngot(1)).fi(Oxygen.getGas(1000)).io(AnnealedCopper.getIngot(1)).add(500, 120, 1200);
        BLAST_FURNACE.RB().ii(Iridium.getIngot(3), Osmium.getIngot(1)).fi(Helium.getGas(1000)).io(Osmiridium.getIngotH(4)).add(500, 1920, 2900);
        BLAST_FURNACE.RB().ii(Naquadah.getIngot(1), Osmiridium.getIngot(1)).fi(Argon.getGas(1000)).io(NaquadahAlloy.getIngotH(2)).add(500, 30720, NaquadahAlloy.getBlastTemp());

        /** FUSION AGE **/

        //Power Gen Recipes
        FUSION_REACTOR.RB().fi(Deuterium.getGas(125), Tritium.getGas(125)).fo(Helium.getPlasma(125)).add(16, 4096, 40000000); //Mark 1 Cheap
        FUSION_REACTOR.RB().fi(Deuterium.getGas(125), Helium3.getGas(125)).fo(Helium.getPlasma(125)).add(16, 2048, 60000000); //Mark 1 Expensive
        FUSION_REACTOR.RB().fi(Carbon.getLiquid(125), Helium3.getGas(125)).fo(Oxygen.getPlasma(125)).add(32, 4096, 80000000); //Mark 1 Expensive
        FUSION_REACTOR.RB().fi(Aluminium.getLiquid(16), Lithium.getLiquid(125)).fo(Sulfur.getPlasma(125)).add(32, 10240, 240000000); //Mark 2 Cheap
        FUSION_REACTOR.RB().fi(Beryllium.getLiquid(16), Deuterium.getGas(375)).fo(Nitrogen.getPlasma(175)).add(16, 16384, 180000000); //Mark 2 Expensive
        FUSION_REACTOR.RB().fi(Silicon.getLiquid(16), Magnesium.getLiquid(16)).fo(Iron.getPlasma(125)).add(32, 8192, 360000000); //Mark 3 Cheap
        FUSION_REACTOR.RB().fi(Potassium.getLiquid(16), Fluorine.getGas(125)).fo(Nickel.getPlasma(125)).add(16, 32768, 480000000); //Mark 3 Expensive

        //Material Gen Recipes
        FUSION_REACTOR.RB().fi(Beryllium.getLiquid(16), Tungsten.getLiquid(16)).fo(Platinum.getLiquid(16)).add(32, 32768, 150000000);
        FUSION_REACTOR.RB().fi(Neodymium.getLiquid(16), Hydrogen.getGas(16)).fo(Europium.getLiquid(16)).add(64, 24576, 150000000);
        FUSION_REACTOR.RB().fi(Lutetium.getLiquid(16), Chrome.getLiquid(16)).fo(Americium.getLiquid(16)).add(96, 49152, 200000000);
        FUSION_REACTOR.RB().fi(Plutonium.getLiquid(16), Thorium.getLiquid(16)).fo(Naquadah.getLiquid(16)).add(64, 32768, 300000000);
        FUSION_REACTOR.RB().fi(Americium.getLiquid(16), Naquadria.getLiquid(16)).fo(Neutronium.getLiquid(1)).add(1200, 98304, 600000000);
        FUSION_REACTOR.RB().fi(Lithium.getLiquid(16), Tungsten.getLiquid(16), Iridium.getLiquid(16)).add(32, 32768, 300000000);
        FUSION_REACTOR.RB().fi(Tungsten.getLiquid(16), Helium.getGas(16)).fo(Osmium.getLiquid(16)).add(64, 24578, 150000000);
        FUSION_REACTOR.RB().fi(Manganese.getLiquid(16), Hydrogen.getGas(16)).fo(Iron.getLiquid(16)).add(64, 8192, 120000000);
        FUSION_REACTOR.RB().fi(Mercury.getLiquid(16), Magnesium.getLiquid(16)).fo(Uranium.getLiquid(16)).add(64, 49152, 240000000);
        FUSION_REACTOR.RB().fi(Gold.getLiquid(16), Aluminium.getLiquid(16)).fo(Uranium.getLiquid(16)).add(64, 49152, 240000000);
        FUSION_REACTOR.RB().fi(Uranium.getLiquid(16), Helium.getGas(16)).fo(Plutonium.getLiquid(16)).add(128, 49152, 480000000);
        FUSION_REACTOR.RB().fi(Vanadium.getLiquid(16), Hydrogen.getGas(125)).fo(Chrome.getLiquid(16)).add(64, 24576, 140000000);
        FUSION_REACTOR.RB().fi(Gallium.getLiquid(16), Radon.getGas(125)).fo(Duranium.getLiquid(16)).add(64, 16384, 140000000);
        FUSION_REACTOR.RB().fi(Titanium.getLiquid(16), Duranium.getLiquid(125)).fo(Tritanium.getLiquid(16)).add(64, 32768, 200000000);
        FUSION_REACTOR.RB().fi(Gold.getLiquid(16), Mercury.getLiquid(16)).fo(Radon.getGas(125)).add(64, 32768, 200000000);
        FUSION_REACTOR.RB().fi(NaquadahEnriched.getLiquid(15), Radon.getGas(125)).fo(Naquadria.getLiquid(3)).add(64, 49152, 400000000);
    }
}
