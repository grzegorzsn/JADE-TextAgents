package inmemory.agents;

import inmemory.BookSellerAgent;
import inmemory.textProcessing.TextJob;
import inmemory.textProcessing.TextJobPart;
import inmemory.textProcessing.TextJobProcessor;
import jade.core.Agent;
import jade.core.Location;
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

        addBehaviour(new listener());
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

    private class listener extends CyclicBehaviour {
        public void action() {
            ACLMessage msg = myAgent.receive();
            if (msg != null && msg.getLanguage().equals("text-jobs")) {
                //System.out.println("Worker " + getLocalName() + " some msg received");
                String ont = msg.getOntology();
                ACLMessage reply;
                String content;
                switch (ont)
                {
                    case "text-job-sending-job":
                        content = msg.getContent();
                        reply = msg.createReply();
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
                        //if(tj != null) tj.displayName();
                        reply.setOntology("text-job-sending-job");
                        myAgent.send(reply);
                        break;

                    case "text-job-part-to-process":
                        content = msg.getContent();
                        reply = msg.createReply();
                        TextJobPart part = null;
                        try {
                            part = (TextJobPart)Serializer.fromString(content);
                            System.out.println("Worker " + getLocalName() + " job received");
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        TextJobPart processedPart = TextJobProcessor.process(part);
                        reply.setContent(myAgent.getName());
                        reply.setOntology("text-job-processed-part");
                        try {
                            content = Serializer.toString(processedPart);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        reply.setContent(content);
                        myAgent.send(reply);
                        break;

                    case "text-job-inivitation":
                        reply = msg.createReply();
                        msg.getSender().getResolversArray();
                        Location dest = null;
                        try {
                            dest = (Location)Serializer.fromString( msg.getContent());
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        if (dest != null) {
                            doMove(dest);
                            reply.setOntology("text-job-inivitation-accepted");
                        }
                        else
                        {
                            reply.setOntology("text-job-inivitation-incorrect");
                        }
                        myAgent.send(reply);
                        break;

                    default:
                        System.out.println("WORKER: unknown message");
                }

                addBehaviour(new listener());
                block();
            }
            else {
                block();
            }
        }
    }  // End of inner class OfferRequestsServer


}
