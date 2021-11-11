//package ECM2414SoftwareDevCoursework;
/**
 * Bag object file
 * 
 * @author Louis Alexander
 * @version 1.0
 *
 */

import java.util.Random;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.AbstractMap.SimpleEntry;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.io.BufferedReader;
import java.io.IOException;

enum bagName {A, B, C, X, Y, Z}

public class Bag {
    // define class variables
    private bagName name;
    private List<Entry<Integer, bagName>> bagContents = new ArrayList<Entry<Integer, bagName>>();
    private Random rand = new Random();
    public final ReentrantLock bagLock;

    
    /** 
     * @param name
     */
    public void setName(bagName name){
        this.name = name;
    }
    
    /** 
     * @return bagName
     */
    public bagName getName(){
        return this.name;
    }

    
    /** 
     * @return Boolean
     * returns a boolean value true if the bag is empty
     */
    public Boolean isEmpty(){
        if (this.bagContents.size() == 0){
            return true;
        }else{
            return false;
        }
    }

    
    /** 
     * @param pebble
     * Adds a whole pebble entry to the bag contents
     */
    public void addPebble(Entry<Integer, bagName> pebble){
        this.bagContents.add(pebble);
    }
    
    /** 
     * @param value
     * creates a pebble entry based on the integer value and the bag name and adds it to the bag contents
     */
    public void addPebble(Integer value){
        this.bagContents.add(new SimpleEntry<>(value, this.name));
    }
    
    /** 
     * @param value
     * @param bag
     * creates a pebble based on the integer value and the given bag name (not necessarily the same as the instance of bag) and adds it to the bag contents
     */
    public void addPebble(Integer value, bagName bag){
        this.bagContents.add(new SimpleEntry<>(value, bag));
    }
    
    /** 
     * @return List<Entry<Integer, bagName>>
     * returns the ArrayList of pebbles representing the bag contents
     */
    public List<Entry<Integer, bagName>> getPebbles(){
        return this.bagContents;
    }
    
    /** 
     * @param pebbles
     * sets the bag contents to the given ArrayList of pebbles
     */
    public void setPebbles(List<Entry<Integer, bagName>> pebbles){
        this.bagContents = pebbles;
    }
    /**
     * Clears the bag contents
     */
    public void clearPebbles(){
        this.bagContents = new ArrayList<Entry<Integer, bagName>>();
    }

    
    /** 
     * @param pebbleFile
     * Loads pebbles from the given csv
     * Handles any errors that may have been thrown
     */
    public void loadPebbles(String pebbleFile){
        ///
        /// need to verify enough pebbles for the game some how (maybe pass in number of players)
        ///
        Path filePath = Paths.get(pebbleFile);
        try(BufferedReader reader = Files.newBufferedReader(filePath, StandardCharsets.UTF_8)){
            String line = reader.readLine();
            while (line != null){
                String[] pebbles = line.split(","); // create a list of integers
                for (String pebble : pebbles){
                    try{
                        Integer pebbleValue = Integer.parseInt(pebble);
                        if (pebbleValue < 0){ // if pebble value is negitive make positive (this is a design choice - another option would be throw an error)
                            pebbleValue = - pebbleValue;
                        }else if (pebbleValue == 0){
                            throw new IOException("A pebble value of 0 was attempted to be used"); // pebble value cannot be 0
                        }
                        this.addPebble(pebbleValue, this.name);
                    }catch (Exception exception){
                        throw new IOException("A pebble value that could not be converted into an Int was used");
                    }
                }
                line = reader.readLine();
            }
        }catch (IOException IOException){
            System.out.println("Invalid file!");
        }
    }

    
    /** 
     * @return Entry<Integer, bagName>
     * Get a random pebbel from the bag contents and return it
     */
    public Entry<Integer, bagName> getRandomPebble(){
        int randomInt = rand.nextInt(bagContents.size()); // generate a random int based on bag size
        Entry<Integer, bagName> pebble = this.bagContents.get(randomInt); // find pebble
        bagContents.remove(randomInt); // remove it from the bag
        if (pebble == null){
            pebble = getRandomPebble();
        }
        return pebble;
    }
    /**
     * 
     * @param name
     * Create an empty bag
     */
    public Bag(bagName name){
        setName(name);
        bagLock = new ReentrantLock();
    }
    /**
     * 
     * @param name
     * @param pebbleFile
     * Crate a bag and fill with pebbles
     */
    public Bag(bagName name, String pebbleFile){
        setName(name);
        bagLock = new ReentrantLock();
        loadPebbles(pebbleFile);
    }
}
