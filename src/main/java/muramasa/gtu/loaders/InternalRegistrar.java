package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.gtu.api.GregTechAPI;
import muramasa.gtu.api.cover.CoverPump;
import muramasa.gtu.api.cover.CoverConveyor;
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
                //GregTechAPI.registerFluidCell(Data.CellTin.get(1));
                //GregTechAPI.registerFluidCell(Data.CellSteel.get(1));
                //GregTechAPI.registerFluidCell(Data.CellTungstensteel.get(1));

                GregTechAPI.registerCover(GregTechAPI.CoverNone);
                GregTechAPI.registerCover(GregTechAPI.CoverPlate);
                GregTechAPI.registerCover(GregTechAPI.CoverOutput);
                GregTechAPI.registerCover(GregTechAPI.CoverConveyor);
                GregTechAPI.registerCover(GregTechAPI.CoverPump);

                GregTechAPI.registerCoverStack(Data.ConveyorLV.get(1), new CoverConveyor(Tier.LV));
                GregTechAPI.registerCoverStack(Data.ConveyorMV.get(1), new CoverConveyor(Tier.MV));
                GregTechAPI.registerCoverStack(Data.ConveyorHV.get(1), new CoverConveyor(Tier.HV));
                GregTechAPI.registerCoverStack(Data.ConveyorEV.get(1), new CoverConveyor(Tier.EV));
                GregTechAPI.registerCoverStack(Data.ConveyorIV.get(1), new CoverConveyor(Tier.IV));
                GregTechAPI.registerCoverStack(Data.PumpLV.get(1), new CoverPump(Tier.LV));
                GregTechAPI.registerCoverStack(Data.PumpMV.get(1), new CoverPump(Tier.MV));
                GregTechAPI.registerCoverStack(Data.PumpHV.get(1), new CoverPump(Tier.HV));
                GregTechAPI.registerCoverStack(Data.PumpEV.get(1), new CoverPump(Tier.EV));
                GregTechAPI.registerCoverStack(Data.PumpIV.get(1), new CoverPump(Tier.IV));
                MaterialType.PLATE.all().forEach(m -> GregTechAPI.registerCoverStack(m.getPlate(1), GregTechAPI.CoverPlate));
                break;
            case WORLDGEN:
                WorldGenLoader.init();
                break;
            case DATA_READY:
                Materials.init();
                break;
            case RECIPE:
                //OreDictLoader.init();
                //CraftingRecipeLoader.init();
                MaterialRecipeLoader.init();
                MachineRecipeLoader.init();
                break;
        }
    }
}
