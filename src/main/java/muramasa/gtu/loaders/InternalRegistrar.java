package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.cover.impl.CoverEnergy;
import muramasa.gtu.api.cover.impl.CoverFluid;
import muramasa.gtu.api.cover.impl.CoverItem;
import muramasa.gtu.api.data.Materials;
import muramasa.gtu.api.machines.Tier;
import muramasa.gtu.api.materials.MaterialType;
import muramasa.gtu.api.registration.IGregTechRegistrar;
import muramasa.gtu.api.registration.RegistrationEvent;
import muramasa.gtu.common.Data;

public class InternalRegistrar implements IGregTechRegistrar {

    @Override
    public String getId() {
        return Ref.MODID;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case ITEM:
                GregTechAPI.registerFluidCell(Data.CellTin.get(1));
                GregTechAPI.registerFluidCell(Data.CellSteel.get(1));
                GregTechAPI.registerFluidCell(Data.CellTungstensteel.get(1));

                GregTechAPI.registerCover(GregTechAPI.CoverNone);
                GregTechAPI.registerCover(GregTechAPI.CoverItem);
                GregTechAPI.registerCover(GregTechAPI.CoverFluid);
                GregTechAPI.registerCover(GregTechAPI.CoverEnergy);
                GregTechAPI.registerCover(GregTechAPI.CoverPlate);

                GregTechAPI.registerCoverStack(Data.ConveyorLV.get(1), new CoverItem(Tier.LV));
                GregTechAPI.registerCoverStack(Data.ConveyorMV.get(1), new CoverItem(Tier.MV));
                GregTechAPI.registerCoverStack(Data.ConveyorHV.get(1), new CoverItem(Tier.HV));
                GregTechAPI.registerCoverStack(Data.ConveyorEV.get(1), new CoverItem(Tier.EV));
                GregTechAPI.registerCoverStack(Data.ConveyorIV.get(1), new CoverItem(Tier.IV));
                GregTechAPI.registerCoverStack(Data.PumpLV.get(1), new CoverFluid(Tier.LV));
                GregTechAPI.registerCoverStack(Data.PumpMV.get(1), new CoverFluid(Tier.MV));
                GregTechAPI.registerCoverStack(Data.PumpHV.get(1), new CoverFluid(Tier.HV));
                GregTechAPI.registerCoverStack(Data.PumpEV.get(1), new CoverFluid(Tier.EV));
                GregTechAPI.registerCoverStack(Data.PumpIV.get(1), new CoverFluid(Tier.IV));
                GregTechAPI.registerCoverStack(Data.EnergyPort.get(1), new CoverEnergy(Tier.LV)); //TODO Tiered energy ports?
                MaterialType.PLATE.getMats().forEach(m -> GregTechAPI.registerCoverStack(m.getPlate(1), GregTechAPI.CoverPlate));
                break;
            case MATERIAL_INIT:
                Materials.init();
                break;
            case WORLDGEN:
                WorldGenLoader.init();
                break;
            case RECIPE:
                OreDictLoader.init();
                CraftingRecipeLoader.init();
                MaterialRecipeLoader.init();
                MachineRecipeLoader.init();
                break;
        }
    }
}
