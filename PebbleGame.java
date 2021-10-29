import java.util.ArrayList;
import java.util.List;


public class PebbleGame{

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
        ...
        public Player(String arg){

        }

        public void run(){
            
        }
    }

    public static void main(String[] args){
        main game code here
    }
}