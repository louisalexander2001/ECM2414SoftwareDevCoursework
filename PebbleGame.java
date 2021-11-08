package ECM2414SoftwareDevCoursework;

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

    private Integer numOfPlayers;
    private List<Bag> bags = new ArrayList<Bag>();
    private List<Player> players = new ArrayList<Player>();
    private List<Thread> threadPool = new ArrayList<Thread>();
    private PebbleGame game = this;
    private volatile Player winner;
    public final ReentrantLock gameLock;

    public void refillBag(Bag bag){
        List<Entry<Integer, bagName>> pebbles;
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

    public synchronized void runGame(){
        for (Thread thread : this.threadPool){
            thread.start();
        }
    }

    public void endGame(){
        System.out.println("The winner was palyer: " + this.winner.playerNumber.toString());
        for (Player player : this.players){
            player.closeFile();
        }
        System.out.println("All files are now closed.\nThank you for playing.");
        System.out.println("This is now the endgame method then the program should end.");
        this.gameLock.unlock();
        System.exit(0);
    }

    public void stop(){
        this.gameLock.lock();
        System.out.println("Stop has been called");
        for (Thread thread : this.threadPool){
            thread.interrupt();
        }
        this.endGame();
    }

    public Bag bagGenerator(Integer numOfPlayers, bagName bagName, Scanner sc){
        Boolean flag = true;
        Bag bag = new Bag(bagName);
        while (flag){
            System.out.println("Please enter the location of bag " + bagName.toString() + " to load: ");
            String input = sc.nextLine();
            bag = new Bag(bagName, input);
            if (bag.getPebbles().size() < numOfPlayers*11){
                System.out.println("Please enter a valid file that has at least 11 times the number of pebbles as number of players.");
            }else{
                break;
            }
        }
        return bag;
    }

    class Player extends Thread{
        
        private Integer playerNumber;
        private List<Entry<Integer, bagName>> playersHand = new ArrayList<Entry<Integer, bagName>>();
        private bagName previousPickBag;
        private FileWriter writer;
        private Random random = new java.util.Random();
        private PebbleGame game;


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
        
        public void closeFile(){
            try{
                this.writer.close();
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }

        public void pickAPebble(){
            Bag randomBag = bags.get(this.random.nextInt(3));
            randomBag.bagLock.lock();
            try{
                if (randomBag.isEmpty()){
                    game.refillBag(randomBag);
                }
                Entry<Integer, bagName> pebble = randomBag.getRandomPebble();
                this.playersHand.add(pebble);
                this.previousPickBag = pebble.getValue();
                try{
                    this.writer.write("Player" + this.playerNumber.toString() + " has drawn a " + pebble.getKey().toString() +
                                 " from bag " + pebble.getValue().toString()+"\n");
                    this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString()+"\n");
                    this.writer.flush();
                }catch (IOException e){
                    System.out.println("An IOException occurred.");
                    e.printStackTrace();
                }
            }finally{
                randomBag.bagLock.unlock();
            }
        }
        public void discardAPebble(){
            int randint = random.nextInt(10);
            Entry<Integer, bagName> pebble = this.playersHand.get(randint);
            this.playersHand.remove(randint);
            pebble.setValue(this.previousPickBag);
            switch (this.previousPickBag){
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
            try{
                this.writer.write("Player" + this.playerNumber.toString() + " has discarded a " + pebble.getKey().toString() +
                                  " to bag " + this.previousPickBag.toString()+"\n");
                this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString()+"\n");
                this.writer.flush();
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }

        public Player(Integer playerNumber, PebbleGame game){
            this.playerNumber = playerNumber;
            this.game = game;
            String fileName = "player"+playerNumber.toString()+"_output.txt";
            File file = new File(fileName);


            try{
                if(file.exists() && !file.isDirectory()) { 
                    this.writer = new FileWriter(fileName);
                }else{
                    file.createNewFile();
                    this.writer = new FileWriter(fileName);
                }
            } catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }

        public void run(){
            for (int i=0; i<10; i++){
                this.pickAPebble();
            }
            Boolean winningHand = false;
            winningHand = checkHand();
            while (!Thread.currentThread().isInterrupted() && !winningHand){
                discardAPebble();
                pickAPebble();
                winningHand = checkHand();
            }
            if (winningHand){
                game.winner = this;
                game.stop();
            }
        }
    }

    public PebbleGame(){
        //setup scanner
        Scanner sc = new Scanner(System.in); 
        gameLock = new ReentrantLock();
        //greeting
        System.out.println("Welcome to the PebbleGame!!\nYou will be asked to enter the number of players.\n"
                         + "You will then be asked for the location of three files in turn containing the comma "
                         + "seperated integer values for the pebble weights. \nThe integer balues must be strictly"
                         + " positive.\nThe game will then be simulated, and output written to files in this directory.");
        
        System.out.println("Please enter the number of players: "); 
        this.numOfPlayers = sc.nextInt();
        sc.nextLine();
        //create bags
        bags.add(bagGenerator(this.numOfPlayers, bagName.X, sc));
        bags.add(bagGenerator(this.numOfPlayers, bagName.Y, sc));
        bags.add(bagGenerator(this.numOfPlayers, bagName.Z, sc));

        bags.add(new Bag(bagName.A));
        bags.add(new Bag(bagName.B));
        bags.add(new Bag(bagName.C));
        for (int i=0; i<this.numOfPlayers; i++){
            this.players.add(new Player(i, this));
            this.threadPool.add(new Thread(players.get(i)));
        }
        this.runGame();

        
    }

    public static void main(String[] args){
        PebbleGame game = new PebbleGame();
    }
}




// notes to self. child thread (player) throws interrupt which parent thread (game) is listening for with isInterruped() and then the parent thread can intterupt al the other threads
// my program might be going wrong due to deadlock

// parent thread (game) should call wait() and then the child thread should notify the parent thread which then interrups all children. parent can then check for a winning hand 

// using volitile may be useful