package td2;

import java.util.HashMap;
import java.util.Map;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.lang.acl.ACLMessage;

public class StockageAgent extends Agent {
	
	Map<Integer, Integer> map = new HashMap<Integer, Integer>();
	
	protected void setup(){
		this.addBehaviour(new StockageBehaviour());
	}
	public class StockageBehaviour extends Behaviour {

		@Override
		public void action() {
			ACLMessage message = receive();
			if (message != null)
			{
				switch (message.getPerformative()){
				case ACLMessage.REQUEST:
					Integer n = Integer.parseInt(message.getContent());
					Integer res = map.get(n);
					ACLMessage reply = message.createReply();
					if(res != null){
						reply.setContent(String.valueOf(res));
						reply.setPerformative(ACLMessage.INFORM);
					}
					else{
						reply.setContent(String.valueOf(n));
						reply.setPerformative(ACLMessage.FAILURE);
					}
					send(reply);
					break;
				case ACLMessage.INFORM:
					String stringStockage = message.getContent();
					String[] nums = stringStockage.split(",");
					Integer resStockage = Integer.parseInt(nums[0]);
					Integer numStockage = Integer.parseInt(nums[1]);
					
					map.put(numStockage, resStockage);
					break;
				}
			}
			else {
				block();
			}
		}

		@Override
		public boolean done() {
			
			return false;
		}
	}
}
