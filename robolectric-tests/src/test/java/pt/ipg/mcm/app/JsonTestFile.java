package pt.ipg.mcm.app;

import com.google.gson.Gson;
import org.junit.Assert;
import pt.ipg.mcm.app.instances.App;

import java.io.BufferedInputStream;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;

public class JsonTestFile {
    private String path;

    public JsonTestFile(String path) {
        this.path = path;
    }

    public <T> T getRequestObject(Class<T> clazz) throws IOException {
        URL url = JsonTestFile.class.getResource(path);
        Assert.assertNotNull("Object url is null to path:" + path, url);
        DataInputStream stream = new DataInputStream(new BufferedInputStream(url.openStream()));



        String s;
        while ((s = stream.readLine()) != null) {
            System.out.println(s);
        }
        stream.close();

        return App.get().getObjectMapper().readValue(new InputStreamReader(url.openStream()), clazz);
    }

    public static void writeRequest(String fileName, Object source) throws IOException {
        String path = JsonTestFile.class.getResource(".").getPath();
        path += fileName;

        File file = new File(path);
        FileWriter fileWriter = new FileWriter(file);
        Gson gson = new Gson();
        fileWriter.write(gson.toJson(source));
        fileWriter.close();
        Assert.assertNotNull(JsonTestFile.class.getResource(fileName));
    }


}
