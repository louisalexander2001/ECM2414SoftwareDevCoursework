import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Random;

public class PebbleGame{

    private Integer numOfPlayers;
    private List<Bag> bags = new ArrayList();
    private List<Player> players = new ArrayList();
    private List<Thread> threadPool;

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
        private List<Entry<Integer, bagName>> playersHand = new ArrayList<>();
        private bagName previousPickBag;
        private FileWriter writer;
        Random random = new java.util.Random();


        private Boolean checkHand(){
            int total = 0;
            for (Entry<Integer, bagName> pebble : this.playersHand){
                total += pebble.getKey();
            }
            if (total == 100){
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
            Entry<Integer, bagName> pebble = bags.get(random.nextInt(3)).getRandomPebble();
            this.playersHand.add(pebble);
            this.previousPickBag = pebble.getValue();
            try{
                this.writer.write("Player" + this.playerNumber.toString() + " has drawn a " + pebble.getKey().toString() +
                                  " from bag " + pebble.getValue().toString());
                this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString());
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }
        public void discardAPebble(){
            Integer randInt = random.nextInt(10);
            Entry<Integer, bagName> pebble = this.playersHand.get(randInt);
            this.playersHand.remove(randInt);
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
                                  " to bag " + this.previousPickBag.toString());
                this.writer.write("Player" + this.playerNumber.toString() + "'s hand is " + this.playersHand.toString());
            }catch (IOException e){
                System.out.println("An IOException occurred.");
                e.printStackTrace();
            }
        }

        public Player(Integer playerNumber){
            this.playerNumber = playerNumber;
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

            public void run(){
                Boolean handStatus = false;
                handStatus = checkHand();
                while (!handStatus){
                    discardAPebble();
                    pickAPebble();
                    handStatus = checkHand();
                }
                // here i tell everyone i have a hand
            }
        }

    public PebbleGame(){
        //setup scanner
        Scanner sc= new Scanner(System.in); 
        //greeting
        System.out.println("Welcome to the PebbleGame!!\n You will be asked to enter the number of players.\n"
                         + "You will then be asked for the location of three files in turn containing the comma "
                         + "seperated integer values for the pebble weights. \n The integer balues must be strictly"
                         + " positive.\nThe game will then be simulated, and output written to files in this directory.");
        
        System.out.print("Please enter the number of players: "); 
        this.numOfPlayers = sc.nextInt();
        //create bags
        System.out.print("Please enter the location of bag 0 to load: "); 
        bags.add(new Bag(bagName.X, sc.nextLine()));
        System.out.print("Please enter the location of bag 1 to load: "); 
        bags.add(new Bag(bagName.Y, sc.nextLine()));
        System.out.print("Please enter the location of bag 2 to load: "); 
        bags.add(new Bag(bagName.Z, sc.nextLine()));
        bags.add(new Bag(bagName.A));
        bags.add(new Bag(bagName.B));
        bags.add(new Bag(bagName.C));
        for (int i=0; i<this.numOfPlayers; i++){
            this.players.add(new Player(i));
            this.threadPool.add(new Thread(players.get(i)));
        }
        for (Thread thread : threadPool){
            thread.start();
        }
        
    }

    public static void main(String[] args){
        PebbleGame game = new PebbleGame();
    }
}

///game output files shoule be in cwd