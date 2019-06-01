package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.cover.impl.CoverEnergy;
import muramasa.gtu.api.cover.impl.CoverFluid;
import muramasa.gtu.api.cover.impl.CoverItem;
import muramasa.gtu.api.data.ItemType;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.materials.GenerationFlag;
import muramasa.gtu.api.machines.Tier;
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

                GregTechAPI.registerCoverCatalyst(ItemType.ConveyorLV.get(1), new CoverItem(Tier.LV));
                GregTechAPI.registerCoverCatalyst(ItemType.ConveyorMV.get(1), new CoverItem(Tier.MV));
                GregTechAPI.registerCoverCatalyst(ItemType.ConveyorHV.get(1), new CoverItem(Tier.HV));
                GregTechAPI.registerCoverCatalyst(ItemType.ConveyorEV.get(1), new CoverItem(Tier.EV));
                GregTechAPI.registerCoverCatalyst(ItemType.ConveyorIV.get(1), new CoverItem(Tier.IV));
                GregTechAPI.registerCoverCatalyst(ItemType.PumpLV.get(1), new CoverFluid(Tier.LV));
                GregTechAPI.registerCoverCatalyst(ItemType.PumpMV.get(1), new CoverFluid(Tier.MV));
                GregTechAPI.registerCoverCatalyst(ItemType.PumpHV.get(1), new CoverFluid(Tier.HV));
                GregTechAPI.registerCoverCatalyst(ItemType.PumpEV.get(1), new CoverFluid(Tier.EV));
                GregTechAPI.registerCoverCatalyst(ItemType.PumpIV.get(1), new CoverFluid(Tier.IV));
                GregTechAPI.registerCoverCatalyst(ItemType.EnergyPort.get(1), new CoverEnergy(Tier.LV)); //TODO Tiered energy ports?
                GenerationFlag.PLATE.getMats().forEach(m -> GregTechAPI.registerCoverCatalyst(m.getPlate(1), GregTechAPI.CoverPlate));
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
