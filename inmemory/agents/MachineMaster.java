package inmemory.agents;

// java -cp lib\jade.jar;out\production\agents\ jade.Boot -gui -agents s1:inmemory.agents.MachineMaster;w1:inmemory.agents.Worker;w2:inmemory.agents.Worker;w3:inmemory.agents.Worker

import inmemory.textProcessing.TextJob;
import inmemory.textProcessing.TextJobPart;
import inmemory.textProcessing.TextJobProcessor;
import jade.core.AID;
import jade.core.Agent;
import jade.core.behaviours.*;
import jade.lang.acl.ACLMessage;
import jade.lang.acl.MessageTemplate;
import jade.domain.DFService;
import jade.domain.FIPAException;
import jade.domain.FIPAAgentManagement.DFAgentDescription;
import jade.domain.FIPAAgentManagement.ServiceDescription;
import jade.wrapper.ControllerException;

import java.io.IOException;

/**
 * Created by Grzegorz on 2017-01-23.
 */

public class MachineMaster extends Agent{
    private MachineMasterGUI myGui;
    private AID[] workersOnPlatform;
    private AID[] workersOnMachine;
    private TextJobPart[] partsToProcess = null;
    private TextJobPart[] partsProcessed = null;
    // Put agent initializations here
    protected void setup() {
        // Create and show the GUI
        myGui = new MachineMasterGUI(this);
        myGui.showGui();
        System.out.println("MachineMaster "+getAID().getName()+" HERE I AM.");

        // Register the service in the yellow pages
        DFAgentDescription dfd = new DFAgentDescription();
        dfd.setName(getAID());
        ServiceDescription sd = new ServiceDescription();
        sd.setType("text-jobs");
        sd.setName("text-jobs-master");
        //sd.setName("text-jobs-master");
        dfd.addServices(sd);
        try {
            DFService.register(this, dfd);
        }
        catch (FIPAException fe) {
            fe.printStackTrace();
        }

        // Add the behaviour
        addBehaviour(new refreshWorkersOnMachinePeriodically(this, 1000));

        //  Add the behaviour
         addBehaviour(new sendJobsBehaviour());

         addBehaviour(new listener());
    }

    public void sendJobs()
    {
        addBehaviour(new sendJobsBehaviour());
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

    public void manageJob(String path, String[] input) {
        partsToProcess = TextJobProcessor.loadParts(path, input);
        addBehaviour(new refreshWorkersOnPlatform());
        addBehaviour(new sendPartsToWorkers());
        addBehaviour(new listener());
    }

    private class sendPartsToWorkers extends Behaviour {

        public void action() {
            if(workersOnPlatform == null || workersOnPlatform.length < 1)
            {
                System.out.println("MachineMaster "+getAID().getName()+" I DO NOT SEE ANY WORKERS");
                return;
            }
            int nrOfWorkers = workersOnPlatform.length;
            int currentWorker = 0;
            for( TextJobPart part : partsToProcess)
            {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                String content = "";
                request.setLanguage("text-jobs");
                request.setOntology("text-job-part-to-process");
                request.addReceiver(workersOnPlatform[currentWorker]);
                try {
                    content = Serializer.toString(part);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request.setConversationId("text-jobs-request");
                request.setReplyWith("request" + System.currentTimeMillis()); // Unique value
                request.setContent(content);
                myAgent.send(request);
                currentWorker++;
                if(currentWorker >= nrOfWorkers) currentWorker = 0;
            }
            addBehaviour(new listener());
            block();
        }

        public boolean done() {
            return true;
        }
    }

    private class refreshWorkersOnPlatform extends Behaviour {

        public void action() {
            DFAgentDescription template = new DFAgentDescription();
            ServiceDescription sd = new ServiceDescription();
            sd.setType("text-jobs");
            sd.setName("text-jobs-worker");
            template.addServices(sd);
            try {
                DFAgentDescription[] result = DFService.search(myAgent, template);
                workersOnPlatform = new AID[result.length];
                for (int i = 0; i < result.length; ++i) {
                    workersOnPlatform[i] = result[i].getName();
                    System.out.println("Worker "+workersOnPlatform[i]);
                }
            }
            catch (FIPAException fe) {
                fe.printStackTrace();
            }
        }

        public boolean done() {
            return true;
        }
    }

    private class refreshWorkersOnMachinePeriodically extends TickerBehaviour {

        public refreshWorkersOnMachinePeriodically(Agent a, long period) {
            super(a, period);
        }

    protected void onTick() {
        myAgent.addBehaviour(new refreshWorkersOnPlatform());
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
            if(workersOnPlatform == null || workersOnPlatform.length < 1)
            {
                System.out.println("MachineMaster "+getAID().getName()+" I DO NOT SEE ANY WORKERS");
                return;
            }
            int nrOfWorkers = workersOnPlatform.length;
            int currentWorker = 0;
            for( TextJobPart part : partsToProcess)
            {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                String content = "";
                request.setLanguage("text-jobs");
                request.setOntology("text-job-sending-job");
                try {
                    content = Serializer.toString(part);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request.setConversationId("text-jobs-request");
                request.setReplyWith("request" + System.currentTimeMillis()); // Unique value
                request.setContent(content);
                myAgent.send(request);
                currentWorker++;
                if(currentWorker >= nrOfWorkers) currentWorker = 0;
            }
            TextJob tj = new TextJob();
            addBehaviour(new listener());
            block();
        }

        public boolean done() {
            return true;
        }
    }

    private class listener extends Behaviour {
        public void action()
        {
            ACLMessage msg = myAgent.receive();
            if (msg != null  && msg.getLanguage() == "text-jobs") {
                String ont = msg.getOntology();
                String content;
                switch (ont)
                {
                    case "text-jobs-job-result":
                        System.out.println("Result received");
                        break;
                    case "text-job-inivitation-accepted":
                        break;
                    case "text-job-processed-part":
                        content = msg.getContent();
                        TextJobPart part = null;
                        try {
                            part = (TextJobPart)Serializer.fromString(content);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        System.out.println("MASTER: Received part " + part.getNumberInQueue() + ". Results: " + part.getResults()) ;
                        break;
                    default:
                        System.out.println("MASTER: Unknown message received");
                }
            }
            addBehaviour(new listener());
            block();
        }

        public boolean done()
        {
            return true;
        }
    }

    private class sendJobsBehaviour extends Behaviour
    {
        private int repliesCnt = 0; // The counter of replies from workers
        private MessageTemplate mt; // The template to receive replies

        public void action() {
            // Send the cfp to all sellers
            if(workersOnPlatform == null || workersOnPlatform.length < 1)
            {
                System.out.println("MachineMaster "+getAID().getName()+" I DO NOT SEE ANY WORKERS");
                return;
            }
            ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
            for (int i = 0; i < workersOnPlatform.length; ++i) {
                request.addReceiver(workersOnPlatform[i]);
            }
            TextJob tj = new TextJob();
            String content = "";
            request.setLanguage("text-jobs");
            request.setOntology("text-job-sending-job");

            try {
                content = Serializer.toString(tj);
            } catch (IOException e) {
                e.printStackTrace();
            }
            request.setConversationId("text-jobs-request");
            request.setReplyWith("request" + System.currentTimeMillis()); // Unique value
            request.setContent(content);
            myAgent.send(request);
            mt = MessageTemplate.and(MessageTemplate.MatchConversationId("text-jobs-invitation"),
                    MessageTemplate.MatchInReplyTo(request.getReplyWith()));
            block();
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
