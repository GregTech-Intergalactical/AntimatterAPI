package muramasa.antimatter.datagen.json;

public class JRotationModel extends JLoaderModel {
    int[] rotation;
    public static JRotationModel model(){
        return new JRotationModel();
    }

    public JRotationModel rotation(int[] rotation){
        this.rotation = rotation;
        return this;
    }
}
