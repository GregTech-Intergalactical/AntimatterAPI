package muramasa.antimatter.structure;

@FunctionalInterface
public interface IRequirement {

    boolean test(StructureResult result);
}
