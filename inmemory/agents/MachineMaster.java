package inmemory.agents;

import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;

/**
 * Created by Grzegorz on 2017-01-23.
 */

public class MachineMaster extends Agent{
    private MachineMasterGUI myGui;
    private AID[] workersOnPlatform;
    private AID[] workersOnMachine;

    // Put agent initializations here
    protected void setup() {
        // Create and show the GUI
        myGui = new MachineMasterGUI(this);
        myGui.showGui();

        // Register the service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("text-jobs");
        sd.setName("machine-with-text-jobs");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add the behaviour
        //addBehaviour(new OfferRequestsServer());

        //  Add the behaviour
        // addBehaviour(new PurchaseOrdersServer());
    }

    // Put agent clean-up operations here
    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
        // Close the GUI
        myGui.close();
        // Printout a dismissal message
        System.out.println("MachineMaster "+getAID().getName()+" terminating.");
    }

    private class refreshWorkersOnPlatform extends TickerBehaviour {
        public refreshWorkersOnPlatform(Agent a, long period) {
            super(a, period);
        }

        protected void onTick() {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("book-selling");
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                workersOnPlatform = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    workersOnPlatform[i] = result[i].getName();
                }
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }
    }

    private class refreshWorkersOnMachine extends TickerBehaviour {
        public refreshWorkersOnMachine(Agent a, long period) {
            super(a, period);
        }
        protected void onTick() {
        }
    }

    private class inviteWorkers extends Behaviour
    {
        private int repliesCnt = 0; // The counter of replies from workers
        private MessageTemplate mt; // The template to receive replies

        public void action() {
                    // Send the cfp to all sellers
                    ACLMessage cfp = new ACLMessage(ACLMessage.CFP);
                    for (int i = 0; i < workersOnPlatform.length; ++i) {
                        cfp.addReceiver(workersOnPlatform[i]);
                    }
                    cfp.setConversationId("book-trade");
                    cfp.setReplyWith("cfp" + System.currentTimeMillis()); // Unique value
                    myAgent.send(cfp);
                    mt = MessageTemplate.and(MessageTemplate.MatchConversationId("book-trade"),
                            MessageTemplate.MatchInReplyTo(cfp.getReplyWith()));
            }

        public boolean done() {
            return true;
        }
    }

    private class processJob extends Behaviour {

        public void action() {
        }

        public boolean done() {
                return true;
        }

    }


}
