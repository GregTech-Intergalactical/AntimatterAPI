package muramasa.antimatter.tree;

import muramasa.antimatter.AntimatterAPI;
import muramasa.antimatter.Ref;
import muramasa.antimatter.registration.IAntimatterObject;
import net.minecraft.block.*;
import net.minecraft.block.material.Material;
import net.minecraft.state.StateContainer;

public class BlockRubberSapling extends SaplingBlock implements IGrowable, IAntimatterObject {
    final static RubberTree TREE = new RubberTree();
    public BlockRubberSapling() {
        super(TREE, Block.Properties.create(Material.PLANTS).doesNotBlockMovement().tickRandomly().hardnessAndResistance(0.0F).sound(SoundType.PLANT));
        setRegistryName(Ref.ID + ":" + getId());
        AntimatterAPI.register(BlockRubberSapling.class, this);
    }

    @Override
    protected void fillStateContainer(StateContainer.Builder<Block, BlockState> builder) {
        builder.add(STAGE);
    }

    @Override
    public String getId() {
        return "rubber_sapling";
    }
}
