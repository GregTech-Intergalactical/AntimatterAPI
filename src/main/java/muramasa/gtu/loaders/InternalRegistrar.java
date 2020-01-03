package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.antimatter.GregTechAPI;
import muramasa.antimatter.cover.CoverConveyor;
import muramasa.antimatter.cover.CoverPump;
import muramasa.gtu.data.Guis;
import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.registration.IGregTechRegistrar;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.gtu.common.Data;

public class InternalRegistrar implements IGregTechRegistrar {

    @Override
    public String getId() {
        return Ref.MODID;
    }

    @Override
    public void onRegistrationEvent(RegistrationEvent event) {
        switch (event) {
            case GUI:
                Guis.init();
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
