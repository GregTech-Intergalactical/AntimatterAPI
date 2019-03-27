package muramasa.gregtech.api.registration;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IHasModelOverride {

    @SideOnly(Side.CLIENT)
    void initModel();
}
