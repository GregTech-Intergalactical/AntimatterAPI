package muramasa.antimatter.machine.types;

import muramasa.antimatter.Data;
import muramasa.antimatter.blockentity.multi.BlockEntityMultiMachine;
import muramasa.antimatter.cover.CoverFactory;
import muramasa.antimatter.cover.ICover;
import muramasa.antimatter.gui.widget.TankIconWidget;
import muramasa.antimatter.machine.Tier;
import muramasa.antimatter.blockentity.multi.BlockEntityHatch;
import net.minecraft.core.Direction;
import net.minecraft.world.item.context.BlockPlaceContext;
import net.minecraft.world.level.block.state.properties.Property;

import static muramasa.antimatter.machine.MachineFlag.*;

public class HatchMachine extends Machine<HatchMachine> {
    String idForHandlers;

    public HatchMachine(String domain, String id, CoverFactory cover) {
        super(domain, id);
        idForHandlers = id.replace("hatch_", "").replace("_hatch", "");
        setTile(BlockEntityHatch::new);
        setTiers(Tier.getAllElectric());
        addFlags(HATCH, COVERABLE);
        setGUI(Data.BASIC_MENU_HANDLER);
        setVerticalFacingAllowed(true);
        covers(ICover.emptyFactory, ICover.emptyFactory, cover, ICover.emptyFactory, ICover.emptyFactory, ICover.emptyFactory);
        setOutputCover(cover);
        frontCovers();
        allowFrontIO();
        blockColorHandler((state, world, pos, machine, i) -> {
            if (machine instanceof BlockEntityHatch<?> hatch){
                return hatch.componentHandler.map(c -> {
                    BlockEntityMultiMachine<?> multiMachine = c.getControllers().stream().findFirst().orElse(null);
                    if (multiMachine != null){
                        return multiMachine.getMachineType().blockColorHandler.getBlockColor(multiMachine.getBlockState(), multiMachine.getLevel(), multiMachine.getBlockPos(), multiMachine, i);
                    }
                    return -1;
                }).orElse(-1);
            }
            return -1;
        });
    }

    public HatchMachine setIdForHandlers(String idForHandlers) {
        this.idForHandlers = idForHandlers;
        return this;
    }

    public String getIdForHandlers() {
        return idForHandlers;
    }

    @Override
    protected void setupGui() {
        super.setupGui();
        addGuiCallback(t -> {
            if (has(FLUID)){
                t.addWidget(TankIconWidget.build().setPos(8, 39));
            }
        });
    }
}
