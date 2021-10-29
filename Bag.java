import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;


enum bagName {A, B, C, X, Y, Z}

public class Bag {
    bagName name;
    private List<Integer> bagContents = new ArrayList();
    Random rand = new Random();

    public void setName(bagName name){
        this.name = name;
    }
    public bagName getName(){
        return this.name;
    }

    public Boolean isEmpty(){
        if (this.bagContents.size() < 1){
            return true;
        }else{
            return false;
        }
    }

    public List<Integer> getPebbles(){
        return this.bagContents;
    }
    public void setPebbles(List<Integer> pebbles){
        this.bagContents = pebbles;
    }
    public void clearPebbles(){
        this.bagContents.clear();
    }

    private void loadPebbles(String pebbleFile){
        Path filePath = Paths.get(pebbleFile);
        try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){
            String line = reader.readLine();
            while (line != null){
                String[] pebbles = line.split(",");
                for (String pebble : pebbles){
                    this.bagContents.add(Integer.parseInt(pebble));
                }
                line = reader.readLine();
            }
        }catch (IOException IOException){
            IOException.printStackTrace();
        }
    }

    public synchronized Integer getRandomPebble(){
        int randomInt = rand.nextInt(bagContents.size());
        int pebble = bagContents.get(randomInt);
        bagContents.remove(randomInt);
        return pebble;
    }

    public Bag(bagName name, String pebbleFile){
        setName(name);
        loadPebbles(pebbleFile);
    }
}
