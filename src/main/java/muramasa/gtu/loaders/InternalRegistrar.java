package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.ItemFlag;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;

public class InternalRegistrar implements IGregTechRegistrar {

    @Override
    public String getId() {
        return Ref.MODID;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case COVER:
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
                ItemFlag.PLATE.getMats().forEach(m -> GregTechAPI.registerCoverCatalyst(m.getPlate(1), GregTechAPI.CoverPlate));
                GregTechAPI.registerCoverCatalyst(ItemType.ComputerMonitor.get(1), GregTechAPI.CoverMonitor);
                break;
            case MATERIAL_INIT:
                Materials.init();
                break;
            case CRAFTING_RECIPE:
                CraftingRecipeLoader.init();
                break;
            case MATERIAL_RECIPE:
                MaterialRecipeLoader.init();
                break;
            case MACHINE_RECIPE:
                MachineRecipeLoader.init();
                break;
        }
    }
}
