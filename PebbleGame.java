import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Scanner;


public class PebbleGame{

    private Integer numOfPlayers;
    private List<Bag> bags = new ArrayList();


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

    class Player implements Runnable{
        
        private Integer playerNumber;
        private List<Entry<Integer, bagName>> playersHand = new ArrayList<>();

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
        
        public Player(String arg){

        }

        public void run(){
            
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
        bags.add(new Bag(bagName.A));
        bags.add(new Bag(bagName.B));
        bags.add(new Bag(bagName.C));
        System.out.print("Please enter the location of bag 0 to load: "); 
        bags.add(new Bag(bagName.X, sc.nextLine()));
        System.out.print("Please enter the location of bag 1 to load: "); 
        bags.add(new Bag(bagName.Y, sc.nextLine()));
        System.out.print("Please enter the location of bag 2 to load: "); 
        bags.add(new Bag(bagName.Z, sc.nextLine()));
    }

    public static void main(String[] args){
        PebbleGame game = new PebbleGame();
    }
}

///game output files shoule be in cwd