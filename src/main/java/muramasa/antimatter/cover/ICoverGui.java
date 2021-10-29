package muramasa.antimatter.cover;

import muramasa.antimatter.Ref;
import muramasa.antimatter.capability.IGuiHandler;
import muramasa.antimatter.util.Utils;
import net.minecraft.inventory.container.INamedContainerProvider;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.StringTextComponent;

public interface ICoverGui extends ICover, INamedContainerProvider, IGuiHandler {

    default ITextComponent getDisplayName() {
        return new StringTextComponent(Utils.underscoreToUpperCamel(this.getId()));
    }

    default String getDomain() {
        return Ref.ID;
    }
}
