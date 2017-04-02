package td03;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class JsonHelper
{
	// insert, search
	public String serialisation(String act, int val) 
	{
		ObjectMapper mapper = new ObjectMapper();
		String s = null;
		try{
			actionClass mc = new actionClass(act, val);
			s = mapper.writeValueAsString(mc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return s;
	}
		
	public actionClass deserialisation(String s) 
	{
		ObjectMapper mapper = new ObjectMapper();
		actionClass mc;
		try{
			mc = mapper.readValue(s, actionClass.class);
			return mc;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static class actionClass {
		private String action;
		private int val = 0;
		
		public actionClass() {
		}
			
		public actionClass(String action, int val) {
			this.action = action;
			this.val = val;
		}
				
		public String getAction(){
			return action;
			
		}
		public int getVal(){
			return val;
		}
	}
}
