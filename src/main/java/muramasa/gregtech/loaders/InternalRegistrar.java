package muramasa.gregtech.loaders;

import muramasa.gregtech.Ref;
import muramasa.gregtech.api.GregTechAPI;
import muramasa.gregtech.api.data.Materials;
import muramasa.gregtech.api.data.ItemType;
import muramasa.gregtech.api.registration.GregTechRegistrar;
import muramasa.gregtech.api.materials.ItemFlag;
import muramasa.gregtech.api.materials.Material;

public class InternalRegistrar extends GregTechRegistrar {

    @Override
    public String getId() {
        return Ref.MODID;
    }

    @Override
    public void onCoverRegistration() {
        GregTechAPI.registerCover(GregTechAPI.CoverNone);
        GregTechAPI.registerCover(GregTechAPI.CoverItem);
        GregTechAPI.registerCover(GregTechAPI.CoverFluid);
        GregTechAPI.registerCover(GregTechAPI.CoverEnergy);
        GregTechAPI.registerCover(GregTechAPI.CoverPlate);
        GregTechAPI.registerCover(GregTechAPI.CoverMonitor);

        GregTechAPI.registerCoverCatalyst(ItemType.ConveyorLV.get(1), GregTechAPI.CoverItem);
        GregTechAPI.registerCoverCatalyst(ItemType.ConveyorMV.get(1), GregTechAPI.CoverItem);
        GregTechAPI.registerCoverCatalyst(ItemType.ConveyorHV.get(1), GregTechAPI.CoverItem);
        GregTechAPI.registerCoverCatalyst(ItemType.ConveyorEV.get(1), GregTechAPI.CoverItem);
        GregTechAPI.registerCoverCatalyst(ItemType.ConveyorIV.get(1), GregTechAPI.CoverItem);
        GregTechAPI.registerCoverCatalyst(ItemType.PumpLV.get(1), GregTechAPI.CoverFluid);
        GregTechAPI.registerCoverCatalyst(ItemType.PumpMV.get(1), GregTechAPI.CoverFluid);
        GregTechAPI.registerCoverCatalyst(ItemType.PumpHV.get(1), GregTechAPI.CoverFluid);
        GregTechAPI.registerCoverCatalyst(ItemType.PumpEV.get(1), GregTechAPI.CoverFluid);
        GregTechAPI.registerCoverCatalyst(ItemType.PumpIV.get(1), GregTechAPI.CoverFluid);
        GregTechAPI.registerCoverCatalyst(ItemType.EnergyPort.get(1), GregTechAPI.CoverEnergy);
        for (Material mat : ItemFlag.PLATE.getMats()) {
            GregTechAPI.registerCoverCatalyst(mat.getPlate(1), GregTechAPI.CoverPlate);
        }
        GregTechAPI.registerCoverCatalyst(ItemType.ComputerMonitor.get(1), GregTechAPI.CoverMonitor);
    }

    @Override
    public void onMaterialRegistration() {
        //NOOP
    }

    @Override
    public void onMaterialInit() {
        Materials.init();
    }

    @Override
    public void onCraftingRecipeRegistration() {
        CraftingRecipeLoader.init();
    }

    @Override
    public void onMachineRecipeRegistration() {
        MachineRecipeLoader.init();
    }

    @Override
    public void onMaterialRecipeRegistration() {
        MaterialRecipeLoader.init();
    }
}
