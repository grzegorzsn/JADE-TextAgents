package inmemory.agents;

import inmemory.BookSellerAgent;
import inmemory.textProcessing.TextJob;
import jade.core.Agent;
import jade.core.behaviours.CyclicBehaviour;
import jade.domain.DFService;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.domain.FIPAException;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.lang.acl.UnreadableException;

import java.io.IOException;
import java.util.Hashtable;

/**
 * Created by Grzegorz on 2017-01-23.
 */
public class Worker extends Agent {
    protected void setup() {

        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("text-jobs");
        sd.setName("text-jobs-worker");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add the behaviour serving queries from buyer agents
        addBehaviour(new JobRequestServer());

        // Add the behaviour serving purchase orders from buyer agents
        // addBehaviour(new BookSellerAgent.PurchaseOrdersServer());

        System.out.println("WORKER ALIVE");
    }

    protected void takeDown() {
        // Deregister from the yellow pages
        try {
            DFService.deregister(this);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }
    }

    private class JobRequestServer extends CyclicBehaviour {
        public void action() {
            MessageTemplate mt = MessageTemplate.MatchPerformative(ACLMessage.REQUEST);
            ACLMessage msg = myAgent.receive(mt);
            if (msg != null) {
                // CFP Message received. Process it
                String content = msg.getContent();
                ACLMessage reply = msg.createReply();
                TextJob tj = null;
                try {
                    tj = (TextJob)Serializer.fromString(content);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (ClassNotFoundException e) {
                    e.printStackTrace();
                }
                reply.setContent(myAgent.getName());
                System.out.println(myAgent.getName() + " -----> JOB NAME IS BELOW");
                if(tj != null) tj.displayName();
                myAgent.send(reply);
            }
            else {
                block();
            }
        }
    }  // End of inner class OfferRequestsServer


}
