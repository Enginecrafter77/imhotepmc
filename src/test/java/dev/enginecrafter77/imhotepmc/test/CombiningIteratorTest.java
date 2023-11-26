package dev.enginecrafter77.imhotepmc.test;

import dev.enginecrafter77.imhotepmc.util.CombiningIterator;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import scala.actors.threadpool.Arrays;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CombiningIteratorTest {
	@Test
	public void testSimpleCrossCombinations()
	{
		List<String> l1 = Arrays.asList(new String[] {"a", "b", "c"});
		List<String> l2 = Arrays.asList(new String[] {"1", "2", "3"});

		List<String> concat = StreamSupport.stream(CombiningIterator.combinations(l1, l2).spliterator(), false).map(CombiningIteratorTest::concat).collect(Collectors.toList());

		Assertions.assertEquals(9, concat.size());
		Assertions.assertEquals( "a1", concat.get(0));
		Assertions.assertEquals( "a2", concat.get(1));
		Assertions.assertEquals( "a3", concat.get(2));
		Assertions.assertEquals( "b1", concat.get(3));
		Assertions.assertEquals( "b2", concat.get(4));
		Assertions.assertEquals( "b3", concat.get(5));
		Assertions.assertEquals( "c1", concat.get(6));
		Assertions.assertEquals( "c2", concat.get(7));
		Assertions.assertEquals( "c3", concat.get(8));
	}

	@Test
	public void testEmptyCrossCombinations()
	{
		List<String> l1 = Arrays.asList(new String[] {"a", "b", "c"});
		List<String> l2 = Arrays.asList(new String[0]);

		List<String> concat = StreamSupport.stream(CombiningIterator.combinations(l1, l2).spliterator(), false).map(CombiningIteratorTest::concat).collect(Collectors.toList());

		Assertions.assertEquals(0, concat.size());
	}

	@Test
	public void testMinimalCrossCombinations()
	{
		List<String> l1 = Arrays.asList(new String[] {"a"});
		List<String> l2 = Arrays.asList(new String[] {"1"});

		List<String> concat = StreamSupport.stream(CombiningIterator.combinations(l1, l2).spliterator(), false).map(CombiningIteratorTest::concat).collect(Collectors.toList());

		Assertions.assertEquals(1, concat.size());
		Assertions.assertEquals("a1", concat.get(0));
	}

	@Test
	public void testSimpleSelfCombinations()
	{
		List<String> l1 = Arrays.asList(new String[] {"a", "b", "c", "d"});

		List<String> concat = StreamSupport.stream(CombiningIterator.selfCombinations(l1).spliterator(), false).map(CombiningIteratorTest::concat).collect(Collectors.toList());

		Assertions.assertEquals(6, concat.size()); // 3!
		Assertions.assertEquals( "ab", concat.get(0));
		Assertions.assertEquals( "ac", concat.get(1));
		Assertions.assertEquals( "ad", concat.get(2));
		Assertions.assertEquals( "bc", concat.get(3));
		Assertions.assertEquals( "bd", concat.get(4));
		Assertions.assertEquals( "cd", concat.get(5));
	}

	@Test
	public void testEmptySelfCombinations()
	{
		List<String> l1 = Arrays.asList(new String[] {"a"});
		List<String> concat = StreamSupport.stream(CombiningIterator.selfCombinations(l1).spliterator(), false).map(CombiningIteratorTest::concat).collect(Collectors.toList());
		Assertions.assertEquals(0, concat.size());
	}

	@Test
	public void testMinimalSelfCombinations()
	{
		List<String> l1 = Arrays.asList(new String[] {"a", "b"});
		List<String> concat = StreamSupport.stream(CombiningIterator.selfCombinations(l1).spliterator(), false).map(CombiningIteratorTest::concat).collect(Collectors.toList());
		Assertions.assertEquals(1, concat.size());
		Assertions.assertEquals( "ab", concat.get(0));
	}

	public static String concat(CombiningIterator.Pair<String, String> pair)
	{
		return pair.getFirst() + pair.getSecond();
	}
}
