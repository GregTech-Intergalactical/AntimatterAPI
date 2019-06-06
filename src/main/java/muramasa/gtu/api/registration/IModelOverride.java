package muramasa.gtu.api.registration;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IModelOverride {

    @SideOnly(Side.CLIENT)
    void onModelRegistration();
}
