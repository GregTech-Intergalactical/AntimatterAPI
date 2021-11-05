package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import net.minecraft.item.BlockItemUseContext;
import net.minecraft.state.Property;
import net.minecraft.util.Direction;

import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.HATCH;

public class HatchMachine extends Machine<HatchMachine> {

    public HatchMachine(String domain, String id, CoverFactory cover) {
        super(domain, id);
        setTile(() -> new TileEntityHatch<>(this));
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, COVERABLE);
        setGUI(Data.HATCH_MENU_HANDLER);
        setAllowVerticalFacing(true);
        covers(cover);
        setOutputCover(cover);
        frontCovers();
        allowFrontIO();
    }

    @Override
    public Direction handlePlacementFacing(BlockItemUseContext ctxt, Property<?> which, Direction dir) {
        return dir.getOpposite();
    }
}
