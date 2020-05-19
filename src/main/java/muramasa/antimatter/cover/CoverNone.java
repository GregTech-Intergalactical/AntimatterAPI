package muramasa.antimatter.cover;

import muramasa.antimatter.machine.Tier;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.inventory.container.Container;
import net.minecraft.util.text.ITextComponent;

import javax.annotation.Nullable;

public class CoverNone extends Cover {

    @Override
    public String getId() {
        return "none";
    }
}
