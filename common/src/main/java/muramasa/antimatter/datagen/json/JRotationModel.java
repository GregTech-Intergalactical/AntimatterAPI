package muramasa.antimatter.datagen.json;

public class JRotationModel extends JLoaderModel {
    int[] rotation;
    public static JRotationModel model(){
        return new JRotationModel();
    }

    public static JRotationModel modelKeepElements() {
        JRotationModel model = new JRotationModel();
        model.elements = null;
        return model;
    }

    public JRotationModel rotation(int[] rotation){
        this.rotation = rotation;
        return this;
    }
}
