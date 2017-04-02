package td2;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class operationJsonHelper
{
	public String serialisation(int a, int b) 
	{
		ObjectMapper mapper = new ObjectMapper();
		String s = null;
		try{
			multClass mc = new multClass(a, b);
			s = mapper.writeValueAsString(mc);
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		
		return s;
	}
	
	public multClass deserialisation(String s) 
	{
		ObjectMapper mapper = new ObjectMapper();
		multClass mc;
		try{
			mc = mapper.readValue(s, multClass.class);
			return mc;
		}
		catch(Exception e) {
			e.printStackTrace();
		}
		return null;
	}
	
	public static class multClass {
		private String operation;
		private int num1;
		private int num2;
		
		public multClass() {
		}
		
		public multClass(String operation, int a, int b) {
			this.operation = operation;
			num1 = a;
			num2 = b;
		}
		
		public multClass(int a, int b){
			operation = "x";
			num1 = a;
			num2 = b;
		}
		
		public String getOperation(){
			return operation;
			
		}
		public int getNum1(){
			return num1;
		}
		public int getNum2(){
			return num2;
		}
		
	}
}
