package test;

import java.util.ArrayList;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gshare.rest.vertx.config.restconfig.RestRequest;
import com.gshare.rest.vertx.processing.helper.BuiltInClassNotFoundException;

public class ClassLoadTest {
	
	public static void main(String[] args) throws JsonProcessingException {
		
		/*
		Class builtInClass = null;
		try {
			builtInClass = Class.forName("com.gshare.rest.vertx.processing.builtin.impl.LoginBuiltIn");
		} catch (ClassNotFoundException e) {
			e.printStackTrace();			
		}
		
		System.out.println(builtInClass.toString());
		*/
		
		/*
		String aaa = "00000000001111111111222222222233          00000000001111111111222222222233";
		
		String[] sp = aaa.split("[\\s]+");
		
		System.out.println(sp[0]);
		*/
		
		/*
		
		ArrayList<String> al = new ArrayList<String>();
		al.add("aa");
		
		String which = al.get(0);
		
		which = "bb";
		
		ArrayList<Object> newOne = new ArrayList<Object>();
		
		
		
		System.out.println(al.get(0));
		*/
		ObjectMapper mapper = new ObjectMapper();
		
		System.out.println(mapper.writerWithDefaultPrettyPrinter().writeValueAsString(new RestRequest()));
	}

}
