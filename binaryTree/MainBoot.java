package td03;

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
			AgentController ac = mc.createNewAgent("Reception", "td03.ReceptAgent", null);
			ac.start();
			AgentController ac2 = mc.createNewAgent("Root", "td03.NodeAgent", new Object[] {mc});
			ac2.start();
		}
		catch (Exception ex) {
			ex.printStackTrace();
		}
	}
}