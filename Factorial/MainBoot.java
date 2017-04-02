package td2;

import jade.core.ProfileImpl;
import jade.core.Runtime;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;

public class MainBoot {
	public static String MAIN_PROPERTIES_FILE = "properties/main";
	 
	public static void main(String[] args) {
		Runtime rt = Runtime.instance();
		ProfileImpl p = null;
		try {
			p = new ProfileImpl(MAIN_PROPERTIES_FILE);
			AgentContainer mc = rt.createMainContainer(p);
			AgentController ac = mc.createNewAgent("Fac", "td2.FactorialAgent", null);
			ac.start();
			AgentController ac2 = mc.createNewAgent("M1", "td2.MultiAgent", null);
			ac2.start();
			AgentController ac3 = mc.createNewAgent("M2", "td2.MultiAgent", null);
			ac3.start();
			AgentController ac4 = mc.createNewAgent("Stockage", "td2.StockageAgent", null);
			ac4.start();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}
