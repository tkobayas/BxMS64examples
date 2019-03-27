import java.io.IOException;
import java.io.ObjectInputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class DeserializeSer {

    public static void main(String[] args) {

        Path path = Paths.get("./org.kie.example:project1:1.0.0-SNAPSHOT-jbpmSessionId.ser");

        try (ObjectInputStream ois = new ObjectInputStream(Files.newInputStream(path))) {
            Long knownSessionId = (Long)ois.readObject();
            System.out.println("knownSessionId = " + knownSessionId);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }
}
