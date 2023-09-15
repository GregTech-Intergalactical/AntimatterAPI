package muramasa.antimatter.integration.fabric.megane;

import lol.bai.megane.api.MeganeModule;
import lol.bai.megane.api.registry.ClientRegistrar;
import lol.bai.megane.api.registry.CommonRegistrar;
import muramasa.antimatter.Ref;
import muramasa.antimatter.integration.fabric.megane.provider.MachineEnergyProvider;
import muramasa.antimatter.integration.fabric.megane.provider.MachineProgressProvider;
import muramasa.antimatter.tile.TileEntityMachine;

public class AntimatterMeganeModule implements MeganeModule {

    @Override
    public void registerCommon(CommonRegistrar registrar) {
        registrar.addEnergy(TileEntityMachine.class, new MachineEnergyProvider());
        registrar.addProgress(TileEntityMachine.class, new MachineProgressProvider());
    }

    @Override
    public void registerClient(ClientRegistrar registrar) {
        registrar.addEnergyInfo(Ref.ID, 0xEEE600, "EU");
    }
}
