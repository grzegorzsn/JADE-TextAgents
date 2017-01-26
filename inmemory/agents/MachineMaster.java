package inmemory.agents;

// java -cp lib\jade.jar;out\production\agents\ jade.Boot -gui -agents s1:inmemory.agents.MachineMaster;w1:inmemory.agents.Worker;w2:inmemory.agents.Worker;w3:inmemory.agents.Worker
// java -cp lib\jade.jar;out\production\agents\ jade.Boot -container -host 192.168.0.80 -agents w4:inmemory.agents.Worker

import inmemory.DataContainer;
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
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by Grzegorz on 2017-01-23.
 */

public class MachineMaster extends Agent {
    private MachineMasterGUI myGui;
    private AID[] workersOnPlatform;
    private AID[] workersOnMachine;
    private ArrayList<TextJobPart> partsToProcess = null;
    private ArrayList<TextJobPart> partsProcessed = null;
    private boolean jobProcessing = false;
    private OutputGUI resultsGui;
    private Random random = new Random();
    private boolean inmmemory = true; // TODO GUI radiobutton needed.
    private StringBuilder fullText = new StringBuilder();

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
         //addBehaviour(new sendJobsBehaviour());

         addBehaviour(new listener());
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

        /*  TODO
            According to inmmemory flag,
            jobs should be processed on the same machine or on other machines.
            To simplify machine may be understood as single container.

            If inmemory is true workers from other machines should be invited to machine of master.
            Then workers on machine should be asked to process parts of job.

            Else job parts should be sent to workers on other machines.
            In this case job parts must not be processed on the machine, which contains master.
            */

        partsToProcess = TextJobProcessor.loadParts(path, input);
        partsProcessed = new ArrayList<TextJobPart>();
        for (TextJobPart part : partsToProcess)
            part.setId(random.nextInt());
        jobProcessing = true;
        addBehaviour(new refreshWorkersOnPlatform());
        addBehaviour(new inviteWorkers());
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
            for(AID workerAID : workersOnPlatform)
            {
                ACLMessage request = new ACLMessage(ACLMessage.REQUEST);
                request.addReceiver(workerAID);
                String content = null;
                request.setLanguage("text-jobs");
                request.setOntology("text-job-inivitation");
                try {
                    content = Serializer.toString(here());
                } catch (IOException e) {
                    e.printStackTrace();
                }
                request.setContent(content);
                myAgent.send(request);
            }
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
            if (msg != null  && msg.getLanguage().equals("text-jobs")) {
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
                        if(!jobProcessing) break; // TODO send message to Worker, that there is no job processing.
                        content = msg.getContent();
                        TextJobPart part = null;
                        try {
                            part = (TextJobPart)Serializer.fromString(content);
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        System.out.println("Part received: " +  part.getResults());
                        partsProcessed.add(part); // TODO verify part is correctly processed.
                        if(checkJobProcessed()) manageJobProcessed();
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

    private boolean checkJobProcessed() {
        // TODO: check more precissly.
        return partsProcessed.size() == partsToProcess.size();

    }

    private void manageJobProcessed()
    {
        Collections.sort(partsProcessed);

        DataContainer.foundLines = new ArrayList<Integer>();

        System.out.println("MASTER: job done, parts sorted.");
        for(TextJobPart part : partsProcessed) {
            fullText.append(part.getLines().toString());

            DataContainer.foundLines.addAll(part.getResults());
        }

        DataContainer.TextToParse = fullText.toString();

        resultsGui = new OutputGUI();
        resultsGui.showOutput();

    }



    private class processJob extends Behaviour {

        public void action() {
        }

        public boolean done() {
                return true;
        }

    }


}
