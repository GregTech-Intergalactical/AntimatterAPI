package muramasa.gtu.api.materials;

import muramasa.gtu.api.util.Utils;

public class MaterialStack {

    public Material m;
    public int s;

    public MaterialStack(Material material, int size) {
        m = material;
        s = size;
    }
    
    @Override
    public String toString() {
        String string = "";
        if (m.getChemicalFormula() == null || m.getChemicalFormula().isEmpty()) {
            string += m.getDisplayName() == null ? "|?|" : " |" + m.getDisplayName() + "| ";
        } else if (m.getProcessInto().size() > 1) {
            string += '(' + m.getChemicalFormula() + ')';
        } else {
            string += m.getChemicalFormula();
        }
        if (s > 1) {
            string += Utils.digitsToSubscript(Long.toString(s));
        }
        return string;
    }
}
