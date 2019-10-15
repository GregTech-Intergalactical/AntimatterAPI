//package muramasa.gtu.integration.ctx;
//
//import muramasa.gtu.api.machines.types.Machine;
//import muramasa.gtu.api.structure.Structure;
//import muramasa.gtu.api.structure.StructureBuilder;
//import stanhebben.zenscript.annotations.ZenMethod;
//
//public class CTStructureBuilder {
//
//    private Machine machine;
//    private StructureBuilder builder;
//    private Structure structure;
//
//    public CTStructureBuilder(Machine machine) {
//        this.machine = machine;
//    }
//
//    @ZenMethod
//    public CTStructureBuilder start() {
//        builder = new StructureBuilder();
//        return this;
//    }
//
//    @ZenMethod
//    public CTStructureBuilder of(String... slices) {
//        builder.of(slices);
//        return this;
//    }
//
//    @ZenMethod
//    public CTStructureBuilder at(String key, String object) {
////        builder.at(key, object);
//        return this;
//    }
//
//    @ZenMethod
//    public CTStructureBuilder build() {
//        structure = builder.build();
//        return this;
//    }
//
//    @ZenMethod
//    public CTStructureBuilder offset(int x, int y) {
//        structure.offset(x, y);
//        return this;
//    }
//
//    @ZenMethod
//    public CTStructureBuilder finish() {
//        machine.setStructure(structure);
//        return this;
//    }
//}
