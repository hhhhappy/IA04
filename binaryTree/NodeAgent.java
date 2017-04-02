package td03;

import java.util.Random;
import java.util.Scanner;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.Behaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.wrapper.AgentContainer;
import jade.wrapper.AgentController;
import jade.wrapper.StaleProxyException;


public class NodeAgent extends Agent {
	public enum EnumChild {
		e_left,
		e_right
	};
	private AgentContainer mc;
	private String[] childNames = {"", ""};
	private int val;
	private boolean isValSetted = false;
	
	// getters & setters
	public String getChildName(EnumChild idx) {
		return childNames[idx.ordinal()];
	}

	public void setChildName(EnumChild idx, String name) {
		childNames[idx.ordinal()] = name;
	}

	public int getVal() {
		return val;
	}

	public void setVal(int val) {
		this.val = val;
		this.isValSetted = true;
	}

	public boolean isValSetted() {
		return isValSetted;
	}

	// set up
	protected void setup(){
		// get arguments
		Object[] args = getArguments();
		mc = (AgentContainer) args[0];
		if (!this.getName().equals("Root" + "@tdia04")) {
			val = (int)args[1];
			isValSetted = true;
		} 

		// add behaviors
		this.addBehaviour(new RequestBehaviour(this));
	}
	
	// create child agent
	protected void createChildNode(EnumChild idx, int val) {
		String name = "Node" + val;
		this.setChildName(idx, name);
		// create
		try {
		AgentController ac2 = mc.createNewAgent(name, "td03.NodeAgent", new Object[] {mc, val});
		ac2.start();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
	
	// pass request to child
	private void requestChild(EnumChild idx, String s, String id) {
		ACLMessage msg = new ACLMessage(ACLMessage.REQUEST);
		msg.setContent(s);
		msg.setConversationId(id);
		msg.addReceiver(new AID(getChildName(idx), AID.ISLOCALNAME));
		send(msg);
	}
		
	// left child or right child
	private EnumChild getChildIdx(int val) {
		if (getVal() > val) {
			return EnumChild.e_left;
		} else {
			return EnumChild.e_right;
		}
	}
	
	// behaviours
	class RequestBehaviour extends Behaviour {
		NodeAgent parent = null;
		
		public RequestBehaviour(NodeAgent parent) {
			this.parent = parent;
		}
		
		@Override
		public void action() {	
			MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST); 
			ACLMessage message = receive(mt);
			if(message != null){
				String s = message.getContent();
				
				// get msg content
				JsonHelper helper = new JsonHelper();
				JsonHelper.actionClass ac = helper.deserialisation(s);
				ACLMessage reply = message.createReply();
				
				// cases
				switch (ac.getAction()) {
				case "insert":
					addBehaviour(new InsertBehaviour(parent, ac.getVal(), s, reply));
					break;
				
				case "search":
					addBehaviour(new SearchBehaviour(parent, ac.getVal(), s, reply));
					break;
					
				case "display":
					addBehaviour(new DisplayBehaviour(parent, s, reply));
					break;
				}
			}
		}

		@Override
		public boolean done() {
			return false;
		}
	}
	
	// abstract node behaviours
	abstract class NodeBehaviour extends Behaviour {
		boolean isSent = false;		
		NodeAgent parent = null;
		ACLMessage reply;
		
		public NodeBehaviour(NodeAgent p, ACLMessage r) {
			parent = p;
			reply = r;
		}
		
		@Override
		public boolean done() {
			return isSent;
		}
		
		protected void replyMessage(String s) {
			reply.setContent(s);
			reply.setPerformative(ACLMessage.INFORM);
			send(reply);
			isSent = true;
		}
	}
	
	// insert
	class InsertBehaviour extends NodeBehaviour {
		String conversationId;
		
		public InsertBehaviour(NodeAgent p, int val, String s, ACLMessage r) {
			super(p, r);
			conversationId = reply.getConversationId();
			if (!parent.isValSetted()) {
				parent.setVal(val);
				replyMessage(val + " insert successfully!");
			} else if (parent.getVal() == val) {
				replyMessage(val + " already insert!");
			} else {
				// decide left or right
				EnumChild idx = getChildIdx(val);
				// insert to ask child to insert
				if (!parent.getChildName(idx).isEmpty()) {
					parent.requestChild(idx, s, conversationId);
				} else {
					parent.createChildNode(idx, val);
					replyMessage(val + " insert successfully!");
				}
			}
		}
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
													MessageTemplate.MatchConversationId(conversationId)); 
			ACLMessage message = receive(mt);
			if(message != null) {
				replyMessage(message.getContent());
			}
		}		
	}
	
	
	// search 
	class SearchBehaviour extends NodeBehaviour {
		String conversationId;
		
		public SearchBehaviour(NodeAgent p, int val, String s, ACLMessage r) {
			super(p, r);
			conversationId = reply.getConversationId();
			
			if (!parent.isValSetted()) {
				replyMessage(val + " doesn't exist.");
			} else if (parent.getVal() == val) {
				replyMessage(val + " exists in the tree");
			} else {
				// decide left or right
				EnumChild idx = getChildIdx(val);
				// insert to ask child to insert
				if (!parent.getChildName(idx).isEmpty()) {
					parent.requestChild(idx, s, conversationId);
				} else {
					replyMessage(val + " doesn't exist.");
				}
			}
		}
		
		@Override
		public void action() {
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
													MessageTemplate.MatchConversationId(conversationId)); 
			ACLMessage message = receive(mt);
			if(message != null){
				replyMessage(message.getContent());
			}
		}

	}
	
	
	// display
	class DisplayBehaviour extends NodeBehaviour {
		boolean InfoGot[] = {false, false};
		String infoChild[] = {"", ""};
		String conversationIds[] = {"left", "right"};
		
		public DisplayBehaviour(NodeAgent p, String s, ACLMessage r) {
			super(p, r);
			if (!parent.isValSetted()) {
				replyMessage("The binary tree is empty!");
			} else {
				for (EnumChild idx : EnumChild.values()) {
					conversationIds[idx.ordinal()] += reply.getConversationId();
					if (!parent.getChildName(idx).isEmpty()) {
						requestChild(idx, s, conversationIds[idx.ordinal()]);
					} else {
						InfoGot[idx.ordinal()] = true;
					}
				}
			}
		}
		
		@Override
		public void action() {
			// send message
			if (InfoGot[0] && InfoGot[1]) {
				replyMessage("(" + infoChild[0]
								 + String.valueOf(parent.getVal())
								 + infoChild[1]
								 + ")");
			}
			
			// get msg from child 1 or child 2
			MessageTemplate mt = MessageTemplate.and(MessageTemplate.MatchPerformative(ACLMessage.INFORM),
													MessageTemplate.or(MessageTemplate.MatchConversationId(conversationIds[0]),
																		MessageTemplate.MatchConversationId(conversationIds[1]))); 
			ACLMessage message = receive(mt);			
			if(message != null) {
				int idx = (message.getConversationId() == conversationIds[0]) ? 0 : 1;
				infoChild[idx] = message.getContent();
				InfoGot[idx] = true;
			}
		}

	}
}