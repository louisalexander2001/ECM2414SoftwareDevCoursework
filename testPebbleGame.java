import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Map.Entry;
import java.util.AbstractMap.SimpleEntry;
import java.util.ArrayList;
import java.util.List;

import org.junit.Test;

public class testPebbleGame {
    @Test
    public void testRefillBag(){
        PebbleGame game = new PebbleGame();
        game.bags.add(new Bag(bagName.X));
        game.bags.add(new Bag(bagName.Y));
        game.bags.add(new Bag(bagName.Z));
        game.bags.add(new Bag(bagName.A));
        game.bags.get(3).addPebble(1);
        game.bags.get(3).addPebble(2);
        game.bags.get(3).addPebble(3);
        game.refillBag(game.bags.get(0));
        assertEquals(3, game.bags.get(0).getPebbles().size());
        assertEquals(0, game.bags.get(1).getPebbles().size());
    }

    @Test
    public void testGetHand(){
        PebbleGame game = new PebbleGame();
        game.players.add(game.new Player(1, game));
        List<Entry<Integer, bagName>> hand = new ArrayList<Entry<Integer, bagName>>();
        hand.add(new SimpleEntry<>(1, bagName.X));
        hand.add(new SimpleEntry<>(2, bagName.Y));
        hand.add(new SimpleEntry<>(3, bagName.Z));
        game.players.get(0).playersHand = hand;
        assertEquals(hand, game.players.get(0).getHand());
    }

    @Test
    public void testCheckHand(){
        PebbleGame game = new PebbleGame();
        game.players.add(game.new Player(1, game));
        List<Entry<Integer, bagName>> hand = new ArrayList<Entry<Integer, bagName>>();
        hand.add(new SimpleEntry<>(1, bagName.X));
        hand.add(new SimpleEntry<>(2, bagName.Y));
        hand.add(new SimpleEntry<>(3, bagName.Z));
        game.players.get(0).playersHand = hand;
        game.players.add(game.new Player(1, game));
        hand = new ArrayList<Entry<Integer, bagName>>();
        hand.add(new SimpleEntry<>(11, bagName.X));
        hand.add(new SimpleEntry<>(20, bagName.Y));
        hand.add(new SimpleEntry<>(4, bagName.Z));
        hand.add(new SimpleEntry<>(15, bagName.Z));
        hand.add(new SimpleEntry<>(5, bagName.Z));
        hand.add(new SimpleEntry<>(3, bagName.Z));
        hand.add(new SimpleEntry<>(6, bagName.Z));
        hand.add(new SimpleEntry<>(2, bagName.Z));
        hand.add(new SimpleEntry<>(4, bagName.Z));
        hand.add(new SimpleEntry<>(30, bagName.Z));
        game.players.get(1).playersHand = hand;
        assertTrue(!game.players.get(0).checkHand());
        assertTrue(game.players.get(1).checkHand());
    }

    @Test
    public void testPickAPebble(){
        PebbleGame game = new PebbleGame();
        game.bags.add(new Bag(bagName.X));
        game.bags.add(new Bag(bagName.Y));
        game.bags.add(new Bag(bagName.Z));
        game.bags.get(0).addPebble(1);
        game.bags.get(1).addPebble(2);
        game.bags.get(2).addPebble(3);
        game.players.add(game.new Player(1, game));
        game.players.get(0).pickAPebble();
        assertTrue((game.players.get(0).playersHand.get(0).getKey() == 1) ||
                   (game.players.get(0).playersHand.get(0).getKey() == 2) ||
                   (game.players.get(0).playersHand.get(0).getKey() == 3));
        assertTrue((game.bags.get(0).getPebbles().size() == 0) ||
                   (game.bags.get(1).getPebbles().size() == 0) ||
                   (game.bags.get(2).getPebbles().size() == 0));
    }

    @Test
    public void testDiscardAPebble(){
        PebbleGame game = new PebbleGame();
        game.bags.add(new Bag(bagName.X));
        game.bags.add(new Bag(bagName.Y));
        game.bags.add(new Bag(bagName.Z));
        game.bags.add(new Bag(bagName.A));
        game.bags.add(new Bag(bagName.B));
        game.bags.add(new Bag(bagName.Z));
        game.players.add(game.new Player(1, game));
        List<Entry<Integer, bagName>> hand = new ArrayList<Entry<Integer, bagName>>();
        hand.add(new SimpleEntry<>(1, bagName.X));
        hand.add(new SimpleEntry<>(2, bagName.Y));
        hand.add(new SimpleEntry<>(3, bagName.Z));
        hand.add(new SimpleEntry<>(4, bagName.X));
        hand.add(new SimpleEntry<>(5, bagName.Y));
        hand.add(new SimpleEntry<>(6, bagName.Z));        
        hand.add(new SimpleEntry<>(7, bagName.X));
        hand.add(new SimpleEntry<>(8, bagName.Y));
        hand.add(new SimpleEntry<>(9, bagName.Z));
        hand.add(new SimpleEntry<>(10, bagName.Z));
        game.players.get(0).playersHand = hand;
        game.players.get(0).previousPickBag = bagName.X;
        game.players.get(0).discardAPebble();
        assertTrue((game.players.get(0).playersHand.size() == 9));
        assertTrue((game.bags.get(3).getPebbles().size() == 1));
    }
}
