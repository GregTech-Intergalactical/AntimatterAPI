package muramasa.gtu.integration.top;

import mcjty.theoneprobe.api.*;
import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IEnergyStorage;
import muramasa.gtu.api.tileentities.TileEntityBasicMachine;
import muramasa.gtu.api.util.Utils;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class EnergyStorageInfoProvider implements IProbeInfoProvider {

	@Override
	public String getID() {
		return Ref.MODID + "_energy_storage";
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
		if (state.getBlock().hasTileEntity(state)) {
            TileEntity tile = Utils.getTile(world, data.getPos());
            if (tile == null) return;
			if (tile instanceof TileEntityBasicMachine) {
				TileEntityBasicMachine machine = (TileEntityBasicMachine) tile;
				if (machine.getMaxProgress() > 0) {
					int progressScaled = machine.getMaxProgress() == 0 ? 0 : (int) Math.floor(machine.getCurProgress() / (machine.getMaxProgress() * 1.0) * 100);
					IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
					horizontalPane.text(TextStyleClass.INFO + "{*gregtech.top.progress*} ");
					horizontalPane.progress(progressScaled, 100, probeInfo.defaultProgressStyle()
						.suffix("%")
						.borderColor(0x00000000)
						.backgroundColor(0x00000000)
						.filledColor(0xFF000099)
						.alternateFilledColor(0xFF000077));
				}
			}
			if (tile.hasCapability(GTCapabilities.ENERGY, null)) {
				IEnergyStorage energyStorage = tile.getCapability(GTCapabilities.ENERGY, null);
				if (energyStorage == null) return;
				IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
				horizontalPane.text(TextStyleClass.INFO + "{*gregtech.top.energy_stored*} " + " ");
				horizontalPane.progress(energyStorage.getEnergyStored(), energyStorage.getMaxEnergyStored(), probeInfo.defaultProgressStyle()
					.suffix("/" + energyStorage.getMaxEnergyStored() + " EU")
					.borderColor(0x00000000)
					.backgroundColor(0x00000000)
					.filledColor(0xFFFFE000)
					.alternateFilledColor(0xFFEED000));
			}
		}
	}
}
