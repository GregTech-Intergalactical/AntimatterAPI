package muramasa.gtu.loaders;

import muramasa.gtu.Ref;
import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.cover.CoverConveyor;
import muramasa.antimatter.cover.CoverPump;
import muramasa.gtu.data.Guis;
import muramasa.antimatter.machines.Tier;
import muramasa.antimatter.materials.MaterialType;
import muramasa.antimatter.registration.IAntimatterRegistrar;
import muramasa.antimatter.registration.RegistrationEvent;
import muramasa.gtu.common.Data;

public class InternalRegistrar implements IAntimatterRegistrar {

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

                AntimatterAPI.registerCover(AntimatterAPI.CoverNone);
                AntimatterAPI.registerCover(AntimatterAPI.CoverPlate);
                AntimatterAPI.registerCover(AntimatterAPI.CoverOutput);
                AntimatterAPI.registerCover(AntimatterAPI.CoverConveyor);
                AntimatterAPI.registerCover(AntimatterAPI.CoverPump);

                AntimatterAPI.registerCoverStack(Data.ConveyorLV.get(1), new CoverConveyor(Tier.LV));
                AntimatterAPI.registerCoverStack(Data.ConveyorMV.get(1), new CoverConveyor(Tier.MV));
                AntimatterAPI.registerCoverStack(Data.ConveyorHV.get(1), new CoverConveyor(Tier.HV));
                AntimatterAPI.registerCoverStack(Data.ConveyorEV.get(1), new CoverConveyor(Tier.EV));
                AntimatterAPI.registerCoverStack(Data.ConveyorIV.get(1), new CoverConveyor(Tier.IV));
                AntimatterAPI.registerCoverStack(Data.PumpLV.get(1), new CoverPump(Tier.LV));
                AntimatterAPI.registerCoverStack(Data.PumpMV.get(1), new CoverPump(Tier.MV));
                AntimatterAPI.registerCoverStack(Data.PumpHV.get(1), new CoverPump(Tier.HV));
                AntimatterAPI.registerCoverStack(Data.PumpEV.get(1), new CoverPump(Tier.EV));
                AntimatterAPI.registerCoverStack(Data.PumpIV.get(1), new CoverPump(Tier.IV));
                MaterialType.PLATE.all().forEach(m -> AntimatterAPI.registerCoverStack(m.getPlate(1), AntimatterAPI.CoverPlate));
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
