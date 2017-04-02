package td03;

import java.util.Scanner;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.core.behaviours.OneShotBehaviour;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;

public class ReceptAgent extends Agent {
	
	int conversationId = 0;
	
	protected void setup(){
		this.addBehaviour(new ReceptFromConsoleBahaviour());
		this.addBehaviour(new ReceptFromAgentInformBahaviour());
	}

	class ReceptFromConsoleBahaviour extends Behaviour {

		@Override
		public void action() {		
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST); 
			ACLMessage message = receive(mt);
			if(message != null){
				String s = message.getContent();
				addBehaviour(new SendActionBehaviour(s));
			}
		}

		@Override
		public boolean done() {
			return false;
		}
	}
	
	class ReceptFromAgentInformBahaviour extends Behaviour {

		@Override
		public void action() {		
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.INFORM); 
			ACLMessage message = receive(mt);
			if(message != null){
				String s = message.getContent();
				System.out.println(s);
			}	
		}
		
		@Override
		public boolean done() {
			return false;
		}
	}
		
	class SendActionBehaviour extends OneShotBehaviour {
		String s;
		public SendActionBehaviour(String s) {
			this.s = s;
		}
		
		@Override
		public void action() {
			Scanner sc = new Scanner(s);
			while (sc.hasNext()) {
				String action = sc.next("[A-Za-z]+").toLowerCase();
				int value = (action.equals("display") ? 0 : sc.nextInt());
				
				// send message
				ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
				msg.addReceiver(new AID("Root", AID.ISLOCALNAME));
				JsonHelper helper = new JsonHelper();
				msg.setContent(helper.serialisation(action, value));
				msg.setConversationId(String.valueOf(conversationId));
				System.out.println(helper.serialisation(action, value));
				send(msg);
				++conversationId;
			}
		}
	}
}

