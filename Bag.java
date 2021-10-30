import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;


enum bagName {A, B, C, X, Y, Z}

public class Bag {
    bagName name;
    List<Entry<Integer, bagName>> bagContents = new ArrayList<Entry<Integer, bagName>>();
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

    public synchronized void addPebble(Entry<Integer, bagName> pebble){
        this.bagContents.add(pebble);
    }
    public synchronized void addPebble(Integer value){
        this.bagContents.add(new SimpleEntry<>(value, this.name));
    }
    public synchronized void addPebble(Integer value, bagName bag){
        this.bagContents.add(new SimpleEntry<>(value, bag));
    }
    public List<Entry<Integer, bagName>> getPebbles(){
        return this.bagContents;
    }
    public synchronized void setPebbles(List<Entry<Integer, bagName>> pebbles){
        this.bagContents = pebbles;
    }
    public synchronized void clearPebbles(){
        this.bagContents.clear();
    }

    private void loadPebbles(String pebbleFile){
        ///
        /// need to verify enough pebbles for the game some how (maybe pass in number of players)
        ///
        Path filePath = Paths.get(pebbleFile);
        try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){
            String line = reader.readLine();
            while (line != null){
                String[] pebbles = line.split(",");
                for (String pebble : pebbles){
                    try{
                        Integer pebbleValue = Integer.parseInt(pebble);
                        if (pebbleValue < 0){
                            pebbleValue = - pebbleValue;
                        }else if (pebbleValue == 0){
                            throw new IOException("A pebble value of 0 was attempted to be used");
                        }
                        this.addPebble(pebbleValue, this.name);
                    }catch (Exception exception){
                        throw new IOException("A pebble value that could not be converted into an Int was used");
                    }
                }
                line = reader.readLine();
            }
        }catch (IOException IOException){
            IOException.printStackTrace();
        }
    }

    public synchronized Entry<Integer, bagName> getRandomPebble(){
        int randomInt = rand.nextInt(bagContents.size());
        Entry<Integer, bagName> pebble = bagContents.get(randomInt);
        bagContents.remove(randomInt);
        return pebble;
    }

    public Bag(bagName name){
        setName(name);
    }

    public Bag(bagName name, String pebbleFile){
        setName(name);
        loadPebbles(pebbleFile);
    }
}
