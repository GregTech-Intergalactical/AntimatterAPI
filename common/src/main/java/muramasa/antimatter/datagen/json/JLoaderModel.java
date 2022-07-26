package muramasa.antimatter.datagen.json;

public class JLoaderModel extends JModel {
    String loader;

    public static JLoaderModel model() {
        return new JLoaderModel();
    }
    /**
     * @return a new jmodel that does not override it's parent's elements
     */
    public static JLoaderModel modelKeepElements() {
        JLoaderModel model = new JLoaderModel();
        model.elements = null;
        return model;
    }

    public static JLoaderModel model(String parent) {
        JLoaderModel model = new JLoaderModel();
        model.parent(parent);
        return model;
    }

    public JLoaderModel loader(String loader){
        this.loader = loader;
        return this;
    }
}
