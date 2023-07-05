package muramasa.antimatter.client.forge;

import net.minecraft.core.BlockPos;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraftforge.client.model.data.ModelProperty;

public class AntimatterModelProperties {
    public static final ModelProperty<BlockAndTintGetter> WORLD = new ModelProperty<>();
    public static final ModelProperty<BlockPos> POS = new ModelProperty<>();
}
