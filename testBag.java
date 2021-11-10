import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

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
        assertEquals("Chack that bag is the right size", 100, bag.getPebbles().size());        
    }


    @Test
    public void testGetName(){
        assertEquals("getname() works", bagName.X, new Bag(bagName.X).getName());
    }

    @Test
    public void testSetName(){
        assertEquals(bagName.Y, new Bag(bagName.X));
    }
}
