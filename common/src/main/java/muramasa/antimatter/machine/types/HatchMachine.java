package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.tile.multi.TileEntityHatch;
import muramasa.antimatter.util.Utils;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;

import static muramasa.antimatter.machine.MachineFlag.COVERABLE;
import static muramasa.antimatter.machine.MachineFlag.HATCH;

public class HatchMachine extends Machine<HatchMachine> {
    String idForHandlers;

    public HatchMachine(String domain, String id, CoverFactory cover) {
        super(domain, id);
        idForHandlers = id.replace("hatch_", "").replace("_hatch", "");
        setTile(TileEntityHatch::new);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, COVERABLE);
        setGUI(Data.HATCH_MENU_HANDLER);
        setAllowVerticalFacing(true);
        covers(cover);
        setOutputCover(cover);
        frontCovers();
        allowFrontIO();
    }

    public HatchMachine setIdForHandlers(String idForHandlers) {
        this.idForHandlers = idForHandlers;
        return this;
    }

    public String getIdForHandlers() {
        return idForHandlers;
    }

    @Override
    public Direction handlePlacementFacing(BlockPlaceContext ctxt, Property<?> which, Direction dir) {
        return dir.getOpposite();
    }

    @Override
    public String getLang(String lang) {
        return Utils.lowerUnderscoreToUpperSpacedRotated(this.getId());
    }
}
