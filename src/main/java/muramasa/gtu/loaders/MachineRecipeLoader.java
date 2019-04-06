package muramasa.gtu.loaders;

import muramasa.gtu.api.recipe.RecipeBuilder;

import static muramasa.gtu.api.data.Machines.BLAST_FURNACE;
import static muramasa.gtu.api.data.Machines.FUSION_REACTOR_1;
import static muramasa.gtu.api.data.Materials.*;

public class MachineRecipeLoader {

    public static RecipeBuilder RB = new RecipeBuilder();

    public static void init() {
        //Power Gen Recipes
        RB.get(FUSION_REACTOR_1).fi(Deuterium.getGas(125), Tritium.getGas(125)).fo(Helium.getPlasma(125)).add(16, 4096, 40000000); //Mark 1 Cheap
        RB.get(FUSION_REACTOR_1).fi(Deuterium.getGas(125), Helium3.getGas(125)).fo(Helium.getPlasma(125)).add(16, 2048, 60000000); //Mark 1 Expensive
        RB.get(FUSION_REACTOR_1).fi(Carbon.getLiquid(125), Helium3.getGas(125)).fo(Oxygen.getPlasma(125)).add(32, 4096, 80000000); //Mark 1 Expensive
        RB.get(FUSION_REACTOR_1).fi(Aluminium.getLiquid(16), Lithium.getLiquid(125)).fo(Sulfur.getPlasma(125)).add(32, 10240, 240000000); //Mark 2 Cheap
        RB.get(FUSION_REACTOR_1).fi(Beryllium.getLiquid(16), Deuterium.getGas(375)).fo(Nitrogen.getPlasma(175)).add(16, 16384, 180000000); //Mark 2 Expensive
        RB.get(FUSION_REACTOR_1).fi(Silicon.getLiquid(16), Magnesium.getLiquid(16)).fo(Iron.getPlasma(125)).add(32, 8192, 360000000); //Mark 3 Cheap
        RB.get(FUSION_REACTOR_1).fi(Potassium.getLiquid(16), Fluorine.getGas(125)).fo(Nickel.getPlasma(125)).add(16, 32768, 480000000); //Mark 3 Expensive

        //Material Gen Recipes
        RB.get(FUSION_REACTOR_1).fi(Beryllium.getLiquid(16), Tungsten.getLiquid(16)).fo(Platinum.getLiquid(16)).add(32, 32768, 150000000);
        RB.get(FUSION_REACTOR_1).fi(Neodymium.getLiquid(16), Hydrogen.getGas(16)).fo(Europium.getLiquid(16)).add(64, 24576, 150000000);
        RB.get(FUSION_REACTOR_1).fi(Lutetium.getLiquid(16), Chrome.getLiquid(16)).fo(Americium.getLiquid(16)).add(96, 49152, 200000000);
        RB.get(FUSION_REACTOR_1).fi(Plutonium.getLiquid(16), Thorium.getLiquid(16)).fo(Naquadah.getLiquid(16)).add(64, 32768, 300000000);
        RB.get(FUSION_REACTOR_1).fi(Americium.getLiquid(16), Naquadria.getLiquid(16)).fo(Neutronium.getLiquid(1)).add(1200, 98304, 600000000);
        RB.get(FUSION_REACTOR_1).fi(Lithium.getLiquid(16), Tungsten.getLiquid(16), Iridium.getLiquid(16)).add(32, 32768, 300000000);
        RB.get(FUSION_REACTOR_1).fi(Tungsten.getLiquid(16), Helium.getGas(16)).fo(Osmium.getLiquid(16)).add(64, 24578, 150000000);
        RB.get(FUSION_REACTOR_1).fi(Manganese.getLiquid(16), Hydrogen.getGas(16)).fo(Iron.getLiquid(16)).add(64, 8192, 120000000);
        RB.get(FUSION_REACTOR_1).fi(Mercury.getLiquid(16), Magnesium.getLiquid(16)).fo(Uranium.getLiquid(16)).add(64, 49152, 240000000);
        RB.get(FUSION_REACTOR_1).fi(Gold.getLiquid(16), Aluminium.getLiquid(16)).fo(Uranium.getLiquid(16)).add(64, 49152, 240000000);
        RB.get(FUSION_REACTOR_1).fi(Uranium.getLiquid(16), Helium.getGas(16)).fo(Plutonium.getLiquid(16)).add(128, 49152, 480000000);
        RB.get(FUSION_REACTOR_1).fi(Vanadium.getLiquid(16), Hydrogen.getGas(125)).fo(Chrome.getLiquid(16)).add(64, 24576, 140000000);
        RB.get(FUSION_REACTOR_1).fi(Gallium.getLiquid(16), Radon.getGas(125)).fo(Duranium.getLiquid(16)).add(64, 16384, 140000000);
        RB.get(FUSION_REACTOR_1).fi(Titanium.getLiquid(16), Duranium.getLiquid(125)).fo(Tritanium.getLiquid(16)).add(64, 32768, 200000000);
        RB.get(FUSION_REACTOR_1).fi(Gold.getLiquid(16), Mercury.getLiquid(16)).fo(Radon.getGas(125)).add(64, 32768, 200000000);
        RB.get(FUSION_REACTOR_1).fi(NaquadahEnriched.getLiquid(15), Radon.getGas(125)).fo(Naquadria.getLiquid(3)).add(64, 49152, 400000000);

        RB.get(BLAST_FURNACE).ii(Tungsten.getIngot(1), Steel.getIngot(1)).io(TungstenSteel.getIngotH(2), DarkAsh.getDustS(1)).add(Math.max(TungstenSteel.getMass() / 80L, 1L) * TungstenSteel.getBlastTemp(), 480, TungstenSteel.getBlastTemp());
        RB.get(BLAST_FURNACE).ii(Tungsten.getIngot(1), Carbon.getDust(1)).io(TungstenCarbide.getIngotH(1), Ash.getDustS(2)).add(Math.max(TungstenCarbide.getMass() / 40L, 1L) * TungstenCarbide.getBlastTemp(), 480, TungstenCarbide.getBlastTemp());
        RB.get(BLAST_FURNACE).ii(Vanadium.getIngot(3), Gallium.getIngot(1)).io(VanadiumGallium.getIngotH(4), DarkAsh.getDustS(2)).add(Math.max(VanadiumGallium.getMass() / 40L, 1L) * VanadiumGallium.getBlastTemp(), 480, VanadiumGallium.getBlastTemp());
        RB.get(BLAST_FURNACE).ii(Niobium.getIngot(1), Titanium.getIngot(1)).io(NiobiumTitanium.getIngotH(2), DarkAsh.getDustS(1)).add(Math.max(NiobiumTitanium.getMass() / 80L, 1L) * NiobiumTitanium.getBlastTemp(), 480, NiobiumTitanium.getBlastTemp());
        RB.get(BLAST_FURNACE).ii(Nickel.getIngot(4), Chrome.getIngot(1)).io(Nichrome.getIngotH(5), DarkAsh.getDustS(2)).add(Math.max(Nichrome.getMass() / 32L, 1L) * Nichrome.getBlastTemp(), 480, Nichrome.getBlastTemp());
        RB.get(BLAST_FURNACE).ii(Ruby.getDust(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(400, 100, 1200);
        RB.get(BLAST_FURNACE).ii(Ruby.getGem(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(320, 100, 1200);
        RB.get(BLAST_FURNACE).ii(GreenSapphire.getDust(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(400, 100, 1200);
        RB.get(BLAST_FURNACE).ii(GreenSapphire.getGem(1)).io(Aluminium.getNugget(3), DarkAsh.getDustT(1)).add(320, 100, 1200);
        RB.get(BLAST_FURNACE).ii(Sapphire.getDust(1)).io(Aluminium.getNugget(3)).add(400, 100, 1200);
        RB.get(BLAST_FURNACE).ii(Sapphire.getGem(1)).io(Aluminium.getNugget(3)).add(320, 100, 1200);
        RB.get(BLAST_FURNACE).ii(Ilmenite.getDust(1), Carbon.getDust(1)).io(WroughtIron.getNugget(4), Rutile.getDustT(4)).add(800, 500, 1700);
        RB.get(BLAST_FURNACE).ii(Magnesium.getDust(2)).fi(Titaniumtetrachloride.getLiquid(1000)).io(Titanium.getIngotH(1), Magnesiumchloride.getDust(2)).add(800, 480, Titanium.getBlastTemp() + 200);

        RB.get(BLAST_FURNACE).ii(Galena.getDust(1)).fi(Oxygen.getGas(2000)).io(Silver.getNugget(4), Lead.getNugget(4)).add(400, 500, 1500);
        RB.get(BLAST_FURNACE).ii(Magnetite.getDust(1)).fi(Oxygen.getGas(2000)).io(WroughtIron.getNugget(4), DarkAsh.getDustS(1)).add(400, 500, 1000);
        RB.get(BLAST_FURNACE).ii(Iron.getIngot(1)).fi(Oxygen.getGas(1000)).io(Steel.getIngot(1), DarkAsh.getDustS(1)).add(500, 120, 1000);
        RB.get(BLAST_FURNACE).ii(PigIron.getIngot(1)).fi(Oxygen.getGas(1000)).io(Steel.getIngot(1), DarkAsh.getDustS(1)).add(100, 120, 1000);
        RB.get(BLAST_FURNACE).ii(WroughtIron.getIngot(1)).fi(Oxygen.getGas(1000)).io(Steel.getIngot(1), DarkAsh.getDustS(1)).add(100, 120, 1000);
        RB.get(BLAST_FURNACE).ii(Copper.getDust(1)).fi(Oxygen.getGas(1000)).io(AnnealedCopper.getIngot(1)).add(500, 120, 1200);
        RB.get(BLAST_FURNACE).ii(Copper.getIngot(1)).fi(Oxygen.getGas(1000)).io(AnnealedCopper.getIngot(1)).add(500, 120, 1200);
        RB.get(BLAST_FURNACE).ii(Iridium.getIngot(3), Osmium.getIngot(1)).fi(Helium.getGas(1000)).io(Osmiridium.getIngotH(4)).add(500, 1920, 2900);
        RB.get(BLAST_FURNACE).ii(Naquadah.getIngot(1), Osmiridium.getIngot(1)).fi(Argon.getGas(1000)).io(NaquadahAlloy.getIngotH(2)).add(500, 30720, NaquadahAlloy.getBlastTemp());
    }
}
