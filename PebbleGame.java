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
    private volatile Player winner;

    private Bag locateBag(bagName name){
        Bag desiredBag = null;
        for (Bag bag : this.bags){
            if (bag.getName() == name){
                desiredBag = bag;
                break;
            }
        }
        return desiredBag;
    }

    private void refillBag(Bag bag){
        System.out.println("refil has been called");
        for (Bag b : bags){
            System.out.println(b.getName().toString() + b.getPebbles().size());
        }
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
        System.out.println("Becomes: ");
        for (Bag b : bags){
            System.out.println(b.getName().toString() + b.getPebbles().size());
        }

    }

    public synchronized void runGame(){
        for (Thread thread : this.threadPool){
            thread.start();
        }
    }

    public void endGame(){
        System.out.println("The winner was: " + this.winner.playerNumber.toString());
        System.out.println("This is now the endgame method then the program should end.");
    }

    public void stop(){
        System.out.println("Stop has been called");
        for (Thread thread : this.threadPool){
            thread.interrupt();
        }
        this.endGame();
    }

    public void safeQuit(){
        for (Player player : this.players){
            player.closeFile();
        }
        System.out.println("All files are now closed.\n Thank you for playing.");
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
            Bag randomBag = bags.get(random.nextInt(3));
            randomBag.bagLock.lock();
            try{
                if (randomBag.isEmpty()){
                    refillBag(randomBag);
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
            Entry<Integer, bagName> pebble = this.playersHand.get(random.nextInt(10));
            this.playersHand.remove(this.playersHand.indexOf(pebble));
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
        //greeting
        System.out.println("Welcome to the PebbleGame!!\nYou will be asked to enter the number of players.\n"
                         + "You will then be asked for the location of three files in turn containing the comma "
                         + "seperated integer values for the pebble weights. \nThe integer balues must be strictly"
                         + " positive.\nThe game will then be simulated, and output written to files in this directory.");
        
        System.out.println("Please enter the number of players: "); 
        this.numOfPlayers = sc.nextInt();
        //create bags
        System.out.println("Please enter the location of bag X to load: ");
        sc.nextLine();
        String input = sc.nextLine();
        bags.add(new Bag(bagName.X, input));
        System.out.println("Please enter the location of bag Y to load: "); 
        input = sc.nextLine();
        bags.add(new Bag(bagName.Y, input));
        System.out.println("Please enter the location of bag Z to load: "); 
        input = sc.nextLine();
        bags.add(new Bag(bagName.Z, input));

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