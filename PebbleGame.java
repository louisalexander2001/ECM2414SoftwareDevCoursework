package ECM2414SoftwareDevCoursework;
/**
 * Pebble game
 * 
 * @author Louis Alexander
 * @version 1.0
 *
 */

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.locks.ReentrantLock;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PebbleGame{

    /**
     * Initialise variables
     */
    private Integer numOfPlayers;
    private List<Bag> bags = new ArrayList<Bag>();
    private List<Player> players = new ArrayList<Player>();
    private List<Thread> threadPool = new ArrayList<Thread>();
    private PebbleGame game = this;
    private volatile Player winner;
    public final ReentrantLock gameLock;

    
    /** 
     * @param bag - the bag to be refilled
     * This function refills a black bag from its corrosponding white bag
     */
    public void refillBag(Bag bag){
        switch (bag.getName()){
            case X:
                bag.setPebbles(this.bags.get(3).getPebbles());
                this.bags.get(3).clearPebbles();
                break;
            case Y:
                bag.setPebbles(this.bags.get(4).getPebbles());
                this.bags.get(4).clearPebbles();
                break;
            case Z:
                bag.setPebbles(this.bags.get(5).getPebbles());
                this.bags.get(5).clearPebbles();
                break;
            case A: case B: case C: // this should never be true
                break;
            default:
                break;

        }
    }

    /**
     * This function starts all the players by starting each thread
     */
    public synchronized void runGame(){
        for (Thread thread : this.threadPool){
            thread.start();
        }
    }

    /**
     * This function prints the winner and their winning hand before closing each of the 
     * players output files, unlocking the game and exeting the program
     */
    public void endGame(){
        List<Integer> playersHand = new ArrayList();
        for (Entry<Integer, bagName> pebble : this.winner.getHand()){
            playersHand.add(pebble.getKey());
        }
        System.out.println("The winner was palyer: " + this.winner.playerNumber.toString() + " with hand: " + playersHand.toString());
        for (Player player : this.players){
            player.closeFile();
        }
        System.out.println("All files are now closed.\nThank you for playing.");
        System.out.println("This is now the endgame method then the program should end.");
        this.gameLock.unlock();
        System.exit(0);
    }

    /**
     * This function locks the instance of the game and then sends an interrupt to each thread that has a player asigned to it.
     */
    public void stop(){
        this.gameLock.lock();
        for (Thread thread : this.threadPool){
            thread.interrupt();
        }
        this.endGame();
    }

    
    /**
     * 
     * @param numOfPlayers
     * @param bagName
     * @param sc
     * @return a bag class Bag
     */
    public Bag bagGenerator(Integer numOfPlayers, bagName bagName, Scanner sc){
        Boolean flag = true;
        Bag bag = new Bag(bagName);
        while (flag){
            System.out.println("Please enter the location of bag " + bagName.toString() + " to load: ");
            if (sc.hasNext("e") || sc.hasNext("E")){
                System.exit(0);
            }
            String input = sc.nextLine();
            bag = new Bag(bagName, input);
            if (bag.getPebbles().size() < numOfPlayers*11){ // ensure there are at least 11 times as many pebbles in the bag as there are players
                System.out.println("Please enter a valid file that has at least 11 times the number of pebbles as number of players.");
            }else{
                break;
            }
        }
        return bag;
    }

    /**
     * Player Class
     */
    class Player extends Thread{
        /**
         * Player variables
         */
        private Integer playerNumber;
        private List<Entry<Integer, bagName>> playersHand = new ArrayList<Entry<Integer, bagName>>();
        private bagName previousPickBag;
        private FileWriter writer;
        private Random random = new java.util.Random();
        private PebbleGame game;
        
        /**
         * 
         * @return List<Entry<Integer, bagName>>
         * Get the ArrayList of the players hand
         */
        public List<Entry<Integer, bagName>> getHand(){
            return this.playersHand;
        }

        /**
         * 
         * @return Boolean
         * returns true 
         */
        private Boolean checkHand(){
            int total = 0;
            for (Entry<Integer, bagName> pebble : this.playersHand){
                total += pebble.getKey();
            }
            if (total == 100){
                try{
                    this.writer.write("!!!!!!!!Player" + this.playerNumber.toString() + " has a hand totalling 100\n");
                    this.writer.flush();
                }catch (IOException e){
                    System.out.println("An IOException occurred.");
                    e.printStackTrace();
                }
                return true;
            }else{
                return false;
            }
        }
        /**
         * Closes the file writer
         */
        public void closeFile(){
            try{
                this.writer.close();
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }
        /**
         * Picks a random pebble from a random bag and adds it to the players hand
         */
        public void pickAPebble(){
            Bag randomBag = bags.get(this.random.nextInt(3)); // Generate random integer to select a bag and then get that bag
            randomBag.bagLock.lock(); // obtain the lock on the bag
            try{
                if (randomBag.isEmpty()){
                    game.refillBag(randomBag); // check if the bag is enpty and if it is refill it
                }
                Entry<Integer, bagName> pebble = randomBag.getRandomPebble(); // call bags' internal getRandomPebble function to get a pebble
                this.playersHand.add(pebble); // Add the pebble to the players hand
                this.previousPickBag = pebble.getValue(); // assign the name of the bag the pebble was picked from to the variable so that the player has a memory of where the last pebble came from
                try{// write the pebble the player picked and the players new hand to the corrosponding players file
                    this.writer.write("Player" + this.playerNumber.toString() + " has drawn a " + pebble.getKey().toString() +
                                 " from bag " + pebble.getValue().toString()+"\n");
                    this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString()+"\n");
                    if (this.playerNumber == 0){
                        for (Bag bag : this.game.bags){
                            this.writer.write(bag.getPebbles());
                        }
                    }
                    this.writer.flush(); // flush the writer buffer
                }catch (IOException e){
                    System.out.println("An IOException occurred.");
                    e.printStackTrace();
                }
            }finally{
                randomBag.bagLock.unlock(); // unlock the bag
            }
        }
        /**
         * Discard a random pebble from the players hand to the corrosponding white bag depending on where the last pebble was picked from.
         */
        public void discardAPebble(){
            int randint = random.nextInt(10); // get random int to select random pebble from players hand
            Entry<Integer, bagName> pebble = this.playersHand.get(randint); // get pebble
            this.playersHand.remove(randint); // remove pebble from players hand
            pebble.setValue(this.previousPickBag); // change the bag name assigned to the pebble to the bagname of where the last pebble came from
            switch (this.previousPickBag){// swithch statement to put pebble in the right place. Obtains the bag lock before adding the pebble to the bag before finally relasing the lock
                case X:
                    bags.get(3).bagLock.lock();
                    try{
                        bags.get(3).addPebble(pebble);
                    }finally{
                        bags.get(3).bagLock.unlock();
                    }
                    break;
                case Y:
                    bags.get(4).bagLock.lock();
                    try{
                        bags.get(4).addPebble(pebble);
                    }finally{
                        bags.get(4).bagLock.unlock();
                    }
                    break;
                case Z:
                    bags.get(5).bagLock.lock();
                    try{
                        bags.get(5).addPebble(pebble);
                    }finally{
                        bags.get(5).bagLock.unlock();
                    }
                    break;
            }
            try{// write the pebble the player picked and the players new hand to the corrosponding players file
                this.writer.write("Player" + this.playerNumber.toString() + " has discarded a " + pebble.getKey().toString() +
                                  " to bag " + this.previousPickBag.toString()+"\n");
                this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString()+"\n");
                this.writer.flush(); // flush the writer buffer
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }
        /**
         * 
         * @param playerNumber - the number the player goes by
         * @param game - the instance of the game being played
         * Player Init function
         */
        public Player(Integer playerNumber, PebbleGame game){
            this.playerNumber = playerNumber;
            this.game = game;
            String fileName = "player"+playerNumber.toString()+"_output.txt";
            File file = new File(fileName);


            try{
                if(file.exists() && !file.isDirectory()) { // check if file exists
                    this.writer = new FileWriter(fileName); // initialise the file writer
                }else{
                    file.createNewFile(); // create file if it doesnt exist
                    this.writer = new FileWriter(fileName); // initialise the file writer
                }
            } catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }

        @Override
        public void run(){ // threaded run function to be executed on Thread.start()
            for (int i=0; i<10; i++){ // pick 10 random pebbles to setup hand
                this.pickAPebble();
            }
            Boolean winningHand = false;
            winningHand = checkHand(); // check hand 
            while (!Thread.currentThread().isInterrupted() && !winningHand){ // whie not interrupted and doesnt have a winning hand
                discardAPebble();
                pickAPebble();
                winningHand = checkHand();
            }
            if (winningHand){
                game.winner = this; // give game a reference to the winning player so details can be extracted
                game.stop();
            }
        }
    }

    public PebbleGame(){
        // setup scanner
        Scanner sc = new Scanner(System.in); 
        gameLock = new ReentrantLock();
        // greeting
        System.out.println("Welcome to the PebbleGame!!\nYou will be asked to enter the number of players.\n"
                         + "You will then be asked for the location of three files in turn containing the comma "
                         + "seperated integer values for the pebble weights. \nThe integer balues must be strictly"
                         + " positive.\nThe game will then be simulated, and output written to files in this directory.");
        
        System.out.println("Please enter the number of players: "); 
        if (sc.hasNext("e") || sc.hasNext("E")){
            System.exit(0);
        }
        this.numOfPlayers = sc.nextInt();
        sc.nextLine(); // needed as input data type changes
        // create black bags
        bags.add(bagGenerator(this.numOfPlayers, bagName.X, sc));
        bags.add(bagGenerator(this.numOfPlayers, bagName.Y, sc));
        bags.add(bagGenerator(this.numOfPlayers, bagName.Z, sc));
        // create white bags
        bags.add(new Bag(bagName.A));
        bags.add(new Bag(bagName.B));
        bags.add(new Bag(bagName.C));

        for (int i=0; i<this.numOfPlayers; i++){ // create players and add them to the ArrayList. Create Threads with a player assigned and add to the threadpool
            this.players.add(new Player(i, this));
            this.threadPool.add(new Thread(players.get(i)));
        }
        this.runGame();

        
    }

    
    /** 
     * @param args
     */
    public static void main(String[] args){
        PebbleGame game = new PebbleGame(); // start an instance of the game
    }
}