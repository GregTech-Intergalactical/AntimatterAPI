package muramasa.gtu.integration.top;

import mcjty.theoneprobe.api.ElementAlignment;
import mcjty.theoneprobe.api.IProbeHitData;
import mcjty.theoneprobe.api.IProbeInfo;
import mcjty.theoneprobe.api.IProbeInfoProvider;
import mcjty.theoneprobe.api.ProbeMode;
import mcjty.theoneprobe.api.TextStyleClass;
import muramasa.gtu.Ref;
import muramasa.gtu.api.capability.GTCapabilities;
import muramasa.gtu.api.capability.IEnergyStorage;
import muramasa.gtu.api.tileentities.TileEntityBasicMachine;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumFacing;
import net.minecraft.world.World;

public class EnergyStorageInfoProvider implements IProbeInfoProvider {

	@Override
	public String getID() {
		return Ref.MODID + "_energy_storage";
	}

	@Override
	public void addProbeInfo(ProbeMode mode, IProbeInfo probeInfo, EntityPlayer player, World world, IBlockState state, IProbeHitData data) {
		if (state.getBlock().hasTileEntity(state)) {
			EnumFacing sideHit = data.getSideHit();
            TileEntity tileEntity = world.getTileEntity(data.getPos());
            if (tileEntity == null) return;
            if (tileEntity instanceof TileEntityBasicMachine) {
            	TileEntityBasicMachine tileEntityProgress = (TileEntityBasicMachine)tileEntity;
            	if (tileEntityProgress.getMaxProgress() > 0) {
            		int progressScaled = tileEntityProgress.getMaxProgress() == 0 ? 0 : (int) Math.floor(tileEntityProgress.getCurProgress() / (tileEntityProgress.getMaxProgress() * 1.0) * 100);
                    IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                    horizontalPane.text(TextStyleClass.INFO + "{*gregtech.top.progress*} ");
                    horizontalPane.progress(progressScaled, 100, probeInfo.defaultProgressStyle()
                        .suffix("%")
                        .borderColor(0x00000000)
                        .backgroundColor(0x00000000)
                        .filledColor(0xFF000099)
                        .alternateFilledColor(0xFF000077));
            	}
            	if (tileEntityProgress.getEnergyHandler().getMaxEnergyStored() > 0) {
            		IEnergyStorage storage = tileEntityProgress.getEnergyHandler();
            		IProbeInfo horizontalPane = probeInfo.horizontal(probeInfo.defaultLayoutStyle().alignment(ElementAlignment.ALIGN_CENTER));
                	horizontalPane.text(TextStyleClass.INFO + "{*gregtech.top.energy_stored*} " + " ");
                    horizontalPane.progress(storage.getEnergyStored(), storage.getMaxEnergyStored(), probeInfo.defaultProgressStyle()
                        .suffix("/" + storage.getMaxEnergyStored() + " EU")
                        .borderColor(0x00000000)
                        .backgroundColor(0x00000000)
                        .filledColor(0xFFFFE000)
                        .alternateFilledColor(0xFFEED000));
            	}
            }
		}
	}

}
