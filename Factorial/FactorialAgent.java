package td2;

import java.util.Random;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class FactorialAgent extends Agent {
	protected void setup(){
		addBehaviour(new BehaviourCylic());
	}
	
	private AID getReceiver(){
		
		AID res = null;
		DFAgentDescription template = new DFAgentDescription();
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operation");
		sd.setName("Multiplication");
		template.addServices(sd);
		try{
			DFAgentDescription [] result = DFService.search(this, template);
			if(result.length > 0){
				Random randomGenerator = new Random();
				int idx = randomGenerator.nextInt(result.length);
				res = result[idx].getName();
			}
		}catch(Exception e){
			e.printStackTrace();
		}
		return res;
		
	}
	
	public class BehaviourCylic extends Behaviour{
		
		int conversationId = 0;

		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.REQUEST), 
					MessageTemplate.and(MessageTemplate.MatchConversationId(String.valueOf(conversationId)),
							MessageTemplate.or(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
									MessageTemplate.MatchPerformative(ACLMessage.FAILURE))
							));		
			ACLMessage message = receive(mt);
			if(message != null){
				
				switch (message.getPerformative()){
				case ACLMessage.REQUEST:
					//receive the message from console
					int n = Integer.parseInt(message.getContent());
					ACLMessage messageCheckStockage = new ACLMessage(ACLMessage.REQUEST);
					messageCheckStockage.addReceiver(new AID("Stockage", AID.ISLOCALNAME));
					messageCheckStockage.setContent(String.valueOf(n));
					messageCheckStockage.setConversationId(String.valueOf(conversationId));
					send(messageCheckStockage);
					break;
				case ACLMessage.INFORM:
					//receive the message from StockageAgent with result
					System.out.println(message.getContent());
					break;
				case ACLMessage.FAILURE:
					//receive the message from StockageAgent without result
					int num = Integer.parseInt(message.getContent());
					addBehaviour(new FactorialBehaviour(conversationId, num));
					conversationId++;
					
					break;
				}
				/*
				int n = Integer.parseInt(message.getContent());
				int res = checkIfExit(n);		
				if (res > 0){
					System.out.println(res);
				}
				else{
					addBehaviour(new FactorialBehaviour(conversationId, n));
					conversationId++;
				}*/
					
			}
		}

		@Override
		public boolean done() {
			return false;
		}
		/*
		private int checkIfExit(int n) {
			
			ACLMessage message = new ACLMessage(ACLMessage.REQUEST);
			message.setConversationId(String.valueOf(conversationId));
			message.addReceiver(new AID("Stockage", AID.ISLOCALNAME));
			message.setContent(String.valueOf(n));
			
			
			
			return -1;
		}*/
	}
	
	public class FactorialBehaviour extends Behaviour {
		int number;
		int cmp;
		int res;
		int conversationId;
		
		public FactorialBehaviour(int conversationId, int fact)
		{
			this.conversationId = conversationId;
			cmp = fact;
			number = fact;
			res = 1;
			caculateParallel();
		}
		
		@Override
		public void action() {
		
			MessageTemplate mt;
			mt = MessageTemplate.and(MessageTemplate.MatchConversationId(conversationId+""), 
					MessageTemplate.MatchPerformative(ACLMessage.INFORM));
			ACLMessage message = receive(mt);
			if(message != null){
					System.out.println("message type: Inform");
					calculate(message);
			}
			else {
				block();
			}
		}

		@Override
		public boolean done() {
			return cmp < 0;
		}
				
		private void calculate (ACLMessage message) {
			res = res * Integer.parseInt(message.getContent());
			if (cmp > 1) {
				caculateParallel();
			}
			else {
				System.out.println(res);
				ACLMessage msg = new ACLMessage(ACLMessage.INFORM);
				msg.addReceiver(new AID("Stockage", AID.ISLOCALNAME));
				msg.setContent(res +"," +number);
				send(msg);
				cmp = -1;
			}
		}
		
		private void caculateParallel()
		{
			calculateMulti (cmp, cmp - 1);
			cmp -= 2;
		}
		
		private void calculateMulti (int a, int b) {
			ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
			msg.setConversationId(String.valueOf(conversationId));
			msg.addReceiver(getReceiver());
			//msg.setContent(a + "x" + b);
			operationJsonHelper helper = new operationJsonHelper();
			msg.setContent(helper.serialisation(a, b));
			System.out.println(helper.serialisation(a, b));
			send(msg);
		}
		
	}
}
