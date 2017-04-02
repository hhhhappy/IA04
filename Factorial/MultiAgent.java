package td2;

import java.util.Random;
import java.util.concurrent.TimeUnit;

import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;

public class MultiAgent extends Agent {
	protected void setup(){
		addBehaviour(new MultiBehaviour("x"));
		DFAgentDescription dfad = new DFAgentDescription();
		dfad.setName(getAID());
		ServiceDescription sd = new ServiceDescription();
		sd.setType("Operation");
		sd.setName("Multiplication");
		dfad.addServices(sd);
		try{
			DFService.register(this, dfad);
		}catch(Exception e){
			e.printStackTrace();
		}
	}
	
	public class MultiBehaviour extends Behaviour {

		private String op;
		public MultiBehaviour(String op) {
			this.op = op;
		}
		
		@Override
		public void action() {
			ACLMessage message = receive();
			if (message != null)
			{
				answer(message);
			}
			else {
				block();
			}
		}

		@Override
		public boolean done() {
			// TODO Auto-generated method stub
			return false;
		}
		public void answer(ACLMessage msg){
			String par = msg.getContent();
			operationJsonHelper helper = new operationJsonHelper();
			operationJsonHelper.multClass mc = helper.deserialisation(par);
			ACLMessage reply = msg.createReply();
			System.out.println(this.getAgent().getName());
			int n = 0, a = mc.getNum1(), b = mc.getNum2();
			if (mc.getOperation().equals("x"))
			{
				n = a * b;
				reply.setPerformative(ACLMessage.INFORM);
				reply.setContent(String.valueOf(n));
			}
			else {
				reply.setPerformative(ACLMessage.FAILURE);
				reply.setContent("unknown operator");
			}
			
			// delay
			try {
				Random randomGenerator = new Random();
				float sec = randomGenerator.nextFloat() * 9.5f + 0.5f;
				System.out.println(sec);
				Thread.sleep((long)(sec * 500));
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			send(reply);
		}
	}
}
