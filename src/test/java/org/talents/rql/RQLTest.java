package org.mayhem.rql;
import static org.junit.Assert.assertEquals;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import net.jazdw.rql.parser.ASTNode;
import net.jazdw.rql.parser.RQLParser;

import org.junit.Before;
import org.junit.Test;


public class RQLTest {
	
	RQLParser p = new RQLParser();

	@Before
	public void setup() {
/*		ValueConverter EventConverter = new ValueConverter() {
			
			@Override
			public Object convert(String arg0) throws ConverterException {
				// TODO Auto-generated method stub
				return null;
			}
		};
		
		//Converter c = new Converter(Map<String, ValueConverter>);
*/	}
	
	/**
	 * Make sure things work by default
	 */
	@Test
	public void basicTest() {
		
		//Create a query
		ASTNode expected = new ASTNode("eq", "name", "jack");
        assertEquals(expected, p.parse("name=jack"));
        
	}
	
	
	@Test
	public void singleIntegerTest() {
		
		//implement the type of filter to process
		MapFilter<Map.Entry<String,Object>> filter = new MapFilter<>();
		
		//Create a map entry
		Map.Entry<String, Object> entry = new AbstractMap.SimpleEntry<String, Object>("port", new Integer(12345));
		
		//put the entry in a list
		List<Entry<String,Object>> entryList = new ArrayList<>();
		entryList.add(entry);
		
		//Create a query
		ASTNode portQuery = p.parse("port=12345");
		List<Entry<String,Object>> results = portQuery.accept(filter, entryList);
		assertEquals(1,results.size());
		
		portQuery = p.parse("port=12346");
		results = portQuery.accept(filter, entryList);
		assertEquals(0,results.size());
	}
	
	@Test
	public void integerStringTest() {
		//implement the type of filter to process
		MapFilter<Map.Entry<String,Object>> filter = new MapFilter<>();
		
		List<Entry<String,Object>> entryList = new ArrayList<>();
		
		//Create a map entry
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", new Integer(12345)));  //this is the matching entry
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", new Integer(1234)));
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", "12345"));  //  --> this yields a type mismatch
		
		//Create a query Criteria
		ASTNode portQuery = p.parse("port=12345");  //- this parses as an Integer
		
		//Results collector
		List<Entry<String,Object>> results = portQuery.accept(filter, entryList);
		assertEquals(1,results.size());  //- only matches the integer supplied.
	}
	
	@Test
	public void stingTest() {
		//implement the type of filter to process
		MapFilter<Map.Entry<String,Object>> filter = new MapFilter<>();
		
		List<Entry<String,Object>> entryList = new ArrayList<>();
		
		//Create a map entry
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", new Integer(12345)));  //this is the matching entry
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", new Integer(1234)));
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", "12345"));  //  --> this yields a type mismatch
		
		//Create a query Criteria
		ASTNode portQuery = p.parse("port=like=*12345"); // - this parses as a string
		
		//Results collector
		List<Entry<String,Object>> results = portQuery.accept(filter, entryList);
		assertEquals(1,results.size()); // this only matches the string
	}
	
	@Test
	public void sting2Test() {
		//implement the type of filter to process
		MapFilter<Map.Entry<String,Object>> filter = new MapFilter<>();
		
		List<Entry<String,Object>> entryList = new ArrayList<>();
		
		//Create a map entry
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", new Integer(12345)));  //this is the matching entry
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", new Integer(1234)));
		entryList.add(new AbstractMap.SimpleEntry<String, Object>("port", "12345"));  //  --> this yields a type mismatch
		
		//Create a query Criteria
		ASTNode portQuery = p.parse("port=like='12345'"); //this parses as a string
		
		//Results collector
		List<Entry<String,Object>> results = portQuery.accept(filter, entryList);
		assertEquals(1,results.size());  //only matches the string
	}
	
}