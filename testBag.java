import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import org.junit.Test;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;


public class testBag {
    @Test
    public void tesBagCreationA(){
        Bag bag = new Bag(bagName.X);
        assertTrue("An instance of bag was created", bag instanceof Bag);
        assertEquals("Bag name was assigned correctly", bagName.X, bag.getName());
        assertEquals("Chack that bag is empty", 0, bag.getPebbles().size());
    }   

    @Test
    public void tesBagCreationB(){
        Bag bag = new Bag(bagName.X, "example_file_2.txt");
        assertTrue("An instance of bag was created", bag instanceof Bag);
        assertEquals("Bag name was assigned correctly", bagName.X, bag.getName());
        assertEquals(bag.getPebbles().toString(), 100, bag.getPebbles().size()); 
    }


    @Test
    public void testGetName(){
        assertEquals("getname() works", bagName.X, new Bag(bagName.X).getName());
    }

    @Test
    public void testSetName(){
        Bag bag = new Bag(bagName.X);
        bag.setName(bagName.Y);
        assertEquals(bagName.Y, bag.getName());
    }

    @Test
    public void testIsEmpty(){
        Bag bag = new Bag(bagName.X);
        assertTrue(bag.isEmpty());
    }

    @Test
    public void testAddPebbleA(){
        Bag bag = new Bag(bagName.X);
        bag.addPebble(new SimpleEntry<>(5, bagName.X));
        assertEquals(1, bag.getPebbles().size());
        assertEquals(new SimpleEntry<>(5, bagName.X), bag.getPebbles().get(0));
    }

    @Test
    public void testAddPebbleB(){
        Bag bag = new Bag(bagName.X);
        bag.addPebble(5);
        assertEquals(1, bag.getPebbles().size());
        assertEquals(new SimpleEntry<>(5, bagName.X), bag.getPebbles().get(0));
    }

    @Test
    public void testAddPebbleC(){
        Bag bag = new Bag(bagName.X);
        bag.addPebble(5, bagName.X);
        assertEquals(1, bag.getPebbles().size());
        assertEquals(new SimpleEntry<>(5, bagName.X), bag.getPebbles().get(0));
    }

    @Test
    public void testGetPebbles(){
        Bag bag = new Bag(bagName.X);
        bag.addPebble(new SimpleEntry<>(5, bagName.X));
        assertTrue(bag.getPebbles() instanceof ArrayList);
        List<Entry<Integer, bagName>> bagContents = new ArrayList<Entry<Integer, bagName>>();
        bagContents.add(new SimpleEntry<>(5, bagName.X));
        assertEquals(bagContents, bag.getPebbles());
    }

    @Test
    public void testSetPebbles(){
        Bag bag = new Bag(bagName.X);
        bag.addPebble(new SimpleEntry<>(5, bagName.X));
        List<Entry<Integer, bagName>> bagContents = new ArrayList<Entry<Integer, bagName>>();
        bagContents.add(new SimpleEntry<>(3, bagName.X));
        bag.setPebbles(bagContents);
        assertTrue(bag.getPebbles() instanceof ArrayList);
        assertEquals(bagContents, bag.getPebbles());
    }

    @Test
    public void testClearPebbles(){
        Bag bag = new Bag(bagName.X);
        bag.addPebble(5, bagName.X);
        bag.clearPebbles();
        assertEquals(0, bag.getPebbles().size());
    }

    @Test
    public void testLoadPebbles(){
        Bag bag = new Bag(bagName.X);
        bag.loadPebbles("example_file_1.csv");
        assertEquals(100, bag.getPebbles().size());
        assertTrue(bag.getPebbles() instanceof ArrayList);
    }

    @Test
    public void testGetRandomPebble(){
        Bag bag = new Bag(bagName.X);
        bag.addPebble(6, bagName.X);
        bag.addPebble(5, bagName.X);
        bag.addPebble(4, bagName.X);
        assertTrue(bag.getRandomPebble() instanceof Entry<?, ?>);
    }
}
