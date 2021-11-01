import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.net.PasswordAuthentication;
import java.util.Random;

public class PebbleGame{

    private Integer numOfPlayers;
    private List<Bag> bags = new ArrayList<Bag>();
    private List<Player> players = new ArrayList<Player>();
    private List<Thread> threadPool = new ArrayList<Thread>();
    private PebbleGame game = this;
    private Player winner;

    private synchronized Bag locateBag(bagName name){
        Bag desiredBag = null;
        for (Bag bag : this.bags){
            if (bag.getName() == name){
                desiredBag = bag;
                break;
            }
        }
        return desiredBag;
    }

    private synchronized void refillBag(Bag bag){
        switch (bag.getName()){
            case X:
                Bag bagA = locateBag(bagName.A);
                bag.setPebbles(bagA.getPebbles());
                bagA.clearPebbles();
                break;
            case Y:
                Bag bagB = locateBag(bagName.B);
                bag.setPebbles(bagB.getPebbles());
                bagB.clearPebbles();
                break;
            case Z:
                Bag bagC = locateBag(bagName.C);
                bag.setPebbles(bagC.getPebbles());
                bagC.clearPebbles();
                break;
            case A: case B: case C: // this should never be true
                break;
            default:
                break;
        }

    }

    public void safeQuit(){
        for (Player player : this.players){
            player.closeFile();
        }
        System.out.println("All files are now closed.\n Thank you for playing.");
    }

    class Player implements Runnable{
        
        private Integer playerNumber;
        private List<Entry<Integer, bagName>> playersHand = new ArrayList<Entry<Integer, bagName>>();
        private bagName previousPickBag;
        private FileWriter writer;
        Random random = new java.util.Random();
        private PebbleGame game;


        private synchronized Boolean checkHand(){
            int total = 0;
            for (Entry<Integer, bagName> pebble : this.playersHand){
                total += pebble.getKey();
            }
            if (total == 100){
                try{
                    this.writer.write("!!!!!!!!Player" + this.playerNumber.toString() + " has a hand totalling 100\n");
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

        public synchronized void pickAPebble(){
            Entry<Integer, bagName> pebble = bags.get(random.nextInt(3)).getRandomPebble();
            this.playersHand.add(pebble);
            this.previousPickBag = pebble.getValue();
            try{
                this.writer.write("Player" + this.playerNumber.toString() + " has drawn a " + pebble.getKey().toString() +
                                  " from bag " + pebble.getValue().toString()+"\n");
                this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString()+"\n");
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }
        public synchronized void discardAPebble(){
            Integer randInt = random.nextInt(10);
            Entry<Integer, bagName> pebble = this.playersHand.get(randInt);
            this.playersHand.remove(playersHand.indexOf(pebble));
            switch (this.previousPickBag){
                case X:
                    bags.get(0).addPebble(pebble);
                case Y:
                    bags.get(1).addPebble(pebble);
                case Z:
                    bags.get(2).addPebble(pebble);
            }
            try{
                this.writer.write("Player" + this.playerNumber.toString() + " has discarded a " + pebble.getKey().toString() +
                                  " to bag " + this.previousPickBag.toString()+"\n");
                this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString()+"\n");
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }

        public Player(Integer playerNumber){
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
            Boolean handStatus = false;
            handStatus = checkHand();
            synchronized (this){
                while (!Thread.interrupted()){
                    while (!handStatus){
                        discardAPebble();
                        pickAPebble();
                        handStatus = checkHand();
                    }
                    winner = this;
                    game.notify();
                }
            }
        }
    }

    public PebbleGame(){
        //setup scanner
        Scanner sc = new Scanner(System.in); 
        //greeting
        System.out.println("Welcome to the PebbleGame!!\nYou will be asked to enter the number of players.\n"
                         + "You will then be asked for the location of three files in turn containing the comma "
                         + "seperated integer values for the pebble weights. \nThe integer balues must be strictly"
                         + " positive.\nThe game will then be simulated, and output written to files in this directory.");
        
        // System.out.println("Please enter the number of players: "); 
        // this.numOfPlayers = sc.nextInt();
        // //create bags
        // System.out.println("Please enter the location of bag X to load: ");
        // String input = sc.nextLine();
        // bags.add(new Bag(bagName.X, input));
        // System.out.println("Please enter the location of bag Y to load: "); 
        // input = sc.nextLine();
        // bags.add(new Bag(bagName.Y, input));
        // System.out.println("Please enter the location of bag Z to load: "); 
        // input = sc.nextLine();

        System.out.println("Please enter the number of players: "); 
        this.numOfPlayers = 5;
        //create bags
        System.out.println("Please enter the location of bag X to load: ");
        bags.add(new Bag(bagName.X, "example_file_3.txt"));
        System.out.println("Please enter the location of bag Y to load: "); 
        bags.add(new Bag(bagName.Y, "example_file_3.txt"));
        System.out.println("Please enter the location of bag Z to load: "); 
        bags.add(new Bag(bagName.Z, "example_file_3.txt"));
        bags.add(new Bag(bagName.A));
        bags.add(new Bag(bagName.B));
        bags.add(new Bag(bagName.C));
        for (Bag bag : bags){
            System.out.println(bag.getPebbles().toString());
        }
        for (int i=0; i<this.numOfPlayers; i++){
            this.players.add(new Player(i));
            this.threadPool.add(new Thread(players.get(i)));
        }
        for (Thread thread : threadPool){
            thread.start();
        }
        synchronized (this){
            try{
                this.wait();
                for (Thread thread : threadPool){
                    thread.interrupt();
                }
            }catch(InterruptedException e){
                e.printStackTrace();
            }
        }

        
    }

    public static void main(String[] args){
        PebbleGame game = new PebbleGame();
    }
}




// notes to self. child thread (player) throws interrupt which parent thread (game) is listening for with isInterruped() and then the parent thread can intterupt al the other threads
// my program might be going wrong due to deadlock

// parent thread (game) should call wait() and then the child thread should notify the parent thread which then interrups all children. parent can then check for a winning hand 

// using volitile may be useful