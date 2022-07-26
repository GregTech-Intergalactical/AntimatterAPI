package muramasa.antimatter.datagen.json;

public class JLoaderModel extends JModel {
    String loader;

    public static JLoaderModel model() {
        return new JLoaderModel();
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
